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

/**
 * 1st argument: log file
 * 2nd argument: minimum stage size
 * 3rd argument: the fullname of the class to return the ground truth from the input log file
 * @param args
 */
public class StageMiningLowestMinCutOnly extends AbstractStageMining {
	
	/**
	 * 1st argument: log file
	 * 2nd argument: name of ending event
	 * 3rd argument: transition node will be included in the preceding or suceeding phase
	 * 4th argument: sequence threshold.
	 * @param args
	 */
	public static void main(String[] args) {
		OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
		try {
			System.out.println("Import log file");
			XLog log = (XLog)logImporter.importFile(System.getProperty("user.dir") + "\\" + args[0]);
			LogUtilites.addStartEndEvents(log);
		    
		    System.out.println("Start phase mining");
		    AbstractStageMining miner = new StageMiningLowestMinCutOnly();
			
			DecompositionTree tree = miner.mine(log, Integer.valueOf(args[1]));
			miner.setDebug(true);
			System.out.println("Finish phase mining");
			
			
			//-------------------------------
			// Print the result
			//-------------------------------
			tree.print();
			
			//-------------------------------
			// Calculate Rand index
			//-------------------------------
//			int bestLevel = tree.getBestLevelIndex();
			int bestLevelIndex = tree.getMaxLevelIndex();
			ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
			System.out.println("Level Index: " + bestLevelIndex);
			System.out.println("Transition nodes from beginning: " + tree.getTransitionNodes(bestLevelIndex));
			System.out.println("Transition nodes by creation order: " + tree.getTransitionNodesByCreationOrder(bestLevelIndex));
			System.out.println("Stages = " + tree.getActivityLabelSets(bestLevelIndex).toString());
			System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
			
			double randIndex = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
			double fowlkes = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
			double jaccard = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
			System.out.println("Rand Index = " + randIndex);
			System.out.println("Fowlkes–Mallows Index = " + fowlkes);
			System.out.println("Jaccard Index = " + jaccard);
			
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
				System.out.println("Bulding graph from log failed.");
				//return null;
			}			
			GraphUtils.removeSelfLoops(graph);
		} catch (ParseException e) {
			e.printStackTrace();
			//return null;
		}
		
		//-------------------------------
		// Build decomposition tree based on graph cuts
		//-------------------------------
		if (this.debug) System.out.println("Build dendrograms");
		
		// Initialize the decomposition tree
		DecompositionTree tree = new DecompositionTree(graph);
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		
		// Compute min-cut for all vertices in the graph
		// NOTE: the candidate set has been sorted in ascending order of min-cut
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		for (Vertex2 v : candidates) {
			LinkDendrogram<IVertex> selected = null;
			if (this.debug) System.out.println("Check candidate node: " + v.getName());
			
			// Select a dendrogram with min cut at the bottom level
			for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
				if (d.getMemberSet().contains(v)) {
					selected = d;
					break;
				}
			}
			if (selected == null) {
				throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
			}
			
			//Perform graph cut
			List<Set<IVertex>> cutComponents = graph.cut(v);
	    	Set<IVertex> cluster0 = new HashSet<IVertex>(selected.getMemberSet());
	    	cluster0.retainAll(cutComponents.get(0));
	    	cluster0.add(v);
	    	
	    	Set<IVertex> cluster1 = new HashSet<IVertex>(selected.getMemberSet());
	    	cluster1.removeAll(cluster0);
	    	if (this.debug) System.out.println("Cluster1.size=" + cluster0.size() + ". Cluster2.size=" + cluster1.size());

	    	//Check the min stage size
	    	if ((cluster0.size()+1) < minStageSize || (cluster1.size()) < minStageSize) {
	    		if (this.debug) System.out.println("Cluster size is too small for cut-point = " + v.getName());
	    		if (this.debug) System.out.println("Cluster1=" + cluster0.toString());
	    		if (this.debug) System.out.println("Cluster2=" + cluster1.toString());
	    	}
	    	else {
		    	//Create the new bottom level and attach it to the decomposition tree
		    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, cluster0, selected.getSource(), v, v.getMinCut());
				LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, cluster1, v, selected.getSink(), v.getMinCut());
		    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
		    	int index = bottomLevelTemp.indexOf(selected);
		    	bottomLevelTemp.remove(selected);
		    	bottomLevelTemp.add(index, dendro1);
		    	bottomLevelTemp.add(index+1, dendro2);	
		    	
		    	double mod = tree.computeModularity(bottomLevelTemp);
		    	tree.addBottomLevel(bottomLevelTemp, mod);
	    	}
		}
		
		return tree;
	}
	
	
	
	
	
}
