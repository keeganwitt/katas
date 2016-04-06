package com.github.keeganwitt.katas.gameoflife.model;

import org.junit.Assert;
import org.junit.Test;

public class GridTest {
    @Test
    public void testLoadingGridFromString() {
        String gridString =
                "! a comment line\n" +
                "..OOO\n" +
                "...O\n" +
                "OO\n" +
                ".O\n";
        boolean[][] expectedGrid = new boolean[5][5];
        expectedGrid[0][2] = true;
        expectedGrid[0][3] = true;
        expectedGrid[0][4] = true;
        expectedGrid[1][3] = true;
        expectedGrid[2][0] = true;
        expectedGrid[2][1] = true;
        expectedGrid[3][1] = true;
        Grid grid = Grid.createGrid(gridString, 5, 0, 0);
        Assert.assertEquals(7, grid.getNumLiveCells());
        for (int i = 0 ; i < 5; i++)
            for (int j = 0 ; j < 5; j++)
                Assert.assertEquals("("+i+", "+j+") was not as expected.", expectedGrid[i][j], grid.getGridState().get(i, j));
    }

    @Test
    public void testToString() {
        String gridString =
                "..OOO" + Grid.NEWLINE +
                "...O" + Grid.NEWLINE +
                "OO" + Grid.NEWLINE +
                ".O";
        Grid grid = Grid.createGrid(gridString, 5, 0, 0);
        String expectedGridString =
                ". . O O O" + Grid.NEWLINE +
                ". . . O ." + Grid.NEWLINE +
                "O O . . ." + Grid.NEWLINE +
                ". O . . ." + Grid.NEWLINE +
                ". . . . .";
        Assert.assertEquals(expectedGridString, grid.toString());
    }
}
