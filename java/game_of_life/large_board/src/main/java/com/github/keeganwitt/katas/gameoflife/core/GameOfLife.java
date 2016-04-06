package com.github.keeganwitt.katas.gameoflife.core;

import com.github.keeganwitt.katas.gameoflife.model.GameOfLifeStateChange;
import com.github.keeganwitt.katas.gameoflife.model.Grid;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * This implementation is optimized for simulating large grids.  It takes advantage of the fact for large grids, most
 * of the cells aren't alive and a small proportion of the cells change state in any given generation.  It also takes
 * advantage of the fact that for a single generation, the next state of each cell can be computed independently. This
 * allows ForkJoin when the grid is large enough to make it worth the extra overhead.
 */
public class GameOfLife {
    private static final int FORK_JOIN_THRESHOLD = 300;  // TODO: is this the optimal threshold?
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    /**
     * Operates according to the rules
     * <ul>
     * <li>Any live cell with fewer than two live neighbours dies, as if caused by under-population.</li>
     * <li>Any live cell with two or three live neighbours lives on to the next generation.</li>
     * <li>Any live cell with more than three live neighbours dies, as if by overcrowding.</li>
     * <li>Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.</li>
     * </ul>
     */
    public static void nextGeneration(Grid grid) {
        GameOfLifeStateChange gameOfLifeStateChange;
        if (grid == null || grid.getGridState() == null || grid.getGridState().getSparseGrid().size() == 0)
            return;
        if (grid.getHeight() < FORK_JOIN_THRESHOLD)
            gameOfLifeStateChange = compute(grid, grid.getGridState().getSparseGrid().firstKey(), grid.getGridState().getSparseGrid().lastKey());
        else
            gameOfLifeStateChange = FORK_JOIN_POOL.invoke(new GameOfLifeTask(grid, grid.getGridState().getSparseGrid().firstKey(), grid.getGridState().getSparseGrid().lastKey()));
        // adding and removing cells explicitly rather than cloning the entire grid and modifying it for performance
        for (Map.Entry<Integer, Integer> cellToRemove : gameOfLifeStateChange.getCellsToRemove())
            grid.getGridState().removeCell(cellToRemove.getKey(), cellToRemove.getValue());
        for (Map.Entry<Integer, Integer> cellToAdd : gameOfLifeStateChange.getCellsToAdd())
            grid.getGridState().addCell(cellToAdd.getKey(), cellToAdd.getValue());
    }

    static GameOfLifeStateChange compute(Grid grid, int startRow, int endRow) {
        GameOfLifeStateChange gameOfLifeStateChange = new GameOfLifeStateChange();
        for (Map.Entry<Integer, Integer> cell : grid.getGridState()) {
            int i = cell.getKey();
            int j = cell.getValue();
            if (i < startRow)
                continue;
            if (i > endRow)
                return gameOfLifeStateChange;

            computeForCell(grid, gameOfLifeStateChange, i, j);

            // compute for neighbors
            if (i > 0 && j > 0)  // northwest
                computeForCell(grid, gameOfLifeStateChange, i - 1, j - 1);
            if (i > 0)  // north
                computeForCell(grid, gameOfLifeStateChange, i - 1, j);
            if (i > 0 && j < grid.getWidth() - 1)  // northeast
                computeForCell(grid, gameOfLifeStateChange, i - 1, j + 1);
            if (j > 0)  // west
                computeForCell(grid, gameOfLifeStateChange, i, j - 1);
            if (j < grid.getWidth() - 1)  // east
                computeForCell(grid, gameOfLifeStateChange, i, j + 1);
            if (i < grid.getHeight() - 1 && j > 0)  // southwest
                computeForCell(grid, gameOfLifeStateChange, i + 1, j - 1);
            if (i < grid.getHeight() - 1)  // south
                computeForCell(grid, gameOfLifeStateChange, i + 1, j);
            if (i < grid.getHeight() - 1 && j < grid.getWidth() - 1)  // southeast
                computeForCell(grid, gameOfLifeStateChange, i + 1, j + 1);
        }
        gameOfLifeStateChange.incrementNumLiveCellChange(gameOfLifeStateChange.getCellsToAdd().size() - gameOfLifeStateChange.getCellsToRemove().size());
        return gameOfLifeStateChange;
    }

    static void computeForCell(Grid grid, GameOfLifeStateChange gameOfLifeStateChange, int i, int j) {
        int adjacentLiveCells = countAdjacentLiveCells(grid, i, j);
        if (grid.getGridState().get(i, j) && (adjacentLiveCells < 2 || adjacentLiveCells > 3)) // cell dies
            gameOfLifeStateChange.getCellsToRemove().add(new AbstractMap.SimpleImmutableEntry<>(i, j));
        else if (!grid.getGridState().get(i, j) && adjacentLiveCells == 3)  // cell is born
            gameOfLifeStateChange.getCellsToAdd().add(new AbstractMap.SimpleImmutableEntry<>(i, j));
    }

    static int countAdjacentLiveCells(Grid grid, int i, int j) {
        // TODO: remove duplication between this and compute?
        // TODO: profiling indicates this is now where most of the time is spent (besides nextGeneration() itself).  What can be done?
        int adjacentLiveCells = 0;
        if (i > 0 && j > 0 && grid.getGridState().get(i - 1, j - 1))  // northwest
            adjacentLiveCells++;
        if (i > 0 && grid.getGridState().get(i - 1, j))  // north
            adjacentLiveCells++;
        if (i > 0 && j < grid.getWidth() - 1 && grid.getGridState().get(i - 1, j + 1))  // northeast
            adjacentLiveCells++;
        if (j > 0 && grid.getGridState().get(i, j - 1))  // west
            adjacentLiveCells++;
        if (j < grid.getWidth() - 1 && grid.getGridState().get(i, j + 1))  // east
            adjacentLiveCells++;
        if (i < grid.getHeight() - 1 && j > 0 && grid.getGridState().get(i + 1, j - 1))  // southwest
            adjacentLiveCells++;
        if (i < grid.getHeight() - 1 && grid.getGridState().get(i + 1, j))  // south
            adjacentLiveCells++;
        if (i < grid.getHeight() - 1 && j < grid.getWidth() - 1 && grid.getGridState().get(i + 1, j + 1))  // southeast
            adjacentLiveCells++;
        return adjacentLiveCells;
    }
}
