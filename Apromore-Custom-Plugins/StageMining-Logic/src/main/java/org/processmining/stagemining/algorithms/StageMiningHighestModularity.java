package org.processmining.stagemining.algorithms;

import org.processmining.stagemining.models.DecompositionTree;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.groundtruth.ExampleClass;
import org.processmining.stagemining.models.graph.Vertex2;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;
import org.processmining.stagemining.utils.GraphUtils;
import org.processmining.stagemining.utils.LogUtilites;
import org.processmining.stagemining.utils.Measure;
import org.processmining.stagemining.utils.OpenLogFilePlugin;
import org.deckfour.xes.model.XLog;
import com.aliasi.cluster.LinkDendrogram;
//import com.rapidminer.RapidMiner;

/**
 * This class mine phase models based on min-cut
 * @author Bruce
 *
 */
public class StageMiningHighestModularity extends AbstractStageMining {
	
	/**
	 * 1st argument: log file
	 * 2nd argument: minimum stage size
	 * 3rd argument: number of stages
	 * 4th argument: the fullname of the class to return the ground truth from the input log file
	 * @param args
	 */
	public static void main(String[] args) {
		OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
		try {
			System.out.println("Import log file");
			XLog log = (XLog)logImporter.importFile(System.getProperty("user.dir") + "\\" + args[0]);
			LogUtilites.addStartEndEvents(log);
		    
		    System.out.println("Start phase mining");
		    AbstractStageMining miner = new StageMiningHighestModularity();
			miner.setDebug(true);
			long startTime = System.currentTimeMillis();
			DecompositionTree tree = miner.mine(log, Integer.valueOf(args[1]));
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Total Time: " + totalTime + " milliseconds");
			System.out.println("Finish phase mining");
			
			//-------------------------------
			// Print the result
			//-------------------------------
			tree.print();
			
			//-------------------------------
			// Calculate index
			//-------------------------------
			int bestLevelIndex = tree.getBestLevelIndex();
//			
//			for (Set<IVertex> stage : tree.getStageList(bestLevelIndex)) {
//				System.out.println("Source: " + GraphUtils.getDirectedConnectionWeightFromSource(tree.getGraph(), tree.getGraph().getSource(), stage));
//				System.out.println("Sink: " + GraphUtils.getDirectedConnectionWeightToSink(tree.getGraph(), stage, tree.getGraph().getSink()));
//			}
			
			ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
			System.out.println("Best Level Index: " + bestLevelIndex);
			System.out.println("Transition nodes from beginning: " + tree.getTransitionNodes(bestLevelIndex));
			System.out.println("Transition nodes by creation order: " + tree.getTransitionNodesByCreationOrder(bestLevelIndex));
			System.out.println("Modularity by creation order: " + tree.getModularitiesByCreationOrder());
			System.out.println("Stages = " + tree.getActivityLabelSets(bestLevelIndex).toString());
			System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
			
//			double randIndex = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
			double fowlkes = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
//			double jaccard = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
//			System.out.println("Rand Index = " + randIndex);
			System.out.println("Fowlkes–Mallows Index = " + fowlkes);
//			System.out.println("Jaccard Index = " + jaccard);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DecompositionTree mine(XLog log, int minStageSize) throws Exception {
		//-------------------------------
		// Build graph from log
		//-------------------------------
		if (this.debug) System.out.println("Build graph from log");
		WeightedDirectedGraph graph = null;
		try {
			graph = GraphUtils.buildGraph(log);
			if (graph==null) {
				if (this.debug) System.out.println("Bulding graph from log failed.");
				//return null;
			}			
			GraphUtils.removeSelfLoops(graph);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//-------------------------------
		// Compute candidate list
		//-------------------------------
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		
		// Initialize the dendrogram with root node
		// NOTE: root node contains all nodes in the graph except the source and the sink
		DecompositionTree tree = new DecompositionTree(graph);
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		List<LinkDendrogram<IVertex>> SD = tree.getBottomLevel();
		
		double SDMod = tree.getModularity(SD);
		Vertex2 SDNode = null;
		Set<Vertex2> currentCandidates = new HashSet<Vertex2>(candidates); 
		Set<Vertex2> bestNodes = new HashSet<Vertex2>();
		
		while (!currentCandidates.isEmpty()) {
			for (Vertex2 v : currentCandidates) {
				if (this.debug) System.out.println();
				if (this.debug) System.out.println("Check node:" + v.getName());
				
				// Find a stage containing the node to cut
		    	LinkDendrogram<IVertex> selected = null;
		    	for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
					if (d.getMemberSet().contains(v)) {
						selected = d;
						break;
					}
				}
				if (selected == null) {
					throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
				}
				
				// Take graph cut
				List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
				Set<IVertex> stage1 = cutResult.get(0);
				Set<IVertex> stage2 = cutResult.get(1);
				if (this.debug) System.out.println("Stage1.size = " + stage1.size() + ", Stage2.size = " + stage2.size());
				
		    	//Check min stage size
		    	if (stage1.size() >= minStageSize && stage2.size() >= minStageSize) { 
			    	//Create the new bottom level
			    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, stage1, selected.getSource(), v, v.getMinCut());
					LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, stage2, v, selected.getSink(), v.getMinCut());
			    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
			    	int index = bottomLevelTemp.indexOf(selected);
			    	bottomLevelTemp.remove(selected);
			    	bottomLevelTemp.add(index, dendro1);
			    	bottomLevelTemp.add(index+1, dendro2);	    	
					
					//Compute modularity and the best bottom level
					double newMod = tree.computeModularity(bottomLevelTemp);
					if (this.debug) System.out.println("Modularity = " + newMod);
					if (newMod > SDMod) {
						SD = bottomLevelTemp;
						SDNode = v;
						SDMod = newMod;
					}
					else {
						if (this.debug) System.out.println("** Modularity is decreasing");
					}
		    	}
		    	else {
		    		if (this.debug) {
		    			System.out.println("Cluster size is smaller than the minimum stage size!");
//			    		System.out.println("Cluster1=" + stage1.toString());
//			    		System.out.println("Cluster2=" + stage2.toString());
		    		}
		    	}
			}
			
			//Attach the best bottom level to the decomposition tree
			if (SD != tree.getBottomLevel()) {
				if (this.debug) System.out.println("BEST TRANSITION NODE: " + SDNode.getName() + ". Modularity = " + SDMod);
				if (this.debug) System.out.println("**********************************************");
				tree.addBottomLevel(SD, SDMod);
				bestNodes.add(SDNode);
				currentCandidates = new HashSet<Vertex2>(candidates);
				currentCandidates.removeAll(bestNodes);
			}
			else {
				//System.out.println("No more valid cut points found from the candidate set. Current candidate set=" + currentCandidates.toString());
				break;
			}
		}

