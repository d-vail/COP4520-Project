// Needleman-Wunsch Sequence Alignment

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;

class NeedlemanWunsch {
    public static void main(String[] args) {
        String s1 = new String();
        String s2 = new String();
        int match = 1;
        int mismatch = -1;
        int gap = -1;
        long startTime, stopTime;

        // Read sequences from file
        try (BufferedReader br = new BufferedReader(new FileReader("ShortSequences.txt"))) {
            s1 = br.readLine();
            s2 = br.readLine();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Run sequential
        startTime = System.nanoTime();
        SequentialNeedleWunsch sequentialNeedleWunsch = new SequentialNeedleWunsch(s1, s2, match, mismatch, gap);
        sequentialNeedleWunsch.fillMatrices();
        stopTime = System.nanoTime();
        long sequentialTime = stopTime - startTime;

        // Run parallel
        startTime = System.nanoTime();
        ParallelNeedleWunsch parallelNeedleWunsch = new ParallelNeedleWunsch(s1, s2, match, mismatch, gap);
        parallelNeedleWunsch.fillMatrices();
        stopTime = System.nanoTime();
        long parallelTime = stopTime - startTime;

        // Print alignments
        // sequentialNeedleWunsch.printOptimal();
        // System.out.println();
        // parallelNeedleWunsch.printOptimal();

        // Print matrices
        // sequentialNeedleWunsch.printMatrices();
        // System.out.println();
        // parallelNeedleWunsch.printMatrices();
        // System.out.println();

        // Check for differences between sequential and parallel matrices
        int[][] sequentialScores = sequentialNeedleWunsch.getScores();
        int[][] parallelScores = parallelNeedleWunsch.getScores();

        for (int i = 0; i < sequentialScores.length; i++) {
            for (int j = 0; j < parallelScores.length; j++) {
                if (sequentialScores[i][j] != parallelScores[i][j]) {
                    System.out.println("There is a mismatch at: " + i + ", " + j);
                    System.exit(-1);
                }
            }
        }

        // Print execution times
        System.out.println("Sequential execution time: " + sequentialTime + " nanoseconds");
        System.out.println("Parallel execution time:   " + parallelTime + " nanoseconds");
        System.out.println("Speed up: " + ((float) sequentialTime / parallelTime));
    }
}

class SequentialNeedleWunsch {
    String s1, s2;
    int[][] scores;
    char[][] traceback;
    int match, mismatch, gap;
    int lastRowIndex, lastColIndex;

    public SequentialNeedleWunsch(String s1, String s2, int match, int mismatch, int gap) {
        this.s1 = s1;
        this.s2 = s2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
        lastRowIndex = s2.length();
        lastColIndex = s1.length();

        scores = new int[lastRowIndex + 1][lastColIndex + 1];
        traceback = new char[lastRowIndex + 1][lastColIndex + 1];

        scores[0][0] = 0;
        traceback[0][0] = 'x';

        for (int i = 1; i <= lastColIndex; i++) {
            scores[0][i] = i * gap;
            traceback[0][i] = 'l';
        }

        for (int i = 1; i <= lastRowIndex; i++) {
            scores[i][0] = i * gap;
            traceback[i][0] = 'u';
        }
    }

    public void fillMatrices() {
        int left, up, diagonal;

        for (int i = 1; i <= lastRowIndex; i++) {
            for (int j = 1; j <= lastColIndex; j++) {
                left = scores[i][j - 1] + gap;
                up = scores[i - 1][j] + gap;

                if (s1.charAt(j - 1) == s2.charAt(i - 1)) {
                    diagonal = scores[i - 1][j - 1] + match;
                } else {
                    diagonal = scores[i - 1][j - 1] + mismatch;
                }

                scores[i][j] = Math.max(left, Math.max(up, diagonal));

                if (scores[i][j] == diagonal) {
                    traceback[i][j] = 'd';
                } else if (scores[i][j] == up) {
                    traceback[i][j] = 'u';
                } else {
                    traceback[i][j] = 'l';
                }
            }
        }
    }

    public void printMatrices() {
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                System.out.print(String.format("%3d ", scores[i][j]));
            }

            System.out.println();
        }

        System.out.println();

        for (int i = 0; i < traceback.length; i++) {
            for (int j = 0; j < traceback.length; j++) {
                System.out.print(String.format("%3c ", traceback[i][j]));
            }

            System.out.println();
        }
    }

    public int[][] getScores() {
        return scores;
    }

    public void printOptimal() {
        printOptimalHelper(lastColIndex, lastRowIndex);
    }

    private void printOptimalHelper(int i, int j) {
        if (traceback[i][j] == 'x') {
            return;
        } else if (traceback[i][j] == 'l') {
            printOptimalHelper(i, j - 1);
            System.out.println(s1.charAt(j - 1) + "-");
        } else if (traceback[i][j] == 'u') {
            printOptimalHelper(i - 1, j);
            System.out.println("-" + s2.charAt(i - 1));
        } else if (traceback[i][j] == 'd') {
            printOptimalHelper(i - 1, j - 1);
            System.out.println(s1.charAt(j - 1) + "" + s2.charAt(i - 1));
        }
    }
}

