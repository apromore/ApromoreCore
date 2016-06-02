package ee.ut.utilities.FES;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import ee.ut.eventstr.BehaviorRelation;

public class FreqEventStructure {
	private double[][] matrix;
	private String[] labels;
	private String[] newlabels;
	private String[] events;
	
	private Set<String> duplicateLabels;
	
	private Map<Set<String>, Set<Double>> freqrelation;
	
	private String[] createEventsFromLabels(String[] labels) {
		String[] ev = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			ev[i] = labels[i] + i;
		}
		return ev;
	}
	
	private int getOccurrenceCount(String curlabel, int startindex) {
		for (int i = startindex - 1; i > 0; i--) {
			if (labels[i].equals(curlabel)) { 
				duplicateLabels.add(curlabel);
				if (matrix[i][startindex] > 0) {
					return Integer.parseInt(newlabels[i].substring(labels[i].length() + 1)) + 1;
				}
			}
		}
		
		return 1;
	}
	
	private void prepareNewLabels() {
		newlabels[0] = labels[0] + "_1";
		if (labels.length > 1) {
			for (int i = 1; i < labels.length; i++) {
				newlabels[i] = labels[i] + "_" + getOccurrenceCount(labels[i], i);
			}
		}
	}
	
	private double[][] createFreqFromPES(ReducedPrimeEventStructure<Integer> pes) {
		double[][] m = new double[pes.getBRelMatrix().length][pes.getBRelMatrix().length];
		BehaviorRelation[][] br = pes.getBRelMatrix();

		int pos;
		
		for (int i = 0; i < br.length; i++) {
			for (int j = i + 1; j < br.length; j++) {
				List<Integer> nextItems = new ArrayList<Integer>();
				List<List<Integer>> branches = new ArrayList<>();
				
				if (br[i][j] == BehaviorRelation.CONCURRENCY) {
					m[i][j] = 0;
				}
				else if (br[i][j] == BehaviorRelation.CAUSALITY) { //causal
					pos = j;
					nextItems.clear();
					while ((pos < br.length) && (br[i][pos] == BehaviorRelation.CAUSALITY)) {
						nextItems.add(pos);
						pos++;
					}
					
					branches.add(new ArrayList<Integer>());
					branches.get(0).add(nextItems.get(0)); // add first element
					
					if (nextItems.size() > 1) {
						for (int k = 1; k < nextItems.size(); k++) {
							int b = 0;
							while ((b < branches.size()) && (br[nextItems.get(k)][branches.get(b).get(0)] == BehaviorRelation.CONFLICT)) b++; 
							if (b == branches.size()) {
								branches.add(new ArrayList<Integer>());
							}
							branches.get(b).add(nextItems.get(k));
						}
					}
					
					double pvalue = 1 / branches.size();
					
					for (int k = j; k < pos; k++) {
						m[i][k] = pvalue;
					}
					j = pos - 1;
				}
				else {
					m[i][j] = 0;
				}
			}
		}
		return m;
	}
	
	private void setupFES() {
		this.duplicateLabels = new HashSet<String>();
		
		prepareNewLabels(); // create labels with a suffix indicating occurrence count of that label

		freqrelation = new HashMap<Set<String>,Set<Double>>();
		
		Set<String> key;
		Set<Double> values;
				
		for (int i = 0; i < newlabels.length; i++) {
			for (int j = i + 1; j < newlabels.length; j++) {
				key = new LinkedHashSet<String>();
				values = new LinkedHashSet<Double>();
				
				key.add(newlabels[i]);
				key.add(newlabels[j]);

				values.add(matrix[i][j]);
				
				if (freqrelation.containsKey(key)) {
					freqrelation.get(key).add(matrix[i][j]);
				}
				else {
					freqrelation.put(key, values);	
				}
			}
		}
	}
	
	public FreqEventStructure(double[][] matrix, String[] labels, String events[]) {
		this.matrix = matrix;
		this.labels = labels;
		this.events = events;
		this.newlabels = new String[labels.length];
		
		setupFES();
	}
	
	public FreqEventStructure(double[][] matrix, String[] labels) {
		this.matrix = matrix;
		this.labels = labels;
		this.events = createEventsFromLabels(labels);
		this.newlabels = new String[labels.length];
		
		setupFES();
	}
	
	public FreqEventStructure(ReducedPrimeEventStructure<Integer> pes, String[] labels) {
		this.matrix = createFreqFromPES(pes);
		this.labels = labels;
		this.events = createEventsFromLabels(labels);
		this.newlabels = new String[labels.length];
		
		setupFES();
	}
	
	public void completeMatrix() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = i + 1; j < matrix.length; j++) {
				matrix[j][i] = matrix[i][j];
			}
		}
	}
	
	public void setFreq(int col, int row, double value) {
		if ((matrix.length > col) && (matrix.length > row)) {
			matrix[col][row] = value;
		}
	}
	
	public double getFreq(int col, int row) {
		if ((matrix.length > col) && (matrix.length > row)) {
			return matrix[col][row];
		}
		else {
			return -1;
		}
	}
	
	public double[][] getMatrix() {
		return matrix;
	}
	
	public String[] getLabels() {
		return labels;
	}
	
	public int getNewLabelCount(String label) {
		int lblcount = 0;
		for (String s: newlabels) {
			if (s.equals(label)) lblcount++;
		}
		
		return lblcount;
	}
	
	public String[] getEvents() {
		return events;
	}
	
	public Set<Double> getFrequencyRelation(String fromlabel, String tolabel) {
		Set<String> key = new LinkedHashSet<String>();
		
		key.add(fromlabel);
		key.add(tolabel);
		
		return getFrequencyRelation(key);
	}
	
	public Set<Double> getFrequencyRelation(Set<String> labels) {
		return freqrelation.get(labels);
	}
	
	public Map<Set<String>, Set<Double>> getFrequencyRelations() {
		return freqrelation;
	}
	
	public Set<Set<String>> getFrequencyKeys() {
		return freqrelation.keySet();
	}
	
	public Set<Entry<Set<String>, Set<Double>>> getEntrySet() {
		return freqrelation.entrySet();
	}
	
	public int getSize() {
		return matrix.length;
	}
	
	public boolean isDuplicate(String label) {
		String truelabel = label.substring(0, label.lastIndexOf("_"));

		return duplicateLabels.contains(truelabel);
	}
	
	public String toString() {
		String str = "";
		for (String s: labels) {
			str += s + " ";
		}
		str += "\n";
			
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				str += matrix[i][j] + " ";
			}
			str += "\n";
		}	
		return str;
	}
	
	public static FreqEventStructure createFESfromFile(String filename) {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(filename));
			String line = buffer.readLine();

			int size = Integer.parseInt(line);
			double[][] matrix = new double[size][size];
			String[] labels = new String[size];
			String[] events = new String[size];

			line = buffer.readLine();
			StringTokenizer token = new StringTokenizer(line);
			int k = 0;
			while (token.hasMoreTokens()) {
				String node = token.nextToken();
				events[k] = node + "" + k;
				labels[k] = node;
				k++;
			}
			
			for (int i = 0; i < matrix.length; i++) {
				line = buffer.readLine();
				token = new StringTokenizer(line);
				for (int j = 0; j < matrix.length; j++) {
					String rel = token.nextToken();
					matrix[i][j] = Double.parseDouble(rel);
				}
			}

			buffer.close();
			
			return new FreqEventStructure(matrix, labels, events);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
