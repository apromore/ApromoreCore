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

package ee.ut.eventstr.comparison.differences;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import com.google.common.collect.BiMap;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.nets.unfolding.BPstructBP;
import hub.top.uma.DNode;
import org.jbpt.hypergraph.abs.GObject;
import org.jbpt.pm.Activity;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.utils.IOUtils;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import ee.ut.bpmn.BPMNReader;
import ee.ut.nets.unfolding.Multiplicity;
import ee.ut.eventstr.PESSemantics;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

public class ModelAbstractions {
    // Readers and parsers
    BPMNReader reader;
    Unfolder_PetriNet unfolder;
    Unfolding2PES parserPES;

    // Models
    private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> bpmnModel;
    PetriNet net;
    PetriNet unfolding;
	PESSemantics<Integer> pes;
    NewUnfoldingPESSemantics<Integer> pessem;
    HashSet<String> labels;

    // Labels for dealing with duplicates in the model
    HashMap<String, String> mapLabelsO2N;
    HashMap<String, Integer> labelCounter;

	// Map from the tasks in the BPMN model to the Petri net
	private BiMap<FlowNode, Node> mapTasks2Trans;
    // Map from the tasks in the BPMN model to the Petri net
    private HashMap<Node, FlowNode> mapTasks2TransReverse;

	// Map from the nodes in the branching process to the net
	private HashMap<Node, Node> mapBP2Net;
    // Map from the unfolding to the net
    private HashMap<Short, Node> mapUnf2Net;
    // Map from the branching process to the PES
    private BiMap<DNode, Integer> mapUnf2PES;
    // Map from the PES to the branching process
    private HashMap<Integer, DNode> mapPES2Unf;
    // Map from the events in the PES to the net
    private HashMap<Integer, Node> mapPES2Net;

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

