package com.github.keeganwitt.katas.httpmonitor

import com.github.keeganwitt.katas.httpmonitor.model.AggregatingWindow
import com.github.keeganwitt.katas.httpmonitor.model.LogEvent
import spock.lang.Specification

class LogEventHandlerSpec extends Specification {
    LogEventHandler subject
    ByteArrayOutputStream os

    def setup() {
        os = new ByteArrayOutputStream()
        subject = new LogEventHandler(os, 10, 1, 3)
    }

    def cleanup() {
        os.close()
    }

    def "frequency report shows most frequently section with the most hits after reporting time window reached"() {
        when:
        subject.handle(new LogEvent(path: "/api/user", date: 1))
        subject.handle(new LogEvent(path: "/report", date: 5))
        subject.handle(new LogEvent(path: "/api/user", date: 10))
        subject.handle(new LogEvent(path: "/api/user", date: 131))

        then:
        os.flush()
        os.toString() == "/api -> hit 2 times${System.lineSeparator()}"
    }

    def "frequency report doesn't trigger if time window was not reached"() {
        when:
        subject.handle(new LogEvent(path: "/api/user", date: 1))
        subject.handle(new LogEvent(path: "/report", date: 6))
        subject.handle(new LogEvent(path: "/api/user", date: 12))

        then:
        os.flush()
        os.toString() == ""
    }

    def "resets frequency metrics when time window reached"() {
        when:
        subject.handle(new LogEvent(path: "/api/user", date: 1))
        subject.handle(new LogEvent(path: "/report", date: 5))
        subject.handle(new LogEvent(path: "/api/user", date: 11))
        subject.handle(new LogEvent(path: "/api/user", date: 132))

        then:
        os.flush()
        os.toString() == "/api -> hit 2 times${System.lineSeparator()}"
        os.reset()

        when:
        subject.handle(new LogEvent(path: "/report", date: 132))
        subject.handle(new LogEvent(path: "/report", date: 137))
        subject.handle(new LogEvent(path: "/api/user", date: 141))
        subject.handle(new LogEvent(path: "/api/user", date: 262))

        then:
        os.flush()
        os.toString() == "/report -> hit 2 times${System.lineSeparator()}"
    }

    def "when the 10th second hasn't yet fallen outside reporting window (and won't because it doesn't have a log event), continues to wait"() {
        when: "the 10th second hasn't fallen outside reporting window yet"
        subject.handle(new LogEvent(path: "/api/user", date: 1))
        subject.handle(new LogEvent(path: "/api/user", date: 2))
        subject.handle(new LogEvent(path: "/report", date: 5))
        subject.handle(new LogEvent(path: "/api/user", date: 131))

        then: "waits to print frequency report"
        os.flush()
        os.toString() == ""
        os.reset()

        when: "the 131st second is outside reporting window"
        subject.handle(new LogEvent(path: "/api/user", date: 252))

        then: "prints report"
        os.flush()
        os.toString() == "/api -> hit 2 times${System.lineSeparator()}"
    }

    def "keep out of sequence log events, when still inside reporting window"() {
        when:
        subject.handle(new LogEvent(path: "/api/user", date: 1))
        subject.handle(new LogEvent(path: "/api/user", date: 11))
        subject.handle(new LogEvent(path: "/api/user", date: 5))
        subject.handle(new LogEvent(path: "/api/user", date: 7))
        subject.handle(new LogEvent(path: "/api/user", date: 132))

        then:
        os.flush()
        os.toString() == "/api -> hit 4 times${System.lineSeparator()}"
    }

    def "prints an alert when number of log events in alert window exceeds threshold"() {
        given:
        subject.aggregatingWindow = Mock(AggregatingWindow)

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 17))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 3L
        os.flush()
        os.toString() == "High traffic generated an alert - hits = 3, triggered at 17${System.lineSeparator()}"
    }

    def "alert doesn't trigger multiple times during warning period"() {
        given:
        subject.aggregatingWindow = Mock(AggregatingWindow)

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 17))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 3L
        os.flush()
        os.toString() == "High traffic generated an alert - hits = 3, triggered at 17${System.lineSeparator()}"
        os.reset()

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 17))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 3L
        os.flush()
        os.toString() == ""
    }

    def "prints a message when alert is recovered from"() {
        given:
        subject.aggregatingWindow = Mock(AggregatingWindow)

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 17))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 3L
        os.flush()
        os.toString() == "High traffic generated an alert - hits = 3, triggered at 17${System.lineSeparator()}"
        os.reset()

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 200))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 2L
        os.flush()
        os.toString().contains("High traffic alert recovered at 200")
    }

    def "alert recovery doesn't print nultiple times"() {
        given:
        subject.aggregatingWindow = Mock(AggregatingWindow)

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 17))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 3L
        os.flush()
        os.toString() == "High traffic generated an alert - hits = 3, triggered at 17${System.lineSeparator()}"
        os.reset()

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 200))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 2L
        os.flush()
        os.toString().contains("High traffic alert recovered at 200")
        os.reset()

        when:
        subject.handle(new LogEvent(path: "/api/user", date: 200))

        then:
        1 * subject.aggregatingWindow.add(_)
        1 * subject.aggregatingWindow.averageHitsPerSecond() >> 2L
        os.flush()
        os.toString() == ""
    }
}
