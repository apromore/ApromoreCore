package au.ltl.utils;
/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import com.google.gwt.thirdparty.guava.common.collect.BiMap;
import hub.top.petrinet.*;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;


public class ModelAbstractions {
    // Readers and parsers
    BPMNReader reader;
    Unfolder_PetriNet unfolder;

    // Models
    private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bpmnModel;
    PetriNet net;
    PetriNet unfolding;
    HashSet<String> labels;

    // Labels for dealing with duplicates in the model
    HashMap<String, String> mapLabelsO2N;
    HashMap<String, Integer> labelCounter;

	// Map from the tasks in the BPMN model to the Petri net
	private BiMap<FlowNode, Node> mapTasks2Trans;

    // Map from the tasks in the BPMN model to the Petri net
    private HashMap<Node, FlowNode> mapTasks2TransReverse; //from net to bmpn model. Gli do un nodo della petri e mi da un task del bpmn

	// Map from the nodes in the branching process to the net
	private HashMap<Node, Node> mapBP2Net; //STA QUI
    // Map from the unfolding to the net
    private HashMap<Short, Node> mapUnf2Net;

    public HashMap<Node, FlowNode> getMapTasks2TransReverse() {
		return mapTasks2TransReverse;
	}



	public ModelAbstractions(byte[] modelArray) throws JDOMException, IOException, SAXException, ParserConfigurationException {
        // Read the BPMN model and create the Petri net
        readModels(modelArray);

		labels = new HashSet<String>();
		labels.addAll(reader.getTaskLabels());
	}

    private void readModels(byte[] modelArray) {
        try {
            InputStream input = new ByteArrayInputStream(modelArray);

            reader = new BPMNReader(input);
            bpmnModel = reader.getModel();
            net = reader.getNet();

            mapTasks2Trans = reader.getTasksUMATrans();
            mapTasks2TransReverse = reader.getTasksUMATransReverse();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ------ Hacking for dealing with duplicate transitions during the unfolding ------
    private void label2Uniques(){
        // Label to distinct labels
        mapLabelsO2N = new HashMap<>();
        labelCounter = new HashMap<String, Integer>();

        for(Transition t : net.getTransitions()) {
            String tName = t.getName();
            if (!labelCounter.containsKey(tName)) {
                labelCounter.put(tName, 0);
                mapLabelsO2N.put(t.getName(), tName);
            }else {
                t.setName(tName + "-" + labelCounter.get(tName));
                labelCounter.put(tName, labelCounter.get(tName) + 1);
                mapLabelsO2N.put(t.getName(), tName);
            }
        }
    }

    public PetriNet getUnfolding(){
        if(unfolding == null)
            computeUnfolding(labels);

        return unfolding;
    }

    public Unfolder_PetriNet getUnfolder(){
        return unfolder;
    }

    private void computeUnfolding(Set<String> labels){
        HashSet<String> commonLabels = new HashSet<>(labels);
        HashSet<String> silent = new HashSet<String>(labels);
        for (Transition t : net.getTransitions())
            silent.add(t.getName());
        silent.removeAll(commonLabels);

        mapBP2Net = new HashMap<>();

        label2Uniques();

        // Unfolding
        unfolder = new Unfolder_PetriNet(net, BPstructBP.MODE.EQUAL_PREDS, silent);
        unfolder.computeUnfolding();
        mapUnf2Net = unfolder.getMapDNodeTrans();

        commonLabels.addAll(mapLabelsO2N.keySet());

        HashMap<Node, Multiplicity> repetitions = new HashMap<>();
        PetriNet branchingProcess = unfolder.getUnfoldingAsPetriNet(commonLabels, repetitions, mapBP2Net);

//        // Label back
//        for(Transition t : branchingProcess.getTransitions())
//            if(mapLabelsO2N.containsKey(t.getName()))
//                t.setName(mapLabelsO2N.get(t.getName()));

        this.unfolding = branchingProcess;
    }

    private void computeUnfolding2(){
        HashSet<String> commonLabels = new HashSet<>();
        HashSet<String> silent = new HashSet<String>(labels);
        for (Transition t : net.getTransitions())
            silent.add(t.getName());
        silent.removeAll(commonLabels);

        this.mapBP2Net = new HashMap<>();

        label2Uniques();

        // Unfolding
        unfolder = new Unfolder_PetriNet(net, BPstructBP.MODE.ESPARZA, silent);
        unfolder.computeUnfolding();

        mapUnf2Net = unfolder.getMapDNodeTrans();

        commonLabels.addAll(mapLabelsO2N.keySet());

        HashMap<Node, Multiplicity> repetitions = new HashMap<>();
        PetriNet branchingProcess = unfolder.getUnfoldingAsPetriNet(commonLabels, repetitions, mapBP2Net);

//        // Label back
//        for(Transition t : branchingProcess.getTransitions())
//            if(mapLabelsO2N.containsKey(t.getName()))
//                t.setName(mapLabelsO2N.get(t.getName()));

        this.unfolding = branchingProcess;
    }

    public Node getTransition(String label){
        for(Transition t : net.getTransitions())
            if(t.getName().trim().equals(label.trim()))
                return t;

        System.out.println("No transition with the given label");
        System.exit(0);

        return null;
    }

    public FlowNode getEnd(){ // end event
        for(FlowNode node : bpmnModel.getFlowNodes())
            if(bpmnModel.getAllSuccessors(node).size() == 0)
                return node;

        return null;
    }

    public FlowNode getStart(){
        for(FlowNode node : bpmnModel.getFlowNodes())
            if(bpmnModel.getAllPredecessors(node).size() == 0)
                return node;

        return null;
    }

    public String getName(String path) {
        String name = path.substring(path.lastIndexOf("/") + 1);
        return name;
    }

    public HashMap<String, String> getMapLabelsO2N(){ return mapLabelsO2N; }

	public HashSet<String> getLabels() { return labels; }

	public HashMap<Node, Node> getMapBP2Net() {
		return mapBP2Net;
	}

	public BPMNReader getReader() {
		return this.reader;
	}
	
	public PetriNet getNet(){
		return net;
	}

    public BiMap<FlowNode, Node> getMapTasks2Trans() { return mapTasks2Trans; }

    public Bpmn<BpmnControlFlow<FlowNode>, FlowNode> getBpmnModel() {
        return bpmnModel;
    }
}
