/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package au.edu.qut.bpmn.structuring.wrapper;

import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.Process2JSON;
import ee.ut.bpstruct.Restructurer;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Adriano on 4/05/2016.
 */
public class BPStructWrapper {

    private BPMNDiagram diagram;		//initial diagram
    private long taskCounter;		//id for processes and tasks
    private Map<Long, BPMNNode> originalNodes;
    private String jResponse;

    public void BPStructWrapper(){}

    public BPMNDiagram getStructured(Collection<Flow> edges) {
        diagram = new BPMNDiagramImpl("struct_diagram");
        originalNodes = new HashMap<>();
        taskCounter = 0;
        jResponse = null;
        
        if( !structure(edges) ) return null;
        if( !rebuild() ) return null;
        
        return diagram;
    }
    
    
    private boolean structure(Collection<Flow> edges) {
        Map<BPMNNode, UUID> processedNodes = new HashMap<>();

        Set<JsonTask> tasks = new HashSet<>();
        Set<JsonGateway> gateways = new HashSet<>();
        Set<JsonFlow> flows = new HashSet<>();

        JsonTask jTask;
        JsonGateway jGate;
        JsonFlow jFlow;
        JsonProcess jProcess;

        BPMNNode src, tgt;
        UUID srcUID, tgtUID;

        for( Flow flow : edges ) {

            src = flow.getSource();
            tgt = flow.getTarget();

            /**** checking whether srcNode and tgtNode have been already processed before ****/
            srcUID = processedNodes.get(src);
            tgtUID = processedNodes.get(tgt);

            /**** first time we meet this tgtNode ****/
            if( tgtUID == null ) {
                taskCounter++;
                if(tgt instanceof Gateway) {
                    jGate = new JsonGateway(taskCounter, ((Gateway) tgt).getGatewayType());
                    gateways.add(jGate);
                    tgtUID = jGate.uid;
                } else {
                    jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
                    tasks.add(jTask);
                    tgtUID = jTask.uid;
                }
                originalNodes.put(taskCounter, tgt);
                processedNodes.put(tgt, tgtUID);
            }

            /**** first time we meet this srcNode ****/
            if( srcUID == null ) {
                taskCounter++;
                if (src instanceof Gateway) {
                    jGate = new JsonGateway(taskCounter, ((Gateway) src).getGatewayType());
                    gateways.add(jGate);
                    srcUID = jGate.uid;
                } else {
                    jTask = new JsonTask(taskCounter, Long.toString(taskCounter));
                    tasks.add(jTask);
                    srcUID = jTask.uid;
                }
                originalNodes.put(taskCounter, src);
                processedNodes.put(src, srcUID);
            }

            jFlow = new JsonFlow("", srcUID, tgtUID);
            flows.add(jFlow);
            //System.out.println("json- Added flow: " + src.getId() +  " > " + tgt.getId());
        }

        jProcess = new JsonProcess("diagram", tasks, gateways, flows);

        try {
            //System.out.println("Process:" + jProcess.toString());
            jResponse = struct(jProcess.toString());
            if( jResponse == null ) throw new Exception("Process NULL.");
            System.out.println("DEBUG - BPStruct: Response GOT.");
        } catch (Exception e) {
            System.out.println("WARNING - Exception in BPStruct: " + e.getClass().getSimpleName() + "\t");
            jResponse = null;
            return false;
        }

        return true;
    }

    private boolean rebuild() {
        System.out.println("DEBUG - rebuilding process");

        Map<String, BPMNNode> processedNodes = new HashMap<>();

        JSONObject jsonProcessObject;
        JSONArray tasks, gateways, flows;
        JSONObject o;
        int i;

        BPMNNode node, src, tgt;
        String nodeUID, srcUID, tgtUID;
        long taskID;

        try {
            jsonProcessObject = new JSONObject(jResponse);

            /** parsing tasks **/
            tasks = jsonProcessObject.getJSONArray("tasks");
            for( i = 0; i < tasks.length(); i++ ) {
                o = tasks.getJSONObject(i);
                taskID = Long.parseLong( o.getString("label") );
                nodeUID = o.getString("id");

                node = createNode(taskID);
                if( node == null ) throw new JSONException("ERROR - task: " + taskID + " cannot be created!");
                processedNodes.put(nodeUID, node);
            }

            /** parsing gateways **/
            gateways = jsonProcessObject.getJSONArray("gateways");
            for( i = 0; i < gateways.length(); i++ ) {
                o = gateways.getJSONObject(i);
                nodeUID = o.getString("id");

                node = createGateway(o.getString("type"));
                if( node == null ) throw new JSONException("ERROR - gateway: " + nodeUID + " cannot be created!");
                processedNodes.put(nodeUID, node);
            }

            /** parsing flows **/
            flows = jsonProcessObject.getJSONArray("flows");
            for( i = 0; i < flows.length(); i++ ) {
                o = flows.getJSONObject(i);
                srcUID = o.getString("src");
                tgtUID = o.getString("tgt");

                if( processedNodes.containsKey(srcUID) ) src = processedNodes.get(srcUID);
                else throw new JSONException("ERROR - parsing flows: source not found: " + srcUID);

                if( processedNodes.containsKey(tgtUID) ) tgt = processedNodes.get(tgtUID);
                else throw new JSONException("ERROR - parsing flows: target not found: " + tgtUID);

                diagram.addFlow(src, tgt, o.getString("label"));
                //System.out.println("diagram- added flow: " + src.getId() +  " > " + tgt.getId());
            }

        } catch(Exception e) {
            System.out.println("ERROR - cannot rebuild the process: " + e.getMessage());
            return false;
        }

        return true;
    }


