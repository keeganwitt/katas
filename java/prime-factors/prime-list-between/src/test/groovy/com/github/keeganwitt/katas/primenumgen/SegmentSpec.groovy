package com.github.keeganwitt.katas.primenumgen

import spock.lang.Specification

class SegmentSpec extends Specification {
    void "getting and setting on array offsets from segment start"() {
        given:
        def segmentAccessor = new Segment(10).from(10, 20)

        when:
        segmentAccessor.set(15, false)

        then:
        !segmentAccessor.get(15)
    }

    void "reset() sets all to true"() {
        given:
        def segment = new Segment(12)
        def segmentAccessor = segment.from(15, 27)
        (15..26).each{ i -> segmentAccessor.set(i, false) }

        when:
        segment.reset()

        then:
        (15..26).each{ i -> assert segmentAccessor.get(i) }
    }

    void "throws IllegalArgumentException when trying to get a number less than segmentStart"() {
        when:
        new Segment(4).from(1, 4).get(0)

        then:
        thrown IllegalArgumentException
    }

    void "throws IllegalArgumentException when trying to get a number greater than segmentEnd"() {
        when:
        new Segment(4).from(1, 4).get(5)

        then:
        thrown IllegalArgumentException
    }

    void "throws IllegalArgumentException when trying to set a number less than segmentStart"() {
        when:
        new Segment(4).from(1, 4).set(0, false)

        then:
        thrown IllegalArgumentException
    }

    void "throws IllegalArgumentException when trying to set a number greater than segmentEnd"() {
        when:
        new Segment(4).from(1, 4).set(5, false)

        then:
        thrown IllegalArgumentException
    }
}
