package com.github.keeganwitt.katas.drivers.parser;

import lombok.*;

@AllArgsConstructor
@ToString
public class DriverCommand implements Command {
    @Getter
    @Setter
    @NonNull
    private String driverName;
}
