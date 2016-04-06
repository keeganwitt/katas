package com.github.keeganwitt.katas.gameoflife;

import com.github.keeganwitt.katas.gameoflife.core.GameOfLife;
import com.github.keeganwitt.katas.gameoflife.model.Grid;
import org.apache.commons.cli.*;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.IOException;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    private static final String APP_NAME = "gameOfLife";
    private static final long MIN_TIME_BETWEEN_ANIMATED_GENERATIONS = 800;

    public static void main(String[] args) {
        try {
            // TODO: allow user to opt-in to HashLife algorithm?
            // options to be set by command line args
            File inputGridFile = null;
            int gridSize = Grid.DEFAULT_GRID_SIZE;
            int xOffset = Grid.DEFAULT_X_OFFSET;
            int yOffset = Grid.DEFAULT_Y_OFFSET;
            int generationsToSimulate = Integer.MAX_VALUE;
            boolean animate = false;
            boolean showCount = false;

            // TODO: Swing or JavaFX UI?

            // parse commandline options
            Options options = createOptions();
            CommandLine cmd = new DefaultParser().parse(options, args);
            HelpFormatter formatter = new HelpFormatter();
            if (cmd.hasOption('h')) {
                formatter.printHelp(APP_NAME, options);
                System.exit(0);
            }
            if (!cmd.hasOption('i')) {
                formatter.printHelp(APP_NAME, options);
                System.exit(1);
            }
            inputGridFile = new File(cmd.getOptionValue('i'));
            if (cmd.hasOption('s'))
                gridSize = Integer.parseInt(cmd.getOptionValue('s'));
            if (cmd.hasOption('x'))
                xOffset = Integer.parseInt(cmd.getOptionValue('x'));
            if (xOffset < 0 || xOffset >= gridSize) {
                formatter.printHelp(APP_NAME, options);
                System.exit(1);
            }
            if (cmd.hasOption('y'))
                yOffset = Integer.parseInt(cmd.getOptionValue('y'));
            if (yOffset < 0 || yOffset >= gridSize) {
                formatter.printHelp(APP_NAME, options);
                System.exit(1);
            }
            if (cmd.hasOption('g'))
                generationsToSimulate = Integer.parseInt(cmd.getOptionValue('g'));
            if (cmd.hasOption('a'))
                animate = true;
            if (cmd.hasOption('c'))
                showCount = true;
            runSimulation(inputGridFile, gridSize, xOffset, yOffset, generationsToSimulate, showCount, animate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Options createOptions() {
        Options options = new Options();
        options.addOption("i", "input", true, "Input cell file.");
        options.addOption("s", "size", true, "The size of the square grid to create (optional, defaults to " + Grid.DEFAULT_GRID_SIZE + " x " + Grid.DEFAULT_GRID_SIZE  + ".");
        options.addOption("x", "xOffset", true, "X offset from leftmost of grid to load the input cells at. Must be a positive integer smaller than grid size.");
        options.addOption("y", "yOffset", true, "Y offset from top of grid to load the input cells at. Must be a positive integer smaller than grid size.");
        options.addOption("g", "gens", true, "Number of generations to simulate (optional, defaults to Integer.MAX_VALUE). Must be an integer.");
        options.addOption("c", "count", false, "Displays count of live cells each generation rather than displaying the grid (optional defaults to false).");
        options.addOption("a", "animate", false, "Animates screen by erasing previous generations (optional, defaults to off). Will have at least " + MIN_TIME_BETWEEN_ANIMATED_GENERATIONS + " ms between generations.");
        options.addOption("h", "help", false, "Shows this message.");
        return options;
    }

    static void runSimulation(File inputGridFile, int gridSize, int xOffset, int yOffset, int generationsToSimulate, boolean showCount, boolean animate) throws IOException, InterruptedException {
        // TODO: use Jansi to add colors like green for newly born cells, red for newly deceased cells, and default (white/black) otherwise?
        AnsiConsole.systemInstall();
        long start = System.currentTimeMillis();
        Grid grid = Grid.createGrid(inputGridFile, gridSize, xOffset, yOffset);
        long end = System.currentTimeMillis();
        long executionTime = end - start;
        System.out.println("Gen 0 (loaded in " + executionTime + " ms)");
        show(showCount, grid);
        Thread.sleep(MIN_TIME_BETWEEN_ANIMATED_GENERATIONS);
        for (int i = 0; i < generationsToSimulate; i++) {
            start = System.currentTimeMillis();
            GameOfLife.nextGeneration(grid);
            end = System.currentTimeMillis();
            executionTime = end - start;
            if (animate && executionTime < MIN_TIME_BETWEEN_ANIMATED_GENERATIONS)
                Thread.sleep(MIN_TIME_BETWEEN_ANIMATED_GENERATIONS - executionTime);
            if (animate)
                System.out.println(ansi().eraseScreen());
            System.out.println("Gen " + (i + 1) + " (computed in " + executionTime + " ms)");
            show(showCount, grid);
        }
    }

    static void show(boolean showCount, Grid grid) {
        if (showCount)
            System.out.println(grid.getNumLiveCells() + " live cells");
        else
            System.out.println(grid.toString());
    }
}
