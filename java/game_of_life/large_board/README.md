# Large Board Game of Life

## Introduction

This solution optimizes for large grids.  The performance is affected only by the number of live cells.
The size of the grid is irrelevant because only the live cells are stored.
Also, since every sub-grid and cell can have it's next state computed independently of every other sub-grid and cell, Java 7's Fork-Join enabled great parallelization.

## Building and invocation

Java 7 or higher is required.  The input files are [plaintext](http://www.conwaylife.com/wiki/Plaintext) cells files.

First build the app by running

    gradlew shadowJar

Then to invoke, simply tell the app where to read the file and optionally how many generations to simulate, for example

    java -jar build/libs/gameOfLife-all.jar -i src/test/resources/oscillators/pulsar.grid -g 3

Or let Integer.MAX_VALUE generations run, like this

    java -jar build/libs/gameOfLife-all.jar -i src/test/resources/oscillators/pentadecathlon.grid -a

In some cases, you may need to adjust the position of the cells so that new cells don't get killed by falling off the edge, for example

    java -jar build/libs/gameOfLife-all.jar -i src/test/resources/oscillators/pulsar.cells -as 15 -x 1 -y 1

Full usage can be seen by running

    java -jar build/libs/gameOfLife-all.jar -h
