
package de.hpi.bpmn2_0.model.choreography;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.conversation.CorrelationKey;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tChoreographyActivity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tChoreographyActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;sequence>
 *         &lt;element name="participant" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="initiatingParticipantRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tChoreographyActivity", propOrder = {
        "participantRef",
        "correlationKey",
        "initiatingParticipantRef",
        "loopType"
})
@XmlSeeAlso({
        ChoreographyTask.class,
        SubChoreography.class,
        CallChoreography.class
})
public abstract class ChoreographyActivity
        extends Activity {

    @XmlElement(required = true)
    @XmlIDREF
    protected List<Participant> participantRef;
    protected List<CorrelationKey> correlationKey;

    @XmlIDREF
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "IDREF")
    protected Participant initiatingParticipantRef;

    @XmlAttribute
    protected ChoreographyLoopType loopType;

    public ChoreographyActivity(ChoreographyActivity choreoAct) {
        super(choreoAct);

        if (!choreoAct.getParticipantRef().isEmpty()) {
            this.getParticipantRef().addAll(choreoAct.getParticipantRef());
        }
        if (!choreoAct.getCorrelationKey().isEmpty()) {
            this.getCorrelationKey().addAll(choreoAct.getCorrelationKey());
        }

        this.setInitiatingParticipantRef(choreoAct.getInitiatingParticipant());
        this.setLoopType(choreoAct.getLoopType());
    }


    public ChoreographyActivity() {
        super();
    }


    public void addChild(BaseElement child) {
        if (child instanceof Participant) {
            this.getParticipantRef().add((Participant) child);
            if (((Participant) child).isInitiating()) {
                this.setInitiatingParticipantRef((Participant) child);
            }
        }
    }


    public void acceptVisitor(Visitor v) {
        v.visitChoreographyActivity(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the participant property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participant property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipantRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Participant }
     */
    public List<Participant> getParticipantRef() {
        if (participantRef == null) {
            participantRef = new ArrayList<Participant>();
        }
        return this.participantRef;
    }

    /**
     * Gets the value of the initiatingParticipantRef property.
     *
     * @return possible object is
     *         {@link Participant }
     */
    public Participant getInitiatingParticipant() {
        return initiatingParticipantRef;
    }

    /**
     * Sets the value of the initiatingParticipantRef property.
     *
     * @param value allowed object is
     *              {@link Participant }
     */
    public void setInitiatingParticipantRef(Participant value) {
        this.initiatingParticipantRef = value;
    }

    public ChoreographyLoopType getLoopType() {
        return loopType;
    }

    public void setLoopType(ChoreographyLoopType loopType) {
        this.loopType = loopType;
    }

    public List<CorrelationKey> getCorrelationKey() {
        if (correlationKey == null) {
            correlationKey = new ArrayList<CorrelationKey>();
        }
        return correlationKey;
    }

}
