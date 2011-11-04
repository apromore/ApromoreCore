package org.apromore.toolbox.similaritySearch.algorithms;


import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.graph.Graph;


public class FindModelSimilarity {

	
	public static double findProcessSimilarity(Graph g1, 
											   Graph g2,
											   String algortithm, 
											   double ...param) {
		
		if (algortithm.equals("Greedy")) {
			GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
			Object weights[] = {"ledcutoff", param[0],
								"cedcutoff", param[1],
								"vweight", param[2], 
								"sweight", param[3], 
								"eweight", param[4]};
			
			gedepc.setWeight(weights);
			
			double weight = gedepc.computeGED(g1, g2);
			return (1 - (weight < 0.0000001 ? 0: (weight > 1 ? 1 : weight)));
			
		} else if (algortithm.equals("Hungarian")) {
			LinkedList<VertexPair> mapping = AssingmentProblem.getMappingsVetrexUsingNodeMapping(g1, g2, param[0], param[1]);
			double weight = 0.0;
			for (VertexPair vp : mapping) {
				weight += vp.getWeight();
			}
			return (weight/Math.max(g1.getVertices().size(), g2.getVertices().size()));
		}
		
		return 0;
	}
}
