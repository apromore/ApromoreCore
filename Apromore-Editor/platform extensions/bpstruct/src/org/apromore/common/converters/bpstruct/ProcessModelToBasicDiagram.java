package org.apromore.common.converters.bpstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;

/**
 * Converts the JBPT Process Model into a Editor Basic Diagram.
 *
 * @author Cameron James
 */
public class ProcessModelToBasicDiagram {

    private static final String AND = "AND";
    private static final String AND_CONNECTOR = "ParallelGateway";
    private static final String XOR = "XOR";
    private static final String XOR_CONNECTOR = "Exclusive_Databased_Gateway";
    private static final String OR = "OR";
    private static final String OR_CONNECTOR = "Inclusive_Databased_Gateway";
    private static final String NAME = "name";
    private static final String GATEWAY_TYPE = "gatewaytype";

    /**
     * Does the conversion from Process Model (JBPT) into a Basic Diagram (sIgnavio).
     *
     * @param process the Jbpt process model.
     * @return the Basic Diagram of Signavio
     */
    public BasicDiagram convert(Process process) {
        BasicDiagram diagram = new BasicDiagram(process.getId());

        configureDiagram(diagram, process);

        addTasksToDiagram(diagram, process);
        addGatewaysToDiagram(diagram, process);
        addEdgesToDiagram(diagram, process);

        return diagram;
    }


    /* Add all the tasks to diagram. */
    private void addTasksToDiagram(BasicDiagram diagram, Process process) {
        for (Task task : process.getTasks()) {
            diagram.addChildShape(configureBasicNode(task, process));
        }
    }

    /* Add all the gateways to the diagram. */
    private void addGatewaysToDiagram(BasicDiagram diagram, Process process) {
        for (Gateway gateway : process.getGateways()) {
            diagram.addChildShape(configureBasicGateway(gateway, process));
        }
    }

    /* Add all the edges to the diagram. */
    private void addEdgesToDiagram(BasicDiagram diagram, Process process) {
        for (ControlFlow edge : process.getControlFlow()) {
            diagram.addChildShape(configureBasicEdge(edge, process));
        }
    }


    /* Configures the basic details of the Diagram. */
    private void configureDiagram(BasicDiagram diagram, Process process) {
        diagram.setResourceId("oryx-canvas123");
        diagram.setStencilId("BPMNDiagram");

        // Properties
        diagram.setProperty(NAME, process.getName());
        diagram.setProperty("targetnamespace", "http://www.signavio.com/bpmn20");
        diagram.setProperty("expressionlanguage", "http://www.w3.org/1999/XPath");
        diagram.setProperty("typelanguage", "http://www.w3.org/2001/XMLSchema");
        diagram.setProperty("orientation", "vertical");
        diagram.setStencilsetRef(new StencilSetReference("http://b3mn.org/stencilset/bpmn2.0#", "/editor/editor/stencilsets//bpmn2.0/bpmn2.0.json"));

        List<String> ssExtensions = new ArrayList<>();
        ssExtensions.add("http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#");

        diagram.setSsextensions(ssExtensions);
    }


    /* Configures the basic details of the Node. */
    private BasicShape configureBasicNode(Node node, Process process) {
        BasicNode node1 = new BasicNode(node.getId());

        if (isStartEvent(node, process.getControlFlow())) {
            node1.setStencilId("StartNoneEvent");
            node1.setProperty("bgcolor", "#ffffff");
            node1.setProperty("bordercolor", "#000000");
            node1.setProperty("trigger", "None");
        } else if (isEndEvent(node, process.getControlFlow())) {
            node1.setStencilId("EndNoneEvent");
            node1.setProperty("bgcolor", "#ffffff");
            node1.setProperty("bordercolor", "#000000");
            node1.setProperty("trigger", "None");
        } else {
            node1.setStencilId("Task");
            node1.setProperty(NAME, node.getName());
            node1.setProperty("startquantity", 1);
            node1.setProperty("completionquantity", 1);
            node1.setProperty("isforcompensation", false);
            node1.setProperty("tasktype", "None");
            node1.setProperty("bgcolor", "#ffffcc");
            node1.setProperty("bordercolor", "#000000");
            node1.setProperty("looptype", "None");
            node1.setProperty("behavior", "all");
            node1.setProperty("onebehavioreventref:", "signal");
            node1.setProperty("nonebehavioreventref", "signal");
            node1.setProperty("outmsgitemkind", "Information");
            node1.setProperty("inmsgitemkind", "Information");
            node1.setProperty("implementation", "webService");
        }

        node1.setBounds(new Bounds(new Point(0,0), new Point(100,70)));

        return node1;
    }


    /* Configures the basic details of the Gateway. */
    private BasicShape configureBasicGateway(Gateway gateway, Process process) {
        BasicNode node = new BasicNode(gateway.getId());
        node.setProperty(NAME, gateway.getName());

        if (gateway.isAND()) {
            node.setStencilId(AND_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, AND);
            node.setProperty("bordercolor", "#000000");
            node.setProperty("bgcolor", "#ffffff");
            node.setProperty("configurable", "false");

        } else if (gateway.isXOR()) {
            node.setStencilId(XOR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, XOR);
            node.setProperty("bordercolor", "#000000");
            node.setProperty("bgcolor", "#ffffff");
            node.setProperty("configurable", "false");
            node.setProperty("xortype", "Data");
            node.setProperty("markervisible", true);

        } else if (gateway.isOR()) {
            node.setStencilId(OR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, OR);
            node.setProperty("bordercolor", "#000000");
            node.setProperty("bgcolor", "#ffffff");
            node.setProperty("configurable", "false");
        }

        node.setBounds(new Bounds(new Point(0,0), new Point(40,40)));
        addNodesToBasicGateway(node, gateway, process);

        return node;
    }


    /* Configures the basic details of the Edge. */
    private BasicShape configureBasicEdge(ControlFlow flow, Process process) {
        BasicEdge edge = new BasicEdge(flow.getId());

        edge.setStencilId("SequenceFlow");
        edge.setProperty("conditiontype", "None");
        edge.setProperty("absentinconfiguration", "false");
        edge.setProperty("isimmediate", true);
        edge.setProperty("bordercolor", "#000000");

        List<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(0, 0));
        edge.setDockers(points);

        edge.connectToASource(configureBasicNode(flow.getSource(), process));
        edge.connectToATarget(configureBasicNode(flow.getTarget(), process));

        return edge;
    }



    /* Gateway nodes has inbound or outbound nodes which must be specified. */
    private void addNodesToBasicGateway(BasicNode node, Gateway gateway, Process process) {
        for (ControlFlow flow : process.getControlFlow()) {
            if (gateway.getId().equals(flow.getSource().getId())) {
                // We have an outbound gateway node to specify
                node.addOutgoingAndUpdateItsIncomings(configureBasicNode(flow.getTarget(), process));

            } else if (gateway.getId().equals(flow.getTarget().getId())) {
                // We have an inbound gateway node to specify
                node.addIncomingAndUpdateItsOutgoings(configureBasicNode(flow.getSource(), process));
            }
        }
    }

    private boolean isStartEvent(Node node, Collection<ControlFlow> flows) {
        boolean result = true;
        for (ControlFlow flow : flows) {
            if (flow.getTarget().getId().equals(node.getId())) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean isEndEvent(Node node, Collection<ControlFlow> flows) {
        boolean result = true;
        for (ControlFlow flow : flows) {
            if (flow.getSource().getId().equals(node.getId())) {
                result = false;
                break;
            }
        }
        return result;
    }
}
