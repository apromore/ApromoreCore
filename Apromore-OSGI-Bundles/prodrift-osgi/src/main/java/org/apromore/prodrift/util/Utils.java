/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.prodrift.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.prodrift.driftcharacterization.PairRelation;
import org.apromore.prodrift.driftcharacterization.RelationImpact;
import org.apromore.prodrift.model.DriftPoint;

//import javastat.multivariate.PCA;
//import Jama.Matrix;




public class Utils {
	
	public static double log2(int n)
	{
	    return (Math.log(n) / Math.log(2));
	}
	
	
	public static boolean isLong(String a)
	{
		
		try {
			
			Long.valueOf(a);
			return true;
			
		} catch (NumberFormatException e) {
			return false;
		}
		
	}
	
	public static boolean isDouble(String a)
	{
		
		try {
			
			Double.valueOf(a);
			return true;
			
		} catch (NumberFormatException e) {
			return false;
		}
		
	}
	
	public static void printArray(long []arr)
	{
		
		for(int i = 0; i < arr.length; i++)
		{
			
			System.out.print(arr[i] + ", ");
			
		}
		
	}
	
	public static String convertDateToString(Calendar date)
	{
		
		StringBuilder dateString = new StringBuilder();
		dateString.append(date.get(Calendar.YEAR)).append("_").append(date.get(Calendar.MONTH)).append("_").append(date.get(Calendar.DAY_OF_MONTH));
		
		return dateString.toString();
		
	}
	
	public static void setTimeToMidnight(Calendar date)
	{
		
		date.set(GregorianCalendar.HOUR_OF_DAY, 0);
		date.set(GregorianCalendar.MINUTE, 0);
		date.set(GregorianCalendar.SECOND, 0);
		
	}
	
	public static void populateList(List<Integer> li, int[] arr)
	{
		
		for(int i = 0; i < arr.length; i++)
		{
			
			li.add(arr[i]);
			
		}
		
	}
	
	public static void populateList(List<Integer> li, int size, int value)
	{
		
		for(int i = 0; i < size; i++)
		{
			
			li.add(value);
			
		}
		
	}
	
	public static int getSum(int[] arr)
	{
		
		int sum = 0;
		for(int val : arr)
		{
			
			sum += val;
			
		}
		
		return sum;
	}
	
	public static long getSum(long[] arr)
	{
		
		long sum = 0;
		for(long val : arr)
		{
			
			sum += val;
			
		}
		
		return sum;
	}
	
	public static int getSum(List<Integer> list)
	{
		
		int sum = 0;
		for(int val : list)
		{
			
			sum += val;
			
		}
		
		return sum;
	}
	
	public static long getAverage(Object[] arr)
	{
		
		long sum = 0;
		for(int i = 0; i < arr.length; i++)
		{
			
			long val = (long) arr[i];
			sum += val;
			
		}
		
		if(arr.length != 0)
			return sum / arr.length;
		else
			return 0;
	}
	
	public static int getAverage(int[] arr)
	{
		
		int sum = 0;
		for(int val : arr)
		{
			
			sum += val;
			
		}
		
		if(arr.length != 0)
			return sum / arr.length;
		else
			return 0;
	}
	
	public static int getMax(Object[] arr)
	{
		
		int max = 0;
		for(int i = 0; i < arr.length; i++)
		{
			
			int val = (int) arr[i];
			if(val > max)
				max = val;
			
		}
		
		return max;
	}
	
	public static int getMax(List<Integer> list)
	{
		
		int max = 0;
		for(int i = 0; i < list.size(); i++)
		{
			
			int val = (int) list.get(i);
			if(val > max)
				max = val;
			
		}
		
		return max;
	}
	
	public static int getMax(int[] arr)
	{
		
		int max = 0;
		for(int val : arr)
		{
			
			if(val > max)
				max = val;
			
		}
		
		return max;
	}
	
	
	public static int getMin(Object[] arr)
	{
		
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < arr.length; i++)
		{
			
			int val = (int) arr[i];
			if(val < min)
				min = val;
			
		}
		
