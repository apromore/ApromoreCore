
package de.hpi.bpmn2_0.model.data_object;

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

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.misc.ItemDefinition;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tMessage complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tMessage">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMessage")
public class Message
        extends FlowNode {

    @XmlAttribute
    @XmlIDREF
    protected ItemDefinition structureRef;

    @XmlTransient
    private boolean isInitiating;

    public void acceptVisitor(Visitor v) {
        v.visitMessage(this);
    }

    /**
     * Retrieves the association edge connecting the message object with an
     * choreography activity or participant.
     *
     * @return
     */
    public Association getDataConnectingAssociation() {
        List<Association> associationList = new ArrayList<Association>();

        for (FlowElement element : this.getIncoming()) {
            if (element instanceof Association)
                associationList.add((Association) element);
        }

        for (FlowElement element : this.getOutgoing()) {
            if (element instanceof Association)
                associationList.add((Association) element);
        }

        for (Association msgAssociation : associationList) {
            if (msgAssociation.getSourceRef() instanceof ChoreographyActivity
                    || msgAssociation.getSourceRef() instanceof Participant
                    || msgAssociation.getTargetRef() instanceof ChoreographyActivity
                    || msgAssociation.getTargetRef() instanceof Participant) {

                return msgAssociation;
            }
        }

        return null;
    }

    /* Getter & Setter */

    /**
     * Gets the value of the structureRef property.
     *
     * @return possible object is
     *         {@link ItemDefinition }
     */
    public ItemDefinition getStructureRef() {
        return structureRef;
    }

    /**
     * Sets the value of the structureRef property.
     *
     * @param value allowed object is
     *              {@link ItemDefinition }
     */
    public void setStructureRef(ItemDefinition value) {
        this.structureRef = value;
    }

    /**
     * @return the isInitiating
     */
    public boolean isInitiating() {
        return isInitiating;
    }

    /**
     * @param isInitiating the isInitiating to set
     */
    public void setInitiating(boolean isInitiating) {
        this.isInitiating = isInitiating;
    }

}
