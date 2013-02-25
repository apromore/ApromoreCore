package org.apromore.clustering.dissimilarity.measure;

import mathCollection.HashMultiset;
import mathCollection.Multiset;
import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.clustering.dissimilarity.DissimilarityCalc;

import java.util.LinkedHashMap;
import java.util.Map;

public class LJaccardDissimCalc implements DissimilarityCalc {
	private Map<SimpleGraph, Multiset> labels = new LinkedHashMap<SimpleGraph, Multiset>();
	private double threshold;

	public LJaccardDissimCalc(double threshold) {
		this.threshold = threshold;
	}
	
	public double compute(SimpleGraph graph1, SimpleGraph graph2) {
		Multiset mset1 = getMultiset(graph1);
		Multiset mset2 = getMultiset(graph2);

		Multiset union = mset1.union(mset2);
		Multiset symdiff = mset1.symmetricDifference(mset2);
		
		return (double)symdiff.size() / (double)union.size();
	}
	
	@SuppressWarnings("unchecked")
	private Multiset getMultiset(SimpleGraph graph) {
		Multiset mset = labels.get(graph);
		if (mset == null) {			
			mset = new HashMultiset();
			labels.put(graph, mset);
			
			mset.addAll(graph.getLabels());
		}
		return mset;
	}

	public boolean isAboveThreshold(double disim) {
		return disim > threshold;
	}}
