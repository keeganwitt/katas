package com.github.keeganwitt.katas.httpmonitor;

import com.github.keeganwitt.katas.httpmonitor.model.AggregatingWindow;
import com.github.keeganwitt.katas.httpmonitor.model.LogEvent;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles printing reports and alerts from aggregating window to output stream.
 */
public class LogEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LogEventHandler.class);
    private int secondsBetweenFrequencyReports;
    private int topNumberToShow;
    private SortedMap<Integer, Map<String, Integer>> hitsPerSectionInEachSecond;
    private int requestsPerSecondForAlert;
    private AggregatingWindow aggregatingWindow;
    private boolean alertActive;
    private OutputStream outputStream;

    /**
     * Constructs a new log event handler.
     *
     * @param outputStream                   stream to write reports and alerts to
     * @param secondsBetweenFrequencyReports how often to report frequency metrics
     * @param topNumberToShow                top number of sections to show
     * @param requestsPerSecondForAlert      number of average requests per second that trigger alert
     */
    public LogEventHandler(OutputStream outputStream, int secondsBetweenFrequencyReports, int topNumberToShow,
                           int requestsPerSecondForAlert) {
        this.topNumberToShow = topNumberToShow;
        this.secondsBetweenFrequencyReports = secondsBetweenFrequencyReports;
        hitsPerSectionInEachSecond = new TreeMap<>();
        this.requestsPerSecondForAlert = requestsPerSecondForAlert;
        aggregatingWindow = new AggregatingWindow();
        alertActive = false;
        this.outputStream = outputStream;
    }

    /**
     * Adds log event to the metrics to be aggregated, and prints reports and alerts as appropriate.
     *
     * @param logEvent log event to add to metrics and report/alert on
     */
    public void handle(LogEvent logEvent) {
        var aggregatedHitsPerSectionInEachSecond = aggregatingWindow.add(logEvent);
        handleLoadAlert(logEvent);
        handleFrequencyReport(aggregatedHitsPerSectionInEachSecond);
    }

    /**
     * Call when finished parsing log to print the final frequency reports.
     */
    public void end() {
        handleFrequencyReport(aggregatingWindow.getHitsPerSectionForEachSecond().rowMap());
    }

    private void handleLoadAlert(LogEvent logEvent) {
        var hitsPerSecond = aggregatingWindow.averageHitsPerSecond();
        if (hitsPerSecond >= requestsPerSecondForAlert) {
            if (!alertActive) {
                alertActive = true;
                var alertLine = "High traffic generated an alert - hits = " + hitsPerSecond + ", triggered at "
                        + logEvent.getDate() + System.lineSeparator();
                try {
                    outputStream.write(alertLine.getBytes());
                } catch (IOException e) {
                    LOG.error("Exception occurred while trying to write load alert to output stream.", e);
                }
            }
        } else if (alertActive) {
            alertActive = false;
            var alertLine = "High traffic alert recovered at " + logEvent.getDate() + System.lineSeparator();
            try {
                outputStream.write(alertLine.getBytes());
            } catch (IOException e) {
                LOG.error("Exception occurred while trying to write load alert recovery to output stream.", e);
            }
        }
    }

    /**
     * Prints frequency reports from time older than the oldest second in the aggregating window (that way all the log
     * events that could have been aggregated while aggregation was occurring, are included. Any log events that happen
     * after that period has been aggregated (the aggregating window has shifted) will be not be reported on.
     * @param aggregatedHitsPerSectionInEachSecond metrics already aggregated from time older than current aggregation window
     */
    private void handleFrequencyReport(SortedMap<Integer, Map<String, Integer>> aggregatedHitsPerSectionInEachSecond) {
        if (aggregatedHitsPerSectionInEachSecond != null) {
            hitsPerSectionInEachSecond.putAll(aggregatedHitsPerSectionInEachSecond);
            while (!hitsPerSectionInEachSecond.isEmpty() && hitsPerSectionInEachSecond.lastKey() + 1
                    >= hitsPerSectionInEachSecond.firstKey() + secondsBetweenFrequencyReports) {
                var reportWindow = hitsPerSectionInEachSecond.headMap(hitsPerSectionInEachSecond.firstKey()
                        + secondsBetweenFrequencyReports + 1);
                mostFrequentlyHitSections(reportWindow, topNumberToShow).forEach((key, value) -> {
                    var reportLine = key + " -> hit " + value + " times" + System.lineSeparator();
                    try {
                        outputStream.write(reportLine.getBytes());
                    } catch (IOException e) {
                        LOG.error("Exception occurred while trying to write frequency report to output stream.", e);
                    }
                });
                reportWindow.clear();
            }
        }
    }

    /**
     * Returns a frequency map of top hit segments. Note if there is a tie in counts, it is not deterministic which
     * sections will be included if the limit chosen requires omitting sections.
     *
     * @param reportWindow    counts of hits, by section, in each second
     * @param topNumberToShow limit results to this number of top hit sections
     * @return a map with the section as the key, and the hit count as the value
     */
    private Map<String, Long> mostFrequentlyHitSections(SortedMap<Integer, Map<String, Integer>> reportWindow, int topNumberToShow) {
        var sectionCounts = reportWindow.values().stream().flatMap(it -> it.entrySet().stream())
                .map(it -> new AbstractMap.SimpleEntry<>(it.getKey(), (long) it.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
        return sectionCounts.entrySet().stream().sorted(Map.Entry
                .comparingByValue(Comparator.reverseOrder())).limit(topNumberToShow)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
