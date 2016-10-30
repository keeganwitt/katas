package com.github.keeganwitt.katas.primenumgen;

import org.apache.commons.cli.*;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Main {
    private static final String APP_NAME = "primeNumberGenerator";
    // TODO: is this the optimum max segment size?
    static final int MAX_SEGMENT_SIZE = 500_000_000;
    private static PrimeNumberGenerator primeGenerator = new PrimeGenerator(MAX_SEGMENT_SIZE);

    public static void main(String[] args) {
        Options options = createOptions();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            invalidInvocation(options, formatter);
        }

        if (cmd.hasOption('h')) {
            formatter.printHelp(APP_NAME, options);
            System.exit(0);
        }

        if ((!cmd.hasOption('c') && !cmd.hasOption('g'))
                || (cmd.hasOption('c') && cmd.hasOption('g'))
                || (cmd.hasOption('c') && !cmd.hasOption('n'))
                || (cmd.hasOption('g') && (!cmd.hasOption('b') || !cmd.hasOption('e')))) {
            invalidInvocation(options, formatter);
        }

        if (cmd.hasOption('c')) {
            try {
                int n = Integer.parseInt(cmd.getOptionValue('n'));
                primeGenerator = new PrimeGenerator(min(abs(n) + 1, MAX_SEGMENT_SIZE));
                System.out.println(primeGenerator.isPrime(n));
            } catch (NumberFormatException e) {
                invalidInvocation(options, formatter);
            }
        }
        if (cmd.hasOption('g')) {
            try {
                int beginning = Integer.parseInt(cmd.getOptionValue('b'));
                int end = Integer.parseInt(cmd.getOptionValue('e'));
                primeGenerator = new PrimeGenerator(min(abs(beginning - end) + 1, MAX_SEGMENT_SIZE));
                System.out.println(primeGenerator.generate(beginning, end));
            } catch (NumberFormatException e) {
                invalidInvocation(options, formatter);
            }
        }
    }

    private static void invalidInvocation(Options options, HelpFormatter formatter) {
        System.err.println("Invalid invocation");
        formatter.printHelp(APP_NAME, options);
        System.exit(1);
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Shows this message.  Must specify either -c or -g.");
        options.addOption("c", "check", false, "Check if the integer is a prime (can't be used with -g).");
        options.addOption("g", "generate", false, "Generate primes numbers between specified beginning and end numbers (can't be used with -c).");
        options.addOption("n", "number", true, "Integer less than Integer.MAX_VALUE to check if prime (required by -c, no effect with -g).");
        options.addOption("b", "beginning", true, "Integer less than Integer.MAX_VALUE to start primes number generation at (has no effect when used with -c).");
        options.addOption("e", "end", true, "Integer less than Integer.MAX_VALUE to end prime number generation at (has no effect when used with -c).");

        return options;
    }
}
