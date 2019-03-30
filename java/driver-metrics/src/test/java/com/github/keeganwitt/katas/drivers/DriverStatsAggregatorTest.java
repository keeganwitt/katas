package com.github.keeganwitt.katas.drivers;

import com.github.keeganwitt.katas.drivers.parser.DriverCommand;
import com.github.keeganwitt.katas.drivers.parser.TripCommand;
import lombok.var;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class DriverStatsAggregatorTest {
    private DriverStatsAggregator driverStatsAggregator;

    @Before
    public void setup() {
        driverStatsAggregator = new DriverStatsAggregator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsExceptionWhenAddingATripBeforeDriverIsAdded() {
        var trip = new TripCommand("Frank", 5.5, 30);
        driverStatsAggregator.add(trip);
    }

    @Test
    public void addUpdatesMilesAndMinutesDrivenIfDriverIsDefinedFirst() {
        var driverName = "Richard";
        var driver = new DriverCommand(driverName);
        driverStatsAggregator.add(driver);
        var milesDriven = 5.5;
        var minutesDriven = 30;
        var trip = new TripCommand(driverName, milesDriven, minutesDriven);
        driverStatsAggregator.add(trip);
        var list = driverStatsAggregator.sorted().collect(toList());
        assertEquals(1, list.size());
        assertEquals(milesDriven, list.get(0).getValue().getMilesDriven(), 0.01);
        assertEquals(minutesDriven, list.get(0).getValue().getMinutesDriven());
    }

    @Test
    public void sortedSortsValuesByMilesDriven() {
        driverStatsAggregator.add(new DriverCommand("Richard"));
        driverStatsAggregator.add(new TripCommand("Richard", 5.5, 30));
        driverStatsAggregator.add(new DriverCommand("Natalya"));
        driverStatsAggregator.add(new TripCommand("Natalya", 6.5, 30));

        var list = driverStatsAggregator.sorted().collect(toList());
        assertEquals(2, list.size());
        assertEquals("Natalya", list.get(0).getKey());
        assertEquals("Richard", list.get(1).getKey());
    }

    @Test
    public void ignoresTripsWithAverageSpeedOfLessThan5MilesPerHour() {
        driverStatsAggregator.add(new DriverCommand("Siddhartha"));
        driverStatsAggregator.add(new TripCommand("Siddhartha", 4.99, 60));
        var list = driverStatsAggregator.sorted().collect(toList());
        assertEquals(1, list.size());
        assertEquals(0, list.get(0).getValue().getMinutesDriven());
        assertEquals(0, list.get(0).getValue().getMilesDriven(), 0.01);
    }

    @Test
    public void ignoresTripsWithAverageSpeedOfMoreThan100MilesPerHour() {
        driverStatsAggregator.add(new DriverCommand("Siddhartha"));
        driverStatsAggregator.add(new TripCommand("Siddhartha", 100.01, 60));
        var list = driverStatsAggregator.sorted().collect(toList());
        assertEquals(1, list.size());
        assertEquals(0, list.get(0).getValue().getMinutesDriven());
        assertEquals(0, list.get(0).getValue().getMilesDriven(), 0.01);
    }

    @Test
    public void formatResultRoundsMilesDrivenAndAverageSpeed() {
        var entry = new AbstractMap.SimpleEntry<>("Jane", new DriverStats(31.5, 40));
        assertEquals("Jane: 32 miles @ 47 mph", DriverStatsAggregator.formatResult(entry));
    }

    @Test
    public void formatResultExcludesSpeedWhenNoMilesDriven() {
        var entry = new AbstractMap.SimpleEntry<>("Jane", new DriverStats(0, 40));
        assertEquals("Jane: 0 miles", DriverStatsAggregator.formatResult(entry));
    }
}
