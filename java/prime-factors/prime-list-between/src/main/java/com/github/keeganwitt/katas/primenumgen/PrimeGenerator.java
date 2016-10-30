package com.github.keeganwitt.katas.primenumgen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;

public class PrimeGenerator implements PrimeNumberGenerator {
    private static final Logger LOG = LogManager.getLogger(PrimeGenerator.class);
    private Siever siever;
    private int segmentSize;
    private Segment segment;

    public PrimeGenerator(int segmentSize) {
        this.segmentSize = segmentSize;
        segment = new Segment(segmentSize);
        siever = new Siever();
    }

    public List<Integer> generate(int startingValue, int endingValue) {
        // find ordered start & end, since can be passed in either order
        int start = min(startingValue, endingValue);
        int end = max(startingValue, endingValue);

        if (start < 2 && end < 2 ) {
            return emptyList();
        }

        if (start == Integer.MAX_VALUE || end == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("startingValue and endingValue must be less than Integer.MAX_VALUE");
        }

        long startTime = currentTimeMillis();
        // TODO: increase primes size by HotSpot's buffering amount ((n*2)/3 I think) so that ArrayList never gets resized?
        // although ArrayList.contains() is slower than TreeSet.contains(), performance was dramatically better with ArrayList
        List<Integer> primes = new ArrayList<>((int) (end / log(end)));
        int segmentStart = 2;
        int segmentEnd = min(segmentStart + segmentSize - 1, end);
        Segment.SegmentAccessor segmentAccessor = segment.from(segmentStart, segmentEnd);
        siever.sieveSegment(segmentAccessor, primes);
        if (segmentEnd < end) {
            segmentStart = segmentSize + 1;
            segmentEnd = min(segmentStart + segmentSize - 1, end);
            while (segmentStart <= end && segmentStart < Integer.MAX_VALUE - 1) {
                segmentAccessor = segment.from(segmentStart, segmentEnd);
                siever.sieveSegment(segmentAccessor, primes);
                segmentStart = segmentEnd + 1;
                segmentEnd = min(segmentStart + segmentSize - 1, end);
            }
        }

        List<Integer> result = primes.stream().filter(number -> number >= start).collect(Collectors.toList());
        LOG.info("Found {} primes from {} to {} in {}", result.size(), start, end, formatElapsedTime(currentTimeMillis() - startTime));
        return result;
    }

    // TODO: use AKS primality check?
    public boolean isPrime(int value) {
        return generate(value, value).contains(value);
    }

    public static String formatElapsedTime(long millis) {
        return String.format("%d hour, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}
