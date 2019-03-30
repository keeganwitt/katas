package com.github.keeganwitt.katas.drivers;

import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static com.github.keeganwitt.katas.drivers.DriverStatsAggregator.formatResult;
import static com.github.keeganwitt.katas.drivers.parser.LineParser.parseLine;
import static java.lang.System.lineSeparator;

@CommandLine.Command(description = "Parses a file of driver and trip data and generates report of distances driven and average speed.",
        name = "driverMetrics", mixinStandardHelpOptions = true, version = "1.0")
public class Main implements Runnable {
    private static final Logger LOG = LogManager.getLogger();

    @CommandLine.Parameters(index = "0", description = "Input file")
    private File inputFile;

    @CommandLine.Parameters(index = "1", description = "Output file")
    private File outputFile;

    private DriverStatsAggregator driverStatsAggregator = new DriverStatsAggregator();

    @Override
    public void run() {
        populateRepository();
        writeMetrics();
    }

    private void writeMetrics() {
        try {
            var fileWriter = new FileWriter(outputFile);
            try (var writer = new BufferedWriter(fileWriter)) {
                driverStatsAggregator.sorted().forEach(it -> {
                    String outputString = formatResult(it);
                    try {
                        writer.write(outputString);
                        writer.write(lineSeparator());
                    } catch (IOException e) {
                        LOG.error("Unable to write {} to output file.", outputString, e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("Unable to open output file.", e);
        }
    }

    private void populateRepository() {
        try (Stream<String> stream = Files.lines(inputFile.toPath())) {
            stream.forEach(line -> {
                try {
                    driverStatsAggregator.add(parseLine(line));
                } catch (IllegalArgumentException e) {
                    LOG.warn("Unable to parse/add line '{}' ({}), skipped.", line, e.getMessage());
                }
            });
        } catch (IOException e) {
            LOG.error("Exception while closing stream.", e);
        }
    }

    public static void main(String... args) {
        CommandLine.run(new Main(), args);
    }
}
