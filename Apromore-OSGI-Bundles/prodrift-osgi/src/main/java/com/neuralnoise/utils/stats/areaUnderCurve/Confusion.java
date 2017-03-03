// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Confusion.java

package com.neuralnoise.utils.stats.areaUnderCurve;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

// Referenced classes of package auc:
//            PNPoint, AUCCalculator

public class Confusion extends Vector
{

    public Confusion(double d, double d1)
    {
        if(d < 1.0D || d1 < 1.0D)
        {
            totPos = 1.0D;
            totNeg = 1.0D;
            System.err.println((new StringBuilder()).append("ERROR: ").append(d).append(",").append(d1).append(" - ").append("Defaulting Confusion to 1,1").toString());
        } else
        {
            totPos = d;
            totNeg = d1;
        }
    }

    public void addPRPoint(double d, double d1)
        throws NumberFormatException
    {
        if(d > 1.0D || d < 0.0D || d1 > 1.0D || d1 < 0.0D)
            throw new NumberFormatException();
        double d2 = d * totPos;
        double d3 = (d2 - d1 * d2) / d1;
        PNPoint pnpoint = new PNPoint(d2, d3);
        if(!contains(pnpoint))
            add(pnpoint);
    }

    public void addROCPoint(double d, double d1)
        throws NumberFormatException
    {
        if(d > 1.0D || d < 0.0D || d1 > 1.0D || d1 < 0.0D)
            throw new NumberFormatException();
        double d2 = d1 * totPos;
        double d3 = d * totNeg;
        PNPoint pnpoint = new PNPoint(d2, d3);
        if(!contains(pnpoint))
            add(pnpoint);
    }

    public void addPoint(double d, double d1)
        throws NumberFormatException
    {
        if(d < 0.0D || d > totPos || d1 < 0.0D || d1 > totNeg)
            throw new NumberFormatException();
        PNPoint pnpoint = new PNPoint(d, d1);
        if(!contains(pnpoint))
            add(pnpoint);
    }

    public void sort()
    {
        if(AUCCalculator.DEBUG)
            System.out.println("--- Sorting the datapoints !!! ---");
        if(size() == 0)
        {
            System.err.println("ERROR: No data to sort....");
            return;
        }
        PNPoint apnpoint[] = new PNPoint[size()];
        int i = 0;
        for(; size() > 0; removeElementAt(0))
            apnpoint[i++] = (PNPoint)elementAt(0);

        Arrays.sort(apnpoint);
        for(int j = 0; j < apnpoint.length; j++)
            add(apnpoint[j]);

        PNPoint pnpoint;
        for(pnpoint = (PNPoint)elementAt(0); pnpoint.getPos() < 0.001D && pnpoint.getPos() > -0.001D; pnpoint = (PNPoint)elementAt(0))
            removeElementAt(0);

        double d = pnpoint.getNeg() / pnpoint.getPos();
        PNPoint pnpoint1 = new PNPoint(1.0D, d);
        if(!contains(pnpoint1) && pnpoint.getPos() > 1.0D)
            insertElementAt(pnpoint1, 0);
        pnpoint1 = new PNPoint(totPos, totNeg);
        if(!contains(pnpoint1))
            add(pnpoint1);
    }

    public void interpolate()
    {
        if(AUCCalculator.DEBUG)
            System.out.println("--- Interpolating New Points ---");
        if(size() == 0)
        {
            System.err.println("ERROR: No data to interpolate....");
            return;
        }
        for(int i = 0; i < size() - 1; i++)
        {
            PNPoint pnpoint = (PNPoint)elementAt(i);
            PNPoint pnpoint1 = (PNPoint)elementAt(i + 1);
            double d = pnpoint1.getPos() - pnpoint.getPos();
            double d1 = pnpoint1.getNeg() - pnpoint.getNeg();
            double d2 = d1 / d;
            double d3 = pnpoint.getPos();
            double d4 = pnpoint.getNeg();
            PNPoint pnpoint2;
            for(; Math.abs(pnpoint.getPos() - pnpoint1.getPos()) > 1.0009999999999999D; pnpoint = pnpoint2)
            {
                double d5 = d4 + ((pnpoint.getPos() - d3) + 1.0D) * d2;
                pnpoint2 = new PNPoint(pnpoint.getPos() + 1.0D, d5);
                insertElementAt(pnpoint2, ++i);
            }

        }

    }

