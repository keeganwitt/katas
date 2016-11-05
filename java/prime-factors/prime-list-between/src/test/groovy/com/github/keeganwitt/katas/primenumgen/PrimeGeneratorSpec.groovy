package com.github.keeganwitt.katas.primenumgen

import spock.lang.Specification

class PrimeGeneratorSpec extends Specification {
    static final def PRIMES_UNDER_1000 = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
                                          79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
                                          167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251,
                                          257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
                                          353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443,
                                          449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557,
                                          563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647,
                                          653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757,
                                          761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863,
                                          877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983,
                                          991, 997]

    PrimeGenerator subject

    def setup() {
        subject = new PrimeGenerator(100)
    }

    def "generate() returns empty list if range ends before first prime"() {
        expect:
        subject.generate(startingValue, endingValue) == []

        where:
        startingValue || endingValue
        -1            || -1
        -1            || 0
        0             || -1
        0             || 0
        0             || 1
        1             || 0
        1             || 1
    }

    def "generate() doesn't allow invocation with Integer.MAX_VALUE"() {
        when:
        subject.generate(startingValue, endingValue)

        then:
        IllegalArgumentException e = thrown()
        e.message == "startingValue and endingValue must be less than Integer.MAX_VALUE"

        where:
        startingValue      || endingValue
        Integer.MAX_VALUE  || 5
        5                  || Integer.MAX_VALUE
        Integer.MAX_VALUE  || Integer.MAX_VALUE
    }

    def "generate() works with same number for both start and end"() {
        expect:
        subject.generate(11, 11) == [11]
    }

    def "generate(7900, 7920) returns 7901, 7907, and 7919"() {
        expect:
        subject.generate(7900, 7920) == [7901, 7907, 7919]
        subject.generate(7920, 7900) == [7901, 7907, 7919]
    }

    def "generate(0, 1000) finds all primes under 1000"() {
        expect:
        subject.generate(0, 1000) == PRIMES_UNDER_1000
    }

    def "isPrime() returns true when generate says it is, and false when when it it doesn't"() {
        expect:
        (0..1000).each {
            assert subject.isPrime(it) == PRIMES_UNDER_1000.contains(it)
        }
    }

    def "segments range of 3 to #end into #times segments"() {
        given:
        subject = new PrimeGenerator(5)
        subject.siever = Mock(Siever)

        when:
        subject.generate(start, end)

        then:
        times * subject.siever.sieveSegment(_ as Segment.SegmentAccessor, _ as List<Integer>)

        where:
        start || end || times
        3     || 3   || 1
        3     || 6   || 1
        3     || 7   || 2
    }
}
