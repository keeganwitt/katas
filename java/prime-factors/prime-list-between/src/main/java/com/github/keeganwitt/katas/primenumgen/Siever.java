package com.github.keeganwitt.katas.primenumgen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.floor;

public class Siever {
    private static final Logger LOG = LogManager.getLogger(Siever.class);

    // TODO: skip even numbers that aren't 2?
    // TODO: use Sieve of Atkin instead of Sieve of Eratosthenes?
    public void sieveSegment(Segment.SegmentAccessor segmentAccessor, List<Integer> primesFoundSoFar) {
        for (int prime : primesFoundSoFar) {
            int multiple = (int) floor(segmentAccessor.getSegmentStart() / prime);
            // > 0 checks to protect against integer overflows
            while (prime * multiple < segmentAccessor.getSegmentStart() && prime * multiple > 0) {
                multiple++;
            }
            while (prime*multiple <= segmentAccessor.getSegmentEnd() && prime * multiple > 0) {
                segmentAccessor.set(prime * multiple, false);
                multiple++;
            }
        }

        for (int i = segmentAccessor.getSegmentStart(); i <= segmentAccessor.getSegmentEnd() && i > 0; i++) {
            if (segmentAccessor.get(i)) {
                for (int j = 2; i * j <= segmentAccessor.getSegmentEnd() && i * j > 0; j++) {
                    segmentAccessor.set(i * j, false);
                }
            }
        }

        // I despise mutable parameters, but this avoids GC overhead
        int before = primesFoundSoFar.size();
        IntStream.range(segmentAccessor.getSegmentStart(), segmentAccessor.getSegmentEnd() + 1)
                .filter(segmentAccessor::get).forEach(primesFoundSoFar::add);
        LOG.debug("Sieving between {} and {} -> {}", segmentAccessor.getSegmentStart(), segmentAccessor.getSegmentEnd(), primesFoundSoFar.size() - before);
    }
}
