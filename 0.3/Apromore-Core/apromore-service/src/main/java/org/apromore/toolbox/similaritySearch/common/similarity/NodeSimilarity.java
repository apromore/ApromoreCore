package org.apromore.toolbox.similaritySearch.common.similarity;


import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.apromore.toolbox.similaritySearch.graph.Vertex.Type;


public class NodeSimilarity {

	public static double findNodeSimilarity(Vertex n, Vertex m, double labelTreshold) {
		// functions or events -
		// compare the labels of these nodes 
		// tokenize, stem and find the similarity score
		if ((n.getType().equals(Type.function) && m.getType().equals(Type.function)
		 || n.getType().equals(Type.event) && m.getType().equals(Type.event)) 
		 && AssingmentProblem.canMap(n, m)) {
			return LabelEditDistance.edTokensWithStemming(m.getLabel(), 
					n.getLabel(), Settings.STRING_DELIMETER,
					Settings.getEnglishStemmer(), true);
			
		} 
		// gateways
		else if (n.getType().equals(Type.gateway) && m.getType().equals(Type.gateway)) {
			// splits can not be merged with joins
			if (Graph.isSplit(n) && Graph.isJoin(m)
					|| Graph.isSplit(m) && Graph.isJoin(n)) {
				return 0;
			}
            return SemanticSimilarity.getSemanticSimilarity(n, m, labelTreshold);
		}
		return 0;
	}
}
