package com.github.keeganwitt.katas.drivers.parser;

import lombok.*;

@ToString
public class TripCommand implements Command {
    @Getter
    @Setter
    @NonNull
    private String driverName;

    @Getter
    private double milesDriven;

    @Getter
    private int minutesDriven;

    public double calculateAverageSpeed() {
        var hoursDriven = minutesDriven / 60.00;
        return milesDriven / hoursDriven;
    }

    public TripCommand(@NonNull String driverName, double milesDriven, int minutesDriven) {
        this.driverName = driverName;
        setMilesDriven(milesDriven);
        setMinutesDriven(minutesDriven);
    }

    public void setMilesDriven(double milesDriven) {
        if (milesDriven < 0) {
            throw new IllegalArgumentException("Miles driven may not be negative.");
        }
        this.milesDriven = milesDriven;
    }

    public void setMinutesDriven(int minutesDriven) {
        if (minutesDriven <= 0) {
            throw new IllegalArgumentException("Minutes driven may not be negative or zero.");
        }
        this.minutesDriven = minutesDriven;
    }
}
