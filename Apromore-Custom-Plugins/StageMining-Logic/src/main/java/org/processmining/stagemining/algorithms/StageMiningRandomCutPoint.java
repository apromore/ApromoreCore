package org.processmining.stagemining.algorithms;

import org.processmining.stagemining.models.DecompositionTree;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
 * 2nd argument: the fullname of the class to return the ground truth from the input log file
 * 3rd argument: minimum stage size
 * @author Bruce
 *
 */
public class StageMiningRandomCutPoint extends AbstractStageMining {

	public static void main(String[] args) {
		
		/*
		List<Set<String>> X = new ArrayList<Set<String>>();
		Set<String> X1 = new HashSet<String>();
		
		X1 = new HashSet<String>();
		X1.add("a");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("b");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("c");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("s1");
		X.add(X1);
		
		X1 = new HashSet<String>();
		X1.add("d");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("e");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("f");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("s2");
		X.add(X1);
		
		X1 = new HashSet<String>();
		X1.add("g");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("h");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("i");
		X.add(X1);
		X1 = new HashSet<String>();
		X1.add("s3");
		X.add(X1);
		
//		X1 = new HashSet<String>();
//		X1.add("j");
//		X1.add("k");
//		X1.add("l");
//		X.add(X1);
//		
//		X1 = new HashSet<String>();
//		X1.add("s4");
//		X.add(X1);
		
		//////////////////////////////////////////
		
		List<Set<String>> Y = new ArrayList<Set<String>>();
		Set<String> Y1 = new HashSet<String>();
		Y1.add("a");
		Y1.add("b");
		Y1.add("c");
		Y1.add("s1");
		Y.add(Y1);
		
		Y1 = new HashSet<String>();
		Y1.add("d");
		Y1.add("e");
		Y1.add("f");
		Y1.add("s2");
		Y.add(Y1);
		
		Y1 = new HashSet<String>();
		Y1.add("g");
		Y1.add("h");
		Y1.add("i");
		Y1.add("s3");
		Y.add(Y1);
		
//		Y1 = new HashSet<String>();
//		Y1.add("j");
//		Y1.add("k");
//		Y1.add("l");
//		Y1.add("s4");
//		Y.add(Y1);
		
		
		try {
			double randIndex = Measure.computeMeasure(X, Y, 1);
			double fowlkes = Measure.computeMeasure(X, Y, 2);
			double jaccard = Measure.computeMeasure(X, Y, 3);
			System.out.println("Rand Index = " + randIndex);
			System.out.println("Fowlkes = " + fowlkes);
			System.out.println("Jaccard = " + jaccard);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
		OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
		try {
			System.out.println("Import log file");
			XLog log = (XLog)logImporter.importFile(System.getProperty("user.dir") + "\\" + args[0]);
			LogUtilites.addStartEndEvents(log);
		    
		    System.out.println("Start phase mining");
		    AbstractStageMining miner = new StageMiningRandomCutPoint();
			miner.setDebug(true);
			
			DecompositionTree tree = miner.mine(log, Integer.valueOf(args[1]));
			
			//-------------------------------
			// Print the result
			//-------------------------------
			tree.print();
			
			//-------------------------------
			// Calculate Rand index
			//-------------------------------
			int bestLevelIndex = tree.getBestLevelIndex();
			ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
			System.out.println("Best Level Index: " + bestLevelIndex);
			System.out.println("Transition nodes from beginning: " + tree.getTransitionNodes(bestLevelIndex));
			System.out.println("Transition nodes by creation order: " + tree.getTransitionNodesByCreationOrder(bestLevelIndex));
			System.out.println("Modularity by creation order: " + tree.getModularitiesByCreationOrder());
			
			double randIndex = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
			double fowlkes = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
			double jaccard = Measure.computeMeasure(tree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
			System.out.println("Rand Index = " + randIndex);
			System.out.println("Fowlkes–Mallows Index = " + fowlkes);
			System.out.println("Jaccard Index = " + jaccard);
			
			System.out.println("Finish phase mining");

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	public DecompositionTree mine(XLog log, int minStageSize) throws Exception {
		
		//-------------------------------
		// Build graph from log
		//-------------------------------
		System.out.println("Build graph from log");
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
		System.out.println("Search for candidate cut-points");
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		System.out.println("Build dendrograms");
		DecompositionTree tree = new DecompositionTree(graph);
		
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		List<LinkDendrogram<IVertex>> SD_Best = tree.getBottomLevel();
		double SD_Best_Mod = tree.getModularity(SD_Best);
		Set<Vertex2> selectedNodes = new HashSet<Vertex2>();
		
		List<Vertex2> currentCandidates = new ArrayList<Vertex2>(candidates);
		while (!currentCandidates.isEmpty()) {
			// Randomly select the next node from the current candidates
			
			Random rand = new Random();
			int  randIndex = rand.nextInt(currentCandidates.size());
			
			if (this.debug) System.out.println();
			if (this.debug) System.out.println("Random number = " + randIndex + " for range from 0 to " + candidates.size());
			Vertex2 v = currentCandidates.get(randIndex); //pick up one node randomly
			if (this.debug) System.out.println("Check candidate node: " + v.getName());
			
			// Find a stage containing the node to cut
			LinkDendrogram<IVertex> selected = null;
			for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
				if (d.getMemberSet().contains(v)) {
					selected = d;
					break;
				}
			}
			if (selected == null) {
				throw new Exception("Cannot find a containg cluster at the bottom level of the decomposition tree for node " + v.getName());
			}
			
			// Take graph cut
			List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
			Set<IVertex> stage1 = cutResult.get(0);
			Set<IVertex> stage2 = cutResult.get(1);
			if (this.debug) System.out.println("Stage1.size = " + stage1.size() + ", Stage2.size = " + stage2.size());
			
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
				if (newMod > SD_Best_Mod) {
					tree.addBottomLevel(bottomLevelTemp, newMod);
					SD_Best_Mod = newMod;
				}
				else {
					//break;
				}
	    	}
			else {
				if (this.debug) {
					System.out.println("Cluster size is smaller than the minimum stage size!");
//		    		System.out.println("Cluster1=" + stage1.toString());
//		    		System.out.println("Cluster2=" + stage2.toString());
				}
	    	}
			
			selectedNodes.add(v);
			currentCandidates = new ArrayList<Vertex2>(candidates);
			currentCandidates.removeAll(selectedNodes);
		}
		
		return tree;
	}
	
	
	
}
