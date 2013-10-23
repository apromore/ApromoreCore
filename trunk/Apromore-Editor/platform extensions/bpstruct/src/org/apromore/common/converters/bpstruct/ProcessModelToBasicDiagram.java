package org.apromore.common.converters.bpstruct;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
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
    private static final String AND_CONNECTOR = "AndConnector";
    private static final String XOR = "XOR";
    private static final String XOR_CONNECTOR = "XorConnector";
    private static final String OR = "OR";
    private static final String OR_CONNECTOR = "OrConnector";
    private static final String TITLE = "title";
    private static final String GATEWAY_TYPE = "gatewaytype";

    /**
     * Does the conversion from Process Model (JBPT) into a Basic Diagram (sIgnavio).
     * @param process the Jbpt process model.
     * @return the Basic Diagram of Signavio
     */
    public BasicDiagram convert(Process process) {
        BasicDiagram diagram = new BasicDiagram(process.getId());

        diagram.setProperty(TITLE, process.getName());

        addTasksToDiagram(diagram, process);
        addGatewaysToDiagram(diagram, process);
        addEdgesToDiagram(diagram, process);

        return diagram;
    }

    /* Add all the tasks to diagram. */
    private void addTasksToDiagram(BasicDiagram diagram, Process process) {
        for (Task task :process.getTasks()) {
            diagram.addChildShape(buildBasicNode(task));
        }
    }

    /* Add all the gateways to the diagram. */
    private void addGatewaysToDiagram(BasicDiagram diagram, Process process) {
        for (Gateway gateway :process.getGateways()) {
            diagram.addChildShape(buildBasicGateway(gateway));
        }
    }

    /* Add all the edges to the diagram. */
    private void addEdgesToDiagram(BasicDiagram diagram, Process process) {
        for (ControlFlow edge :process.getControlFlow()) {
            diagram.addChildShape(buildBasicEdge(edge));
        }
    }


    private BasicShape buildBasicNode(Node node) {
        BasicNode node1 = new BasicNode(node.getId());
        node1.setProperty(TITLE, node.getName());
        return node1;
    }

    private BasicShape buildBasicGateway(Gateway gateway) {
        BasicNode node = new BasicNode(gateway.getId());
        node.setProperty(TITLE, gateway.getName());

        if (gateway.isAND()) {
            node.setStencilId(AND_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, AND);
        } else if (gateway.isXOR()) {
            node.setStencilId(XOR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, XOR);
        } else if (gateway.isOR()) {
            node.setStencilId(OR_CONNECTOR);
            node.setProperty(GATEWAY_TYPE, OR);
        }

        return node;
    }

    private BasicShape buildBasicEdge(ControlFlow flow) {
        BasicEdge edge = new BasicEdge(flow.getId());
        edge.connectToASource(buildBasicNode(flow.getSource()));
        edge.connectToATarget(buildBasicNode(flow.getTarget()));
        return edge;
    }

}
