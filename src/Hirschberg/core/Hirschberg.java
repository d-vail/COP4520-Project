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
    public int split;
    public int[] retLine;
    public int[][] score;
    public String haRetLong;
    public String[] nwRet = new String[2];
    public String[] haRet = new String[2];
    public String[] optSeqs;

    public Hirschberg(String seq1, String seq2, int match, int mismatch, int gap)
    {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.match = match;
        this.mismatch = mismatch;
        this.gap = gap;
        this.lastColIndex = seq1.length();
        this.lastRowIndex = seq2.length();
        this.optSeqs = new String[2];
        this.optSeqs[0] = new String();
        this.optSeqs[1] = new String();
    }

    public String HirschbergAlgorithm(String seq1, String seq2)
    {
        char[] s1 = new char[seq1.length()];
        char[] s2 = new char[seq2.length()];

        if (seq1.length() == 0) {
            for (int i = 0; i < seq2.length(); i++) {
                s1[i] = '-';
                s2[i] = seq2.charAt(i);
            }

            haRetLong = new String(s1);
            split = haRetLong.length();
            haRetLong.concat(new String(s2));
        }
        else if (seq2.length() == 0) {
            for (int i = 0; i < seq1.length(); i++) {
                s1[i] = seq1.charAt(i);
                s2[i] = '-';
            }

            haRetLong = new String(s1);
            split = haRetLong.length();
            haRetLong.concat(new String(s2));
        }
        else if (seq1.length() == 1 || seq2.length() == 1) {
            nwRet = NeedlemanWunsch(seq1, seq2);

            haRetLong = new String(nwRet[0]);
            split = haRetLong.length();
            haRetLong.concat(nwRet[1]);
        }
        else {
            String seq1a = new String(seq1.substring(0, seq1.length() / 2));
            String seq1b = new String(seq1.substring(seq1.length() / 2 + 1));

            StringBuilder seq1bR = new StringBuilder(seq1.substring(seq1.length() / 2 + 1));
            seq1bR.reverse();
            String seq1bRev = seq1bR.toString();

            StringBuilder seq2bR = new StringBuilder(seq2);
            seq2bR.reverse();
            String seq2Rev = seq2bR.toString();

            int[] lScore = NWScore(seq1b, seq2);
            int[] rScoreRev = NWScore(seq1bRev, seq2Rev);
            int[] rScore = new int[rScoreRev.length];
            for (int i = 0, j = rScoreRev.length - 1; i < rScoreRev.length; i++, j--) {
                rScore[i] = rScoreRev[j];
            }

            int[] sumOfScores = new int[seq2.length()];
            for (int i = 0; i < seq2.length(); i++) {
                sumOfScores[i] = lScore[i] + rScore[i];
            }

            int max = Integer.MIN_VALUE;
            int mid = -1;
            for (int i = 0; i < sumOfScores.length; i++) {
                if (sumOfScores[i] > max) {
                    max = sumOfScores[i];
                    mid = i;
                }
            }

            String haRet0 = HirschbergAlgorithm(seq1a, seq2.substring(0, mid + 1));
            String haRet1 = HirschbergAlgorithm(seq1b, seq2.substring(mid + 1));
            haRet[0] = haRet0;
            haRet[1] = haRet1;

            haRetLong = new String(haRet[0]);
            split = haRetLong.length();
            haRetLong.concat(haRet[1]);
        }

        return haRetLong;
    }

    public int[] NWScore(String s1, String s2)
    {
        int left, up, diagonal;
        score = new int[s1.length()][s2.length()];
        retLine = new int[s2.length()];
        //System.out.println("score: i: " + s1.length() + " j: " + s2.length());
        //System.out.println("retLine: " + s2.length());

        score[0][0] = 0;
        for (int j = 1; j < s2.length(); j++) {
            score[0][j] = score[0][j - 1] + this.gap;
        }
        for (int i = 1; i < s1.length(); i++) {
            score[1][0] = score[0][0] + this.gap;

            for (int j = 1; j < s2.length(); j++) {
                up = score[0][j] + this.gap;
                left = score[1][j - 1] + this.gap;
                diagonal = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? score[0][j - 1] + this.match : score[0][j - 1] + this.mismatch;

                if (up > left && up > diagonal) {
                    score[1][j] = up;
                }
                else if (left > up && left > diagonal) {
                    score[1][j] = left;
                }
                else { // diagonal is the largest of the three
                    score[1][j] = diagonal;
                }
            }

            for (int j = 0; j < s2.length(); j++) {
                score[0][j] = score[1][j];
            }
        }
        for (int i = 0; i < s2.length(); i++) {
            if (s1.length() > 1) {
                retLine[i] = score[1][i];
            }
            else {
                retLine[i] = score[0][i];
            }
        }

        return retLine;
    }

    public String[] NeedlemanWunsch(String s1, String s2)
    {
        lastRowIndex = s2.length();
        lastColIndex = s1.length();

        int[][] scores = new int[lastRowIndex + 1][lastColIndex + 1];
        char[][] traceback = new char[this.seq2.length() + 1][this.seq1.length() + 1];

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

        genOptimalString(traceback, lastColIndex, lastRowIndex);

        return this.optSeqs;
    }

    public void genOptimalString(char[][] traceback, int lastColIndex, int lastRowIndex) {
        genOptimalStringHelper(traceback, lastColIndex, lastRowIndex);
        return;
    }

    private void genOptimalStringHelper(char[][] traceback, int i, int j) {
        //System.out.println("Test 0: i: " + i + " j: " + j);

        if (traceback[i][j] == 'x') {
            this.optSeqs[0] = "";
            this.optSeqs[1] = "";
            //System.out.println("Test 1");
            return;
        } else if (traceback[i][j] == 'l') {
            genOptimalStringHelper(traceback, i, j - 1);
            String seq = seq1.charAt(j - 1) + "-";
            //System.out.println(seq);
            //System.out.println("Test 2");
            System.out.println(seq1.charAt(j - 1) + "-");
            this.optSeqs[0] = this.optSeqs[0] + seq;
        } else if (traceback[i][j] == 'u') {
            genOptimalStringHelper(traceback, i - 1, j);
            String seq = "-" + seq2.charAt(j - 1);
            //System.out.println(seq);
            //System.out.println("Test 3");
            System.out.println("-" + seq2.charAt(i - 1));
            this.optSeqs[1] = seq + this.optSeqs[1];
        } else if (traceback[i][j] == 'd') {
            genOptimalStringHelper(traceback, i - 1, j - 1);
            String seq = seq1.charAt(j - 1) + "" + seq2.charAt(i - 1);
            //System.out.println(seq);
            //System.out.println("Test 4");
            System.out.println(seq1.charAt(j - 1) + "" + seq2.charAt(i - 1));
            this.optSeqs[0] = this.optSeqs[0] + seq1.charAt(j - 1);
            this.optSeqs[1] = this.optSeqs[1] + seq2.charAt(i - 1);
        }
    }

}
