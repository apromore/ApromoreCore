package org.apromore.toolbox.similaritySearch.common.similarity;

import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.graph.Vertex;



public class SemanticSimilarity {

	public static double getSemanticSimilarity(Vertex v1, Vertex v2, double labelTreshold) {
		
		LinkedList<Vertex> v1NonGWParents = v1.getAllNonGWParents();
		LinkedList<Vertex> v2NonGWParents = v2.getAllNonGWParents();
		LinkedList<Vertex> v1NonGWChildren = v1.getAllNonGWChildren();
		LinkedList<Vertex> v2NonGWChildren = v2.getAllNonGWChildren();
		
		LinkedList<VertexPair> parentMappings = AssingmentProblem.getMappingsVetrex(v1NonGWParents, v2NonGWParents, labelTreshold, Settings.getEnglishStemmer(), 0);
		LinkedList<VertexPair> childMappings = AssingmentProblem.getMappingsVetrex(v1NonGWChildren, v2NonGWChildren, labelTreshold, Settings.getEnglishStemmer(), 0);

		return (double)(parentMappings.size() + childMappings.size()) 
				/ (double)(Math.max(v1NonGWParents.size(), v2NonGWParents.size()) + Math.max(v1NonGWChildren.size(), v2NonGWChildren.size()));
	}
}
