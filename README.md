# COP4520 Project: Sequence Alignment and Parallel Programming

## Team

- Tony Pham
- Austin Traub
- Daniel Vail

## Compile and Run Instructions

### Needleman-Wunsch

From the `src/needleman-wunsch` directory, compile and run from the command line with:

```
javac NeedlemanWunsch.java
java NeedlemanWunsch
```

To run an experiment, within the `src/needleman-wunsch` directory use:

```
javac Experiment1.java
java NeedlemanWunsch
```

Experiment #1 utilizes locks on each anti-diagonal.

```
javac Experiment1.java
java NeedlemanWunsch
```

Experiment #2 moves the thread creation to the outside of the loop. This ensures that it only occurs once.

```
javac Experiment2.java
java NeedlemanWunsch
```

Expreiment #3 makes use of Java's Executor Service to offload the creation and management of the threads.

```
javac Experiment3.java
java NeedlemanWunsch
```

Expreiment #4 makes use of Java's IntStream parallel() method.

```
javac Experiment4.java
java NeedlemanWunsch
```

Expreiment #5 attempts to parallelize the initialization of the score and traceback matrices.

```
javac Experiment5.java
java NeedlemanWunsch
```

### Hirschberg

From the `src/hirschberg` directory, compile and run from the command line with:

```
javac HirschbergMain.java
java HirschbergMain
```

### Smith-Waterman

From the `src/smith-waterman` directory, compile and run from the command line with:

```
javac SmithWaterman.java
java SmithWaterman <sequence_a> <sequence_b>
```

Try

```
java SmithWaterman pqraxabcstvq xyaxbacsll
```