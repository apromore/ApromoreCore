
package de.hpi.bpmn2_0.model.event;

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

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tCatchEvent complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCatchEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEvent">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutput" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}outputSet" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}eventDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="eventDefinitionRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parallelMultiple" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCatchEvent", propOrder = {
//    "dataOutput",
//    "dataOutputAssociation",
//    "outputSet",
//    "eventDefinition",
//    "eventDefinitionRef"
})
@XmlSeeAlso({
        StartEvent.class,
        IntermediateCatchEvent.class
})
public abstract class CatchEvent
        extends Event {

    //    protected List<DataOutput> dataOutput;
//    protected List<DataOutputAssociation> dataOutputAssociation;
//    protected TOutputSet outputSet;
    @XmlAttribute
    protected Boolean parallelMultiple;

    /**
     * Gets the value of the dataOutput property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutput property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutput }
     *
     *
     */
//    public List<DataOutput> getDataOutput() {
//        if (dataOutput == null) {
//            dataOutput = new ArrayList<DataOutput>();
//        }
//        return this.dataOutput;
//    }

    /**
     * Gets the value of the dataOutputAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutputAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutputAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutputAssociation }
     *
     *
     */
//    public List<DataOutputAssociation> getDataOutputAssociation() {
//        if (dataOutputAssociation == null) {
//            dataOutputAssociation = new ArrayList<DataOutputAssociation>();
//        }
//        return this.dataOutputAssociation;
//    }

    /**
     * Gets the value of the outputSet property.
     *
     * @return
     *     possible object is
     *     {@link TOutputSet }
     *
     */
//    public TOutputSet getOutputSet() {
//        return outputSet;
//    }

    /**
     * Sets the value of the outputSet property.
     *
     * @param value
     *     allowed object is
     *     {@link TOutputSet }
     *
     */
//    public void setOutputSet(TOutputSet value) {
//        this.outputSet = value;
//    }


    /**
     * Gets the value of the parallelMultiple property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isParallelMultiple() {
        if (parallelMultiple == null) {
            return false;
        } else {
            return parallelMultiple;
        }
    }

    /**
     * Sets the value of the parallelMultiple property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setParallelMultiple(Boolean value) {
        this.parallelMultiple = value;
    }

    public void acceptVisitor(Visitor v) {
        v.visitCatchEvent(this);
    }


}
