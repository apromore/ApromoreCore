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

package de.hpi.bpmn2_0.model.artifacts;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.conversation.ConversationElement;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tArtifact complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tArtifact">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tArtifact")
@XmlSeeAlso({
//    Association.class,
        Group.class,
        TextAnnotation.class
})
public abstract class Artifact
        extends FlowNode {

    /* Business logic methodes */

    /**
     * List of elements already traversed in the graph.
     */
    @XmlTransient
    private List<FlowElement> processedElements;

    /**
     * Find an appropriate {@link Process} container for the data object.
     * <p/>
     * The algorithm checks the source and target neighborhood nodes of the data
     * object and the takes the referenced process of one of the neighbors.
     * <p/>
     * Navigates into both directions.
     */
    public void findRelatedProcess() {
        this.processedElements = new ArrayList<FlowElement>();
        Process process = this.findRelatedProcessRecursivly(this);
        if (process != null) {

            /* Remove from other containment based process reference */
            if (this.getProcess() != null) {
                this.getProcess().removeChild(this);
            }

            this.setProcess(process);
            process.addChild(this);
        }
    }

    /**
     * Navigates into both directions.
     *
     * @param flowElement The {@link FlowElement} to investigate.
     */
    private Process findRelatedProcessRecursivly(FlowElement flowElement) {
        if (flowElement == null)
            return null;

        /* Check if element is processed already */
        if (this.processedElements.contains(flowElement))
            return null;

        this.processedElements.add(flowElement);

        /*
           * Check if one of the neighbors is assigned to a Process, otherwise
           * continue with the after next.
           */

        for (Edge edge : flowElement.getIncoming()) {
            FlowElement sourceRef = edge.getSourceRef();
            if (sourceRef == null)
                continue;
            Process process = sourceRef.getProcess();
            if (process != null)
                return process;
        }

        for (Edge edge : flowElement.getOutgoing()) {
            FlowElement targetRef = edge.getTargetRef();
            if (targetRef == null)
                continue;
            Process process = targetRef.getProcess();
            if (process != null)
                return process;
        }

        /* Continue with the after next nodes */

        for (Edge edge : flowElement.getIncoming()) {
            Process process = this.findRelatedProcessRecursivly(edge
                    .getSourceRef());
            if (process != null)
                return process;
        }

        for (Edge edge : flowElement.getOutgoing()) {
            Process process = this.findRelatedProcessRecursivly(edge
                    .getTargetRef());
            if (process != null)
                return process;
        }

        return null;
    }

    public void acceptVisitor(Visitor v) {
        v.visitArtifact(this);
    }

    /**
     * Checks whether the Artifact is contained in an conversation.
     * <p/>
     * The algorithm checks the source and target neighborhood nodes of the data
     * object.
     * <p/>
     * Navigates into both directions.
     */
    public boolean isConverstionRelated() {
        this.processedElements = new ArrayList<FlowElement>();
        return this.isConversationConversationRecursivly(this);
    }

    /**
     * Navigates into both directions.
     *
     * @param flowElement The {@link FlowElement} to investigate.
     */
    private boolean isConversationConversationRecursivly(FlowElement flowElement) {
        if (flowElement == null)
            return false;

        /* Check if element is processed already */
        if (this.processedElements.contains(flowElement))
            return false;

        this.processedElements.add(flowElement);

        /*
           * Check if one of the neighbors is assigned to a Process, otherwise
           * continue with the after next.
           */

        for (Edge edge : flowElement.getIncoming()) {
            FlowElement sourceRef = edge.getSourceRef();
            if (sourceRef == null)
                continue;

            if (sourceRef instanceof ConversationElement) {
                return true;
            }
        }

        for (Edge edge : flowElement.getOutgoing()) {
            FlowElement targetRef = edge.getTargetRef();
            if (targetRef == null)
                continue;
            if (targetRef instanceof ConversationElement) {
                return true;
            }
        }

        /* Continue with the after next nodes */

        for (Edge edge : flowElement.getIncoming()) {
            boolean result = this.isConversationConversationRecursivly(edge
                    .getSourceRef());
            if (result)
                return result;
        }

        for (Edge edge : flowElement.getOutgoing()) {
            boolean result = this.isConversationConversationRecursivly(edge
                    .getTargetRef());
            if (result)
                return result;
        }

        return false;
    }

}
