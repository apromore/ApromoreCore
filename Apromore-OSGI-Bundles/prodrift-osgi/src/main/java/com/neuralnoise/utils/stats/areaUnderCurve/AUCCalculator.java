// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AUCCalculator.java

package com.neuralnoise.utils.stats.areaUnderCurve;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

// Referenced classes of package auc:
//            Confusion, ReadList

public class AUCCalculator
{

    public AUCCalculator()
    {
    }

    public static void main(String args[])
    {
        readArgs(args);
        Confusion confusion;
        if(fileType.equalsIgnoreCase("list"))
            confusion = ReadList.readFile(fileName, fileType);
        else
            confusion = readFile(fileName, fileType, posCount, negCount);
        confusion.writePRFile((new StringBuilder()).append(fileName).append(".pr").toString());
        confusion.writeStandardPRFile((new StringBuilder()).append(fileName).append(".spr").toString());
        confusion.writeROCFile((new StringBuilder()).append(fileName).append(".roc").toString());
        confusion.calculateAUCPR(minRecall);
        confusion.calculateAUCROC();
    }

    public static void readArgs(String as[])
    {
        fileName = "";
        byte byte0 = 2;
        try
        {
            fileName = as[0];
            fileType = as[1];
            if(!fileType.equalsIgnoreCase("PR") && !fileType.equalsIgnoreCase("ROC") && !fileType.equalsIgnoreCase("list"))
                throw new NoSuchElementException();
            if(fileType.equalsIgnoreCase("PR") || fileType.equalsIgnoreCase("ROC"))
            {
                posCount = Double.parseDouble(as[2]);
                negCount = Double.parseDouble(as[3]);
                byte0 = 4;
                if(posCount < 1.0D || negCount < 1.0D)
                    throw new NumberFormatException();
            }
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception)
        {
            System.err.println("ERROR: Missing Arguments - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <fileType> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
        catch(NumberFormatException numberformatexception)
        {
            System.err.println("ERROR: Incorrect Count arguments, must be positive numbers - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
        catch(NoSuchElementException nosuchelementexception)
        {
            System.err.println("ERROR: Incorrect fileType, must be ROC, PR, LIST - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
        try
        {
            minRecall = Double.parseDouble(as[byte0]);
            if(minRecall < 0.0D || minRecall > 1.0D)
                throw new NumberFormatException();
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception1) { }
        catch(NumberFormatException numberformatexception1)
        {
            System.err.println("ERROR: Incorrect minRecall argument, must be positive between 0 and 1 - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
    }

    public static Confusion readFile(String s, String s1, double d, double d1)
    {
        if(DEBUG)
            System.out.println((new StringBuilder()).append("--- Reading in ").append(s1).append(" File: ").append(s).append(" ---").toString());
        Confusion confusion = new Confusion(d, d1);
        Object obj = null;
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(new File(s)));
            if(!s1.equals("PR") && !s1.equals("ROC") && !s1.equals("pr") && !s1.equals("roc"))
                throw new NoSuchElementException();
            do
            {
                if(!bufferedreader.ready())
                    break;
                String s2 = bufferedreader.readLine();
                if(DEBUG)
                    System.out.println(s2);
                StringTokenizer stringtokenizer = new StringTokenizer(s2, "\t ,");
                try
                {
                    double d2 = Double.parseDouble(stringtokenizer.nextToken());
                    double d3 = Double.parseDouble(stringtokenizer.nextToken());
                    if(DEBUG)
                        System.out.println((new StringBuilder()).append(d2).append("\t").append(d3).toString());
                    if(s1.equals("PR"))
                        confusion.addPRPoint(d2, d3);
                    else
                        confusion.addROCPoint(d2, d3);
                    if(DEBUG)
                        System.out.println("End of Line");
                }
                catch(NumberFormatException numberformatexception)
                {
                    System.err.println("...skipping bad input line (bad numbers)");
                }
                catch(NoSuchElementException nosuchelementexception1)
                {
                    System.err.println("...skipping bad input line (missing data)");
                }
            } while(true);
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
        confusion.sort();
        confusion.interpolate();
        return confusion;
    }

    public static Confusion readArrays(int ai[], double ad[])
    {
        if(ai.length != ad.length || ai.length == 0)
        {
            System.err.println((new StringBuilder()).append(ai.length).append(" ").append(ad.length).toString());
            System.err.println("ERROR: incorrect array lengths - exiting");
            System.exit(-1);
        }
        double d = 0.0D;
        double d1 = 0.0D;
        for(int i = 0; i < ai.length; i++)
        {
            if(ai[i] == 0)
            {
                d1++;
                continue;
            }
            if(ai[i] == 1)
            {
                d++;
            } else
            {
                System.err.println("ERROR: example not 0 or 1 - exiting");
                System.exit(-1);
            }
        }

        Confusion confusion = new Confusion(d, d1);
        double d2 = 0.0D;
        double d3 = 0.0D;
        if(ai[0] == 0)
            d3++;
        else
        if(ai[0] == 1)
        {
            d2++;
        } else
        {
            System.err.println("ERROR: example not 0 or 1 - exiting");
            System.exit(-1);
        }
        for(int j = 1; j < ad.length; j++)
        {
            if(ad[j] != ad[j - 1])
                try
                {
                    confusion.addPoint(d2, d3);
                }
                catch(NumberFormatException numberformatexception)
                {
                    System.err.println("...skipping bad input line (bad numbers)");
                }
            if(ai[j] == 0)
            {
                d3++;
                continue;
            }
            if(ai[j] == 1)
            {
                d2++;
            } else
            {
                System.err.println("ERROR: example not 0 or 1 - exiting");
                System.exit(-1);
            }
        }

        confusion.addPoint(d2, d3);
        confusion.sort();
        confusion.interpolate();
        return confusion;
    }

    private static String fileName;
    private static String fileType;
    private static double posCount;
    private static double negCount;
    private static double minRecall = 0.0D;
    public static boolean DEBUG = false;

}