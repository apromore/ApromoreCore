// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ReadList.java

package com.neuralnoise.utils.stats.areaUnderCurve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

// Referenced classes of package auc:
//            ClassSort, Confusion, PNPoint, AUCCalculator

public class ReadList
{

    public ReadList()
    {
    }

    public static ClassSort[] convertList(LinkedList linkedlist)
    {
        ClassSort aclasssort[] = new ClassSort[linkedlist.size()];
        for(int i = 0; i < aclasssort.length; i++)
            aclasssort[i] = (ClassSort)linkedlist.removeFirst();

        Arrays.sort(aclasssort);
        return aclasssort;
    }

    public static Confusion accuracyScoreAllSplits(ClassSort aclasssort[], int i, int j)
    {
        Arrays.sort(aclasssort);
        for(int k = aclasssort.length - 1; k >= aclasssort.length - 20; k--);
        Confusion confusion = new Confusion(i, j);
        int l = 0;
        double d = aclasssort[aclasssort.length - 1].getProb();
        int i1 = aclasssort[aclasssort.length - 1].getClassification();
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        double ad[] = new double[aclasssort.length];
        int ai1[] = new int[aclasssort.length];
        for(int k1 = 0; k1 < aclasssort.length; k1++)
        {
            ad[k1] = aclasssort[k1].getProb();
            ai1[k1] = aclasssort[k1].getClassification();
        }

        LinkedList linkedlist = new LinkedList();
        for(int l1 = aclasssort.length - 2; l1 >= 0; l1--)
        {
            int j1 = aclasssort[l1].getClassification();
            double d1 = aclasssort[l1].getProb();
            if(i1 == 1 && 0 == j1)
            {
                if(aclasssort[l1 + 1].getProb() <= d1 && aclasssort[l1 + 1].getProb() <= d1)
                    System.out.println("Bad");
                int ai[] = fastAccuracy(ad, ai1, d);
                confusion.addPoint(ai[0], ai[1]);
            }
            l += j1;
            d = d1;
            i1 = j1;
        }

        return confusion;
    }

    public static int[] fastAccuracy(double ad[], int ai[], double d)
    {
        int ai1[] = new int[4];
        for(int i = 0; i < ai1.length; i++)
            ai1[i] = 0;

        for(int j = 0; j < ad.length; j++)
        {
            if(ad[j] >= d)
            {
                if(ai[j] == 1)
                    ai1[0]++;
                else
                    ai1[1]++;
                continue;
            }
            if(ai[j] == 1)
                ai1[2]++;
            else
                ai1[3]++;
        }

        return ai1;
    }

    public static Confusion readFile(String s, String s1)
    {
        int i = 0;
        int j = 0;
        int k = 0;
        LinkedList linkedlist = new LinkedList();
        Object obj = null;
        try
        {
            for(BufferedReader bufferedreader = new BufferedReader(new FileReader(new File(s))); bufferedreader.ready();)
            {
                String s2 = bufferedreader.readLine();
                if(!AUCCalculator.DEBUG);
                StringTokenizer stringtokenizer = new StringTokenizer(s2, "\t ,");
                try
                {
                    double d = Double.parseDouble(stringtokenizer.nextToken());
                    int l = Integer.parseInt(stringtokenizer.nextToken());
                    linkedlist.add(new ClassSort(d, l));
                    if(!AUCCalculator.DEBUG);
                    if(!AUCCalculator.DEBUG);
                }
                catch(NumberFormatException numberformatexception)
                {
                    System.err.println("...skipping bad input line (bad numbers)");
                }
                catch(NoSuchElementException nosuchelementexception1)
                {
                    System.err.println("...skipping bad input line (missing data)");
                }
            }

        }
        catch(FileNotFoundException filenotfoundexception)
        {
            System.err.println((new StringBuilder()).append("ERROR: File ").append(s).append(" not found - exiting...").toString());
            System.exit(-1);
        }
        catch(NoSuchElementException nosuchelementexception)
        {
            System.err.println("...incorrect fileType argument, either PR or ROC - exiting");
            System.exit(-1);
        }
        catch(IOException ioexception)
        {
            System.err.println((new StringBuilder()).append("ERROR: IO Exception in file ").append(s).append(" - exiting...").toString());
            System.exit(-1);
        }
        ClassSort aclasssort[] = convertList(linkedlist);
        ArrayList arraylist = new ArrayList();
        double d1 = aclasssort[aclasssort.length - 1].getProb();
        if(aclasssort[aclasssort.length - 1].getClassification() == 1)
            i++;
        else
            j++;
        k++;
        for(int i1 = aclasssort.length - 2; i1 >= 0; i1--)
        {
            double d2 = aclasssort[i1].getProb();
            int j1 = aclasssort[i1].getClassification();
            System.out.println((new StringBuilder()).append(d2).append(" ").append(j1).toString());
            if(d2 != d1)
                arraylist.add(new PNPoint(i, j));
            d1 = d2;
            if(j1 == 1)
                i++;
            else
                j++;
            k++;
        }

        arraylist.add(new PNPoint(i, j));
        Confusion confusion = new Confusion(i, j);
        PNPoint pnpoint;
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); confusion.addPoint(pnpoint.getPos(), pnpoint.getNeg()))
            pnpoint = (PNPoint)iterator.next();

        confusion.sort();
        confusion.interpolate();
        return confusion;
    }

    public static final int TP = 0;
    public static final int FP = 1;
    public static final int FN = 2;
    public static final int TN = 3;
}