package org.apromore.processdiscoverer.dfg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.collectors.FrequencySetPopulator;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class DFGAbstraction extends AbstractAbstraction {
	public DFGAbstraction(LogDFG logDfg, AbstractionParams params) {
		super(logDfg, params);
		this.diagram = logDfg.getDFG(params);
		this.updateWeights(params);
	}
	

	@Override
	protected void updateArcWeights(AbstractionParams params) {
		arcPrimaryWeights.clear();
		arcSecondaryWeights.clear();
		
		//Edge label format: "[XX\nYY]", XX is the main number, YY is the secondary number
		ArcInfoCollector arcInfoCollector = logDfg.getArcInfoCollector();
		SimplifiedLog log = this.logDfg.getSimplifiedLog();
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
//			String mainNumber = edge.getLabel();
//            String secondaryNumber= "";
//            if(mainNumber.contains("[")) {
//                if(mainNumber.contains("\n")) {
//                    secondaryNumber = mainNumber.substring(mainNumber.indexOf("\n"), mainNumber.length() - 1);
//                    mainNumber = mainNumber.substring(1, mainNumber.indexOf("\n"));
//                }else mainNumber = mainNumber.substring(1, mainNumber.length() - 1);
//            }else {
//                mainNumber = "1";
//                secondaryNumber = "1";
//            }
//            mainNumber = fixNumber(mainNumber);
//            arcPrimaryWeights.put(edge, Double.parseDouble(mainNumber));
//            arcSecondaryWeights.put(edge, secondaryNumber.isEmpty() ? 0 : Double.parseDouble(secondaryNumber));
			List<Integer> sources = log.getCollapsedNameMapping().get(edge.getSource().getLabel());
			List<Integer> targets = log.getCollapsedNameMapping().get(edge.getTarget().getLabel());
			Set<Arc> foundArcs = new HashSet<>();
			for (int source: sources) {
				for (int target: targets) {
					Arc arc = logDfg.getArc(source, target);
					if (arc != null) {
						if (logDfg.isAcceptedArc(source, target, params)) {
							foundArcs.add(arc);
						}
					}
				}
			}
			
			LongArrayList primaryPopulation = new LongArrayList();
			for (Arc arc : foundArcs) {
				primaryPopulation.addAll(arcInfoCollector.getArcMeasurePopulation(arc, params.getPrimaryType()));
			}
			if (!primaryPopulation.isEmpty()) {
				arcPrimaryWeights.put(edge, FrequencySetPopulator.getAggregateInformation(primaryPopulation, params.getPrimaryAggregation()));
			}
			
			if (params.getSecondary()) {
				LongArrayList secondaryPopulation = new LongArrayList();
				for (Arc arc : foundArcs) {
					secondaryPopulation.addAll(arcInfoCollector.getArcMeasurePopulation(arc, params.getSecondaryType()));
				}
				if (!secondaryPopulation.isEmpty()) {
					arcSecondaryWeights.put(edge, FrequencySetPopulator.getAggregateInformation(secondaryPopulation, params.getSecondaryAggregation()));
				}
			}
			
		}
	}
}
