package com.github.keeganwitt.katas.gameoflife.model;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores the sparseGrid of cell states (replaces the typical 2D array).  Only explicitly stores live cells.
 * This again is optimized for large grids which are assumed to be mostly empty.  Some cases will be worse performance
 * because of this.  For example, This would be take more space than storing the grid as boolean[][] in the case of a
 * grid where every cell is alive.  However, cases like these are assumed to be exceptional.
 */
public class GridState implements Iterable<Map.Entry<Integer, Integer>> {
    private TreeMap<Integer, TreeMap<Integer, Boolean>> sparseGrid = new TreeMap<>();
    private int numLiveCells = 0;

    public void addCell(int row, int col) {
        if (sparseGrid.get(row) == null)
            sparseGrid.put(row, new TreeMap<Integer, Boolean>());
        Boolean prevValue = sparseGrid.get(row).put(col, true);
        if (prevValue == null)
            numLiveCells++;
    }

    public void removeCell(int row, int col) {
        Map<Integer, Boolean> r = sparseGrid.get(row);
        if (r != null) {
            Boolean prevValue = r.remove(col);
            if (prevValue != null)
                numLiveCells--;
            if (r.isEmpty())
                sparseGrid.remove(row);
        }
    }

    public Boolean get(int row, int col) {
        Map<Integer, Boolean> r = sparseGrid.get(row);
        if (r == null)
            return false;
        Boolean v = r.get(col);
        return (v != null) ? v : false;
    }

    public TreeMap<Integer, TreeMap<Integer, Boolean>> getSparseGrid() {
        return sparseGrid;
    }

    public int getNumLiveCells() {
        return numLiveCells;
    }

    @Override
    public Iterator<Map.Entry<Integer, Integer>> iterator() {
        return new GridStateIterator(this);
    }

    public static class GridStateIterator implements Iterator<Map.Entry<Integer, Integer>> {
        private GridState grid;
        private int fetchedCount = 0;
        private int row = -1;
        private int col = -1;

        public GridStateIterator(GridState grid) {
            this.grid = grid;
        }

        @Override
        public boolean hasNext() {
            return fetchedCount < grid.getNumLiveCells();
        }

        @Override
        public Map.Entry<Integer, Integer> next() {
            for (Integer row : grid.getSparseGrid().navigableKeySet()) {
                if (row < this.row) {
                    continue;
                } else if (row == this.row && this.col == grid.getSparseGrid().get(row).navigableKeySet().last()) {
                    this.col = -1;
                    continue;
                }
                for (Integer col : grid.getSparseGrid().get(row).navigableKeySet()) {
                    if (col <= this.col)
                        continue;
                    this.row = row;
                    this.col = col;
                    fetchedCount++;
                    return new AbstractMap.SimpleImmutableEntry<>(row, col);
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
