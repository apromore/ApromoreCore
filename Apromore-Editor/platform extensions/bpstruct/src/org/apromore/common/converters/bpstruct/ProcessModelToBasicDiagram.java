package org.apromore.common.converters.bpstruct;

import de.hpi.bpt.process.*;
import de.hpi.bpt.process.Process;
import org.apache.log4j.Logger;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.util.*;

/**
 * Converts the JBPT Process Model into a Editor Basic Diagram.
 *
 * @author Cameron James
 */
public class ProcessModelToBasicDiagram {

    private static final Logger LOGGER = Logger.getLogger(ProcessModelToBasicDiagram.class);

    private static final String AND = "AND";
    private static final String AND_CONNECTOR = "ParallelGateway";
    private static final String XOR = "XOR";
    private static final String XOR_CONNECTOR = "Exclusive_Databased_Gateway";
    private static final String OR = "OR";
    private static final String OR_CONNECTOR = "Inclusive_Databased_Gateway";
    private static final String NAME = "name";
    private static final String GATEWAY_TYPE = "gatewaytype";
    private static final String SEQUENCE_FLOW = "SequenceFlow";

    private Map<String, BasicShape> shapes = new HashMap<>();


    /**
     * Does the conversion from Process Model (JBPT) into a Basic Diagram (sIgnavio).
     *
     * @param process the Jbpt process model.
     * @return the Basic Diagram of Signavio
     */
    public BasicDiagram convert(Process process) {
        BasicDiagram diagram = new BasicDiagram(process.getId());

        configureDiagram(diagram, process);

        addNodesToDiagram(diagram, process);
        addEdgesToDiagram(diagram, process);

        return diagram;
    }


    /* Add all the tasks to diagram. */
    private void addNodesToDiagram(BasicDiagram diagram, Process process) {
        BasicShape shape;
        for (Node node : process.getNodes()) {
            shape = addNode(node, process);
            shapes.put(node.getId(), shape);
            diagram.addChildShape(shape);
        }
    }

    /* Add all the edges to the diagram. */
    private void addEdgesToDiagram(BasicDiagram diagram, Process process) {
        BasicShape shape;
        for (ControlFlow edge : process.getControlFlow()) {
            shape = configureBasicEdge(edge, shapes);
            shapes.put(edge.getId(), shape);
            diagram.addChildShape(shape);
        }
    }


    /* Configures the basic details of the Diagram. */
    private void configureDiagram(BasicDiagram diagram, Process process) {
        diagram.setResourceId("canvas");
        diagram.setStencilId("BPMNDiagram");

        // Properties
        diagram.setProperty(NAME, process.getName());
        diagram.setProperty("documentation", "");
        diagram.setProperty("targetnamespace", "http://www.signavio.com/bpmn20");
        diagram.setProperty("expressionlanguage", "http://www.w3.org/1999/XPath");
        diagram.setProperty("typelanguage", "http://www.w3.org/2001/XMLSchema");
        diagram.setProperty("orientation", "vertical");
        diagram.setStencilsetRef(new StencilSetReference("http://b3mn.org/stencilset/bpmn2.0#", "/signaviocore/editor/stencilsets//bpmn2.0/bpmn2.0.json"));

        List<String> ssExtensions = new ArrayList<>();
        ssExtensions.add("http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#");

        diagram.setSsextensions(ssExtensions);
    }


    /* Work out what type it is and send it on it's way to get added. */
    private BasicShape addNode(Node node, Process process) {
        if (node instanceof Gateway) {
            return addGateway((Gateway) node);
        } else if (node instanceof Task) {
            return addTask((Task) node, process);
        } else {
            System.out.println("Unable to determine Node type: " + node.getId() + " (" + node.toString() + ")");
        }
        return null;
    }


    /* Configures the basic details of the task. */
    private BasicShape addTask(Task task, Process process) {
        BasicNode node = new BasicNode(task.getId());

        if (isStartEvent(task, process.getControlFlow())) {
            node.setStencilId("StartNoneEvent");
            node.setProperty("bgcolor", "#ffffff");
            node.setProperty("bordercolor", "#000000");
            node.setProperty("trigger", "None");
        } else if (isEndEvent(task, process.getControlFlow())) {
            node.setStencilId("EndNoneEvent");
            node.setProperty("bgcolor", "#ffffff");
            node.setProperty("bordercolor", "#000000");
            node.setProperty("trigger", "None");
        } else {
            node.setStencilId("Task");
            node.setProperty(NAME, task.getName());
            node.setProperty("startquantity", 1);
            node.setProperty("completionquantity", 1);
            node.setProperty("isforcompensation", false);
            node.setProperty("tasktype", "None");
            node.setProperty("bgcolor", "#ffffcc");
            node.setProperty("bordercolor", "#000000");
            node.setProperty("looptype", "None");
            node.setProperty("behavior", "all");
            node.setProperty("onebehavioreventref:", "signal");
            node.setProperty("nonebehavioreventref", "signal");
            node.setProperty("outmsgitemkind", "Information");
            node.setProperty("inmsgitemkind", "Information");
            node.setProperty("implementation", "webService");
        }

        node.setBounds(new Bounds(new Point(0,0), new Point(100,70)));

        return node;
    }

    /* Configures the basic details of the Gateway. */
    private BasicShape addGateway(Gateway gateway) {
        BasicNode node = new BasicNode(gateway.getId());
        node.setProperty(NAME, gateway.getName());

        if (gateway.isAND()) {
            node.setStencilId(AND_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, AND);

        } else if (gateway.isXOR()) {
            node.setStencilId(XOR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, XOR);
            node.setProperty("xortype", "Data");
            node.setProperty("markervisible", true);

        } else if (gateway.isOR()) {
            node.setStencilId(OR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, OR);
        }

        node.setProperty("bordercolor", "#000000");
        node.setProperty("bgcolor", "#ffffff");
        node.setProperty("configurable", "false");
        node.setBounds(new Bounds(new Point(0,0), new Point(40,40)));

        return node;
    }


    /* Configures the basic details of the Edge. */
    private BasicShape configureBasicEdge(ControlFlow flow, Map<String, BasicShape> shapes) {
        BasicEdge edge = new BasicEdge(flow.getId());

        edge.setStencilId(SEQUENCE_FLOW);
        edge.setProperty("conditiontype", "None");
        edge.setProperty("absentinconfiguration", "false");
        edge.setProperty("isimmediate", true);
        edge.setProperty("bordercolor", "#000000");

        edge.connectToASource(shapes.get(flow.getSource().getId()));
        edge.addDocker(determineDockerPoint(shapes.get(flow.getSource().getId()), true));

        edge.connectToATarget(shapes.get(flow.getTarget().getId()));
        edge.addDocker(determineDockerPoint(shapes.get(flow.getSource().getId()), false));

        System.out.println("Edge: " + flow.getId() + " (" + flow.getSource().getId() + ", " + flow.getTarget().getId() + ")");

        return edge;
    }

    /* Determine the docking point for this Node */
    private Point determineDockerPoint(BasicShape shape, boolean source) {
        double x = shape.getBounds().getHeight() / 2;
        double y;

        if (source) {
            y = shape.getBounds().getLowerRight().getY();
        } else {
            y = shape.getBounds().getLowerLeft().getY();
        }

        return new Point(x, y);
    }



    /* Determine if this is a starting Node */
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

    /* Determine if this is a ending Node */
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
