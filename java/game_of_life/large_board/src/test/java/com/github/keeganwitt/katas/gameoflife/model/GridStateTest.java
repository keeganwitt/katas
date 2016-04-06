package com.github.keeganwitt.katas.gameoflife.model;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GridStateTest {
    @Test
    public void testIterator() throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource("spaceships/glider.cells").toURI());
        Grid grid = Grid.createGrid(path.toFile(), Grid.DEFAULT_GRID_SIZE, Grid.DEFAULT_X_OFFSET, Grid.DEFAULT_Y_OFFSET);
        int i = 0;
        for (Map.Entry<Integer, Integer> cell : grid.getGridState()) {
            if (i == 0) {
                Assert.assertEquals(0, cell.getKey().intValue());
                Assert.assertEquals(1, cell.getValue().intValue());
            } else if (i == 1) {
                Assert.assertEquals(1, cell.getKey().intValue());
                Assert.assertEquals(2, cell.getValue().intValue());
            } else if (i == 2) {
                Assert.assertEquals(2, cell.getKey().intValue());
                Assert.assertEquals(0, cell.getValue().intValue());
            } else if (i == 3) {
                Assert.assertEquals(2, cell.getKey().intValue());
                Assert.assertEquals(1, cell.getValue().intValue());
            } else if (i == 4) {
                Assert.assertEquals(2, cell.getKey().intValue());
                Assert.assertEquals(2, cell.getValue().intValue());
            }
            if (i > 4)
                Assert.fail();
            i++;
        }
    }
}
