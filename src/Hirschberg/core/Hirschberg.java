/**
 * Hirschberg.java
**/

package core;

import java.util.*;

public class Hirschberg
{
    public String seq1;
    public String seq2;
    public int match;
    public int mismatch;
    public int gap;
    public int lastColIndex, lastRowIndex;
    public int[][] scores;
    public char[][] traceback;

    public Hirschberg(String seq1, String seq2, int match, int mismatch, int gap)
    {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
        this.lastColIndex = seq1.length();
        this.lastRowIndex = seq2.length();

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