    private BPMNNode createNode(long id) {
        BPMNNode node;
        BPMNNode duplicate = null;
        String label;

        if( !originalNodes.containsKey(id) ) {
            System.out.println("ERROR - looked up for a node that does not exist.");
            return null;
        }

        node = originalNodes.get(id);
        label = node.getId().toString();

        if( node instanceof SubProcess) {
            duplicate = diagram.addSubProcess( label,
                    ((Activity) node).isBLooped(),
                    ((Activity) node).isBAdhoc(),
                    ((Activity) node).isBCompensation(),
                    ((Activity) node).isBMultiinstance(),
                    ((Activity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof Activity) {
            duplicate = diagram.addActivity( label,
                    ((Activity) node).isBLooped(),
                    ((Activity) node).isBAdhoc(),
                    ((Activity) node).isBCompensation(),
                    ((Activity) node).isBMultiinstance(),
                    ((Activity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof CallActivity) {
            duplicate = diagram.addCallActivity( label,
                    ((CallActivity) node).isBLooped(),
                    ((CallActivity) node).isBAdhoc(),
                    ((CallActivity) node).isBCompensation(),
                    ((CallActivity) node).isBMultiinstance(),
                    ((CallActivity) node).isBCollapsed(),
                    (SubProcess) null);

        } else if( node instanceof Event ) {
            duplicate = diagram.addEvent( label,
                    ((Event) node).getEventType(),
                    ((Event) node).getEventTrigger(),
                    ((Event) node).getEventUse(),
                    (SubProcess) null,
                    true,
                    null);

        } else if( node instanceof Gateway ) {
            duplicate = diagram.addGateway( label,
                    ((Gateway) node).getGatewayType(),
                    (SubProcess) null);

            duplicate.setParentSwimlane(node.getParentSwimlane());
            ((Gateway) duplicate).setMarkerVisible(((Gateway) node).isMarkerVisible());
            ((Gateway) duplicate).setDecorator(((Gateway) node).getDecorator());
        }

        return duplicate;
    }

    private Gateway createGateway(String type) {
        Gateway g = null;

        if( type.equalsIgnoreCase("xor") ) g = diagram.addGateway("", Gateway.GatewayType.DATABASED, (SubProcess) null);
        else if( type.equalsIgnoreCase("or") ) g = diagram.addGateway("", Gateway.GatewayType.INCLUSIVE, (SubProcess) null);
        else if( type.equalsIgnoreCase("and") ) g = diagram.addGateway("", Gateway.GatewayType.PARALLEL, (SubProcess) null);

        return g;
    }


    private String struct(String jsonProc) throws Exception {

        Process process = JSON2Process.convert(jsonProc);

        int gCounter = 0;
        for(de.hpi.bpt.process.Gateway g : process.getGateways() ) {
            if( g.getName().isEmpty() ) g.setName("gw" + gCounter++);
        }

        Restructurer restructurer = new Restructurer(process);
        if(restructurer.perform()) {
            return Process2JSON.convert(restructurer.proc);
        } else {
            return null;
        }
    }

    private class JsonProcess {
        private String jsonProcess;

        private JsonProcess(String name, Set<JsonTask> tasks, Set<JsonGateway> gateways, Set<JsonFlow> flows) {
            boolean first;
            Set<String>  duplicate = new HashSet<>();

            jsonProcess = "{\"name\":\"" + name + "\",\"gateways\":[";
            first = true;
            for(JsonGateway g : gateways) {
                if(!first) jsonProcess += ",";
                else first = false;
                jsonProcess += g.toString();
            }

            jsonProcess += "],\"tasks\":[";
            first = true;
            for(JsonTask t : tasks) {
                if(!first) jsonProcess += ",";
                else first = false;
                jsonProcess += t.toString();
            }

            jsonProcess += "],\"flows\":[";
            first = true;
            for(JsonFlow f : flows) {
                if(!duplicate.contains(f.toString())) duplicate.add(f.toString());
                else {
                    System.out.println("JsonProcess => FOUND DUPLICATE FLOW: " + f.toString());
                    continue;
                }
                if(!first) jsonProcess += ",";
                else first = false;
                jsonProcess += f.toString();
            }

            jsonProcess += "]}";
        }

        public void deleteORgates() {
            jsonProcess = jsonProcess.replaceAll("\"OR\"", "\"XOR\"");
        }

        @Override
        public String toString() {
            return jsonProcess;
        }
    }

    private class JsonTask {
        private UUID uid;
        private String jTask;

        private JsonTask(long id, String label) {
            this.uid = new UUID(id, id);
            jTask = new String("{\"id\":\"" + uid.toString() + "\",\"label\":\"" + label + "\"}");
        }

        @Override
        public String toString() {
            return jTask;
        }
    }

    private class JsonGateway {
        private String jGate;
        private UUID uid;

        private JsonGateway(long id, Gateway.GatewayType type) {
            String sType = "XOR";
            this.uid = new UUID(id, id);

            switch( type ) {
                case DATABASED:
                case EVENTBASED:
                    sType = "XOR";
                    break;
                case INCLUSIVE:
                case COMPLEX:
                    sType = "OR";
                    break;
                case PARALLEL:
                    sType = "AND";
                    break;
            }

            jGate = new String("{\"id\":\"" + uid.toString() + "\",\"type\":\"" + sType + "\"}");
        }

        @Override
        public String toString() {
            return jGate;
        }
    }

    private class JsonFlow {
        private String jFlow;

        private JsonFlow(String label, UUID srcUID, UUID tgtUID) {
            jFlow = new String("{\"label\":\"" + label + "\",\"src\":\"" + srcUID.toString() + "\",\"tgt\":\"" + tgtUID.toString() + "\"}");
        }

        @Override
        public String toString() {
            return jFlow;
        }
    }

}
