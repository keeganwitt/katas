package com.github.keeganwitt.katas.httpmonitor.model

import com.github.keeganwitt.katas.httpmonitor.model.LogEvent
import spock.lang.Specification

class LogEventSpec extends Specification {
    def "getSection returns first part of path"() {
        given:
        def subject = new LogEvent(path: path)

        expect:
        subject.section == section

        where:
        path        | section
        "/api/user" | "/api"
        "/api/"     | "/api"
        "/api"      | "/api"
    }
}
