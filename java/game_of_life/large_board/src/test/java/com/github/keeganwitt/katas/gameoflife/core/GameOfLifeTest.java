package com.github.keeganwitt.katas.gameoflife.core;

import com.github.keeganwitt.katas.gameoflife.model.Grid;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GameOfLifeTest {
    // TODO: a lot of these are more integration tests than unit tests.  It'd be preferable to test the individual conditions, but for now this was shorter since there are so many permutations.
    // TODO: add performance tests

    @Test
    public void testCountsAllAdjacentLiveCells() {
        String gridString =
                "OOO\n" +
                "OOO\n" +
                "OOO\n";
        Grid grid = Grid.createGrid(gridString, 3, 0, 0);
        Assert.assertEquals(3, GameOfLife.countAdjacentLiveCells(grid, 0, 0));
        Assert.assertEquals(5, GameOfLife.countAdjacentLiveCells(grid, 0, 1));
        Assert.assertEquals(3, GameOfLife.countAdjacentLiveCells(grid, 0, 2));
        Assert.assertEquals(5, GameOfLife.countAdjacentLiveCells(grid, 1, 0));
        Assert.assertEquals(8, GameOfLife.countAdjacentLiveCells(grid, 1, 1));
        Assert.assertEquals(5, GameOfLife.countAdjacentLiveCells(grid, 1, 2));
        Assert.assertEquals(3, GameOfLife.countAdjacentLiveCells(grid, 2, 0));
        Assert.assertEquals(5, GameOfLife.countAdjacentLiveCells(grid, 2, 1));
        Assert.assertEquals(3, GameOfLife.countAdjacentLiveCells(grid, 2, 2));
    }

    @Test
    public void testNextGenerationWithBlock() throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource("stills/block.cells").toURI());
        Grid grid = Grid.createGrid(path.toFile(), Grid.DEFAULT_GRID_SIZE, Grid.DEFAULT_X_OFFSET, Grid.DEFAULT_Y_OFFSET);
        for (int i = 0; i < 20; i++) {
            GameOfLife.nextGeneration(grid);
            Assert.assertEquals(4, grid.getNumLiveCells());
            Assert.assertTrue(grid.getGridState().get(0, 0));
            Assert.assertTrue(grid.getGridState().get(0, 1));
            Assert.assertTrue(grid.getGridState().get(1, 0));
            Assert.assertTrue(grid.getGridState().get(1, 1));
        }
    }

    @Test
    public void testNextGenerationWithBeacon() throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource("oscillators/beacon.cells").toURI());
        Grid grid = Grid.createGrid(path.toFile(), Grid.DEFAULT_GRID_SIZE, Grid.DEFAULT_X_OFFSET, Grid.DEFAULT_Y_OFFSET);
        for (int i = 0; i < 20; i++) {
            GameOfLife.nextGeneration(grid);
            if (i % 2 == 0) {
                Assert.assertEquals(8, grid.getNumLiveCells());
                Assert.assertTrue(grid.getGridState().get(0, 0));
                Assert.assertTrue(grid.getGridState().get(0, 1));
                Assert.assertTrue(grid.getGridState().get(1, 0));
                Assert.assertTrue(grid.getGridState().get(1, 1));
                Assert.assertTrue(grid.getGridState().get(2, 2));
                Assert.assertTrue(grid.getGridState().get(2, 3));
                Assert.assertTrue(grid.getGridState().get(3, 2));
                Assert.assertTrue(grid.getGridState().get(3, 3));
            } else {
                Assert.assertEquals(6, grid.getNumLiveCells());
                Assert.assertTrue(grid.getGridState().get(0, 0));
                Assert.assertTrue(grid.getGridState().get(0, 1));
                Assert.assertTrue(grid.getGridState().get(1, 0));
                Assert.assertTrue(grid.getGridState().get(2, 3));
                Assert.assertTrue(grid.getGridState().get(3, 2));
                Assert.assertTrue(grid.getGridState().get(3, 3));
            }
        }
    }

    @Test
    public void testNextGenerationWithBipole() throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource("oscillators/bipole.cells").toURI());
        Grid grid = Grid.createGrid(path.toFile(), Grid.DEFAULT_GRID_SIZE, Grid.DEFAULT_X_OFFSET, Grid.DEFAULT_Y_OFFSET);
        for (int i = 0; i < 20; i++) {
            GameOfLife.nextGeneration(grid);
            Assert.assertEquals(8, grid.getNumLiveCells());
            Assert.assertTrue(grid.getGridState().get(0, 0));
            Assert.assertTrue(grid.getGridState().get(0, 1));
            Assert.assertTrue(grid.getGridState().get(1, 0));
            Assert.assertTrue(grid.getGridState().get(3, 4));
            Assert.assertTrue(grid.getGridState().get(4, 3));
            Assert.assertTrue(grid.getGridState().get(4, 4));
            if (i % 2 == 0) {
                Assert.assertTrue(grid.getGridState().get(2, 1));
                Assert.assertTrue(grid.getGridState().get(2, 3));
            } else {
                Assert.assertTrue(grid.getGridState().get(1, 2));
                Assert.assertTrue(grid.getGridState().get(3, 2));
            }
        }
    }
}
