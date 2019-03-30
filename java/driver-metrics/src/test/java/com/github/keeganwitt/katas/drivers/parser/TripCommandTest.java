package com.github.keeganwitt.katas.drivers.parser;

import lombok.var;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TripCommandTest {
    @Test
    public void calculatesMilesPerHour() {
        var trip = new TripCommand("Me", 50, 30);
        assertEquals(100, trip.calculateAverageSpeed(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowNegativeMilesDriven() {
        new TripCommand("Me", -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowNegativeMinutesDriven() {
        new TripCommand("Me", 1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowZeroMinutesDriven() {
        new TripCommand("Me", 1, 0);
    }
}
