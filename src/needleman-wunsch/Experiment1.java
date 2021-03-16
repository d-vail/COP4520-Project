// Needleman-Wunsch Sequence Alignment

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class NeedlemanWunsch {
    public static void main(String[] args) {
        String s1 = "GTTAATTCTACAGCAAAACGATCATATGCAGATCCGCAGTGGCCGGTAGACACACGTCCACCCCGCTGCTCTGTGACAGGGACTAAAGAGGCGAAGATTA";
        String s2 = "TCGTGTGTGCCCCGTTATGGTCGAGTTCGGTCAGAGCGTCATTGCGAGTAGTCGTTTGCTTTCTCGAATTCCGAGCGATTAAGCGTGACAGTCCCAGCGA";
        // String s1 = "ATTAT";
        // String s2 = "TTATA";
        int match = 1;
        int mismatch = -1;
        int gap = -1;
        long startTime, stopTime;

        startTime = System.nanoTime();
        SequentialNeedleWunsch sequentialNeedleWunsch = new SequentialNeedleWunsch(s1, s2, match, mismatch, gap);
        sequentialNeedleWunsch.fillMatrices();
        sequentialNeedleWunsch.printOptimal();
        stopTime = System.nanoTime();
        long executionTime1 = stopTime - startTime;

        System.out.println();

        startTime = System.nanoTime();
        ParallelNeedleWunsch parallelNeedleWunsch = new ParallelNeedleWunsch(s1, s2, match, mismatch, gap);
        parallelNeedleWunsch.fillMatrices();
        parallelNeedleWunsch.printOptimal();
        stopTime = System.nanoTime();
        long executionTime2 = stopTime - startTime;

        System.out.println();
        System.out.println("Sequential execution time: " + executionTime1 + " nanoseconds");
        System.out.println("Parallel execution time:   " + executionTime2 + " nanoseconds");
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

                if (diagonal == scores[i][j]) {
                    traceback[i][j] = 'd';
                } else if (up == scores[i][j]) {
                    traceback[i][j] = 'u';
                } else {
                    traceback[i][j] = 'l';
                }
            }
        }
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
    }

    int numRows;
    int antiDiagonal;
    int currRow;
    AtomicInteger tempCurrRow = new AtomicInteger();

    public void fillMatrices() {
        numRows = s1.length() + 1;

        for (antiDiagonal = 1; antiDiagonal < 2 * numRows - 1; antiDiagonal++) {
            currRow = (antiDiagonal < numRows) ? 0 : antiDiagonal - numRows + 1;
            tempCurrRow.set(currRow);
            int numThreads = 2;
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
    }

    public void run() {
        int left, up, diagonal;

        while (tempCurrRow.get() <= antiDiagonal - currRow) {
            int temp = tempCurrRow.getAndIncrement();

            if (temp > antiDiagonal - currRow) {
                break;
            }

            int row = temp;
            int col = antiDiagonal - temp;

            if (row == 0 || col == 0) {
                continue;
            }

            left = scores[row][col - 1] + gap;
            up = scores[row - 1][col] + gap;

            if (s1.charAt(col - 1) == s2.charAt(row - 1)) {
                diagonal = scores[row - 1][col - 1] + match;
            } else {
                diagonal = scores[row - 1][col - 1] + mismatch;
            }

            scores[row][col] = Math.max(left, Math.max(up, diagonal));

            if (diagonal == scores[row][col]) {
                traceback[row][col] = 'd';
            } else if (up == scores[row][col]) {
                traceback[row][col] = 'u';
            } else {
                traceback[row][col] = 'l';
            }
        }
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