package org.apromore.clustering.dissimilarity.measure;

import matching.algos.DistanceAlgoAbstr;
import matching.algos.GraphEditDistanceGreedy;
import nl.tue.tm.is.graph.SimpleGraph;

public class GEDNonDeterministicCalc {
	
	static double ledcutoff = 0.5;
	
	static double usepuredistance = 1.0; //0.0 represents 'false', 1.0 represents 'true'
	static double prunewhen = 100.0;
	static double pruneto = 10.0;
	static double useepsilon = 0.0; //0.0 represents 'false', 1.0 represents 'true'
	static boolean considerevents = true;
	
	static DistanceAlgoAbstr gedepc = new GraphEditDistanceGreedy(); 

	static double vweight = 1.0;
	static double sweight = 1.0;
	static double eweight = 1.0;					

	public GEDNonDeterministicCalc(double ledc) {
		ledcutoff = ledc;
		Object weights[] = {"vweight",vweight,"sweight",sweight,"eweight",eweight,"ledcutoff",ledcutoff,"usepuredistance",usepuredistance,"prunewhen",prunewhen,"pruneto",pruneto,"useepsilon",useepsilon};
		gedepc.setWeight(weights);
	}

	public double compute(SimpleGraph graph1, SimpleGraph graph2) {		
		return gedepc.compute(graph1,graph2);
	}
}
