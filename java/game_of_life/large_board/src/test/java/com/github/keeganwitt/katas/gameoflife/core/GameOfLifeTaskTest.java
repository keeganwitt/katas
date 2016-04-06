package com.github.keeganwitt.katas.gameoflife.core;

import com.github.keeganwitt.katas.gameoflife.model.GameOfLifeStateChange;
import org.junit.Assert;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameOfLifeTaskTest {

    @Test
    public void testCombinesResults() {
        Set<Map.Entry<Integer, Integer>> expectedCellsToRemove = new HashSet<>();
        Set<Map.Entry<Integer, Integer>> expectedCellsToAdd = new HashSet<>();
        Set<Map.Entry<Integer, Integer>> expectedCoordinatesOfCellsThatMightChange = new HashSet<>();
        GameOfLifeStateChange left = new GameOfLifeStateChange();
        Map.Entry<Integer, Integer> cell = new AbstractMap.SimpleImmutableEntry<>(0, 0);
        left.getCellsToRemove().add(cell);
        expectedCellsToRemove.add(cell);
        cell = new AbstractMap.SimpleImmutableEntry<>(1, 1);
        left.getCellsToAdd().add(cell);
        expectedCellsToAdd.add(cell);
        cell = new AbstractMap.SimpleImmutableEntry<>(2, 2);
        left.getCoordinatesOfCellsThatMightChange().add(cell);
        expectedCoordinatesOfCellsThatMightChange.add(cell);
        left.incrementNumLiveCellChange(1);
        GameOfLifeStateChange right = new GameOfLifeStateChange();
        cell = new AbstractMap.SimpleImmutableEntry<>(3, 3);
        right.getCellsToRemove().add(cell);
        expectedCellsToRemove.add(cell);
        cell = new AbstractMap.SimpleImmutableEntry<>(4, 4);
        right.getCellsToAdd().add(cell);
        expectedCellsToAdd.add(cell);
        cell = new AbstractMap.SimpleImmutableEntry<>(5, 5);
        right.getCoordinatesOfCellsThatMightChange().add(cell);
        expectedCoordinatesOfCellsThatMightChange.add(cell);
        right.incrementNumLiveCellChange(1);

        GameOfLifeStateChange result = GameOfLifeTask.combine(left, right);
        result.getCellsToRemove().containsAll(expectedCellsToRemove);
        result.getCellsToAdd().containsAll(expectedCellsToAdd);
        result.getCoordinatesOfCellsThatMightChange().containsAll(expectedCoordinatesOfCellsThatMightChange);
        Assert.assertEquals(2, result.getNumLiveCellChange());
    }

    // TODO: how to test compute()?
}