    public double calculateAUCPR(double d)
    {
        if(AUCCalculator.DEBUG)
            System.out.println("--- Calculating AUC-PR ---");
        if(d < 0.0D || d > 1.0D)
        {
            System.err.println("ERROR: invalid minRecall, must be between 0 and 1 - returning 0");
            return 0.0D;
        }
        if(size() == 0)
        {
            System.err.println("ERROR: No data to calculate....");
            return 0.0D;
        }
        double d1 = d * totPos;
        int i = 0;
        PNPoint pnpoint = (PNPoint)elementAt(i);
        PNPoint pnpoint2 = null;
        try
        {
            for(; pnpoint.getPos() < d1; pnpoint = (PNPoint)elementAt(++i))
                pnpoint2 = pnpoint;

        }
        catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception)
        {
            System.out.println("ERROR: minRecall out of bounds - exiting...");
            System.exit(-1);
        }
        double d2 = (pnpoint.getPos() - d1) / totPos;
        double d3 = pnpoint.getPos() / (pnpoint.getPos() + pnpoint.getNeg());
        double d4 = d2 * d3;
        if(pnpoint2 != null)
        {
            double d5 = pnpoint.getPos() / totPos - pnpoint2.getPos() / totPos;
            double d6 = pnpoint.getPos() / (pnpoint.getPos() + pnpoint.getNeg()) - pnpoint2.getPos() / (pnpoint2.getPos() + pnpoint2.getNeg());
            double d8 = d6 / d5;
            double d10 = pnpoint2.getPos() / (pnpoint2.getPos() + pnpoint2.getNeg()) + (d8 * (d1 - pnpoint2.getPos())) / totPos;
            double d12 = 0.5D * d2 * (d10 - d3);
            d4 += d12;
        }
        d2 = pnpoint.getPos() / totPos;
        for(int j = i + 1; j < size(); j++)
        {
            PNPoint pnpoint3 = (PNPoint)elementAt(j);
            double d7 = pnpoint3.getPos() / totPos;
            double d9 = pnpoint3.getPos() / (pnpoint3.getPos() + pnpoint3.getNeg());
            double d11 = (d7 - d2) * d9;
            double d13 = 0.5D * (d7 - d2) * (d3 - d9);
            d4 += d11 + d13;
            PNPoint pnpoint1 = pnpoint3;
            d2 = d7;
            d3 = d9;
        }

