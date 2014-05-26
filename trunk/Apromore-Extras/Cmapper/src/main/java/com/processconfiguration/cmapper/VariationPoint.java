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
interface VariationPoint {

    /** @return BPMN identifier */
    String getId();

    /** @return human-legible name */
    String getName();

    int getFlowCount();

    String getFlowId(int flowIndex);

    String getFlowName(int flowIndex);

    List<Configuration> getConfigurations();

    void addConfiguration();

    void removeConfiguration(int configurationIndex);

    TGatewayDirection getGatewayDirection();

    /**
     * A configuration of this variation point.
     */
    interface Configuration {

        String getCondition();

        void setCondition(final String newCondition);

        TGatewayType getGatewayType();

        void setGatewayType(final TGatewayType newGatewayType);

        Boolean isFlowActive(int flowIndex);

        void setFlowActive(int flowIndex, Boolean newFlowActivity);
    }
}