		return tree;
	}
	
	public DecompositionTree mine(XLog log, int minStageSize, int stageCount) throws Exception {
		//-------------------------------
		// Build graph from log
		//-------------------------------
		if (this.debug) System.out.println("Build graph from log");
		WeightedDirectedGraph graph = null;
		try {
			graph = GraphUtils.buildGraph(log);
			if (graph==null) {
				if (this.debug) System.out.println("Bulding graph from log failed.");
				//return null;
			}			
			GraphUtils.removeSelfLoops(graph);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//-------------------------------
		// Compute candidate list
		//-------------------------------
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		
		// Initialize the dendrogram with root node
		// NOTE: root node contains all nodes in the graph except the source and the sink
		DecompositionTree tree = new DecompositionTree(graph);
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		List<LinkDendrogram<IVertex>> SD = tree.getBottomLevel();
		
		double SDMod = tree.getModularity(SD);
		Vertex2 SDNode = null;
		Set<Vertex2> currentCandidates = new HashSet<Vertex2>(candidates); 
		Set<Vertex2> bestNodes = new HashSet<Vertex2>();
		
		while (!currentCandidates.isEmpty() && tree.getBottomLevel().size() < stageCount) {
			for (Vertex2 v : currentCandidates) {
				if (this.debug) System.out.println();
				if (this.debug) System.out.println("Check node:" + v.getName());
				
				// Find a stage containing the node to cut
		    	LinkDendrogram<IVertex> selected = null;
		    	for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
					if (d.getMemberSet().contains(v)) {
						selected = d;
						break;
					}
				}
				if (selected == null) {
					throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
				}
				
				// Take graph cut
				List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
				Set<IVertex> stage1 = cutResult.get(0);
				Set<IVertex> stage2 = cutResult.get(1);
				if (this.debug) System.out.println("Stage1.size = " + stage1.size() + ", Stage2.size = " + stage2.size());
				
		    	//Check min stage size
		    	if (stage1.size() >= minStageSize && stage2.size() >= minStageSize) { 
			    	//Create the new bottom level
			    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, stage1, selected.getSource(), v, v.getMinCut());
					LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, stage2, v, selected.getSink(), v.getMinCut());
			    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
			    	int index = bottomLevelTemp.indexOf(selected);
			    	bottomLevelTemp.remove(selected);
			    	bottomLevelTemp.add(index, dendro1);
			    	bottomLevelTemp.add(index+1, dendro2);	    	
					
					//Compute modularity and the best bottom level
					double newMod = tree.computeModularity(bottomLevelTemp);
					if (this.debug) System.out.println("Modularity = " + newMod);
					if (newMod > SDMod) {
						SD = bottomLevelTemp;
						SDNode = v;
						SDMod = newMod;
					}
					else {
						if (this.debug) System.out.println("** Modularity is decreasing");
					}
		    	}
		    	else {
		    		if (this.debug) {
		    			System.out.println("Cluster size is smaller than the minimum stage size!");
//			    		System.out.println("Cluster1=" + stage1.toString());
//			    		System.out.println("Cluster2=" + stage2.toString());
		    		}
		    	}
			}
			
			//Attach the best bottom level to the decomposition tree
			if (SD != tree.getBottomLevel()) {
				if (this.debug) System.out.println("BEST TRANSITION NODE: " + SDNode.getName() + ". Modularity = " + SDMod);
				if (this.debug) System.out.println("**********************************************");
				tree.addBottomLevel(SD, SDMod);
				bestNodes.add(SDNode);
				currentCandidates = new HashSet<Vertex2>(candidates);
				currentCandidates.removeAll(bestNodes);
			}
			else {
				//System.out.println("No more valid cut points found from the candidate set. Current candidate set=" + currentCandidates.toString());
				break;
			}
		}

		return tree;
	}
	
}
