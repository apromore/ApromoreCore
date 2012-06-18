package org.apromore.toolbox.similaritySearch.tools;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.toolbox.similaritySearch.algorithms.FindModelSimilarity;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;

public class SearchForSimilarProcesses {

	
	public static double findProcessesSimilarity(CanonicalProcessType search, CanonicalProcessType d, String algortithm, double... param) {
		if (search.getNet().size() == 0 || d.getNet().size() == 0) {
			return 0;
		}
		
		Graph searchGraph = CPFModelParser.readModel(search);
		searchGraph.setIdGenerator(new IdGeneratorHelper());
		searchGraph.removeEmptyNodes();
		
		double similarity = 0;
		for (Graph dbGraph : CPFModelParser.readModels(d)) {
			dbGraph.setIdGenerator(new IdGeneratorHelper());
			dbGraph.removeEmptyNodes();
			
			double netsimilarity = FindModelSimilarity.findProcessSimilarity(
					searchGraph, dbGraph, algortithm, param);
			if (netsimilarity > similarity) {
				similarity = netsimilarity;
			}
		}
		return similarity;
	}

}
