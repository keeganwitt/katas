# prime-list-between

This is solution uses the Sieve of Eratsothenes, looking at 1 segment of possible primes at a time.

## Building

Java 8 or higher is required.

    gradlew shadowJar

### Coverage information

    gradlew test jacocoTestReport

Note that my usage of system-rules seems to interfere with Jacoco coverage metrics for Main (I think because it
registers a `SecurityManager` that prevents the actual execution of `System.exit()`).
Also, my checks protecting against integer overflows aren't seen as covered because the tests covering those cases
don't run by default.

### Include long-running tests

    gradlew -DlongTests=true test

## Running

### Check if a number is prime

    java -jar build/libs/prime-list-between-all.jar -c -n <number-to-check>

### Generate all prime numbers between 2 numbers

The ending numbers doesn't have to be larger than the beginning number (i.e. it can be the same number or smaller).

    java -jar build/libs/prime-list-between-all.jar -g -b <beginning-number> -e <ending-number>

### Display usage

    java -jar build/libs/prime-list-between-all.jar -h

## Design notes

* The interface and wording of description seems to imply a deterministic rather than heuristic solution, so approaches
like elliptic curve primality testing are out.
* The interface requires a complete list of primes be returned, this prevents infinitely incremental sieve approaches,
and is also the constraining factor memory-wise.  In my test generating primes from 0 to 1B, over 99% of the 4GB of
memory was consumed by the final result list.

## Description

Your task is to use test driven development to implement a prime number generator that
returns an ordered list of all prime numbers in a given range (inclusive of the endpoints).
You must implement the interface specified below. You may also create any other
methods, interfaces and/or classes that you deem necessary to complete the project.
You should also develop a small main program to drive your generator and to allow the
user to specify the prime number range via the command line. To successfully
complete the exercise, all unit tests must pass as well as provide 100% code coverage.

### Notes

* The code should handle inverse ranges such that 1-10 and 10-1 are equivalent.
* Ensure that you run a test against the range 7900 and 7920 (valid primes are 7901, 7907, 7919).

### Interface

    Interface PrimeNumberGenerator {
        List<Integer> generate(int startingValue, int endingValue);
        boolean isPrime(int
    }
