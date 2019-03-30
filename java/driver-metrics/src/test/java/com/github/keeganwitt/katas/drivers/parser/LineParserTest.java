package com.github.keeganwitt.katas.drivers.parser;

import lombok.var;
import org.junit.Test;

import static com.github.keeganwitt.katas.drivers.parser.LineParser.parseLine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LineParserTest {
    @Test(expected = IllegalArgumentException.class)
    public void parseLineThrowsExceptionWhenCommandNotRecognized() {
        parseLine("BadCommand");
    }

    @Test
    public void parsesDriverCommand() {
        var driver = parseLine("Driver Frank");
        assertTrue(driver instanceof DriverCommand);
        assertEquals("Frank", ((DriverCommand) driver).getDriverName());
    }

    @Test
    public void parsesDriverCommandWithNameContainingSpaces() {
        var driver = parseLine("Driver Mary Beth");
        assertEquals("Mary Beth", ((DriverCommand) driver).getDriverName());
    }

    @Test
    public void parsesTripCommand() {
        var trip = parseLine("Trip Xavier 07:15 07:45 17.0");
        assertTrue(trip instanceof TripCommand);
        assertEquals("Xavier", ((TripCommand) trip).getDriverName());
        assertEquals(17.0, ((TripCommand) trip).getMilesDriven(), 0.01);
        assertEquals(30, ((TripCommand) trip).getMinutesDriven());
    }

    @Test
    public void parsesTripCommandWithTimeOverOneHour() {
        var trip = parseLine("Trip Xavier 07:15 08:45 17.0");
        assertTrue(trip instanceof TripCommand);
        assertEquals("Xavier", ((TripCommand) trip).getDriverName());
        assertEquals(17.0, ((TripCommand) trip).getMilesDriven(), 0.01);
        assertEquals(90, ((TripCommand) trip).getMinutesDriven());
    }

    @Test
    public void parsesDriverWithNameContainingSpace() {
        var trip = parseLine("Trip Mary Jane 07:10 07:45 21.0");
        assertTrue(trip instanceof TripCommand);
        assertEquals("Mary Jane", ((TripCommand) trip).getDriverName());
        assertEquals(21.0, ((TripCommand) trip).getMilesDriven(), 0.01);
        assertEquals(35, ((TripCommand) trip).getMinutesDriven());
    }
}
