package com.github.keeganwitt.katas.primenumgen;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;

public class MainTests {
    private static final String INVALID_INVOCATION = "Invalid invocation" + lineSeparator();
    private static final String USAGE =
            "usage: primeNumberGenerator" + lineSeparator() +
            " -b,--beginning <arg>   Integer less than Integer.MAX_VALUE to start" + lineSeparator() +
            "                        primes number generation at (has no effect when" + lineSeparator() +
            "                        used with -c)." + lineSeparator() +
            " -c,--check             Check if the integer is a prime (can't be used" + lineSeparator() +
            "                        with -g)." + lineSeparator() +
            " -e,--end <arg>         Integer less than Integer.MAX_VALUE to end prime" + lineSeparator() +
            "                        number generation at (has no effect when used with" + lineSeparator() +
            "                        -c)." + lineSeparator() +
            " -g,--generate          Generate primes numbers between specified" + lineSeparator() +
            "                        beginning and end numbers (can't be used with -c)." + lineSeparator() +
            " -h,--help              Shows this message.  Must specify either -c or -g." + lineSeparator() +
            " -n,--number <arg>      Integer less than Integer.MAX_VALUE to check if" + lineSeparator() +
            "                        prime (required by -c, no effect with -g)." + lineSeparator();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void helpOptionPrintsHelp() {
        exit.expectSystemExit();
        exit.checkAssertionAfterwards(() -> assertEquals(USAGE, systemOutRule.getLog()));
        Main.main(new String[]{"-h"});
    }

    @Test
    public void callingWithInvalidOptionPrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-t"});
    }

    @Test
    public void callingWithoutCheckOrGeneratePrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[0]);
    }

    @Test
    public void callingWithBothCheckAndGeneratePrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-c", "-g"});
    }

    @Test
    public void callingCheckWithoutNumberToCheckPrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-c"});
    }

    @Test
    public void callingCheckWithNumberToCheckPrintsResult() {
        Main.main(new String[]{"-c", "-n", "2"});
        assertEquals("true" + lineSeparator(), systemOutRule.getLog());
    }

    @Test
    public void callingCheckWithNumberToCheckThatsNotANumberErrorsOut() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-c", "-n", "nan"});
    }

    @Test
    public void callingGenerateWithoutBeginningNumberPrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-g", "-e", "1"});
    }

    @Test
    public void callingGenerateWithoutEndingNumberPrintsInvalidInvocation() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-g", "-b", "1"});
    }

    @Test
    public void callingGenerateWithBeginningAndEndingNumbersPrintsPrimes() {
        Main.main(new String[]{"-g", "-b", "1", "-e", "10"});
        assertEquals("[2, 3, 5, 7]" + lineSeparator(), systemOutRule.getLog());
    }

    @Test
    public void callingGenerateWithBeginningNumberThatsNotANumberErrorsOut() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-g", "-b", "nan"});
    }

    @Test
    public void callingGenerateWithEndingNumberThatsNotANumberErrorsOut() {
        exit.expectSystemExitWithStatus(1);
        exit.checkAssertionAfterwards(() -> {
            assertEquals(INVALID_INVOCATION, systemErrRule.getLog());
            assertEquals(USAGE, systemOutRule.getLog());
        });
        Main.main(new String[]{"-g", "-e", "nan"});
    }
}