		return min;
	}
	
	public static int getMin(List<Integer> list)
	{
		
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < list.size(); i++)
		{
			
			int val = list.get(i);
			if(val < min)
				min = val;
			
		}
		
		return min;
	}
	
	public static int getMin(int[] arr)
	{
		
		int min = Integer.MAX_VALUE;
		for(int val : arr)
		{
			
			if(val < min)
				min = val;
			
		}
		
		return min;
	}
	
	public static void permGen(char[] s,int i,int k,char[] buff) {
        if(i<k) {
            for(int j=0;j<s.length;j++) {

                buff[i] = s[j];
                permGen(s,i+1,k,buff);
            }
        }       
        else {

         System.out.println(String.valueOf(buff)); 

        }

    }
	
	public static void combGen(int[] s,int i,int k, int r, int[] buff, List<List<Integer>> combs) {
        
		if(i < k) 
        {
            for(int j = r ; j < s.length; j++) 
            {
            	if(s.length - (j + 1) >= k - (i+1))
            	{
	            	buff[i] = s[j];
	                combGen(s, i+1, k, j+1, buff, combs);
	            }
            }
        }       
        else 
        {

        	List<Integer> comb = new ArrayList<>();
        	for (int j = 0; j < buff.length; j++) {
				comb.add(buff[j]);
			}
        	combs.add(comb);
        	
//        	for (int j = 0; j < buff.length; j++) {
//        		System.out.print(buff[j]); 
//			}
        	

        }

    }
	
	public static void combGen_seq(short[] s, short k, List<List<Short>> combs) {
        
		for(int j = 0; j <= s.length - k;)
		{
			List<Short> comb = new ArrayList<>();
			for(int r = 0; r < k; r++)
			{
				comb.add(s[j++]);
			}
			j -= (k - 1);
			combs.add(comb);
		}

    }
	
	public static <T> int countIntersection(Set<T> set1, Set<T> set2){
	    //assuming first argument to be smaller than the later;
	    //however double checking to be sure
	    if (set1.size() > set2.size()) {
	        //swap the references;
	        Set<T> tmp = set1;
	        set1 = set2;
	        set2 = tmp;
	    }
	    int result = 0;
	    for (T item : set1) {
	        if (set2.contains(item)){
	            //item found in both the sets
	            result++;
	        }
	    }
	    return result;
	}
	
	public static void writeToFile_Integer(String filePath, List<List<Integer>> listOfLists)
	{
		
		BufferedWriter writer;
		
		int maxListSize = 0;
		for(List<Integer> li : listOfLists)
		{
			
			if(li.size() > maxListSize)
				maxListSize = li.size();
			
		}
		
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));
			
			for(int i = 0; i < maxListSize; i++)
			{
				
				for(int j = 0; j < listOfLists.size(); j++)
				{
				
					if(listOfLists.get(j).size() > i)
						writer.write(listOfLists.get(j).get(i) + ",");
					else
						writer.write(" " + ",");
					
				}
				
				writer.write("\n");
				
			}
				
	        
	        writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public static void writeToFile_Long(String filePath, List<List<Long>> listOfLists)
	{
		
		BufferedWriter writer;
		
		int maxListSize = 0;
		for(List<Long> li : listOfLists)
		{
			
			if(li.size() > maxListSize)
				maxListSize = li.size();
			
		}
		
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));
			
			for(int i = 0; i < maxListSize; i++)
			{
				
				for(int j = 0; j < listOfLists.size(); j++)
				{
				
					if(listOfLists.get(j).size() > i)
						writer.write(listOfLists.get(j).get(i) + ",");
					else
						writer.write(" " + ",");
					
				}
				
				writer.write("\n");
				
			}
				
	        
	        writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	
	public static void writeToFile_Double(String filePath, List<List<Double>> listOfLists)
	{
		
		BufferedWriter writer;
		
		int maxListSize = 0;
		for(List<Double> li : listOfLists)
		{
			
			if(li.size() > maxListSize)
				maxListSize = li.size();
			
		}
		
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));
			
			for(int i = 0; i < maxListSize; i++)
			{
				
				for(int j = 0; j < listOfLists.size(); j++)
				{
				
					if(listOfLists.get(j).size() > i)
						writer.write(listOfLists.get(j).get(i) + ",");
					else
						writer.write(" " + ",");
					
				}
				
				writer.write("\n");
				
			}
				
	        
	        writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public static void writeToFile_String(String filePath, List<List<String>> listOfLists)
	{
		
		BufferedWriter writer;
		
		int maxListSize = 0;
		for(List<String> li : listOfLists)
		{
			
			if(li.size() > maxListSize)
				maxListSize = li.size();
			
		}
		
		try {

			writer = new BufferedWriter(new FileWriter(new File(filePath)));
			
			for(int i = 0; i < maxListSize; i++)
			{
				
				for(int j = 0; j < listOfLists.size(); j++)
				{
				
					if(listOfLists.get(j).size() > i)
						writer.write(listOfLists.get(j).get(i) + ",");
					else
						writer.write(" " + ",");
					
				}
				
				writer.write("\n");
				
			}
				
	        
	        writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public static void clearFile(String filePath)
	{
		
		
       	try {
       		FileWriter fw = new FileWriter(filePath, false);
			
			fw.write("");
			
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static double chiSquareValue(double obs, double exp)
	{
		
		return Math.pow(obs - exp, 2.0) / exp;
		
	}
	
	// To remove the columns in frequency matrix whose both expected and observed frequencies are zero
	public static long[][] removeZeroFreqColumns(long [][]freqMatrix)
	{
	
		int nonValueAddingCoulmns = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {
			
			if (freqMatrix[0][i] <= 0 && freqMatrix[1][i] <= 0)
			{
				
				nonValueAddingCoulmns++;
				
			}
			
		}
		
		if(nonValueAddingCoulmns > 0)
		{
			
			long [][] freqMatrixAfterCut = new long[2][freqMatrix[1].length - nonValueAddingCoulmns];
			int index = 0;
			for (int i = 0; i < freqMatrix[1].length; i++) {
				
				if (freqMatrix[0][i] > 0 || freqMatrix[1][i] > 0)
				{
					
					freqMatrixAfterCut[0][index] = freqMatrix[0][i];
					freqMatrixAfterCut[1][index] = freqMatrix[1][i];
					index++;
					
				}
				
			}
			
			return freqMatrixAfterCut;
			
		}
		
		return freqMatrix;
			
	}
	
	// To remove the columns in frequency matrix whose both expected and observed frequencies are below minExpectedFrequency
		public static long[][] reduceDimensionality_cuttingColumnsWithFreqBelowThreshold(long [][]freqMatrix, int minExpectedFrequency)
		{
		
			int nonValueAddingCoulmns = 0;
			for (int i = 0; i < freqMatrix[1].length; i++) {
				
				if (freqMatrix[0][i] < minExpectedFrequency || freqMatrix[1][i] < minExpectedFrequency)
				{
					
					nonValueAddingCoulmns++;
					
				}
				
			}
			
			long [][] freqMatrixAfterCut = new long[2][freqMatrix[1].length - nonValueAddingCoulmns];
			int index = 0;
			for (int i = 0; i < freqMatrix[1].length; i++) {
				
				if (freqMatrix[0][i] >= minExpectedFrequency && freqMatrix[1][i] >= minExpectedFrequency)
				{
					
					freqMatrixAfterCut[0][index] = freqMatrix[0][i];
					freqMatrixAfterCut[1][index] = freqMatrix[1][i];
					index++;
					
				}
				
			}
			
			return freqMatrixAfterCut;
				
		}
	
	// To remove the columns in frequency matrix that have the same values for observed and expected variables
	public static long[][] reduceDimensionality_cuttingNonValueAddingColumns(long [][]freqMatrix)
	{
	
		int valueAddingCoulmns = 0;
		double thresholdChiSQValue = -1.0;
		int allowedNonValueAddingCol = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {
			
			double chiSquareValue = chiSquareValue(freqMatrix[0][i], freqMatrix[1][i]);
			if (chiSquareValue > thresholdChiSQValue || allowedNonValueAddingCol > 0)
			{
				
				valueAddingCoulmns++;
				if(chiSquareValue <= thresholdChiSQValue)
					allowedNonValueAddingCol--;
				
			}
			
		}
		
		long [][] freqMatrixAfterCut = new long[2][valueAddingCoulmns];
		int index = 0;
		allowedNonValueAddingCol = 0;
		for (int i = 0; i < freqMatrix[1].length; i++) {
			
			double chiSquareValue = chiSquareValue(freqMatrix[0][i], freqMatrix[1][i]);
			if (chiSquareValue > thresholdChiSQValue || allowedNonValueAddingCol > 0)
			{
				
				freqMatrixAfterCut[0][index] = freqMatrix[0][i];
				freqMatrixAfterCut[1][index] = freqMatrix[1][i];
				index++;
				if(chiSquareValue <= thresholdChiSQValue)
					allowedNonValueAddingCol--;
				
			}
			
		}
		
		return freqMatrixAfterCut;
			
	}
	
	
//	public double[][] ReduceDimensionality_PCA(double level, java.lang.String covChoice, double[][] data)
//	{
////		
////		PCA testclass1 = new PCA(0.95, "covariance", data);
////		
////		return data;
//		
//	}
	
	public static double factoriel(double n)
	{
		
		if(n > 1)
			return n * factoriel(n-1);
		else
			return 1;
		
	}
	
	public static List<Double> SMASmoothing(ArrayList<Double> pValVector, ArrayList<Integer> winSizeVector, float oscilationRatio) {
        
        List<Double> smoothPValues = new ArrayList<>();
		for (int i = 0; i < pValVector.size(); i++) 
		{
			
			Integer smoothingFactor = (int)(winSizeVector.get(i) * oscilationRatio);
			double sum = 0.0;
			int j = i;
			for (; j >= 0 && j > i - smoothingFactor; j--) 
			{
				
				sum += pValVector.get(j);
				
			}			
			smoothPValues.add(sum / (i - j));
			
		}
		
		return smoothPValues;
		
	}
	
	public static List<DriftPoint> copyDriftPointsList(List<DriftPoint> DriftPointsList)
	{
		
		List<DriftPoint> DriftPointsList_copy = new ArrayList<>();
		for (int i = 0; i < DriftPointsList.size(); i++) 
		{
			
			DriftPoint cur_dp = DriftPointsList.get(i);
			DriftPoint dp = new DriftPoint(cur_dp.getDriftPointActual(),
					cur_dp.getDriftTimeActual(), cur_dp.getIntraTraceDriftAreaStartPoint(),
					cur_dp.getIntraTraceDriftAreaEndPoint(), cur_dp.getDriftPointDetected(),
					cur_dp.getDriftTimeDetected(), cur_dp.getTruePositive(), cur_dp.getFalsePositive(), cur_dp.getDetectionDelay());
		
			DriftPointsList_copy.add(dp);
			
		}
		
		return DriftPointsList_copy;
		
	}
	
	public static String convertArrayToRvector(int[] arr)
	{
		
		StringBuilder covariate = new StringBuilder("c(");
        for(int num : arr)
        	covariate.append(num).append(",");
        covariate.delete(covariate.lastIndexOf(","), covariate.length());
        covariate.append(")");
		
        return covariate.toString();
		
	}
	
	public static String convertArrayToRvector(double[] arr)
	{
		
		StringBuilder covariate = new StringBuilder("c(");
        for(double num : arr)
        	covariate.append(num).append(",");
        covariate.delete(covariate.lastIndexOf(","), covariate.length());
        covariate.append(")");
		
        return covariate.toString();
		
	}
	
	public static void emptyFile(String filePath)
	{
		
		try {
			
			FileWriter fw = new FileWriter(filePath, false);
	        fw.write("");
	        fw.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(final String[] args) throws Exception {
		
//		double[][] testscores = { {36, 62, 45, 45, 62, 45, 45, 62, 45, 45},
//           {58, 54, 45, 45, 62, 45, 45, 62, 45, 45},{58, 54, 45, 45, 62, 45, 45, 62, 45, 45},{58, 54, 45, 45, 62, 45, 45, 62, 45, 45},
//           {58, 54, 45, 45, 62, 45, 45, 62, 45, 45},{58, 54, 45, 45, 62, 45, 45, 62, 45, 45},{58, 54, 45, 45, 62, 45, 45, 62, 45, 45}};
//		
//		
//
//		PCA testclass1 = new PCA(1, "covariance", testscores);
//		System.out.println(testclass1.principalComponents.length +"  "+testclass1.principalComponents[0].length);
//		
//		System.out.println("1st component vector (non-null constructor)    = [" +
//		testclass1.principalComponents[0][0] + "  " +
//		testclass1.principalComponents[0][1] + "  " + "]");
//
//
//		System.out.println("variances of components (non-null constructor) = [" +
//		testclass1.variance[0] + "]");
//
//		System.out.println(testclass1.output); 
		
//		for(int i = 0; i < 100000; i++)
//			factoriel(1000);
//		
//		System.out.println("done");
		
		
//		        System.out.println("Running a demonstration program on some sample data ...");
//		        /** Training data matrix with each row corresponding to data point and
//		        * each column corresponding to dimension. */
//		        Matrix trainingData = new Matrix(new double[][] {
//		            {1, 2, 3, 4, 5, 6},
//		            {6, 5, 4, 3, 2, 1},
//		            {2, 2, 2, 2, 2, 2},
//		            {6, 5, 4, 3, 2, 1},
//		            {6, 5, 4, 3, 2, 1}});
//		        com.mkobos.pca_transform.PCA pca = new com.mkobos.pca_transform.PCA(trainingData);
//		        /** Test data to be transformed. The same convention of representing
//		        * data points as in the training data matrix is used. */
//		        Matrix testData = new Matrix(new double[][] {
//		                {1, 2, 3, 4, 5, 6},
//		                {1, 2, 1, 2, 1, 2},
//		                {1, 2, 1, 2, 1, 2}});
//		        /** The transformed test data. */
//		        Matrix transformedData =
//		            pca.transform(testData, com.mkobos.pca_transform.PCA.TransformationType.ROTATION);
//		        System.out.println("Transformed data (each row corresponding to transformed data point):");
//		        for(int r = 0; r < transformedData.getRowDimension(); r++){
//		            for(int c = 0; c < transformedData.getColumnDimension(); c++){
//		                System.out.print(transformedData.get(r, c));
//		                if (c == transformedData.getColumnDimension()-1) continue;
//		                System.out.print(", ");
//		            }
//		            System.out.println("");
//		        }
		    }

	
	
}
