/**
 * HirschbergMain.java
**/

package core;

import java.util.*;

public class HirschbergMain
{
    // public String seq1 = "GTTAATTCTACAGCAAAACGATCATATGCAGATCCGCAGTGGCCGGTAGACACACGTCCACCCCGCTGCTCTGTGACAGGGACTAAAGAGGCGAAGATTA";
    // public String seq2 = "TCGTGTGTGCCCCGTTATGGTCGAGTTCGGTCAGAGCGTCATTGCGAGTAGTCGTTTGCTTTCTCGAATTCCGAGCGATTAAGCGTGACAGTCCCAGCGA";
    public String seq1 = "ATTAT";
    public String seq2 = "TTATA";
    public int match = 1;
    public int mismatch = -1;
    public int gap = -1;
    public long startTime, stopTime;
    public long minTime, maxTime, avgTime;
    public long exeTime[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public Hirschberg hirschberg;
    public HirschbergParallel hirschbergParallel;

    public static void main(String args[])
    {
        System.out.println();

        // Hirschberg runs
        for (int i = 0; i < 10; i++)
        {
            startTime = System.nanoTime();
            hirschberg = Hirschberg(seq1, seq2, match, mismatch, gap);
            hirschberg.HirschbergAlgorithm();
            stopTime = System.nanoTime();
            exeTime[i] = stopTime - startTime;
            System.out.println("Sequential execution time: " + exeTime[i] + " nanoseconds");
            hirschberg.printOptimal()
        }

        minTime = 1000000000000;
        maxTime = 0;
        for (int i = 0; i < 10; i++)
        {
            if (exeTime[i] < minTime)
                minTime = exeTime[i];
            else if (exeTime[i] > maxTime)
                maxTime = exeTime[i];

            avgTime += avgTime;
        }
        avgTime /= 10;

        System.out.println();
        System.out.println("*******************************");
        System.out.println("Sequential metadata:")
        System.out.println("Sequential minimum execution time: " + minTime + " nanoseconds");
        System.out.println("Sequential maximum execution time: " + maxTime + " nanoseconds");
        System.out.println("Sequential average execution time: " + avgTime + " nanoseconds");
        System.out.println("*******************************");
        System.out.println();

        // HirschbergParallel runs
        for (int i = 0; i < 10; i++)
        {
            startTime = System.nanoTime();
            hirschbergParallel = HirschbergParallel(seq1, seq2);
            hirschbergParallel.HirschbergParallelAlgorithm();
            stopTime = System.nanoTime();
            exeTime[i] = stopTime - startTime;
            System.out.println("Parallel execution time: " + exeTime[i] + " nanoseconds");
            hirschbergParallel.printOptimal();
        }

        minTime = 1000000000000;
        maxTime = 0;
        for (int i = 0; i < 10; i++)
        {
            if (exeTime[i] < minTime)
                minTime = exeTime[i];
            else if (exeTime[i] > maxTime)
                maxTime = exeTime[i];

            avgTime += avgTime;
        }
        avgTime /= 10;

        System.out.println();
        System.out.println("*******************************");
        System.out.println("Parallel metadata:")
        System.out.println("Parallel minimum execution time: " + minTime + " nanoseconds");
        System.out.println("Parallel maximum execution time: " + maxTime + " nanoseconds");
        System.out.println("Parallel average execution time: " + avgTime + " nanoseconds");
        System.out.println("*******************************");
        System.out.println();
    }
}
