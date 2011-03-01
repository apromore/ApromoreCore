package org.apromore.toolbox.similaritySearch.tools;


import java.util.LinkedList;
import java.util.List;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.toolbox.similaritySearch.algorithms.FindModelSimilarity;
import org.apromore.toolbox.similaritySearch.common.CPFModelParser;
import org.apromore.toolbox.similaritySearch.common.IdGeneratorHelper;
import org.apromore.toolbox.similaritySearch.graph.Graph;



public class SearchForSimilarProcesses {
	
//	public static LinkedList<String> searchForSimilarProcesses(CanonicalProcessType search, 
//												List<CanonicalProcessType> database,
//												String algortithm, 
//												double threshold,
//												double ...param) {
//		
//		LinkedList<String> toReturn = new LinkedList<String>();
//		Graph searchGraph = CPFModelParser.readModel(search);
//		
//		for (CanonicalProcessType d : database) {
//			Graph dbGraph = CPFModelParser.readModel(d);
//			
//			double similarity = FindModelSimilarity.findProcessSimilarity(searchGraph, dbGraph, algortithm, param);
//			System.out.println("similarity " + similarity);
//			if (similarity >= threshold) {
//				toReturn.add(d.getName());
//			}
//		}
//		return toReturn;
//	}
	
	public static double findProcessesSimilarity(
			CanonicalProcessType search, CanonicalProcessType d,
			String algortithm, double... param) {

	System.out.println(">> SIMILARITY " + search.getName() + " <> "+ d.getName() + " id: "+d.getRootId());
		
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
//		System.out.println("similarity " + similarity);
		return similarity;
	}

}
