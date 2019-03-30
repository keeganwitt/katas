package com.github.keeganwitt.katas.drivers.parser;

import lombok.var;

public final class LineParser {
    private LineParser() {
    }

    public static Command parseLine(String line) {
        if (line.startsWith("Driver")) {
            var driverName = line.substring(line.indexOf(" ") + 1);
            return new DriverCommand(driverName);
        } else if (line.startsWith("Trip")) {
            var parts = line.split(" ");
            var milesDriven = Double.parseDouble(parts[parts.length - 1]);
            var endTime = parts[parts.length - 2];
            var startTime = parts[parts.length - 3];
            var startTimeParts = startTime.split(":");
            var startTimeHours = Integer.parseInt(startTimeParts[0]);
            var startTimeMinutes = Integer.parseInt(startTimeParts[1]);
            var endTimeParts = endTime.split(":");
            var endTimeHours = Integer.parseInt(endTimeParts[0]);
            var endTimeMinutes = Integer.parseInt(endTimeParts[1]);
            var minutesDriven = (endTimeHours - startTimeHours) * 60 + endTimeMinutes - startTimeMinutes;
            var driverName = line.substring(5, line.indexOf(startTime + " " + endTime) - 1);
            return new TripCommand(driverName, milesDriven, minutesDriven);
        } else {
            throw new IllegalArgumentException("Unknown command.");
        }
    }
}
