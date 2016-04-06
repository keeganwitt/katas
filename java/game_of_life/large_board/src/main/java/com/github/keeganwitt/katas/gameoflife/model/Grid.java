package com.github.keeganwitt.katas.gameoflife.model;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Grid {
    // TODO: wrap around instead of cells falling off the grid?
    // TODO: support more file formats (http://psoup.math.wisc.edu/mcell/ca_files_formats.html)?
    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String INITIAL_STATE_FILE_ENCODING = "UTF-8";
    public static final int DEFAULT_GRID_SIZE = 12;
    public static final int DEFAULT_X_OFFSET = 0;
    public static final int DEFAULT_Y_OFFSET = 0;
    public static final char LIVE_CELL_PRESENT_CHAR = 'O';
    public static final char NO_LIVE_CELL_PRESENT_CHAR = '.';
    private GridState gridState;
    private int height;
    private int width;

    public static Grid createGrid(String initialStateString, int gridSize, int xOffset, int yOffset) {
        String[] lines = initialStateString.split("\r?\n");
        List<String> linesWithoutComments = findLinesWithoutComments(lines);
        return createGrid(linesWithoutComments, gridSize, xOffset, yOffset);
    }

    public static Grid createGrid(File initialStateFile, int gridSize, int xOffset, int yOffset) throws IOException {
        return createGrid(new String(Files.readAllBytes(initialStateFile.toPath()), INITIAL_STATE_FILE_ENCODING), gridSize, xOffset, yOffset);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public GridState getGridState() {
        return gridState;
    }

    public int getNumLiveCells() {
        return gridState.getNumLiveCells();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                sb.append(gridState.get(i, j) ? LIVE_CELL_PRESENT_CHAR : NO_LIVE_CELL_PRESENT_CHAR).append(j < width - 1 ? " " : "");
            sb.append(i < height - 1 ? NEWLINE : "");
        }
        return sb.toString();
    }

    Grid(int height, int width) {
        if (height < 1 || width < 1)
            throw new IllegalArgumentException("Grid must contain at least 1 row and 1 column.");
        this.height = height;
        this.width = width;
        gridState = new GridState();
    }

    static Grid createGrid(List<String> linesWithoutComments, int gridSize, int xOffset, int yOffset) {
        Grid grid = new Grid(gridSize, gridSize);
        for (int i = 0; i < linesWithoutComments.size() + yOffset; i++) {
            char[] lineArr = null;
            if (i < linesWithoutComments.size())
                lineArr = linesWithoutComments.get(i).toCharArray();
            if (lineArr != null && lineArr.length + xOffset >= grid.width)
                throw new IllegalArgumentException("Input grid file too wide for current grid.");
            if (lineArr == null)
                continue;
            for (int j = 0; j < lineArr.length + xOffset; j++) {
                if (i + yOffset >= grid.height)
                    throw new IllegalArgumentException("Input grid file too tall for current grid.");
                char c;
                if (j < lineArr.length)
                    c = lineArr[j];
                else
                    c = NO_LIVE_CELL_PRESENT_CHAR;
                if (c != NO_LIVE_CELL_PRESENT_CHAR && c != LIVE_CELL_PRESENT_CHAR)
                    throw new IllegalArgumentException("Only '" + NO_LIVE_CELL_PRESENT_CHAR + "' and '" + LIVE_CELL_PRESENT_CHAR + "' characters are allowed.  Encountered '" + c + "' at " + grid.height + ", " + j + ".");
                if (c == LIVE_CELL_PRESENT_CHAR) {
                    grid.gridState.addCell(i + yOffset, j + xOffset);
                }
            }
        }
        return grid;
    }

    static List<String> findLinesWithoutComments(String[] lines) {
        List<String> linesWithoutCommentsOrNewlines = new ArrayList<>(Arrays.asList(lines));
        for (String line : lines)
            if (line.startsWith("!"))
                linesWithoutCommentsOrNewlines.remove(line);
        return linesWithoutCommentsOrNewlines;
    }
}
