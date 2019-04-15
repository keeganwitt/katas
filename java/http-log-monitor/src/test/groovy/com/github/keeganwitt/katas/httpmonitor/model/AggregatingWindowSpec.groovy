package com.github.keeganwitt.katas.httpmonitor.model

import spock.lang.Specification

class AggregatingWindowSpec extends Specification {
    AggregatingWindow subject

    def setup() {
        subject = new AggregatingWindow()
    }

    def "averageHitsPerSecond() calculates the average hits per second in 2 minute window"() {
        when:
        239.times { subject.add(new LogEvent(path: "/report", date: 1)) }

        then:
        subject.averageHitsPerSecond() == 2
    }

    def "adding a log event outside the 2 minute window, shifts the two minute window"() {
        when:
        120.times { subject.add(new LogEvent(path: "/report", date: 1)) }
        def metricsFromPreviousTimeWindows = subject.add(new LogEvent(path: "/report", date: 122))
        239.times { subject.add(new LogEvent(path: "/report", date: 122)) }

        then:
        metricsFromPreviousTimeWindows == [1: ["/report": 120]]
        subject.averageHitsPerSecond() == 2
    }

    def "when shifting window, keeps results that are still inside the two minute window"() {
        when:
        60.times { subject.add(new LogEvent(path: "/report", date: 1)) }
        120.times { subject.add(new LogEvent(path: "/report", date: 60)) }
        def metricsFromPreviousTimeWindows = subject.add(new LogEvent(path: "/report", date: 122))
        119.times { subject.add(new LogEvent(path: "/report", date: 122)) }

        then:
        metricsFromPreviousTimeWindows == [1: ["/report": 60]]
        subject.averageHitsPerSecond() == 2
    }

    def "after shifting window, adding a result before the beginning of window isn't added to window"() {
        when:
        60.times { subject.add(new LogEvent(path: "/report", date: 1)) }
        240.times { subject.add(new LogEvent(path: "/report", date: 122)) }
        60.times { subject.add(new LogEvent(path: "/report", date: 1)) }

        then:
        subject.averageHitsPerSecond() == 2
    }
}
