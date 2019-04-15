package com.github.keeganwitt.katas.httpmonitor.parser;

import com.github.keeganwitt.katas.httpmonitor.model.LogEvent;
import lombok.var;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * This parser takes CSV input in the form
 * <pre>"remotehost","rfc931","authuser","date","request","status","bytes"</pre>
 * The presence of the header is optional.
 */
public class CsvHttpLogParser implements Iterable<LogEvent>, Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(CsvHttpLogParser.class);
    private static final String REMOTE_HOST = "remotehost";
    private static final String RFC931 = "rfc931";
    private static final String AUTH_USER = "authuser";
    private static final String DATE = "date";
    private static final String REQUEST = "request";
    private static final String STATUS = "status";
    private static final String BYTES = "bytes";
    private static final String[] HEADER = {REMOTE_HOST, RFC931, AUTH_USER, DATE, REQUEST, STATUS, BYTES};
    private CSVParser csvParser;

    /**
     * Create a new CsvHttpLogParser on the specified stream.
     *
     * @param inputStream stream to parse
     * @throws IOException when an error occurs during parsing
     */
    public CsvHttpLogParser(final InputStream inputStream) throws IOException {
        csvParser = CSVParser.parse(inputStream, Charset.defaultCharset(),
                CSVFormat.RFC4180.withHeader(HEADER));
    }

    /**
     * Returns an iterator on the InputStream. The InputStream is lazily loaded to populate the iterator.
     *
     * @return the iterator from the InputStream
     */
    @Override
    public Iterator<LogEvent> iterator() {
        return new LogEventIterator(csvParser.iterator());
    }

    /**
     * Closes the underlying readers and iterators
     *
     * @throws IOException when an error occurred during closing
     */
    @Override
    public void close() throws IOException {
        csvParser.close();
    }

    /**
     * This class wraps CSVRecords returned by CSVParser, to provide LogEvent objects that are easier to work with.
     */
    private static class LogEventIterator implements Iterator<LogEvent> {
        private Iterator<CSVRecord> csvRecordIterator;

        LogEventIterator(Iterator<CSVRecord> csvRecordIterator) {
            this.csvRecordIterator = csvRecordIterator;
        }

        @Override
        public boolean hasNext() {
            return csvRecordIterator.hasNext();
        }

        @Override
        public LogEvent next() {
            var csvRecord = csvRecordIterator.next();
            if (csvRecord.size() != HEADER.length) {
                LOG.error("Line did not contain expected number of tokens.");
                return null;
            }
            if (REMOTE_HOST.equals(csvRecord.get(0))) {
                // skip the header record if the current line appears to be a header
                return next();
            }
            var logEvent = new LogEvent();
            logEvent.setRemoteHost(csvRecord.get(REMOTE_HOST));
            logEvent.setAuthUser(csvRecord.get(AUTH_USER));
            try {
                logEvent.setDate(Integer.parseInt(csvRecord.get(DATE)));
            } catch (NumberFormatException e) {
                LOG.error("{} contained a value that was not an integer.", DATE);
                return null;
            }
            var request = csvRecord.get(REQUEST);
            var requestParts = request.split(" ");
            if (requestParts.length != 3) {
                LOG.error("{} did not contain 3 values separated by spaces.", REQUEST);
                return null;
            }
            logEvent.setHttpMethod(requestParts[0]);
            logEvent.setPath(requestParts[1]);
            try {
                logEvent.setStatus(Short.parseShort(csvRecord.get(STATUS)));
            } catch (NumberFormatException e) {
                LOG.error("{} contained a value that was not a short.", STATUS);
                return null;
            }
            try {
                logEvent.setBytes(Long.parseLong(csvRecord.get(BYTES)));
            } catch (NumberFormatException e) {
                LOG.error("{} contained a value that was not a long.", BYTES);
                return null;
            }
            return logEvent;
        }
    }
}
