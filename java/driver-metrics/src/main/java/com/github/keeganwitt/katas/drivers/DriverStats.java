package com.github.keeganwitt.katas.drivers;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DriverStats implements Comparable<DriverStats> {
    @Getter
    @Setter
    private double milesDriven;

    @Getter
    @Setter
    private int minutesDriven;

    public void addTrip(double miles, int minutes) {
        minutesDriven += minutes;
        milesDriven += miles;
    }

    public double calculateAverageSpeed() {
        return milesDriven / (minutesDriven / 60.00);
    }

    @Override
    public int compareTo(DriverStats o) {
        return Double.compare(o.getMilesDriven(), milesDriven);
    }
}
