package org.processmining.stagemining.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class Measure {
	public static double computeRandIndex(List<Set<String>> phaseModel, List<Set<String>> truth) throws Exception {
		System.out.println("Selectd Phase Model = " + phaseModel.toString());
		System.out.println("Ground Truth = " + truth.toString());
		int[][] sim = new int[phaseModel.size()][truth.size()];
		for (int i=0;i<sim.length;i++) {
			for (int j=0;j<sim[0].length;j++) {
				Set<String> intersection = new HashSet<String>(phaseModel.get(i));
				intersection.retainAll(truth.get(j));
				sim[i][j] = intersection.size();
			}
		}
		
		int N = 0;
		for (Set<String> phase : phaseModel) {
			N += phase.size();
		}
		if (N<=2) {
			throw new Exception("Cannot compute rand index for total number of elements less than 2 (N=" + N + ")");
		}
		
		//double part1 = 1.0*factorial(N)/(factorial(2)*factorial(N-2));
		double part1 = CombinatoricsUtils.binomialCoefficientDouble(N, 2);
		
		double part2 = 0.0;
		for (int i=0;i<sim.length;i++) {
			int part2i = 0;
			for (int j=0;j<sim[0].length;j++) {
				part2i += sim[i][j]; 
			}
			part2 += part2i*part2i;
		}
		
		double part3 = 0.0;
		for (int j=0;j<sim[0].length;j++) {
			int part3j = 0;
			for (int i=0;i<sim.length;i++) {
				part3j += sim[i][j]; 
			}
			part3 += part3j*part3j;
		}
		
		double part4 = 0.0;
		for (int i=0;i<sim.length;i++) {
			for (int j=0;j<sim[0].length;j++) {
				part4 += sim[i][j]*sim[i][j]; 
			}
		}
		
		double randIndex = 1.0*(part1 - ((part2 + part3)/2 - part4))/part1;
		
		return randIndex;	
	}
	
	/*
	 * Argument:
	 * 1 : Rand Index
	 * 2 : Fowlkes–Mallows
	 * 3 : Jaccard
	 */
	public static double computeMeasure(List<Set<String>> phaseModel, List<Set<String>> truth, int method) throws Exception {
		// Get all distinct labels
		Set<String> labels = new HashSet<String>();
		for (Set<String> cluster : phaseModel) {
			for (String label : cluster) {
				if (!labels.contains(label)) labels.add(label);
			}
 		}
		
		// Set up distinct pair strings, delimited by @
		Set<String> pairs = new HashSet<String>();
		for (String label1 : labels) {
			for (String label2 : labels) {
				if (!label1.equals(label2) && !pairs.contains(label1 + "@" + label2) && !pairs.contains(label2 + "@" + label1)) {
					pairs.add(label1 + "@" + label2);
				}
			}
		}
		
		// Compute the a,b,c,d for the Rand Index formula
		int n11=0,n00=0,n01=0,n10=0;
		for (String pair : pairs) {
			String[] split = pair.split("@");
			Set<String> phase0 = null;
			Set<String> phase1 = null;
			Set<String> truth0 = null;
			Set<String> truth1 = null;
			
			for (Set<String> phase : phaseModel) {
				if (phase.contains(split[0])) {
					phase0 = phase;
				}
				if (phase.contains(split[1])) {
					phase1 = phase;
				}
			}
			
			for (Set<String> truthSet : truth) {
				if (truthSet.contains(split[0])) {
					truth0 = truthSet;
				}
				if (truthSet.contains(split[1])) {
					truth1 = truthSet;
				}
			}
			
			if (phase0 == phase1 && truth0 == truth1) {
				n11++;
			}
			else if (phase0 != phase1 && truth0 != truth1) {
				n00++;
			}
			else if (phase0 == phase1 && truth0 != truth1) {
				n10++;
			}
			else if (phase0 != phase1 && truth0 == truth1) {
				n01++;
			}
		}
		
		double measure = 0; 
		if (method==1) { //Rand Index
			measure = 1.0*(n11+n00)/(n11+n10+n01+n00);
		}
		else if (method==2) { //Fowlkes–Mallows
			measure = 1.0*n11/Math.sqrt((n11+n10)*(n11+n01));
		}
		else if (method==3) { //Jaccard
			measure = 1.0*(n11)/(n11+n10+n01);
		}
		
		return measure;	
	}
	

	
	private double factorial(int n) {
		double result = 1;
		for (int i=2;i<=n;i++) {
			result = result*i;
		}
		return result;
	}
}
