package org.processmining.stagemining.algorithms;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.groundtruth.ExampleClass;
import org.processmining.stagemining.utils.LogUtilites;
import org.processmining.stagemining.utils.Measure;
import org.processmining.stagemining.utils.OpenLogFilePlugin;
import org.deckfour.xes.model.XLog;
//import com.rapidminer.RapidMiner;

/**
 * This class mine phase models based on min-cut
 * @author Bruce
 *
 */
public class StageMiningHighestModularityNoStageSize {
	/**
	 * 1st argument: log file
	 * 2nd argument: minimum stage size (NOT USED IN THIS CLASS)
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
		    AbstractStageMining miner = new StageMiningHighestModularity();
			miner.setDebug(false);
			
			double bestMod = 0;
			DecompositionTree bestTree = null;
			int bestStageSize = 0;
			for (int k=2;k<(LogUtilites.getDistinctEventClassCount(log) / 2);k++) {
				System.out.println();
				System.out.println("STAGE MINING FOR minStageSize = " + k);
				
				DecompositionTree ktree = miner.mine(log,k);
				
				if (ktree != null) {
					//ktree.print();
					double mod = ktree.getModularity(ktree.getBestLevelIndex());
					int bestLevelIndex = ktree.getBestLevelIndex();
					ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
//					System.out.println("Best Level Index = " + bestLevelIndex);
					System.out.println("Transition nodes from beginning: " + ktree.getTransitionNodes(bestLevelIndex));
					System.out.println("Stages = " + ktree.getActivityLabelSets(bestLevelIndex).toString());
//					System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
					System.out.println("Modularity by creation order: " + ktree.getModularitiesByCreationOrder());
					System.out.println("Best Modularity = " + mod);
					
//					double randIndex = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
					double fowlkes = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
//					double jaccard = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
//					System.out.println("Rand Index = " + randIndex);
					System.out.println("Fowlkes–Mallows Index = " + fowlkes);
//					System.out.println("Jaccard Index = " + jaccard);
					
					if (mod > bestMod) {
						bestMod = mod;
						bestTree = ktree;
						bestStageSize = k;
					}
				}
			}
			
			if (bestTree != null) {
				System.out.println("");
				System.out.println("BEST STAGE DECOMPOSITION");
				//-------------------------------
				// Print the result
				//-------------------------------
				//bestTree.print();
				
				//-------------------------------
				// Calculate Rand index
				//-------------------------------
				int bestLevelIndex = bestTree.getBestLevelIndex();
				ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
				System.out.println("Best Level Index = " + bestLevelIndex);
				System.out.println("Best stage size = " + bestStageSize);
				System.out.println("Transition nodes from beginning: " + bestTree.getTransitionNodes(bestLevelIndex));
				System.out.println("Transition nodes by creation order: " + bestTree.getTransitionNodesByCreationOrder(bestLevelIndex));
				System.out.println("Modularity by creation order: " + bestTree.getModularitiesByCreationOrder());
				System.out.println("Stages = " + bestTree.getActivityLabelSets(bestLevelIndex).toString());
				System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
				
//				double randIndex = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
				double fowlkes = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
//				double jaccard = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
//				System.out.println("Rand Index = " + randIndex);
				System.out.println("Fowlkes–Mallows Index = " + fowlkes);
//				System.out.println("Jaccard Index = " + jaccard);
				
				System.out.println("Finish phase mining");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
