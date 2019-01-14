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

package de.hpi.bpmn2_0.model.data_object;

import de.hpi.bpmn2_0.model.BaseElement;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tOutputSet complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tOutputSet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="dataOutputRefs" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="optionalOutputRefs" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="whileExecutingOutputRefs" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inputSetRefs" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOutputSet", propOrder = {
        "dataOutputRefs",
        "optionalOutputRefs",
        "whileExecutingOutputRefs",
        "inputSetRefs"
})
public class OutputSet
        extends BaseElement {

    @XmlIDREF
    @XmlElement(type = DataOutput.class)
    protected List<DataOutput> dataOutputRefs;
    @XmlIDREF
    @XmlElement(type = DataOutput.class)
    protected List<DataOutput> optionalOutputRefs;
    @XmlIDREF
    @XmlElement(type = DataOutput.class)
    protected List<DataOutput> whileExecutingOutputRefs;
    @XmlIDREF
    @XmlElement(type = InputSet.class)
    protected List<InputSet> inputSetRefs;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the dataOutputRefs property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutputRefs property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutputRefs().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutput}
     */
    public List<DataOutput> getDataOutputRefs() {
        if (dataOutputRefs == null) {
            dataOutputRefs = new ArrayList<DataOutput>();
        }
        return this.dataOutputRefs;
    }

    /**
     * Gets the value of the optionalOutputRefs property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the optionalOutputRefs property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptionalOutputRefs().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutput}
     */
    public List<DataOutput> getOptionalOutputRefs() {
        if (optionalOutputRefs == null) {
            optionalOutputRefs = new ArrayList<DataOutput>();
        }
        return this.optionalOutputRefs;
    }

    /**
     * Gets the value of the whileExecutingOutputRefs property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the whileExecutingOutputRefs property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWhileExecutingOutputRefs().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutput}
     */
    public List<DataOutput> getWhileExecutingOutputRefs() {
        if (whileExecutingOutputRefs == null) {
            whileExecutingOutputRefs = new ArrayList<DataOutput>();
        }
        return this.whileExecutingOutputRefs;
    }

    /**
     * Gets the value of the inputSetRefs property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputSetRefs property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputSetRefs().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link InputSet}
     */
    public List<InputSet> getInputSetRefs() {
        if (inputSetRefs == null) {
            inputSetRefs = new ArrayList<InputSet>();
        }
        return this.inputSetRefs;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

}
