package com.github.keeganwitt.katas.httpmonitor.model;

import com.google.common.collect.TreeBasedTable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

/**
 * Tracks number of hits per section in each second, limited to the size of reporting window. The window slides, as
 * events in the future of the current time window are added. Currently stores 120 seconds of hits.
 */
public class AggregatingWindow {
    private static final Logger LOG = LoggerFactory.getLogger(AggregatingWindow.class);
    private static final int MAXIMUM_SECONDS_TO_STORE = 120;
    private int hitsInWindow;
    @Getter
    private TreeBasedTable<Integer, String, Integer> hitsPerSectionForEachSecond;

    /**
     * Constructs a new alert window object.
     */
    public AggregatingWindow() {
        hitsPerSectionForEachSecond = TreeBasedTable.create();
        this.hitsInWindow = 0;
    }

    /**
     * Adds a log event to the alert window. This will cause the window to slide forward if it is newer than the highest
     * second to store.
     *
     * @param logEvent the log even to add to the alert window
     * @return metrics of time now outside time window
     */
    public SortedMap<Integer, Map<String, Integer>> add(LogEvent logEvent) {
        int logDate = logEvent.getDate();
        String logSection = logEvent.getSection();

        if (hitsPerSectionForEachSecond.isEmpty() || logDate >= hitsPerSectionForEachSecond.rowMap().firstKey()) {
            int count = Optional.ofNullable(hitsPerSectionForEachSecond.get(logDate, logSection)).orElse(0);
            hitsPerSectionForEachSecond.put(logDate, logSection, ++count);
            hitsInWindow++;
        } else {
            LOG.warn("Encountered log event from {}, earlier than the beginning of aggregating window ({}).", logDate, hitsPerSectionForEachSecond.rowMap().firstKey());
        }

        if (logDate > hitsPerSectionForEachSecond.rowMap().firstKey() + MAXIMUM_SECONDS_TO_STORE) {
            int newBeginningOfWindow = logDate - MAXIMUM_SECONDS_TO_STORE;
            SortedMap<Integer, Map<String, Integer>> entriesToRemove = hitsPerSectionForEachSecond.rowMap().headMap(newBeginningOfWindow);
            hitsInWindow -= entriesToRemove.values().stream().mapToInt(it -> it.values().stream().mapToInt(Integer::intValue).sum()).sum();
            TreeBasedTable<Integer, String, Integer> entriesToReturn = TreeBasedTable.create();
            entriesToRemove.forEach((outerKey, outerValue) -> outerValue.forEach((innerKey, innerValue) ->
                    entriesToReturn.put(outerKey, innerKey, innerValue)));
            entriesToRemove.clear();
            return entriesToReturn.rowMap();
        }

        return null;
    }

    /**
     * Calculates the average hits per second (total of all hits across all the seconds in window, divided by the number
     * of seconds in the window).
     *
     * @return average hits per second
     */
    public int averageHitsPerSecond() {
        return (int) Math.ceil((double) hitsInWindow / MAXIMUM_SECONDS_TO_STORE);
    }
}
