package com.github.keeganwitt.katas.gameoflife.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameOfLifeStateChange {
    private Set<Map.Entry<Integer, Integer>> cellsToAdd = new HashSet<>();
    private Set<Map.Entry<Integer, Integer>> cellsToRemove = new HashSet<>();
    private int numLiveCellChange = 0;
    private Set<Map.Entry<Integer, Integer>> coordinatesOfCellsThatMightChange = new HashSet<>();

    public Set<Map.Entry<Integer, Integer>> getCellsToAdd() {
        return cellsToAdd;
    }

    public Set<Map.Entry<Integer, Integer>> getCellsToRemove() {
        return cellsToRemove;
    }

    public Set<Map.Entry<Integer, Integer>> getCoordinatesOfCellsThatMightChange() {
        return coordinatesOfCellsThatMightChange;
    }

    public int getNumLiveCellChange() {
        return numLiveCellChange;
    }

    public void incrementNumLiveCellChange(int i) {
        numLiveCellChange += i;
    }
}
