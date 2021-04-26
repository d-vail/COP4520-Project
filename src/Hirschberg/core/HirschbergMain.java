/**
 * HirschbergMain.java
**/

package core;

import core.Hirschberg;

import java.io.*;
import java.util.*;

public class HirschbergMain
{
    // public String seq1 = "GTTAATTCTACAGCAAAACGATCATATGCAGATCCGCAGTGGCCGGTAGACACACGTCCACCCCGCTGCTCTGTGACAGGGACTAAAGAGGCGAAGATTA";
    // public String seq2 = "TCGTGTGTGCCCCGTTATGGTCGAGTTCGGTCAGAGCGTCATTGCGAGTAGTCGTTTGCTTTCTCGAATTCCGAGCGATTAAGCGTGACAGTCCCAGCGA";
    // public String seq1 = "ATTAT";
    // public String seq2 = "TTATA";
    public String seq1 = new String();
    public String seq2 = new String();
    public int match = 1;
    public int mismatch = -1;
    public int gap = -1;
    public long startTime, stopTime;
    public long minTime, maxTime, avgTime;
    public long[] exeTime = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public HirschbergMain()
    {
        runSetup();
    }

    public static void main(String args[])
    {
        HirschbergMain hm = new HirschbergMain();

        return;
    }

    public void runSetup()
    {
        // First test is the ShortSequences
        System.out.println("First test is the short sequences.");
        // Read sequences from file
        try (BufferedReader br = new BufferedReader(new FileReader("ShortSequences.txt"))) {
            this.seq1 = br.readLine();
            this.seq2 = br.readLine();
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println();
        runTests();

        // // Second test is the LongSequences
        // System.out.println("Second test is the long sequences.");
        // // Read sequences from file
        // try (BufferedReader br = new BufferedReader(new FileReader("LongSequences.txt"))) {
        //     seq1 = br.readLine();
        //     seq2 = br.readLine();
        // } catch (Exception e) {
        //     System.out.println(e);
        // }
        //
        // System.out.println();
        // runTests();

        // // Third test is the LongestSequences
        // System.out.println("Third test is the longest sequences.");
        // // Read sequences from file
        // try (BufferedReader br = new BufferedReader(new FileReader("LongestSequences.txt"))) {
        //     seq1 = br.readLine();
        //     seq2 = br.readLine();
        // } catch (Exception e) {
        //     System.out.println(e);
        // }
        //
        // System.out.println();
        // runTests();
    }

    public void runTests()
    {
        // Hirschberg runs
        for (int i = 0; i < 10; i++)
        {
            this.startTime = System.nanoTime();
            Hirschberg hirschberg = new Hirschberg(seq1, seq2, match, mismatch, gap);
            hirschberg.HirschbergAlgorithm(hirschberg.seq1, hirschberg.seq2);
            this.stopTime = System.nanoTime();
            this.exeTime[i] = this.stopTime - this.startTime;
            System.out.println("Sequential execution time: " + this.exeTime[i] + " nanoseconds");
        }

        minTime = Integer.MAX_VALUE;
        maxTime = -1;
        avgTime = 0;
        for (int i = 0; i < 10; i++)
        {
            if (exeTime[i] < minTime) {
                minTime = exeTime[i];
            }
            if (exeTime[i] > maxTime) {
                maxTime = exeTime[i];
            }

            avgTime += exeTime[i];
        }
        avgTime /= 10;

        System.out.println();
        System.out.println("*******************************");
        System.out.println("Sequential metadata:");
        System.out.println("Sequential minimum execution time: " + minTime + " nanoseconds");
        System.out.println("Sequential maximum execution time: " + maxTime + " nanoseconds");
        System.out.println("Sequential average execution time: " + avgTime + " nanoseconds");
        System.out.println("*******************************");
        System.out.println();

        // // Hirschberg runs
        // for (int i = 0; i < 10; i++)
        // {
        //     this.startTime = System.nanoTime();
        //     HirschbergParallel hirschbergParallel = new HirschbergParallel(seq1, seq2, match, mismatch, gap);
        //     hirschbergParallel.HirschbergParallelAlgorithm(hirschbergParallel.seq1, hirschbergParallel.seq2);
        //     this.stopTime = System.nanoTime();
        //     this.exeTime[i] = this.stopTime - this.startTime;
        //     System.out.println("Parallel execution time: " + this.exeTime[i] + " nanoseconds");
        // }
        //
        // minTime = Integer.MAX_VALUE;
        // maxTime = -1;
        // avgTime = 0;
        // for (int i = 0; i < 10; i++)
        // {
        //     if (exeTime[i] < minTime) {
        //         minTime = exeTime[i];
        //     }
        //     if (exeTime[i] > maxTime) {
        //         maxTime = exeTime[i];
        //     }
        //
        //     avgTime += exeTime[i];
        // }
        // avgTime /= 10;
        //
        // System.out.println();
        // System.out.println("*******************************");
        // System.out.println("Parallel metadata:");
        // System.out.println("Parallel minimum execution time: " + minTime + " nanoseconds");
        // System.out.println("Parallel maximum execution time: " + maxTime + " nanoseconds");
        // System.out.println("Parallel average execution time: " + avgTime + " nanoseconds");
        // System.out.println("*******************************");
        // System.out.println();
    }
}
