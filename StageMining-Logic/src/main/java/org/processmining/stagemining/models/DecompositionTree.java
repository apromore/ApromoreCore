package org.processmining.stagemining.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.graph.Vertex2;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;
import org.processmining.stagemining.utils.GraphUtils;

import com.aliasi.cluster.LinkDendrogram;

public class DecompositionTree {
	private WeightedDirectedGraph graph = null;
	private LinkDendrogram<IVertex> root = null;
	
    //Store list of dendrograms at the bottom level based on the order of cut
    //Level 0: the root
    //Level 1: two dendrograms divided from level 0
    //Level 2: three dendrograms, where two are divided from one at level 1, others taken from level 1
    //Level 3: four dendrograms, where two are divided from level 2, others taken from level 2.
    //and so on
    private List<List<LinkDendrogram<IVertex>>> levelDendrograms = new ArrayList<List<LinkDendrogram<IVertex>>>();
    
    //Modularity for one level
    private Map<List<LinkDendrogram<IVertex>>,Double> modularityMap = new HashMap<List<LinkDendrogram<IVertex>>, Double>();
    
    public DecompositionTree(WeightedDirectedGraph graph) {
    	this.graph = graph;
    }
    
    public WeightedDirectedGraph getGraph() {
    	return this.graph;
    }
    
    public LinkDendrogram<IVertex> getRoot() {
    	return this.root;
    }
    
    public void setRoot(LinkDendrogram<IVertex> root) {
    	this.root = root;
    }
    
    /*
     * Store the current list of dendrograms at the bottom level 
     */
    public void addBottomLevel(List<LinkDendrogram<IVertex>> bottomLevel, double modularity) {
    	levelDendrograms.add(bottomLevel);
    	this.modularityMap.put(bottomLevel, modularity);
    }
    
    /**
     * Take a graph cut on v and its cut-set
     * @param v
     * @param containingStage
     * @return
     * @throws Exception
     */
    public List<Set<IVertex>> graphCut(Vertex2 v, LinkDendrogram<IVertex> containingStage) throws Exception {
		List<Set<IVertex>> cutComponents = graph.cut(v);
    	Set<IVertex> cluster0 = new HashSet<IVertex>(containingStage.getMemberSet());
    	cluster0.retainAll(cutComponents.get(0));
    	cluster0.add(v);
    	
    	Set<IVertex> cluster1 = new HashSet<IVertex>(containingStage.getMemberSet());
    	cluster1.removeAll(cluster0);
    	
    	List<Set<IVertex>> result = new ArrayList<Set<IVertex>>();
    	result.add(cluster0);
    	result.add(cluster1);
    	return result;
    	
    }
    
    /**
     * Get a list of dendrograms at a level (used to be a bottom level)
     * @param level
     * @return
     * @throws Exception 
     */
    public List<LinkDendrogram<IVertex>> getLevel(int levelIndex) throws Exception {
    	if (levelIndex < 0 || levelIndex >= levelDendrograms.size()) {
    		throw new Exception("Level Index " + levelIndex + " is out of bound");
    	}
    	return levelDendrograms.get(levelIndex);
    }
    
    public List<LinkDendrogram<IVertex>> getBottomLevel() {
    	return levelDendrograms.get(levelDendrograms.size()-1);
    }
    
    public int getMaxLevelIndex() {
    	return levelDendrograms.size()-1;
    }
    
    /**
     * Get list of transition nodes from beginning to end
     * @param level
     * @return
     * @throws Exception
     */
    public List<IVertex> getTransitionNodes(int levelIndex) throws Exception {
    	if (levelIndex < 0 || levelIndex >= levelDendrograms.size()) {
    		throw new Exception("Level Index " + levelIndex + " is out of bound");
    	}
    	
    	List<IVertex> nodes = new ArrayList<IVertex>();
    	for (LinkDendrogram<IVertex> d : this.levelDendrograms.get(levelIndex)) {
    		if (d.getSink() != this.graph.getSink()) {
    			nodes.add(d.getSink());
    		}
    	}
    	return nodes;
    }
    
    public List<IVertex> getTransitionNodesByCreationOrder(int level) throws Exception {
    	if (level < 0 || level >= levelDendrograms.size()) {
    		throw new Exception("Level " + level + " is out of bound of bottom level");
    	}    	
    	
    	List<IVertex> nodes = new ArrayList<IVertex>();
    	for (int i=0; i<this.levelDendrograms.size(); i++) {
    		List<LinkDendrogram<IVertex>> list = this.levelDendrograms.get(i);
			for (LinkDendrogram<IVertex> d : list) {
				if (!nodes.contains(d.getSink()) && d.getSink() != this.graph.getSink()) {
					nodes.add(d.getSink());
				}
			}
    		if (i==level) break;
    	}
    	
    	return nodes;
    }
    
    public List<Set<IVertex>> getStageList(int levelIndex) throws Exception {
    	if (levelIndex < 0 || levelIndex >= levelDendrograms.size()) {
    		throw new Exception("Level Index " + levelIndex + " is out of bound");
    	}    	
    	List<Set<IVertex>> clusters = new ArrayList<Set<IVertex>>();
    	for (LinkDendrogram<IVertex> d : this.levelDendrograms.get(levelIndex)) {
    		clusters.add(d.getMemberSet());
    	}
    	return clusters;
    }
    