class ParallelNeedleWunsch implements Runnable {
    String s1, s2;
    int[][] scores;
    char[][] traceback;
    int match, mismatch, gap;
    int lastRowIndex, lastColIndex;
    int antiDiagonal;
    AtomicInteger currRow;
    AtomicBoolean waitToLoopAgain;
    AtomicBoolean waitToExecuteLoop;
    AtomicInteger threadCount;
    int numRows;
    int firstRowInAntiDiagonal, lastRowInAntiDiagonal;
    final int NUM_THREADS = 8;

    public ParallelNeedleWunsch(String s1, String s2, int match, int mismatch, int gap) {
        this.s1 = s1;
        this.s2 = s2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
        lastRowIndex = s2.length();
        lastColIndex = s1.length();

        scores = new int[lastRowIndex + 1][lastColIndex + 1];
        traceback = new char[lastRowIndex + 1][lastColIndex + 1];

        scores[0][0] = 0;
        traceback[0][0] = 'x';

        for (int i = 1; i <= lastColIndex; i++) {
            scores[0][i] = i * gap;
            traceback[0][i] = 'l';
        }

        for (int i = 1; i <= lastRowIndex; i++) {
            scores[i][0] = i * gap;
            traceback[i][0] = 'u';
        }

        antiDiagonal = 2; // Start at third anti-diagonal
        currRow = new AtomicInteger();
        waitToLoopAgain = new AtomicBoolean(true);
        waitToExecuteLoop = new AtomicBoolean(false);
        numRows = s1.length() + 1; // Number of rows in matrix
        currRow.set(getFirstRowInAntiDiagonal());
        threadCount = new AtomicInteger(0);
        firstRowInAntiDiagonal = getFirstRowInAntiDiagonal();
        lastRowInAntiDiagonal = antiDiagonal - firstRowInAntiDiagonal;
    }

    public void fillMatrices() {
        int numThreads = NUM_THREADS;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(this);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private int getFirstRowInAntiDiagonal() {
        return (antiDiagonal < numRows) ? 0 : antiDiagonal - numRows + 1;
    }

    public void run() {
        while (antiDiagonal < 2 * numRows - 1) {
            int row = currRow.getAndIncrement();

            while (row <= lastRowInAntiDiagonal) {
                int col = antiDiagonal - row;

                // System.out.println("Thread: " + Thread.currentThread().getId() + ", Cell: " +
                // row + ", " + col);

                if (row == 0 || col == 0) {
                    row = currRow.getAndIncrement();
                    continue;
                }
                
                calculateCell(row, col);
                row = currRow.getAndIncrement();
            }

            int loopAgainCount = threadCount.incrementAndGet();

            if (loopAgainCount == NUM_THREADS) {
                // System.out.println("Before Anti-diagonal: " + antiDiagonal);
                antiDiagonal++;
                // System.out.println("After Anti-diagonal: " + antiDiagonal);
                firstRowInAntiDiagonal = getFirstRowInAntiDiagonal();
                lastRowInAntiDiagonal = antiDiagonal - firstRowInAntiDiagonal;
                currRow.set(firstRowInAntiDiagonal);
                threadCount.set(0);
                waitToExecuteLoop.set(true);
                waitToLoopAgain.set(false);
            }

            while (waitToLoopAgain.get()) {
            }

            int executeLoopCount = threadCount.incrementAndGet();

            if (executeLoopCount == NUM_THREADS) {
                threadCount.set(0);
                waitToLoopAgain.set(true);
                waitToExecuteLoop.set(false);
            }

            while (waitToExecuteLoop.get()) {
            }
        }
    }

    private void calculateCell(int row, int col) {
        int left = scores[row][col - 1] + gap;
        int up = scores[row - 1][col] + gap;
        int diagonal;

        if (s1.charAt(col - 1) == s2.charAt(row - 1)) {
            diagonal = scores[row - 1][col - 1] + match;
        } else {
            diagonal = scores[row - 1][col - 1] + mismatch;
        }

        scores[row][col] = Math.max(left, Math.max(up, diagonal));

        if (scores[row][col] == diagonal) {
            traceback[row][col] = 'd';
        } else if (scores[row][col] == up) {
            traceback[row][col] = 'u';
        } else {
            traceback[row][col] = 'l';
        }
    }

    public void printMatrices() {
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                System.out.print(String.format("%3d ", scores[i][j]));
            }

            System.out.println();
        }

        System.out.println();

        for (int i = 0; i < traceback.length; i++) {
            for (int j = 0; j < traceback.length; j++) {
                System.out.print(String.format("%3c ", traceback[i][j]));
            }

            System.out.println();
        }
    }

    public int[][] getScores() {
        return scores;
    }

    public void printOptimal() {
        printOptimalHelper(lastColIndex, lastRowIndex);
    }

    private void printOptimalHelper(int i, int j) {
        if (traceback[i][j] == 'x') {
            return;
        } else if (traceback[i][j] == 'l') {
            printOptimalHelper(i, j - 1);
            System.out.println(s1.charAt(j - 1) + "-");
        } else if (traceback[i][j] == 'u') {
            printOptimalHelper(i - 1, j);
            System.out.println("-" + s2.charAt(i - 1));
        } else if (traceback[i][j] == 'd') {
            printOptimalHelper(i - 1, j - 1);
            System.out.println(s1.charAt(j - 1) + "" + s2.charAt(i - 1));
        }
    }
}