/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processdiscoverer.splitminer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processdiscoverer.abstraction.DFGAbstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.splitminer.dfgp.DFGEdge;
import org.apromore.splitminer.dfgp.DFGNode;
import org.apromore.splitminer.dfgp.DirectlyFollowGraphPlus;
import org.apromore.splitminer.log.SimpleLog;
import org.apromore.splitminer.ui.dfgp.DFGPUIResult;

/**
 * This class is used as a bridging DFGP created from a DFGAbstraction
 * The DFGAbstraction here must be based on frequency and the weight 
 * of nodes and arcs must be total (cumulative) as it is used by SplitMiner
 * 
 * @author Bruce Nguyen
 *
 */
public class ProcessDiscovererDFGP extends DirectlyFollowGraphPlus {
	private DFGAbstraction dfgAbs;

	public ProcessDiscovererDFGP(SimpleLog log, DFGAbstraction dfgAbs, double percentileFrequencyThreshold, double parallelismsThreshold,
						boolean parallelismsFirst) throws Exception {
		super(log, percentileFrequencyThreshold, parallelismsThreshold, DFGPUIResult.FilterType.NOF, parallelismsFirst);
		this.dfgAbs = dfgAbs;
		this.buildDFGP(); // call this at the end
	}
	
	public DFGAbstraction getDFGAbstraction() {
		return this.dfgAbs;
	}
	
	@Override
	public void buildDirectlyFollowsGraph() {
		// These variables must be initialized because they will be used
		// in the addNode() and addEdge() method calls. However, this implicit
		// initialization should be avoided as it breaks the class encapsulation
        nodes = new HashMap<>();
        edges = new HashSet<>();
        outgoings = new HashMap<>();
        incomings = new HashMap<>();
        dfgp = new HashMap<>();
        
        // Bruce: debug only
//        try {
//	        UIContext context = new UIContext();
//	        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//	        UIPluginContext uiPluginContext = context.getMainPluginContext();
//	        BpmnExportPlugin exportPlugin = new BpmnExportPlugin();
//	        exportPlugin.export(uiPluginContext, dfgAbs.getDiagram(), new File("bpmnDiagram_0.bpmn"));
//        }
//        catch (Exception ex) {
//        	ex.printStackTrace();
//        }
        
        Map<BPMNNode, DFGNode> nodeMap = new HashMap<>();
		for (BPMNNode node: dfgAbs.getDiagram().getNodes()) {
			DFGNode dfgNode = new DFGNode(node.getLabel(), log.getReverseMap().get(node.getLabel()));
			dfgNode.increaseFrequency((int)Math.round(dfgAbs.getNodePrimaryWeight(node)));
            this.addNode(dfgNode);
            nodeMap.put(node, dfgNode);
		}
		
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : dfgAbs.getDiagram().getEdges()) {
			DFGEdge dfgEdge = new DFGEdge(nodeMap.get(edge.getSource()), nodeMap.get(edge.getTarget()));
			dfgEdge.increaseFrequency((int)Math.round(dfgAbs.getArcPrimaryWeight(edge)));
            this.addEdge(dfgEdge);
            if (dfgEdge.getFrequency() == 0) {
            	System.out.println("Edge " + dfgEdge.getSourceCode() + "->" + dfgEdge.getTargetCode() + " has 0 weight.");
            } 
		}
	}
	
}