        System.out.println((new StringBuilder()).append("Area Under the Curve for Precision - Recall is ").append(d4).toString());
        return d4;
    }

    public double calculateAUCROC()
    {
        if(AUCCalculator.DEBUG)
            System.out.println("--- Calculating AUC-ROC ---");
        if(size() == 0)
        {
            System.err.println("ERROR: No data to calculate....");
            return 0.0D;
        }
        PNPoint pnpoint = (PNPoint)elementAt(0);
        double d = pnpoint.getPos() / totPos;
        double d1 = pnpoint.getNeg() / totNeg;
        double d2 = 0.5D * d * d1;
        for(int i = 1; i < size(); i++)
        {
            PNPoint pnpoint2 = (PNPoint)elementAt(i);
            double d3 = pnpoint2.getPos() / totPos;
            double d4 = pnpoint2.getNeg() / totNeg;
            double d5 = (d3 - d) * d4;
            double d6 = 0.5D * (d3 - d) * (d4 - d1);
            d2 += d5 - d6;
            PNPoint pnpoint1 = pnpoint2;
            d = d3;
            d1 = d4;
        }

        d2 = 1.0D - d2;
        System.out.println((new StringBuilder()).append("Area Under the Curve for ROC is ").append(d2).toString());
        return d2;
    }

    public void writePRFile(String s)
    {
        System.out.println((new StringBuilder()).append("--- Writing PR file ").append(s).append(" ---").toString());
        if(size() == 0)
        {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(new File(s)));
            for(int i = 0; i < size(); i++)
            {
                PNPoint pnpoint = (PNPoint)elementAt(i);
                double d = pnpoint.getPos() / totPos;
                double d1 = pnpoint.getPos() / (pnpoint.getPos() + pnpoint.getNeg());
                printwriter.println((new StringBuilder()).append(d).append("\t").append(d1).toString());
            }

            printwriter.close();
        }
        catch(IOException ioexception)
        {
            System.out.println((new StringBuilder()).append("ERROR: IO Exception in file ").append(s).append(" - exiting...").toString());
            System.exit(-1);
        }
    }

    public void writeStandardPRFile(String s)
    {
        System.out.println((new StringBuilder()).append("--- Writing standardized PR file ").append(s).append(" ---").toString());
        if(size() == 0)
        {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(new File(s)));
            int i = 0;
            PNPoint pnpoint = null;
            PNPoint pnpoint1 = (PNPoint)elementAt(i);
            for(double d = 1.0D; d <= 100D; d++)
            {
                double d1 = pnpoint1.getPos() / totPos;
                double d2 = -1D;
                if(d / 100D <= d1)
                {
                    if(pnpoint == null)
                    {
                        d2 = pnpoint1.getPos() / (pnpoint1.getPos() + pnpoint1.getNeg());
                    } else
                    {
                        double d3 = pnpoint1.getPos() - pnpoint.getPos();
                        double d5 = pnpoint1.getNeg() - pnpoint.getNeg();
                        double d7 = d5 / d3;
                        double d9 = (d / 100D) * totPos;
                        double d11 = pnpoint.getNeg() + (d9 - pnpoint.getPos()) * d7;
                        d2 = d9 / (d9 + d11);
                    }
                    printwriter.println((new StringBuilder()).append(d / 100D).append("\t").append(d2).toString());
                    continue;
                }
                do
                {
                    pnpoint = pnpoint1;
                    pnpoint1 = (PNPoint)elementAt(++i);
                    d1 = pnpoint1.getPos() / totPos;
                } while(d / 100D > d1);
                double d4 = pnpoint1.getPos() - pnpoint.getPos();
                double d6 = pnpoint1.getNeg() - pnpoint.getNeg();
                double d8 = d6 / d4;
                double d10 = (d / 100D) * totPos;
                double d12 = pnpoint.getNeg() + (d10 - pnpoint.getPos()) * d8;
                d2 = d10 / (d10 + d12);
                printwriter.println((new StringBuilder()).append(d / 100D).append("\t").append(d2).toString());
            }

            printwriter.close();
        }
        catch(IOException ioexception)
        {
            System.out.println((new StringBuilder()).append("ERROR: IO Exception in file ").append(s).append(" - exiting...").toString());
            System.exit(-1);
        }
    }

    public void writeROCFile(String s)
    {
        System.out.println((new StringBuilder()).append("--- Writing ROC file ").append(s).append(" ---").toString());
        if(size() == 0)
        {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(new File(s)));
            printwriter.println("0\t0");
            for(int i = 0; i < size(); i++)
            {
                PNPoint pnpoint = (PNPoint)elementAt(i);
                double d = pnpoint.getPos() / totPos;
                double d1 = pnpoint.getNeg() / totNeg;
                printwriter.println((new StringBuilder()).append(d1).append("\t").append(d).toString());
            }

            printwriter.close();
        }
        catch(IOException ioexception)
        {
            System.out.println((new StringBuilder()).append("ERROR: IO Exception in file ").append(s).append(" - exiting...").toString());
            System.exit(-1);
        }
    }

    public String toString()
    {
        String s = "";
        s = (new StringBuilder()).append(s).append("TotPos: ").append(totPos).append(", TotNeg: ").append(totNeg).append("\n").toString();
        for(int i = 0; i < size(); i++)
            s = (new StringBuilder()).append(s).append(elementAt(i)).append("\n").toString();

        return s;
    }

    private double totPos;
    private double totNeg;
}