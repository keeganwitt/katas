package com.github.keeganwitt.katas.gameoflife.core;

import com.github.keeganwitt.katas.gameoflife.model.GameOfLifeStateChange;
import com.github.keeganwitt.katas.gameoflife.model.Grid;

import java.util.concurrent.RecursiveTask;

public class GameOfLifeTask extends RecursiveTask<GameOfLifeStateChange> {
    private static final int SERIAL_THRESHOLD = 10;  // TODO: is this the optimal threshold?
    private Grid grid;
    private int startRow;
    private int endRow;

    GameOfLifeTask(Grid grid, int startRow, int endRow) {
        this.grid = grid;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    protected GameOfLifeStateChange compute() {
        // TODO: dynamically size based on system resources
        if (endRow - startRow <= SERIAL_THRESHOLD)
            return GameOfLife.compute(grid, startRow, endRow);
        int halfRows = (endRow - startRow) / 2;
        GameOfLifeTask left = new GameOfLifeTask(grid, startRow, startRow + halfRows);
        left.fork();
        GameOfLifeTask right = new GameOfLifeTask(grid, startRow + halfRows + 1, endRow);
        GameOfLifeStateChange rightResult = right.compute();
        GameOfLifeStateChange leftResult = left.join();
        return combine(leftResult, rightResult);
    }

    static GameOfLifeStateChange combine(GameOfLifeStateChange left, GameOfLifeStateChange right) {
        GameOfLifeStateChange result = new GameOfLifeStateChange();
        result.getCellsToRemove().addAll(left.getCellsToRemove());
        result.getCellsToRemove().addAll(right.getCellsToRemove());
        result.getCellsToAdd().addAll(left.getCellsToAdd());
        result.getCellsToAdd().addAll(right.getCellsToAdd());
        result.incrementNumLiveCellChange(left.getNumLiveCellChange() + right.getNumLiveCellChange());
        result.getCoordinatesOfCellsThatMightChange().addAll(right.getCoordinatesOfCellsThatMightChange());
        return result;
    }
}
