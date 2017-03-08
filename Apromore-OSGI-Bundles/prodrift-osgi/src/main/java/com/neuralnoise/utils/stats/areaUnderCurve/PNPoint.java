// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PNPoint.java

package com.neuralnoise.utils.stats.areaUnderCurve;


public class PNPoint
    implements Comparable
{

    public PNPoint(double d, double d1)
    {
        if(d < 0.0D || d1 < 0.0D)
        {
            pos = 0.0D;
            neg = 0.0D;
            System.err.println((new StringBuilder()).append("ERROR: ").append(d).append(",").append(d1).append(" - Defaulting ").append("PNPoint to 0,0").toString());
        } else
        {
            pos = d;
            neg = d1;
        }
    }

    public double getPos()
    {
        return pos;
    }

    public double getNeg()
    {
        return neg;
    }

    public int compareTo(Object obj)
    {
        if(obj instanceof PNPoint)
        {
            PNPoint pnpoint = (PNPoint)obj;
            if(pos - pnpoint.pos > 0.0D)
                return 1;
            if(pos - pnpoint.pos < 0.0D)
                return -1;
            if(neg - pnpoint.neg > 0.0D)
                return 1;
            return neg - pnpoint.neg >= 0.0D ? 0 : -1;
        } else
        {
            return -1;
        }
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof PNPoint)
        {
            PNPoint pnpoint = (PNPoint)obj;
            if(Math.abs(pos - pnpoint.pos) > 0.001D)
                return false;
            return Math.abs(neg - pnpoint.neg) <= 0.001D;
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        String s = "";
        s = (new StringBuilder()).append(s).append("(").append(pos).append(",").append(neg).append(")").toString();
        return s;
    }

    private double pos;
    private double neg;
}