# Kata for Root Insurance

## Initial thoughts

### Definition of Average speed

There are two ways to interpret "average speed". For example,

```
Trip Dan 07:15 07:45 17.3
Trip Dan 06:12 06:32 21.8
```

Will have trips of

```
30 minutes, 17.3 miles, 34.6 mph
20 minutes, 21.8 miles, 65.4 mph
```

We can calculate the mean of the trip average speeds,

```
((34.6 + 65.4) / 2) mph = 50 mph
```

I call this the "average trip speed". Or we could calculate the mean speed across all trips

```
39.1 miles / ((30 + 20) / 60) hours = 46.92 mph
```

I call this the "average speed". It's interesting to think about which result might be more useful, but since the
example says the answer as

```
Dan: 39 miles @ 47 mph
```

It's clear the author of the requirements defines "average speed" the same way I did. Usually it's desirable to avoid
average of averages, since it's usually misleading. If the trip average speed was something we wanted to understand
better, the median trip average speeds would be more useful than the mean.

### Driver commands

Another question that comes up is why have a driver command at all?  Why not just add the driver the first time a trip
is encountered in the file for a driver?  The requirements don't say, but one explanation might be that we want to
discard trips recorded previous to registration.  This imposes further sequence restrictions.

Additionally, the requirements don't say what to do when a duplicate driver command is encountered.  It seemed
reasonable to me to log a warning and continue processing.  Another approach might be to blacklist procesing trips for
that name, since you might be combining data from two people.

## Resource considerations

### Facts & assumptions

1. Drivers can have their results computed independently.
1. For each driver, the results can't be computed until until all the miles driven and time driven have been summed up.
1. Drivers aren't known in advance.
1. Trips aren't in any particular order. Driver additions can happen throughout the file, but it is not allowed to add
a trip before adding the driver.
1. The input file can be very large.

### Conclusions

1. If there are a large number of drivers, memory can be a constraint to hold all the totals. I will not address this
concern yet. If it is a problem, we can split the file between multiple processes/machines. For example, have each
process read a separate copy of the data. The data can be an entire copy, or preprocessed to be chunked so that a driver
never is in more than one chunk. If we use a complete copy and not chunks, each process can skip drivers whose hashed
name is outside the range the process instance handles. Hashed names would be better than using an alphabetic range
since names are probably not evenly distributed (that is, there's probably a lot more names that start with A than start
with Z). Or use Spark/Hadoop, which would make this trivial.  Or we could use a database to synchronize data read by the
different process instances.
1. Time spent on I/O reading the file can be significant if the file is large.  We could have multiple threads reading
the input file, but there's a balance to strike between parallelization vs resource contention between the threads.  The
physical architecture matters here too -- for example a spinning HDD might have significant more time spent moving the
head back and forth for the different threads, whereas that's not the case for a SSD.  I won't attempt to tune this at
this time.
1. If there are a lot of drivers, time spent on CPU calculating the results can be significant (though likely less of a
concern than the previous points).

## Object design

My initial thought was to have a separation between the parsing logic and the aggregation.  I originally used the same
class for aggregating driver results as the object returned by the parser.  To do a name lookup in O(1), I need
something like a HashMap rather than something like a TreeSet (which has lookup of O(log(n))), which means the name
isn't needed in the object -- but it will be useful later when we prepare the results.
I then decided to have separate classes for the results of the parsing and aggregation, so that if either the
aggregation or the input format changes, only one class is aware of the String parsing, and only DriverStatsAggregator
is aware of both sets of classes.  This has some apparent duplication, but it's the same kind of duplication you might
see between a DBO and a DTO, where the classes are intentionally separated.
I decided from the beginning that I wouldn't store all trips, and would instead only increment the counts, to reduce the
memory required.
I viewed the rounding as part of the rules the aggregation layer, so it is done outside DriverStats and in the
DriverStatsAggregator, which does the aggregation end enforces the rules.  Arguably this could have also been in a
separate utility class, but there are a fair number of classes already, and this class would be tightly coupled to
DriverStatsAggregator anyway.
Lastly, it was my goal to minimize the logic present in Main, and delegate all logic to the appropriate class.

## Other notes

I did TDD on everything except Main, I wrote the test for that after writing the class.


## Usage

Build a runnable uber-jar with `gradlew shadowJar`.
Run all the tests with `gradlew check`
