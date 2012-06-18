package org.apromore.service.helper;

import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.TreeVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chathura Ekanayake
 */
public class OperationContext {

	private CPF graph;
	private TreeVisitor treeVisitor;
	private Map<String, Integer> contentUsage;
	private Map<String, Integer> processedFragmentTypes;
	
	public OperationContext() {
		contentUsage = new HashMap<String, Integer>();
		processedFragmentTypes = new HashMap<String, Integer>();
		processedFragmentTypes.put("S", 0);
		processedFragmentTypes.put("P", 0);
		processedFragmentTypes.put("R", 0);
	}

	public CPF getGraph() {
		return graph;
	}
	
	public void setGraph(CPF graph) {
		this.graph = graph;
	}
	
	public TreeVisitor getTreeVisitor() {
		return treeVisitor;
	}
	
	public void setTreeVisitor(TreeVisitor treeVisitor) {
		this.treeVisitor = treeVisitor;
	}

	public void addProcessedFragmentType(String fragmentType) {
		Integer typeCount = processedFragmentTypes.get(fragmentType);
		if (typeCount == null) {
			typeCount = 1;
		} else {
			typeCount++;
		}
		processedFragmentTypes.put(fragmentType, typeCount);
	}

	public int getContentUsage(String contentId) {
		if (contentUsage.containsKey(contentId))
			return contentUsage.get(contentId);
		else 
			return 0;
	}

	public void incrementContentUsage(String contentId) {
		if (!contentUsage.containsKey(contentId)) {
			contentUsage.put(contentId, 1);
		} else {
			int usage = contentUsage.get(contentId);
			usage++;
			contentUsage.put(contentId, usage);
		}
	}
	
	
}
