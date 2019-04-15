package com.github.keeganwitt.katas.httpmonitor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainTest {
    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    @Rule
    public final TextFromStandardInputStream SYSTEM_IN = TextFromStandardInputStream.emptyStandardInputStream();

    @Rule
    public final SystemOutRule SYSTEM_OUT = new SystemOutRule().enableLog();

    @After
    public void cleanup() {
        if (Main.PROPERTIES_FILE.exists()) {
            if (!Main.PROPERTIES_FILE.delete()) {
                LOG.error("Failed to delete {}", Main.PROPERTIES_FILE.getAbsolutePath());
            }
        }
        SYSTEM_OUT.clearLog();
    }

    @Test
    public void endToEndWithDefaultsDueToNoConfigurationFile() {
        List<String> lines = new ArrayList<>();
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573880,\"POST /api/help HTTP/1.0\",200,1234");
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573881,\"POST /api/user HTTP/1.0\",200,1136");
        for (int i = 0; i < 1200; i++) {
            lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573889,\"GET /report HTTP/1.0\",200,1234");
        }
        lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573890,\"GET /report HTTP/1.0\",200,1234");
        SYSTEM_IN.provideLines(lines.toArray(new String[0]));
        Main.main(new String[]{});
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/report -> hit 1201 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("High traffic generated an alert - hits = 10, triggered at 1549573889" + System.lineSeparator()));
    }

    @Test
    public void endToEndWithConfigurationFile() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secondsBetweenFrequencyReports", "3");
        properties.setProperty("topSegmentsLimit", "2");
        properties.setProperty("requestsPerSecondForAlert", "3");
        FileOutputStream outputStream = new FileOutputStream(Main.PROPERTIES_FILE);
        properties.store(outputStream, "HTTP Log Monitor");
        outputStream.flush();
        outputStream.close();
        List<String> lines = new ArrayList<>();
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573880,\"POST /api/help HTTP/1.0\",200,1234");
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573881,\"POST /api/user HTTP/1.0\",200,1136");
        for (int i = 0; i < 1200; i++) {
            lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573889,\"GET /report HTTP/1.0\",200,1234");
        }
        lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573891,\"GET /report HTTP/1.0\",200,1234");
        SYSTEM_IN.provideLines(lines.toArray(new String[0]));
        Main.main(new String[]{});
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/report -> hit 1201 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/api -> hit 2 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("High traffic generated an alert - hits = 3, triggered at 1549573889" + System.lineSeparator()));
    }

    @Test
    public void endToEndWithDefaultsDueToNonIntegersInConfigurationFile() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secondsBetweenFrequencyReports", "NAN");
        properties.setProperty("topSegmentsLimit", "NAN");
        properties.setProperty("requestsPerSecondForAlert", "NAN");
        FileOutputStream outputStream = new FileOutputStream(Main.PROPERTIES_FILE);
        properties.store(outputStream, "HTTP Log Monitor");
        outputStream.flush();
        outputStream.close();
        List<String> lines = new ArrayList<>();
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573880,\"POST /api/help HTTP/1.0\",200,1234");
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573881,\"POST /api/user HTTP/1.0\",200,1136");
        for (int i = 0; i < 1200; i++) {
            lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573889,\"GET /report HTTP/1.0\",200,1234");
        }
        lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573890,\"GET /report HTTP/1.0\",200,1234");
        SYSTEM_IN.provideLines(lines.toArray(new String[0]));
        Main.main(new String[]{});
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/report -> hit 1201 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("High traffic generated an alert - hits = 10, triggered at 1549573889" + System.lineSeparator()));
    }

    @Test
    public void endToEndWithDefaultsDueToNegativeIntegersInConfigurationFile() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secondsBetweenFrequencyReports", "-1");
        properties.setProperty("topSegmentsLimit", "-1");
        properties.setProperty("requestsPerSecondForAlert", "-1");
        FileOutputStream outputStream = new FileOutputStream(Main.PROPERTIES_FILE);
        properties.store(outputStream, "HTTP Log Monitor");
        outputStream.flush();
        outputStream.close();
        List<String> lines = new ArrayList<>();
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573880,\"POST /api/help HTTP/1.0\",200,1234");
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573881,\"POST /api/user HTTP/1.0\",200,1136");
        for (int i = 0; i < 1200; i++) {
            lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573889,\"GET /report HTTP/1.0\",200,1234");
        }
        lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573890,\"GET /report HTTP/1.0\",200,1234");
        SYSTEM_IN.provideLines(lines.toArray(new String[0]));
        Main.main(new String[]{});
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/report -> hit 1201 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("High traffic generated an alert - hits = 10, triggered at 1549573889" + System.lineSeparator()));
    }

    @Test
    public void endToEndWithDefaultsDueToZeroValuesInConfigurationFile() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("secondsBetweenFrequencyReports", "0");
        properties.setProperty("topSegmentsLimit", "0");
        properties.setProperty("requestsPerSecondForAlert", "0");
        FileOutputStream outputStream = new FileOutputStream(Main.PROPERTIES_FILE);
        properties.store(outputStream, "HTTP Log Monitor");
        outputStream.flush();
        outputStream.close();
        List<String> lines = new ArrayList<>();
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573880,\"POST /api/help HTTP/1.0\",200,1234");
        lines.add("\"10.0.0.3\",\"-\",\"apache\",1549573881,\"POST /api/user HTTP/1.0\",200,1136");
        for (int i = 0; i < 1200; i++) {
            lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573889,\"GET /report HTTP/1.0\",200,1234");
        }
        lines.add("\"10.0.0.4\",\"-\",\"apache\",1549573890,\"GET /report HTTP/1.0\",200,1234");
        SYSTEM_IN.provideLines(lines.toArray(new String[0]));
        Main.main(new String[]{});
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("/report -> hit 1201 times" + System.lineSeparator()));
        Assert.assertTrue(SYSTEM_OUT.getLog().contains("High traffic generated an alert - hits = 10, triggered at 1549573889" + System.lineSeparator()));
    }
}
