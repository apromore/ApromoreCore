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

package de.hpi.bpmn2_0.model.conversation;

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tSubConversation complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tSubConversation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tConversationNode">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}conversationNode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="correlationKeyRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSubConversation", propOrder = {
        "conversationNode",
        "artifact",
        "correlationKeyRef",
        "messageFlow",
        "association",
        "conversationLink"})
public class SubConversation extends ConversationNode implements ContainerElement, CallingElement {

    @XmlElementRefs({@XmlElementRef(type = CallConversation.class),
            @XmlElementRef(type = Conversation.class),
            @XmlElementRef(type = SubConversation.class)})
    protected List<ConversationNode> conversationNode;

    @XmlElementRef()
    protected List<Artifact> artifact;

    protected List<MessageFlow> messageFlow;
    @XmlElementRef
    protected List<Association> association;

    protected List<ConversationLink> conversationLink;

    @XmlTransient
    public List<DiagramElement> _diagramElements = new ArrayList<DiagramElement>();

    // @XmlAttribute
    protected CorrelationKey correlationKeyRef;

    public void acceptVisitor(Visitor v) {
        v.visitSubConversation(this);
    }

    public List<BaseElement> getChilds() {
        List<BaseElement> childs = super.getChilds();

        childs.addAll(this.getParticipantRef());
        childs.addAll(this.getMessageFlow());
        childs.addAll(this.getArtifact());
        childs.addAll(this.getConversationNode());
        childs.addAll(this.getConversationLink());
        childs.addAll(this.getAssociation());

        return childs;
    }

    public List<Edge> getChildEdges() {
        List<Edge> edgeList = new ArrayList<Edge>();

        for (FlowElement fe : this.getFlowElement()) {
            if (fe instanceof Edge) {
                edgeList.add((Edge) fe);
            } else if (fe instanceof ContainerElement) {
                edgeList.addAll(((ContainerElement) fe).getChildEdges());
            }
        }

        return edgeList;
    }

    /* Getter & Setter */

    /**
     * Gets the value of the conversationNode property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the conversationNode property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getConversationNode().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SubConversation }{@code >}
     * {@link JAXBElement }{@code <}{@link CallConversation }{@code >}
     * {@link JAXBElement }{@code <}{@link Communication }{@code >}
     * {@link JAXBElement }{@code <}{@link ConversationNode }{@code >}
     */
    public List<ConversationNode> getConversationNode() {
        if (conversationNode == null) {
            conversationNode = new ArrayList<ConversationNode>();
        }
        return this.conversationNode;
    }

    /**
     * Gets the value of the artifact property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the artifact property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getArtifact().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TArtifact }{@code >}
     * {@link JAXBElement }{@code <}{@link TAssociation }{@code >}
     * {@link JAXBElement }{@code <}{@link TGroup }{@code >} {@link JAXBElement }
     * {@code <}{@link TTextAnnotation }{@code >}
     */
    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<Artifact>();
        }
        return this.artifact;
    }

    public List<MessageFlow> getMessageFlow() {
        if (messageFlow == null) {
            messageFlow = new ArrayList<MessageFlow>();
        }
        return messageFlow;
    }

    public List<Association> getAssociation() {
        if (association == null) {
            association = new ArrayList<Association>();
        }

        return association;
    }

    public List<ConversationLink> getConversationLink() {
        if (conversationLink == null) {
            conversationLink = new ArrayList<ConversationLink>();
        }
        return conversationLink;
    }

    /**
     * Gets the value of the correlationKeyRef property.
     *
     * @return possible object is {@link CorrelationKey }
     */
    public CorrelationKey getCorrelationKeyRef() {
        return correlationKeyRef;
    }

    /**
     * Sets the value of the correlationKeyRef property.
     *
     * @param value allowed object is {@link CorrelationKey }
     */
    public void setCorrelationKeyRef(CorrelationKey value) {
        this.correlationKeyRef = value;
    }

    public List<DiagramElement> _getDiagramElements() {
        return _diagramElements;
    }

    public List<FlowElement> getFlowElement() {
        List<FlowElement> flowElements = new ArrayList<FlowElement>();

        flowElements.addAll(getConversationNode());
        flowElements.addAll(getParticipantRef());
        flowElements.addAll(getArtifact());
        flowElements.addAll(getConversationLink());
        flowElements.addAll(getMessageFlow());
        flowElements.addAll(getAssociation());

        return flowElements;
    }

    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        for (ConversationNode node : getConversationNode()) {
            if (node instanceof CallingElement) {
                calledElements.addAll(((CallingElement) node).getCalledElements());
            }
        }

        return calledElements;
    }

}
