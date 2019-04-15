# HTTP Log Monitor

## Usage

### System requirements

* Java 8

### Building the runnable jar

Run `gradlew clean shadowJar`

### Executing the program

The program can be run with `cat sample.csv | java -jar http-log-monitor-all.jar`.

You can also specify properties to control behavior in a properties file in the same directory as the jar. For example,

_http-log-monitor.properties_ 

```properties
secondsBetweenFrequencyReports=10
topSegmentsLimit=3
requestsPerSecondForAlert=10
```

If you provide a properties file, you must run the command in the same directory as the jar currently, so that the properties file can be found.

### Generating input

I've also written a script to create data for load testing. This can be run with

```shell
groovy generate-csv.groovy 1000000 > big-sample.csv
```

The argument to the script is how many log events to generate.

## Performance

### Concurrency opportunities

While I decided to start with a single threaded application, then add threading where it would provide value, here are some opportunities I identified for when I am at that stage.

Each 10 second bucket can have its results computed independently (there is no dependency between the results). This present an opportunity for concurrency. But this will add some effort to retain the ordering of the buckets (I assume this is a requirement). I.e., I don't want to print a report for a time bucket before I've printed the reports for the buckets preceding that bucket in time. Intuitively, IO is likely a higher cost than the report computation, and maintaining the time ordering will introduce memory overhead. For the initial solution, I will not parallelize this unless/until benchmarks indicate it might be advantageous.

