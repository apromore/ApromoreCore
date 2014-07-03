package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.TGatewayType;
import org.apromore.bpmncmap.parser.ParseException;
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

    TGatewayType getGatewayType();

    /**
     * Expand any condition with a complex flow condition to equivalent several conditions with only boolean flow conditions.
     *
     * @param constraints  a BDDC-formatted logical expression, never <code>null</code>; configurations which are not consistent
     *   with the <var>constraints</var> will be elided from the simplified configuration list
     * @throws ParseException  if any of the flow conditions aren't in correct syntax
     */
    void simplify(String constraints) throws ParseException;

    /**
     * A configuration of this variation point.
     */
    interface Configuration {

        /**
         * @return the BDDC formatted logical condition
         */
        String getCondition();

        /**
         * @param newCondition  a BDDC formatted logical condition
         */
        void setCondition(final String newCondition);

        /**
         * @return what sort of gateway this variation point becomes after configuration
         */
        TGatewayType getGatewayType();

        /**
         * @param newGatewayType  what sort of gateway this variation point becomes after configuration
         */
        void setGatewayType(final TGatewayType newGatewayType);

        /**
         * @param flowIndex  which flow to query
         * @return the BDDC formatted logical condition
         */
        String getFlowCondition(int flowIndex);

        /**
         * @param flowIndex  which flow to modify
         * @param newCondition  a BDDC formatted logical condition
         */
        void setFlowCondition(int flowIndex, String newCondition);
    }
}
