/**
 * HirschbergParallel.java
**/

package core;

import java.util.*;

public class HirschbergParallel implements Runnable
{
    public String seq1;
    public String seq2;
    public Thread[] ts;
    public int match;
    public int mismatch;
    public int gap;

    public HirschbergParallel(String seq1, String seq2, int match, int mismatch, int gap)
    {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
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
