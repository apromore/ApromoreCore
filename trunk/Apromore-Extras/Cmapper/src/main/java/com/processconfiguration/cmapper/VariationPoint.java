package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.TGatewayType;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TraversingVisitor;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.bpmn._20100524.model.Visitor;

/**
 * A configurable process model element.
 *
 * Currently, always a BPMN ExclusiveGateway.
 */
class VariationPoint {

    private static Logger LOGGER = Logger.getLogger(VariationPoint.class.getName());

    // Instance variables
    private String              id;
    private String              name;
    private List<String>        flowIds   = new ArrayList<>();
    private List<String>        flowNames = new ArrayList<>();
    private List<Configuration> configurations = new ArrayList<>();
    private TGatewayDirection   gatewayDirection;
    private TGatewayType        gatewayType;

    /**
     * Sole constructor.
     *
     * @param name  human-legible name
     */
    VariationPoint(final TGateway gateway, final TDefinitions definitions, final TGatewayType newGatewayType) {
        this.id          = gateway.getId();
        this.gatewayType = newGatewayType;

        // Figure out a human-legible name for this gateway
        LOGGER.info("Constructing variation point for XOR id=" + gateway.getId() + " name=" + gateway.getName());
        this.name = isEmpty(gateway.getName()) ? abbreviate(gateway.getId()) : gateway.getName();

        // Figure out the gateway direction
        switch (gateway.getGatewayDirection()) {
        case CONVERGING:
            gatewayDirection = TGatewayDirection.CONVERGING;
            definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
                @Override public void visit(final TSequenceFlow flow) {
                    addFlow(flow, flow.getTargetRef(), flow.getSourceRef(), gateway);
                }
            }));
            break;
        case DIVERGING:
            gatewayDirection = TGatewayDirection.DIVERGING;
            definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
                @Override public void visit(final TSequenceFlow flow) {
                    addFlow(flow, flow.getSourceRef(), flow.getTargetRef(), gateway);
                }
            }));
            break;
        default:
            throw new RuntimeException("Gateway " + gateway.getId() + " has unsupported direction: " + gateway.getGatewayDirection());
        }

        // Initial configuration
        addConfiguration();
    }

    /**
     * @param s  a candidate human-legible name
     * @return either <var>s</var>, or an abbreviated version of <var>s</var> if it was too long
     */
    private String abbreviate(String s) {
        if (s.length() < 10) {
            return s;
        } else {
            return s.substring(0, 7) + "...";
        }
    }

    /**
     * @param s  arbitrary text
     * @return whether <var>s</var> is non-<code>null</code> and contains printable characters
     */
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * @param flow  an incoming flow if <var>gateway</var> is converging, an outgoing flow if it is diverging
     * @param node  the element at the other end of the <var>flow</var>
     * @param gateway  a gateway
     */
    private void addFlow(TSequenceFlow flow, TFlowNode node, TFlowNode node2, TGateway gateway) {
        if (node != null && node.getId().equals(gateway.getId())) {
            flowIds.add(flow.getId());

            // Figure out a human-legible name for the flow
            if (!isEmpty(flow.getName())) {
                flowNames.add(flow.getName());
            } else if (!isEmpty(node2.getName())) {
                flowNames.add(node2.getName());
            } else {
                flowNames.add(abbreviate(flow.getId()));
            }
        }
    }

    /** @return BPMN identifier */
    String getId() {
        return id;
    }

    /** @return human-legible name */
    String getName() {
        return name;
    }

    int getFlowCount() {
        return flowNames.size();
    }

    String getFlowId(int flowIndex) {
        return flowIds.get(flowIndex);
    }

    String getFlowName(int flowIndex) {
        return flowNames.get(flowIndex);
    }

    List<Configuration> getConfigurations() {
        return configurations;
    }

    void addConfiguration() {
        configurations.add(new Configuration("1"));
    }

    void removeConfiguration(int configurationIndex) {
        configurations.remove(configurationIndex);
    }

    TGatewayDirection getGatewayDirection() {
        return gatewayDirection;
    }

    /**
     * A configuration of this variation point.
     */
    class Configuration {
        private String condition;
        private TGatewayType gatewayType;
        private Boolean[] isFlowActive;

        Configuration(final String initialCondition) {
            this.condition   = initialCondition;
            this.gatewayType = VariationPoint.this.gatewayType;
            this.isFlowActive = new Boolean[getFlowCount()];
            for (int i = 0; i < getFlowCount(); i++) {
                this.isFlowActive[i] = new Boolean("true");
            }
        }

        String getCondition() {
            return condition;
        }

        void setCondition(final String newCondition) {
            condition = newCondition;
        }

        TGatewayType getGatewayType() {
            return gatewayType;
        }

        void setGatewayType(final TGatewayType newGatewayType) {
            gatewayType = newGatewayType;
        }

        Boolean isFlowActive(int flowIndex) {
            return isFlowActive[flowIndex];
        }

        void setFlowActive(int flowIndex, Boolean newFlowActivity) {
            isFlowActive[flowIndex] = newFlowActivity;
        }
    }
}
