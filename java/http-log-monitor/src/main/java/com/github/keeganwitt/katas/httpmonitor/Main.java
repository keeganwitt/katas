package com.github.keeganwitt.katas.httpmonitor;

import com.github.keeganwitt.katas.httpmonitor.parser.CsvHttpLogParser;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads properties (setting defaults if necessary), invokes parser on stream, and invokes invokes LogEventHandler for
 * each LogEvent parsed.
 */
public class Main implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    static final File PROPERTIES_FILE = new File("http-log-monitor.properties");
    private static final int DEFAULT_SECONDS_BETWEEN_REPORTS = 10;
    private static final int DEFAULT_TOP_NUMBER_TO_SHOW = 1;
    private static final int DEFAULT_REQUESTS_PER_SECOND_FOR_ALERT = 10;
    private static final String SECONDS_BETWEEN_REPORTS = "secondsBetweenFrequencyReports";
    private static final String TOP_SEGMENTS_LIMIT = "topSegmentsLimit";
    private static final String REQUESTS_PER_SECOND_FOR_ALERT = "requestsPerSecondForAlert";

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        // load or default configuration
        var secondsBetweenFrequencyReports = DEFAULT_SECONDS_BETWEEN_REPORTS;
        var topNumberToShow = DEFAULT_TOP_NUMBER_TO_SHOW;
        var requestsPerSecondForAlert = DEFAULT_REQUESTS_PER_SECOND_FOR_ALERT;
        var properties = new Properties();
        try (InputStream inputStream = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(inputStream);
            try {
                secondsBetweenFrequencyReports = Integer.parseInt(properties.getProperty(SECONDS_BETWEEN_REPORTS));
                if (secondsBetweenFrequencyReports < 1) {
                    LOG.warn("Unable to load property {}, using default.", SECONDS_BETWEEN_REPORTS);
                    secondsBetweenFrequencyReports = DEFAULT_SECONDS_BETWEEN_REPORTS;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Unable to load property {}, using default.", SECONDS_BETWEEN_REPORTS, e);
            }
            try {
                topNumberToShow = Integer.parseInt(properties.getProperty(TOP_SEGMENTS_LIMIT));
                if (topNumberToShow < 1) {
                    LOG.warn("Unable to load property {}, using default.", TOP_SEGMENTS_LIMIT);
                    topNumberToShow = DEFAULT_TOP_NUMBER_TO_SHOW;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Unable to load property {}, using default.", TOP_SEGMENTS_LIMIT, e);
            }
            try {
                requestsPerSecondForAlert = Integer.parseInt(properties.getProperty(REQUESTS_PER_SECOND_FOR_ALERT));
                if (requestsPerSecondForAlert < 1) {
                    LOG.warn("Unable to load property {}, using default.", REQUESTS_PER_SECOND_FOR_ALERT);
                    requestsPerSecondForAlert = DEFAULT_REQUESTS_PER_SECOND_FOR_ALERT;
                }
            } catch (NumberFormatException e) {
                LOG.warn("Unable to load property {}, using default.", REQUESTS_PER_SECOND_FOR_ALERT, e);
            }
        } catch (IOException e) {
            LOG.warn("Unable to load properties file, using defaults.", e);
        }

        // setup the input and output streams, and execute the program
        try {
            var inputStream = System.in;
            var outputStream = System.out;
            var parser = new CsvHttpLogParser(inputStream);
            var logEventHandler = new LogEventHandler(outputStream, secondsBetweenFrequencyReports,
                    topNumberToShow, requestsPerSecondForAlert);
            parser.forEach(logEventHandler::handle);
            logEventHandler.end();
            outputStream.flush();
        } catch (IOException e) {
            LOG.error("Unable to instantiate parser.", e);
        }
    }
}
