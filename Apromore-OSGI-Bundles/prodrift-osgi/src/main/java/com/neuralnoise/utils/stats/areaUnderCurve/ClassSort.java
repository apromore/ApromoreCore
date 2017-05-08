// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClassSort.java

package com.neuralnoise.utils.stats.areaUnderCurve;


public class ClassSort
    implements Comparable
{

    public ClassSort(double d, int i)
    {
        val = d;
        classification = i;
    }

    public int getClassification()
    {
        return classification;
    }

    public double getProb()
    {
        return val;
    }

    public int compareTo(Object obj)
    {
        double d = ((ClassSort)obj).getProb();
        if(val < d)
            return -1;
        if(val > d)
            return 1;
        int i = ((ClassSort)obj).getClassification();
        if(i == classification)
            return 0;
        return classification <= i ? 1 : -1;
    }

    private double val;
    private int classification;
}