Reading the input and reporting the results can also be done independently (i.e. I can continue reading the next log event, even if I haven't yet computed the report for the previous 10 second time bucket). This is discussed in more detail in the following section.

## IO

Time spent on I/O reading the file/stream can be significant. With a file, I could have multiple threads reading the input file, but there's a balance to strike between parallelization and resource contention between threads. The physical architecture matters here too -- for example a spinning HDD might have significant more time spent moving the head for the different threads, whereas an SSD would fare better.

I could split the file into chunks and have parallel processes work on each chunk. If I chunk on number of lines, the 10 second consideration could be messed up if a chunk boundary separated 2 log events into separate chunks. I could solve this by chunking by time rather than lines, if the every 10 second aggregate picture is an important requirement. If this is run as a batch process, frameworks like Hadoop or Spark can be used to aid in the chunking and parallelization. However, this approach only works if processing the logs as an offline process.

A way to reduce the IO time is to push as much logic out of the thread that is reading the file/stream. The smallest amount of work I can have in the reader is to grab a line of text, and hand it off to a processing thread. However, the library I chose (Commons CSV) to save engineering effort would require me to create a separate stream for each line. I also have a classic producer/consumer problem here. It wouldn't be worth moving the parsing out of the reading thread if the processing thread consumes slower than the reading thread can produce (this is something that will have to be measured to confirm). For the time being, I'll leave the parsing in the input thread.

## Future enhancements

* Use a database for aggregating metrics, so that log messages parsed after the aggregating window has already passed that time still get counted.
* Separate reading logic from parsing logic (move parsing to processing thread) if appropriate.
* Adding an event to metrics is currently a blocking operation. The reporting can be pulled into a separate thread so the reading and parsing can continue independently.
* Currently the LogEventHandler both aggregates metrics and writes report results. This should probably be split up.
* Make the input log format customizable (or at least offer a selection of standard formats).
* More exception handling in parser (e.g. negative numbers, numbers outside HTTP code range, etc).
* Allow user to specify character encoding instead of using system default.
* Make metrics more generic to make it easier to add new metrics. Probably change the inner map key to allow nullable fields with _equals()_ and _hashCode()_.
* Add additional metrics, like
  * Aggregated count by user of 403s. This might be useful in knowing whether an attack is occurring. The section might be useful as well, as a future improvement.
  * Aggregated count by section of 500s. This might be useful to point out where further troubleshooting might be needed.

## Profiling

Here are the top results from running against a sample 5 million record CSV when single-threaded.

```shell
cat big-sample.csv | java -Xmx1024m -agentlib:hprof=cpu=samples,depth=100,interval=1,monitor=y,format=a,lineno=y,thread=y,file=java.hprof.txt -jar build/libs/http-log-monitor-all.jar
```

<pre> 
CPU SAMPLES BEGIN (total = 13322) Wed Apr 03 21:50:56 2019
rank   self  accum   count trace method
   1 28.26% 28.26%    3765 301409 java.io.FileOutputStream.writeBytes
   2 19.94% 48.20%    2656 301515 org.apache.commons.csv.Lexer.parseEncapsulatedToken
   3  8.32% 56.52%    1109 301450 org.apache.commons.csv.Lexer.parseSimpleToken
   4  8.10% 64.62%    1079 301502 org.apache.commons.csv.Lexer.nextToken
   5  3.59% 68.21%     478 301524 java.lang.String.split
   6  2.39% 70.61%     319 301431 org.apache.commons.csv.CSVParser.nextRecord
   7  1.86% 72.47%     248 301504 org.apache.commons.csv.Lexer.nextToken
   8  1.59% 74.06%     212 301495 java.io.FileInputStream.readBytes
   9  1.35% 75.41%     180 301526 java.util.AbstractCollection.toArray
  10  1.28% 76.69%     170 301518 java.util.TreeMap.getEntryUsingComparator
  11  1.16% 77.85%     155 301543 java.util.TreeMap.getEntryUsingComparator
  12  0.99% 78.84%     132 301569 java.lang.Iterable.forEach
  13  0.86% 79.70%     115 301525 java.util.TreeMap.getFirstEntry
  14  0.83% 80.53%     110 301417 java.lang.Iterable.forEach
  15  0.78% 81.31%     104 301489 org.apache.commons.csv.CSVParser$CSVRecordIterator.getNextRecord
  16  0.77% 82.07%     102 301433 java.util.TreeMap.getEntryUsingComparator
  17  0.59% 82.67%      79 301458 java.io.FileInputStream.readBytes
  18  0.53% 83.20%      71 301523 java.util.TreeMap.put
  19  0.41% 83.61%      55 301558 java.util.TreeMap.getEntry
  20  0.40% 84.01%      53 301444 org.apache.commons.csv.CSVParser.nextRecord
...
</pre>

### Possibly Java bug

I also attempted to get a more accurate profile by running

```shell
cat sample.csv | java -agentlib:hprof=cpu=times,depth=100,monitor=y,format=a,lineno=y,thread=y,file=java.hprof.txt -jar build/libs/http-log-monitor-all.jar
```

But it seems I may have hit [JDK-8027934](https://bugs.openjdk.java.net/browse/JDK-8027934), as I got this exception

<pre>
Exception in thread "main" java.lang.NoClassDefFoundError: java/lang/invoke/LambdaForm$MH
        at com.sun.demo.jvmti.hprof.Tracker.nativeCallSite(Native Method)
        at com.sun.demo.jvmti.hprof.Tracker.CallSite(Tracker.java:99)
        at java.lang.invoke.InvokerBytecodeGenerator.emitNewArray(InvokerBytecodeGenerator.java:889)
        at java.lang.invoke.InvokerBytecodeGenerator.generateCustomizedCodeBytes(InvokerBytecodeGenerator.java:688)
        at java.lang.invoke.InvokerBytecodeGenerator.generateCustomizedCode(InvokerBytecodeGenerator.java:618)
        at java.lang.invoke.LambdaForm.compileToBytecode(LambdaForm.java:654)
        at java.lang.invoke.LambdaForm.prepare(LambdaForm.java:635)
        at java.lang.invoke.MethodHandle.<init>(MethodHandle.java:461)
        at java.lang.invoke.BoundMethodHandle.<init>(BoundMethodHandle.java:58)
        at java.lang.invoke.BoundMethodHandle$Species_L.<init>(BoundMethodHandle.java:211)
        at java.lang.invoke.BoundMethodHandle$Species_L.copyWith(BoundMethodHandle.java:228)
        at java.lang.invoke.MethodHandle.asCollector(MethodHandle.java:1002)
        at java.lang.invoke.MethodHandleImpl$AsVarargsCollector.<init>(MethodHandleImpl.java:460)
        at java.lang.invoke.MethodHandleImpl$AsVarargsCollector.<init>(MethodHandleImpl.java:454)
        at java.lang.invoke.MethodHandleImpl.makeVarargsCollector(MethodHandleImpl.java:445)
        at java.lang.invoke.MethodHandle.setVarargs(MethodHandle.java:1325)
        at java.lang.invoke.MethodHandles$Lookup.getDirectMethodCommon(MethodHandles.java:1670)
        at java.lang.invoke.MethodHandles$Lookup.getDirectMethod(MethodHandles.java:1605)
        at java.lang.invoke.MethodHandles$Lookup.findStatic(MethodHandles.java:781)
        at java.lang.invoke.MethodHandleImpl$Lazy.<clinit>(MethodHandleImpl.java:627)
        at java.lang.invoke.MethodHandleImpl.varargsArray(MethodHandleImpl.java:1506)
        at java.lang.invoke.MethodHandleImpl.varargsArray(MethodHandleImpl.java:1623)
        at java.lang.invoke.MethodHandle.asCollector(MethodHandle.java:999)
        at java.lang.invoke.MethodHandleImpl$AsVarargsCollector.<init>(MethodHandleImpl.java:460)
        at java.lang.invoke.MethodHandleImpl$AsVarargsCollector.<init>(MethodHandleImpl.java:454)
        at java.lang.invoke.MethodHandleImpl.makeVarargsCollector(MethodHandleImpl.java:445)
        at java.lang.invoke.MethodHandle.setVarargs(MethodHandle.java:1325)
        at java.lang.invoke.MethodHandles$Lookup.getDirectMethodCommon(MethodHandles.java:1670)
        at java.lang.invoke.MethodHandles$Lookup.getDirectMethod(MethodHandles.java:1605)
        at java.lang.invoke.MethodHandles$Lookup.findStatic(MethodHandles.java:781)
        at java.lang.invoke.CallSite.<clinit>(CallSite.java:226)
        at java.lang.invoke.MethodHandleNatives.linkCallSiteImpl(MethodHandleNatives.java:307)
        at java.lang.invoke.MethodHandleNatives.linkCallSite(MethodHandleNatives.java:297)
        at com.github.keeganwitt.katas.httpmonitor.Main.run(Main.java:75)
        at com.github.keeganwitt.katas.httpmonitor.Main.main(Main.java:25)
HPROF ERROR: Unexpected Exception found afterward [hprof_util.c:494]
HPROF TERMINATED PROCESS
</pre>

It seems they Oracle has deferred this issue as hprof is viewed as infrequently used and lower priority. In fact, they [removed it](https://openjdk.java.net/jeps/240) in Java 9, which I was unaware of.

# VisualVM Startup Profiler

It was at that point I learned of the nifty [Startup Profiler](https://visualvm.github.io/startupprofiler.html) plugin for VisualVM that provide it's own agent, which can be run like

```cmd
# CPU
type big-sample.csv | java -Xmx1024m -agentpath:C:/bin/VisualVM/profiler/lib/deployed/jdk16/windows-amd64/profilerinterface.dll=C:\bin\VisualVM\profiler\lib,5140 -jar build\libs\http-log-monitor-all.jar
# Memory
type big-sample.csv | java -Xmx1024m -agentpath:C:/bin/VisualVM/profiler/lib/deployed/jdk16/windows-amd64/profilerinterface.dll=C:\bin\VisualVM\profiler\lib,5140 -jar build\libs\http-log-monitor-all.jar
```

### Conclusions

From these results, I can see most time is spent in getting input. Specifically parsing (about 50% of the total time), not the reading IO (only about 10% was spent on reading itself), and not in processing (only about 31.8% of the time was spent processing and writing to the output stream). The best thing to increase throughput would be to separate the reading from the parsing, so that reads can continue while the slower work of parsing continues. I could separate the processing into a separate thread (and I thought about doing this), but the gains would likely not be significant, since the bottleneck would still be in the same thread that feeds it. Logically, the flow is `Read -> Parse -> Aggregate -> Report`. I need to switch libraries I use to parse the CSV so that reading and parsing can be separated into separate threads.

The application can comfortably fit in 200MB of heap. Less than 10MB of memory was in S0, S1, and old gen for the large run, most heap was in Eden, so this should scale well under load (assuming I've generated reasonably realistic test data).
