package org.apromore.toolbox.clustering.dissimilarity.measure;

import matching.algos.DistanceAlgoAbstr;
import matching.algos.GraphEditDistanceOptimalLabel;
import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;

public class LEDBasedDissimCalc implements DissimilarityCalc {
	private double threshold;
	
	static double ledcutoff = 0.48;
	
	static double usepuredistance = 0.0; //0.0 represents 'false', 1.0 represents 'true'
	static double prunewhen = 100.0;
	static double pruneto = 10.0;
	static double useepsilon = 0.0; //0.0 represents 'false', 1.0 represents 'true'
	static boolean considerevents = true;
	
	static DistanceAlgoAbstr gedepc = new GraphEditDistanceOptimalLabel(); 

	static double vweight = 0.2;
	static double sweight = 0.1;
	static double eweight = 0.7;					

	public LEDBasedDissimCalc(double threshold) {
		this.threshold = threshold;
	}

	public double compute(SimpleGraph graph1, SimpleGraph graph2) {
		
		Object weights[] = {"vweight",vweight,"sweight",sweight,"eweight",eweight,"ledcutoff",ledcutoff,"usepuredistance",usepuredistance,"prunewhen",prunewhen,"pruneto",pruneto,"useepsilon",useepsilon};
		gedepc.setWeight(weights);
		
		return gedepc.compute(graph1,graph2);
	}

	public boolean isAboveThreshold(double disim) {
		return disim > threshold;
	}
}
