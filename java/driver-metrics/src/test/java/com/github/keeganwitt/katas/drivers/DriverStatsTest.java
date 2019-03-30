package com.github.keeganwitt.katas.drivers;

import lombok.var;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriverStatsTest {
    @Test
    public void addTripIncrementsMinutesDrivenAndMilesDriven() {
        var d = new DriverStats(2, 2);
        d.addTrip( 3, 2);
        assertEquals(4, d.getMinutesDriven());
        assertEquals(5.0, d.getMilesDriven(), 0.01);
    }

    @Test
    public void calculateAverageSpeedDividesMilesByHours() {
        var d = new DriverStats(100, 10 * 60);
        assertEquals(10, d.calculateAverageSpeed(), 0.01);
    }

    @Test
    public void calculateAverageSpeedHandlesLessThan1Hour() {
        var d = new DriverStats(10, 30);
        assertEquals(20, d.calculateAverageSpeed(), 0.01);
    }

    @Test
    public void compareToComparesByMilesDrivenWithHighestNumberFirst() {
        var d1 = new DriverStats(1, 1);
        var d2 = new DriverStats(2, 1);
        assertEquals(1, d1.compareTo(d2));
        assertEquals(-1, d2.compareTo(d1));
    }
}
