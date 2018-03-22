package org.processmining.stagemining.algorithms;

import org.processmining.stagemining.models.DecompositionTree;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.deckfour.xes.model.XLog;
import com.aliasi.cluster.LinkDendrogram;
//import com.rapidminer.RapidMiner;

/**
 * This class mine phase models based on min-cut
 * @author Bruce
 *
 */
public class StageMiningBruteForce extends AbstractStageMining {

	/**
	 * 1st argument: log file
	 * 2nd argument: minimum stage size
	 * 3rd argument: the fullname of the class to return the ground truth from the input log file
	 * @param args
	 */
	public static void main(String[] args) {
		OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
		try {
			System.out.println("Import log file");
			XLog log = (XLog)logImporter.importFile(System.getProperty("user.dir") + "\\" + args[0]);
			LogUtilites.addStartEndEvents(log);
			
		    System.out.println("Start phase mining");
		    AbstractStageMining miner = new StageMiningBruteForce();
			miner.setDebug(true);
			
			long startTime = System.currentTimeMillis();
			DecompositionTree tree = miner.mine(log, Integer.valueOf(args[1]));
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Finish phase mining");
			System.out.println("Total Time: " + totalTime/1000 + " seconds");
			
			//-------------------------------
			// Print the result
			//-------------------------------
			tree.print();
			
			//-------------------------------
			// Calculate Rand index
			//-------------------------------
			int bestLevelIndex = tree.getMaxLevelIndex(); //get the bottom level of the tree
			ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
			System.out.println("Best Level Index: " + bestLevelIndex);
			System.out.println("Transition nodes from beginning: " + tree.getTransitionNodes(bestLevelIndex));
			System.out.println("Transition nodes by creation order: " + tree.getTransitionNodesByCreationOrder(bestLevelIndex));
			System.out.println("Modularity by creation order: " + tree.getModularitiesByCreationOrder());
			
			//double randIndex = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
			double fowlkes = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
			//double jaccard = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
			//System.out.println("Rand Index = " + randIndex);
			System.out.println("Fowlkes–Mallows Index = " + fowlkes);
			//System.out.println("Jaccard Index = " + jaccard);
			
			
			
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
		// Compute candidate list
		//-------------------------------
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points (sorted): " + candidates.toString());
		Vertex2[] candidateArray = new Vertex2[candidates.size()];
		candidateArray = candidates.toArray(candidateArray);
		if (this.debug) System.out.println("Candidate array for combinations: " + candidateArray.toString());
		if (this.debug) System.out.println("Number of candidate nodes: " + candidateArray.length);
		long totalCombi = 0;
		int kMax = graph.getActivityVertices().size()/minStageSize; // ONLY take maximum kMax from n, others > kMax will return stage size < minStageSize
		for (int k=1;k<=kMax;k++) {
			totalCombi += CombinatoricsUtils.binomialCoefficient(candidateArray.length, k);
		}
		if (this.debug) System.out.println("Total number of combinations: " + totalCombi);
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		DecompositionTree bestTree = null;
		double bestMod = 0.0;
		long counter = 1;
		for (int k=1; k<=kMax; k++) {
			
			// Get all combinations of k-size from the candidate list
			Iterator<int[]> combinations = new Combinations(candidateArray.length, k).iterator();
			if (this.debug) System.out.println();
			if (this.debug) System.out.println("================= COMBINATION OF " + k + " size: " + 
							CombinatoricsUtils.binomialCoefficient(candidateArray.length, k) + " combinations");
				
			// For each combination of k-size: find a stage decomposition
			while (combinations.hasNext()) {
				int[] nodes = combinations.next();
				if (this.debug) System.out.println();
				if (this.debug) System.out.print("COMBINATION " + (counter++) + ": ");
				for (int i=0;i<nodes.length;i++) {
					if (this.debug) System.out.print(candidateArray[nodes[i]].getName() + ",");
				}
				if (this.debug) System.out.println();
				
				// Create a decomposition tree for one combination of nodes
				DecompositionTree tree = new DecompositionTree(graph);
				LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, 
													new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), 
																			graph.getSink(), 0);
				tree.setRoot(root);
				List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
				rootLevel.add(root);
				tree.addBottomLevel(rootLevel, 0.0);
				
				for (int i=0;i<nodes.length;i++) {
					Vertex2 v = candidateArray[nodes[i]]; // each node from a combination as cut-point
					
					// Select the cluster that contains v
					LinkDendrogram<IVertex> selected = null;
					//if (this.debug) System.out.println("");
					//if (this.debug) System.out.println("Check candidate node: " + v.getName());
					for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
						if (d.getMemberSet().contains(v)) {
							selected = d;
							break;
						}
					}
					if (selected == null) {
						throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
					}
					
					//Perform graph cut on the selected node
					List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
					Set<IVertex> stage1 = cutResult.get(0);
					Set<IVertex> stage2 = cutResult.get(1);
					//if (this.debug) System.out.println("Stage1.size=" + stage1.size() + ". Stage2.size=" + stage2.size());

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
				    	
				    	double mod = tree.computeModularity(bottomLevelTemp);
				    	tree.addBottomLevel(bottomLevelTemp, mod);
			    	}	
			    	else {
			    		if (this.debug) System.out.println("Stop decomposing as stage size is smaller than " + minStageSize);
			    		break; // break here because the other nodes in the combination will have the same small stage size 
			    	}
			    	
			    	
				} // end a combination
				
				// If all nodes in the combination are used as cut-points
				if (tree.getMaxLevelIndex() == nodes.length) {
					double mod = tree.getModularity(tree.getMaxLevelIndex()); // get the modularity of the bottom level
					if (this.debug) System.out.print("Compare modularity with current best SD: ");
					if (mod > bestMod) {
						if (this.debug) System.out.println("Better - accepted");
						bestMod = mod;
						bestTree = tree;
					}
					else {
						if (this.debug) System.out.println("No better - rejected");
						tree.clear();
					}
				}
			}
		}
		
		return bestTree;
	}
	
}
