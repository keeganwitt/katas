package com.github.keeganwitt.katas.httpmonitor.parser

import com.github.keeganwitt.katas.httpmonitor.model.LogEvent
import org.apache.commons.csv.CSVParser
import org.mockito.Mockito
import spock.lang.Specification

class CsvHttpLogParserSpec extends Specification {
    def "parses lines into LogEvents, exposed by iterator()"() {
        given:
        def input = """\
            "remotehost","rfc931","authuser","date","request","status","bytes"
            "10.0.0.2","-","user 1",1549573860,"GET /api/user HTTP/1.0",200,1024
            "10.0.0.4","-","user 2",1549573870,"GET /api/help HTTP/1.0",200,2048""".stripIndent()
        def subject = new CsvHttpLogParser(new ByteArrayInputStream(input.bytes))

        when:
        def iterator = subject.iterator()

        then:
        iterator.next() == new LogEvent(remoteHost: "10.0.0.2", authUser: "user 1", date: 1549573860, httpMethod: "GET", path: "/api/user", status: 200, bytes: 1024)

        then:
        iterator.next() == new LogEvent(remoteHost: "10.0.0.4", authUser: "user 2", date: 1549573870, httpMethod: "GET", path: "/api/help", status: 200, bytes: 2048)

        then:
        !iterator.hasNext()
    }

    def "close() delegates to the CsvParser"() {
        given:
        def subject = new CsvHttpLogParser(new ByteArrayInputStream("".bytes))
        subject.csvParser = Mockito.mock(CSVParser)

        when:
        subject.close()

        then:
        Mockito.verify(subject.csvParser, Mockito.only()).close()
    }

    def "parses lines into LogEvents, exposed by iterator(), without a header"() {
        given:
        def input = """\
            "10.0.0.2","-","user 1",1549573860,"GET /api/user HTTP/1.0",200,1024
            "10.0.0.4","-","user 2",1549573870,"GET /api/help HTTP/1.0",200,2048""".stripIndent()
        def subject = new CsvHttpLogParser(new ByteArrayInputStream(input.bytes))

        when:
        def iterator = subject.iterator()

        then:
        iterator.next() == new LogEvent(remoteHost: "10.0.0.2", authUser: "user 1", date: 1549573860, httpMethod: "GET", path: "/api/user", status: 200, bytes: 1024)

        then:
        iterator.next() == new LogEvent(remoteHost: "10.0.0.4", authUser: "user 2", date: 1549573870, httpMethod: "GET", path: "/api/help", status: 200, bytes: 2048)

        then:
        !iterator.hasNext()
    }

    def "returns null when line is improperly formatted"() {
        given:
        def input = """\
            "10.0.0.2","-","user 1",${Long.MAX_VALUE},"GET /api/user HTTP/1.0",200,1024
            "10.0.0.2","-","user 1",1549573860,"GET /api/user HTTP/1.0 extraStuff",200,1024
            "10.0.0.2","-","user 1",1549573860,"GET /api/user HTTP/1.0",${Integer.MAX_VALUE},1024
            "10.0.0.2","-","user 1",1549573860,"GET /api/user HTTP/1.0",200,${BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1))}
            "10.0.0.2","-","user 1","GET /api/user HTTP/1.0",200,1024""".stripIndent()

        when:
        def subject = new CsvHttpLogParser(new ByteArrayInputStream(input.getBytes()))
        Iterator<LogEvent> iterator = subject.iterator()

        then:
        iterator.next() == null
        iterator.next() == null
        iterator.next() == null
        iterator.next() == null
        iterator.next() == null
        !iterator.hasNext()
    }
}
