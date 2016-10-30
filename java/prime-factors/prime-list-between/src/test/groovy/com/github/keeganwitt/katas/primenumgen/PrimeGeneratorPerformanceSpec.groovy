package com.github.keeganwitt.katas.primenumgen

import spock.lang.Ignore
import spock.lang.Requires
import spock.lang.Specification

import static com.github.keeganwitt.katas.primenumgen.Main.MAX_SEGMENT_SIZE
import static java.lang.Math.min

class PrimeGeneratorPerformanceSpec extends Specification {
    private static final String LONG_TESTS_PROPERTY = "longTests"

    // this ran around 5 seconds on my machine
    def "generate(0, 100_000_000) finds all 5_761_455 primes in under 10 secs"() {
        given:
        int upper = 100_000_000
        def subject = new PrimeGenerator(min(upper, MAX_SEGMENT_SIZE))

        when:
        def start = System.currentTimeMillis()
        def results = subject.generate(0, upper).size()
        def stop = System.currentTimeMillis()

        then:
        results == 5_761_455
        stop - start <= timeToMillis(0, 0, 10)
    }

    // this ran around 30 seconds on my machine
    @Requires({System.getProperty(LONG_TESTS_PROPERTY)?.toBoolean()})
    def "generate(0, 500_000_000) finds all 26_355_867 primes in under 40 sec"() {
        given:
        int upper = 500_000_000
        def subject = new PrimeGenerator(min(upper, MAX_SEGMENT_SIZE))

        when:
        def start = System.currentTimeMillis()
        def results = subject.generate(0, upper).size()
        def stop = System.currentTimeMillis()

        then:
        results == 26_355_867
        stop - start <= timeToMillis(0, 0, 40)
    }

    // this ran around 1 minute on my machine with 4gb of heap
    @Ignore
    def "generate(0, 1_000_000_000) finds all 50_847_534 primes in under 80 seconds"() {
        given:
        int upper = 1_000_000_000
        def subject = new PrimeGenerator(min(upper, MAX_SEGMENT_SIZE))

        when:
        def start = System.currentTimeMillis()
        def results = subject.generate(0, upper).size()
        def stop = System.currentTimeMillis()

        then:
        results == 50_847_534
        stop - start <= timeToMillis(0, 1, 20)
    }

    // this one gobbled up too much heap to run
    @Ignore
    def "generate(0, Integer.MAX_VALUE - 1) finds all 455_052_511 primes"() {
        given:
        int upper = Integer.MAX_VALUE - 1
        def subject = new PrimeGenerator(min(upper, MAX_SEGMENT_SIZE))

        when:
        def results = subject.generate(0, upper).size()

        then:
        results == 455_052_511
    }

    private static long timeToMillis(int hours, int minutes, int seconds) {
        return hours * 60 * 60 * 1000 +
               minutes * 60 * 1000 +
               seconds * 1000
    }
}
