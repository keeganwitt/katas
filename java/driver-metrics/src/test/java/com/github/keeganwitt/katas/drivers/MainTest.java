package com.github.keeganwitt.katas.drivers;

import com.google.common.io.Files;
import lombok.var;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;

public class MainTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEndToEnd() throws IOException {
        var input = "Driver Dan" + lineSeparator() +
                "Driver Alex" + lineSeparator() +
                "Driver Bob" + lineSeparator() +
                "BadCommand" + lineSeparator() +
                "Trip Jimmy 07:15 07:45 17.3" + lineSeparator() +  // driver not defined yet
                "Driver Jimmy" + lineSeparator() +
                "Trip Jimmy 07:15 07:15 17.3" + lineSeparator() +  // 0 time
                "Trip Jimmy 08:15 07:15 17.3" + lineSeparator() +  // negative time
                "Trip Jimmy 07:15 07:45 -17.3" + lineSeparator() +  // negative miles
                "Trip Dan 07:15 07:45 17.3" + lineSeparator() +
                "Trip Dan 06:12 06:32 21.8" + lineSeparator() +
                "Trip Alex 12:01 13:16 42.0" + lineSeparator();
        var expectedOutput = "Alex: 42 miles @ 34 mph" + lineSeparator() +
                "Dan: 39 miles @ 47 mph" + lineSeparator() +
                "Bob: 0 miles" + lineSeparator() +
                "Jimmy: 0 miles" + lineSeparator();
        var inputFile = folder.newFile();
        var outputFile = folder.newFile();
        Files.asCharSink(inputFile, Charset.defaultCharset()).write(input);
        Main.main(inputFile.getCanonicalPath(), outputFile.getCanonicalPath());
        var result = Files.asCharSource(outputFile, Charset.defaultCharset()).read();
        assertEquals(expectedOutput, result);
    }
}