    private void computeUnfolding(Set<String> labels){
        HashSet<String> commonLabels = new HashSet<>(labels);
        HashSet<String> silent = new HashSet<String>(labels);
        for (Transition t : net.getTransitions())
            silent.add(t.getName());
        silent.removeAll(commonLabels);

        mapBP2Net = new HashMap<>();

        label2Uniques();

        // Unfolding
        unfolder = new Unfolder_PetriNet(net, MODE.EQUAL_PREDS, silent);
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

    public PESSemantics<Integer> getPESSemantics(Set<String> commonLabels) throws Exception {
        computeUnfolding(commonLabels);
        parserPES = new Unfolding2PES(unfolder, labels, mapLabelsO2N);
        mapUnf2PES = parserPES.getMapEventsBP2ES();
        mapPES2Unf = parserPES.getMapEventsPES2Unf();
        mapPES2Net = new HashMap<>();

        if(pes != null)
            return this.pes;

        this.pes = new PESSemantics<Integer>(parserPES.getPES());
		List<String> labelsPES = this.pes.getLabels();
		for(int i = 0; i < labelsPES.size(); i++){
			String label = labelsPES.get(i);

            if(!pes.getPES().getLabels().get(i).equals("_0_") && !pes.getPES().getLabels().get(i).equals("_1_"))
                mapPES2Net.put(i, getTransition(label));

			if(mapLabelsO2N.containsKey(label))
				labelsPES.set(i, mapLabelsO2N.get(label));
		}

        return this.pes;
    }

    public NewUnfoldingPESSemantics<Integer> getUnfoldingPESSemantics(PetriNet net, HashSet<String> silents) throws Exception {
        computeUnfolding2();
        parserPES = new Unfolding2PES(unfolder, labels, mapLabelsO2N);
        mapUnf2PES = parserPES.getMapEventsBP2ES();
        mapPES2Unf = parserPES.getMapEventsPES2Unf();
        mapPES2Net = new HashMap<>();

        pessem = new NewUnfoldingPESSemantics<Integer>(parserPES.getPES(), parserPES);

        List<String> oldLabels = pessem.getLabels();
        List<String> newLabels = new ArrayList<>();
        int i = 0;
        for(String oldL : oldLabels) {
            if(!oldL.equals("_0_") && !oldL.equals("_1_"))
                mapPES2Net.put(i, getTransition(oldL));

            if (mapLabelsO2N.containsKey(oldL))
                newLabels.add(mapLabelsO2N.get(oldL));
            else
                newLabels.add(oldL);

            i++;
        }

        pessem.setLabels(newLabels);

        return pessem;
    }

    public Node getTransition(String label){
        for(Transition t : net.getTransitions())
            if(t.getName().trim().equals(label.trim()))
                return t;

        System.out.println("No transition with the given label");
        System.exit(0);

        return null;
    }

    public HashSet<FlowNode> getTasksFromConf(BitSet conf){
        HashSet<FlowNode> tasks = new HashSet<>();
        HashSet<DNode> events;

        events = getEvtsConfPES(conf);

        for(DNode node : events)
            if(unfolder.getMapDNodeTrans().containsKey(node.id) && mapTasks2TransReverse.containsKey(unfolder.getMapDNodeTrans().get(node.id)))
                tasks.add(mapTasks2TransReverse.get(unfolder.getMapDNodeTrans().get(node.id)));

        return tasks;
    }

    public boolean isSynth(int i){
       return getLabelPESS(i).equals("_0_") || getLabelPESS(i).equals("_1_");
    }

    public String getLabelPESS(int i){
        if(pessem == null)
            return pes.getLabel(i);

        return pessem.getLabel(i);
    }

    public HashSet<DNode> getEvtsConfPES(BitSet conf){
        HashSet<DNode> events = new HashSet<>();

        for (int i = conf.nextSetBit(0); i >= 0; i = conf.nextSetBit(i+1)) {
            if(isSynth(i) || !labels.contains(getLabelPESS(i)))
                continue;

            Set<DNode> localC = unfolder.getBP().getLocalConfig(mapPES2Unf.get(i));
            events.addAll(localC);
        }

        return events;
    }

    public LinkedHashSet<GObject> getModelSegment(HashSet<Integer> events) {
        HashSet<DNode> union = new HashSet<>();
        HashSet<DNode> intersection = null;
        HashSet<DNode> mappedEvts = new HashSet<>();

        for(Integer event : events)
            if(getTaskFromEvent(event) instanceof Activity) {
                Set<DNode> localConf = unfolder.getBP().getLocalConfig(mapPES2Unf.get(event));
                union.addAll(localConf);

                if (intersection == null) intersection = new HashSet<>(localConf);
                else intersection.retainAll(localConf);

                mappedEvts.add(mapPES2Unf.get(event));
            }

        intersection.removeAll(mappedEvts);
        union.removeAll(intersection);

        LinkedList<DNode> sorted = new LinkedList(union);
        Collections.sort(sorted, new Comparator<DNode>() {
            @Override
            public int compare(DNode o1, DNode o2) {
                return unfolder.getBP().getLocalConfig(o1).size() - unfolder.getBP().getLocalConfig(o2).size();
            }
        });

        LinkedList<GObject> modelNodes = new LinkedList<>();
        for(DNode node : sorted)
            if(mapUnf2Net.containsKey(node.id)) {
                FlowNode task = mapTasks2TransReverse.get(mapUnf2Net.get(node.id));
                if(task != null)
                modelNodes.add(task);
                if(task != null && bpmnModel.getIncomingControlFlow(task) != null)
                modelNodes.addAll(bpmnModel.getIncomingControlFlow(task));
                if(task != null && bpmnModel.getOutgoingControlFlow(task) != null)
                modelNodes.addAll(bpmnModel.getOutgoingControlFlow(task));
            }

        return new LinkedHashSet<>(modelNodes);
    }

    public FlowNode getTaskFromEvent(Integer i){
        Node eventTrans = mapPES2Net.get(i);
        return mapTasks2TransReverse.get(eventTrans);
    }

    public FlowNode getEnd(){
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

    public FlowNode getCommonPredUnf(Integer evt1M, Integer evt2M) {
        DNode evt1Unf = mapPES2Unf.get(evt1M);
        DNode evt2Unf = mapPES2Unf.get(evt2M);

        Set<DNode> localConf1 = new HashSet<>(unfolder.getBP().getLocalConfig(evt1Unf));
        Set<DNode> localConf2 = unfolder.getBP().getLocalConfig(evt2Unf);

        localConf1.retainAll(localConf2);
        localConf1.remove(evt1Unf);
        localConf1.remove(evt2Unf);

        Comparator<DNode> comparator = new Comparator<DNode>() {
            @Override
            public int compare(DNode o1, DNode o2) {
                return unfolder.getBP().getLocalConfig(o2).size() - unfolder.getBP().getLocalConfig(o1).size();
            }
        };

        LinkedList<DNode> localConf1Ord = new LinkedList<>(localConf1);
        Collections.sort(localConf1Ord, comparator);

        DNode lastElem = localConf1Ord.getFirst();
//        if(unfolder.getBP().getBranchingProcess().allEvents.contains(lastElem)) {
//            com.google.gwt.dev.util.collect.HashSet<DNode> directSucc = unfolder.getBP().getBranchingProcess().getAllSuccessors(lastElem);
//            if (mapTasks2TransReverse.containsKey(mapUnf2Net.get(directSucc.iterator().next().id)))
//                lastElem = directSucc.iterator().next();
//        }

        FlowNode lcs = null;
        Iterator<DNode> it = localConf1Ord.iterator();
        while(lcs == null && it.hasNext())
            lcs = mapTasks2TransReverse.get(mapUnf2Net.get(it.next().id));

        if(lcs == null)
            return getEnd();

//        FlowNode lastPred = mapTasks2TransReverse.get(mapUnf2Net.get(lastElem.id));
        if(getBpmnModel().getDirectSuccessors(lcs).iterator().next() instanceof Gateway)
            lcs = getBpmnModel().getDirectSuccessors(lcs).iterator().next();

        return lcs;
    }

    public FlowNode getCommonSuccUnf(Integer evt1M, Integer evt2M) {
        DNode evt1Unf = mapPES2Unf.get(evt1M);
        DNode evt2Unf = mapPES2Unf.get(evt2M);

        LinkedList<DNode> localConf1Ord = new LinkedList<>();

        for(DNode evt : unfolder.getBP().getBranchingProcess().allEvents){
            Set<DNode> localConf = unfolder.getBP().getLocalConfig(evt);
            if(localConf.contains(evt1Unf) && localConf.contains(evt2Unf) && evt != evt1Unf && evt != evt2Unf)
                localConf1Ord.add(evt);
        }

        Comparator<DNode> comparator = new Comparator<DNode>() {
            @Override
            public int compare(DNode o1, DNode o2) {
                return unfolder.getBP().getLocalConfig(o1).size() - unfolder.getBP().getLocalConfig(o2).size();
            }
        };

        Collections.sort(localConf1Ord, comparator);

        FlowNode lcs = null;
        Iterator<DNode> it = localConf1Ord.iterator();
        while(lcs == null && it.hasNext())
            lcs = mapTasks2TransReverse.get(mapUnf2Net.get(it.next().id));

        if(lcs == null)
            return getEnd();

        return lcs;

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

	public PESSemantics<Integer> getPES() {
		return pes;
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
