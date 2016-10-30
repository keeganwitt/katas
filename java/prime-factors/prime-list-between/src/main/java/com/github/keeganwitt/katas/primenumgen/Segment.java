package com.github.keeganwitt.katas.primenumgen;

import java.util.stream.IntStream;

public class Segment {
    // TODO: only allocate memory for odd numbers?

    // BitSet is smaller than boolean[] (on most VMs), but slower
    // array is declared outside accessor so it can be reused without garbage collection
    private boolean[] isPrime;

    public Segment(int segmentSize) {
        isPrime = new boolean[segmentSize];
    }

    public SegmentAccessor from(int segmentStart, int segmentEnd) {
        reset();
        return new SegmentAccessor(segmentStart, segmentEnd);
    }

    public void reset() {
        IntStream.range(0, isPrime.length).parallel().forEach(i -> isPrime[i] = true);
    }

    public class SegmentAccessor {
        private int segmentStart;
        private int segmentEnd;

        public SegmentAccessor(int segmentStart, int segmentEnd) {
            this.segmentStart = segmentStart;
            this.segmentEnd = segmentEnd;
        }

        public boolean get(int n) {
            if (n < segmentStart || n > segmentEnd) {
                throw new IllegalArgumentException("Must only request numbers within segment." + n + " is outside segment " + segmentStart + ", " + segmentEnd + ".");
            }
            return isPrime[n - segmentStart];
        }

        public void set(int n, boolean prime) {
            if (n < segmentStart || n > segmentEnd) {
                throw new IllegalArgumentException("Must only request numbers within segment. " + n + " is outside segment " + segmentStart + ", " + segmentEnd + ".");
            }
            isPrime[n - segmentStart] = prime;
        }

        public int getSegmentStart() {
            return segmentStart;
        }

        public int getSegmentEnd() {
            return segmentEnd;
        }
    }
}
