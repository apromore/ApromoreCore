package org.apromore.prodrift.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apromore.prodrift.config.InductiveMinerConfig;
import org.apromore.prodrift.driftcharacterization.PairRelation;
import org.apromore.prodrift.driftcharacterization.RelationImpact;
import org.apromore.prodrift.model.DriftPoint;
import org.apromore.prodrift.config.DriftDetectionSensitivity;

//import javastat.multivariate.PCA;
//import Jama.Matrix;




public class Utils {

	public static double log2(int n)
	{
		return (Math.log(n) / Math.log(2));
	}

	public static long[][] rotate90(long [][]matrix)
	{
		final int M = matrix.length;
		final int N = matrix[0].length;
		long[][] ret = new long[N][M];
		for (int r = 0; r < M; r++) {
			for (int c = 0; c < N; c++) {
				ret[c][M-1-r] = matrix[r][c];
			}
		}
		return ret;
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

	public static int getMedian(int[] arr)
	{
		if(arr.length == 1)
			return arr[0];

		int median = 0;
		Arrays.sort(arr);
		int middle = ((arr.length) / 2);
		if(arr.length % 2 == 0)
		{
			int medianA = arr[middle];
			int medianB = arr[middle-1];
			median = (medianA + medianB) / 2;
		} else
		{
			median = arr[middle + 1];
		}

		return median;
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

	public static <K> K getInSet(Set<K> set, K element)
	{
		for (K k : set) {
			if (element.equals(k)) {
				return k;
			}
		}
		return null;
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

	public static void permGen_NoRepetition(String[] s,int i,int k,String[] buff, List<String> list) {
		if(i < k) {
			for(int j = 0; j < s.length; j++)
			{
				boolean insert = true;
				for(int l = 0; l < i; l++)
					if(buff[l] == s[j])
					{
						insert = false;
						break;
					}
				if(insert)
				{
					buff[i] = s[j];
					permGen_NoRepetition(s, i+1, k, buff, list);
				}

			}
		}
		else {

//        	System.out.println(String.join("", buff));
			list.add(String.join("", buff));

		}

	}

	public static void permGen_NoRepetition_OnLists(List<List<String>> s,int i,int k, List<Integer> usedIndices, String[] buff, List<String> collectionList) {

		if(i < k) {
			for(int j = 0; j < s.size(); j++)
			{
				List<String> sl = s.get(j);
				for(int m = 0; m < sl.size(); m++)
				{
					if(!usedIndices.contains(j))
					{
						buff[i] = sl.get(m);
						usedIndices.add(j);
						permGen_NoRepetition_OnLists(s, i+1, k, usedIndices, buff, collectionList);
						for(int r = usedIndices.size() - 1; r >= i; r--)usedIndices.remove(i);
					}
				}
			}
		}
		else {

//        	System.out.println(String.join("", buff));
			collectionList.add(String.join("", buff));

		}

	}

	public static void combGen_NoRepetition_OnLists(List<List<String>> s,int i,int k, List<Integer> usedIndices, String[] buff, List<String> collectionList) {

		if(i < k) {
			List<String> sl = s.get(i);
			for(int m = 0; m < sl.size(); m++)
			{
				if(!usedIndices.contains(i))
				{
					buff[i] = sl.get(m);
					usedIndices.add(i);
					combGen_NoRepetition_OnLists(s, i+1, k, usedIndices, buff, collectionList);
					for(int r = usedIndices.size() - 1; r >= i; r--)usedIndices.remove(i);
				}
			}
		}
		else {

//        	System.out.println(String.join("", buff));
			collectionList.add(String.join("", buff));

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

	public static <T> List<T> getIntersection(List<T> list1_, List<T> list2_)
	{
		List<T> list1 = new ArrayList<>(list1_);
		List<T> list2 = new ArrayList<>(list2_);

		if (list1.size() > list2.size()) {
			//swap the references;
			List<T> tmp = list1;
			list1 = list2;
			list2 = tmp;
		}

		List<T> intersection = new ArrayList<T>();

		for (T item : list1) {
			if (list2.contains(item)){
				//item found in both the sets
				intersection.add(item);
				list2.remove(item);
			}
		}

		return intersection;
	}

	public static <T> List<T> getUnion(List<T> list1_, List<T> list2_)
	{
		List<T> list1 = new ArrayList<>(list1_);
		List<T> list2 = new ArrayList<>(list2_);

		List<T> union = new ArrayList<T>();
		union.addAll(list1);
		union.addAll(list2);

		List<T> intersection = getIntersection(list1, list2);
		for (T item : intersection)
		{
			union.remove(item);
		}

		return union;
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




	private static void checkArray(final double[][] in)
			throws NullArgumentException, DimensionMismatchException,
			NotPositiveException {

		if (in.length < 2) {
			throw new DimensionMismatchException(in.length, 2);
		}

		if (in[0].length < 2) {
			throw new DimensionMismatchException(in[0].length, 2);
		}

		checkRectangular(in);
		checkNonNegative(in);

	}

	private static void checkRectangular(final double[][] in)
			throws NullArgumentException, DimensionMismatchException {
		checkNotNull(in);
		for (int i = 1; i < in.length; i++) {
			if (in[i].length != in[0].length) {
				throw new DimensionMismatchException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, in[i].length,
						in[0].length);
			}
		}
	}

	private static void checkNotNull(Object o)
			throws NullArgumentException {
		if (o == null) {
			throw new NullArgumentException();
		}
	}

	public static void checkNonNegative(final double[][] in)
			throws NotPositiveException {
		for (int i = 0; i < in.length; i ++) {
			for (int j = 0; j < in[i].length; j++) {
				if (in[i][j] < 0) {
					throw new NotPositiveException(in[i][j]);
				}
			}
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