    /**
     * Prepare to compute modularity for a list of stages
     * @param bottomLevel
     * @return
     */
	public double computeModularity(List<LinkDendrogram<IVertex>> bottomLevel) {
		List<Set<IVertex>> oneLevelPhases = new ArrayList<Set<IVertex>>(); // list of phases at the current level
		List<IVertex> transitionNodes = new ArrayList<IVertex>();
		for (int k=0; k<bottomLevel.size(); k++) {
			LinkDendrogram<IVertex> d = bottomLevel.get(k);
			Set<IVertex> oneLevelSet = new HashSet<IVertex>(d.getMemberSet()); // set of nodes in the current phase at the current level
			if (d.getSink()!= graph.getSink()) {
				Vertex2 tNode = (Vertex2) d.getSink(); // the cut point (or transition node)
				transitionNodes.add(tNode);
			}
			// Add the source and the sink of the graph
			if (d.getSource() == graph.getSource()) oneLevelSet.add(d.getSource());
			if (d.getSink() == graph.getSink()) oneLevelSet.add(d.getSink());
			
			oneLevelPhases.add(oneLevelSet);
		}

		return GraphUtils.computeModularitySplitTransitionNode(graph, oneLevelPhases, transitionNodes);
	}
	
    public double getModularity(int levelIndex) throws Exception {
    	if (levelIndex < 0 || levelIndex >= levelDendrograms.size()) {
    		throw new Exception("Level index " + levelIndex + " is out of bound of bottom level");
    	}    	
    	return this.modularityMap.get(this.levelDendrograms.get(levelIndex));
    }
    
    public double getModularity(List<LinkDendrogram<IVertex>> level) throws Exception {
    	if (!this.modularityMap.containsKey(level)) {
    		throw new Exception("Non-existant level");
    	}    	
    	return this.modularityMap.get(level);
    }
    
    public List<Double> getModularitiesByCreationOrder() throws Exception {
    	List<Double> modularities = new ArrayList<Double>();
    	for (int i=0;i<=this.getMaxLevelIndex();i++) {
    		modularities.add(this.getModularity(i));
    	}
    	return modularities;
    }
	
	public int getBestLevelIndex() throws Exception {
		int bestLevel = 0;
		double bestMeasure = 0.0;
		for (int i=0; i<=this.getMaxLevelIndex(); i++) {
			if (this.getModularity(i) > bestMeasure) {
				bestMeasure = this.getModularity(i);
				bestLevel = i;
			}
		}
		return bestLevel;
	}
	
	public void print() throws Exception {
		System.out.println("RESULT");
		//System.out.println("======= Level 1 - Global Measure 0.0"); //by default level 1 is the whole graph, global measure = 0.0
		//start from 1 since the 0th element is the whole graph
		
		for (int i=0; i<=this.getMaxLevelIndex(); i++) {
			System.out.println("======= Level " + (i+1) + " - Global Measure = " + this.getModularity(i) + " =================");
			
			// Within one level: list of dendrograms
			for (int j=0; j<this.getLevel(i).size(); j++) {
				System.out.println("---------------- Phase " + (j+1) + " ----------------");
				System.out.println("Node set: " + this.getLevel(i).get(j).getMemberSet().toString());
				System.out.println("TRANSITION NODE: " + this.getLevel(i).get(j).getSink().getName());
				System.out.println("Cut-set: " + ((Vertex2)this.getLevel(i).get(j).getSink()).getMinCutSet());
				System.out.println("---------------- End of Phase " + (j+1) + " ------------");
			}
		}
	}
	
	public List<Set<String>> getActivityLabelSets(int levelIndex) throws Exception {
		List<Set<IVertex>> stageModel = this.getStageList(levelIndex);
		List<IVertex> transitionNodes = this.getTransitionNodes(levelIndex);
		List<Set<String>> result = new ArrayList<Set<String>>();
		
		if (stageModel != null) {
			for (int i=0; i<stageModel.size(); i++) {
				Set<String> stageNodeNames = new HashSet<String>();
				for (IVertex v : stageModel.get(i)) {
					if (v != this.graph.getSource() && v != this.graph.getSink()) {
						stageNodeNames.add(v.getName());
					}
				}
				if (i < transitionNodes.size()) stageNodeNames.add(transitionNodes.get(i).getName());
				result.add(stageNodeNames);
			}
		}
		else {
			throw new Exception("Something wrong. Null stagemodel is returned for level index =" + levelIndex + " of the decomposition tree");
		}
		return result;
	}
	
	public void clear() {
		this.levelDendrograms.clear();
		this.modularityMap.clear();
		this.graph = null;
		this.root = null;
	}
	

	
//	public double computeModularity(int level) throws Exception {
//		double mod = this.computeModularity(this.getLevel(level));
//		this.setModularity(this.getLevel(level), mod);
//		return mod;
//	}
}
