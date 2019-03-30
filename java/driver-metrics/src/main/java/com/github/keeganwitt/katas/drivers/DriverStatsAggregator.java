package com.github.keeganwitt.katas.drivers;

import com.github.keeganwitt.katas.drivers.parser.Command;
import com.github.keeganwitt.katas.drivers.parser.DriverCommand;
import com.github.keeganwitt.katas.drivers.parser.TripCommand;
import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.round;

public class DriverStatsAggregator {
    private static final Logger LOG = LogManager.getLogger();
    private static final int MIN_ALLOWED_SPEED = 5;
    private static final int MAX_ALLOWED_SPEED = 100;

    private Map<String, DriverStats> driverStats;

    public DriverStatsAggregator() {
        driverStats = new HashMap<>();
    }

    public void add(Command command) {
        if (command instanceof DriverCommand) {
            add((DriverCommand) command);
        } else {
            add((TripCommand) command);
        }
    }

    public void add(DriverCommand driver) {
        driverStats.put(driver.getDriverName(), new DriverStats());
    }

    public void add(TripCommand trip) {
        if (!driverStats.containsKey(trip.getDriverName())) {
            throw new IllegalArgumentException("Driver '" + trip.getDriverName() + "' must be defined before trips can be added.");
        }
        var averageSpeed = trip.calculateAverageSpeed();
        if (averageSpeed < MIN_ALLOWED_SPEED || averageSpeed > MAX_ALLOWED_SPEED) {
            LOG.warn("Trip {} had an average speed of {}, and so was ignored.", trip, trip.calculateAverageSpeed());
        } else {
            driverStats.get(trip.getDriverName()).addTrip(trip.getMilesDriven(), trip.getMinutesDriven());
        }
    }

    public Stream<Map.Entry<String, DriverStats>> sorted() {
        return driverStats.entrySet().stream().sorted(Map.Entry.comparingByValue());
    }

    public static String formatResult(Map.Entry<String, DriverStats> it) {
        var milesDriven = round(it.getValue().getMilesDriven());
        var outputString = it.getKey() + ": " + milesDriven + " miles";
        if (milesDriven > 0) {
            outputString += " @ " + round(it.getValue().calculateAverageSpeed()) + " mph";
        }
        return outputString;
    }
}
