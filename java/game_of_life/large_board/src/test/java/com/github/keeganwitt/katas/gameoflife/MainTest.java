package com.github.keeganwitt.katas.gameoflife;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public class MainTest {
    @Test
    public void testCreatesOption() {
        Options options = Main.createOptions();
        Assert.assertEquals(8, options.getOptions().size());
        Assert.assertTrue(options.hasOption("a") && !options.getOption("a").hasArg());
        Assert.assertTrue(options.hasOption("c") && !options.getOption("c").hasArg());
        Assert.assertTrue(options.hasOption("g") && options.getOption("g").hasArg());
        Assert.assertTrue(options.hasOption("h") && !options.getOption("h").hasArg());
        Assert.assertTrue(options.hasOption("i") && options.getOption("i").hasArg());
        Assert.assertTrue(options.hasOption("s") && options.getOption("s").hasArg());
        Assert.assertTrue(options.hasOption("x") && options.getOption("x").hasArg());
        Assert.assertTrue(options.hasOption("y") && options.getOption("y").hasArg());
    }
}
