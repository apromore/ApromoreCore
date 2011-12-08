/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.model.choreography;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.diagram.SignavioUUID;

/**
 * <p>Java class for tChoreographyTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tChoreographyTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tChoreographyActivity">
 *       &lt;sequence>
 *         &lt;element name="messageFlowRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tChoreographyTask", propOrder = {
    "messageFlowRef"
})
public class ChoreographyTask
    extends ChoreographyActivity
{

	@XmlIDREF
    @XmlElement(type = MessageFlow.class/*required = true*/)
    protected List<MessageFlow> messageFlowRef;
   
     /**
	 * Creates a MessageFlow to create schema valid XML
	 * 
	 * @param choreography
	 */
	public void createMessageFlows(Choreography choreography) {
		/* Insert a message flow from first to last participant */
		if(getParticipantRef().size() >= 2) {
			
			// The initiating participant has to be the source of the 
			// message flow
			Participant p1 = getParticipantRef().get(0);
			Participant p2 = getParticipantRef().get(1);
			Participant source = p1;
			Participant target = p2;
			
			if(p2.isInitiating()) {
				source = p2;
				target = p1;
			}
			
			/* Create message flow */
			MessageFlow msgFlow = new MessageFlow();
			msgFlow.setId(SignavioUUID.generate());
			
			/* Append message object */
			if(source._msgRef != null) {
				msgFlow.setMessageRef(source._msgRef);
			}
			
			msgFlow.setSourceRef(source);
			msgFlow.setTargetRef(target);
			
			/* Add references */
			getMessageFlows().add(msgFlow);
			choreography.getMessageFlow().add(msgFlow);
			
			/* Insert the optional second message flow */
			if(target._msgRef != null) {
				
				/* Create message flow */
				MessageFlow msgFlow2 = new MessageFlow();
				msgFlow2.setId(SignavioUUID.generate());
				
				/* Append message object */
				msgFlow2.setMessageRef(target._msgRef);
				
				msgFlow2.setSourceRef(target);
				msgFlow2.setTargetRef(source);
				
				/* Add references */
				getMessageFlows().add(msgFlow2);
				choreography.getMessageFlow().add(msgFlow2);
			}
			
			
		}
	}
	
	public void acceptVisitor(Visitor v){
		v.visitChoreographyTask(this);
	}
    
    /**
     * Gets the value of the messageFlowRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageFlowRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageFlowRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MessageFlow }
     * 
     * 
     */
    public List<MessageFlow> getMessageFlows() {
        if (messageFlowRef == null) {
            messageFlowRef = new ArrayList<MessageFlow>();
        }
        return this.messageFlowRef;
    }

}
