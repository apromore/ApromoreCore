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

import de.hpi.bpmn2_0.model.RootElement;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tDataStore complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tDataStore">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataState" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="itemSubjectRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataStore", propOrder = {
        "dataState",
        "name",
        "capacity",
        "isUnlimited"
})
public class DataStore
        extends RootElement {

    protected DataState dataState;

    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
    @XmlAttribute
    protected int capacity;
    @XmlAttribute
    protected boolean isUnlimited;

//	/**
//	 * 
//	 * Basic method for the conversion of BPMN2.0 to the editor's internal format. 
//	 * {@see BaseElement#toShape(BPMN2DiagramConverter)}
//	 * @param converterForShapeCoordinateLookup an instance of {@link BPMN2DiagramConverter}, offering several lookup methods needed for the conversion.
//	 */
//    public Shape toShape(BPMN2DiagramConverter converterForShapeCoordinateLookup)  {
//		Shape shape = super.toShape(converterForShapeCoordinateLookup);
//
//		shape.setStencil(new StencilType("DataStore"));
//        
//        //shape.putProperty("", );
//        
//		return shape;
//	}	

    /* Getter & Setter */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isUnlimited() {
        return isUnlimited;
    }

    public void setUnlimited(boolean isUnlimited) {
        this.isUnlimited = isUnlimited;
    }

    @XmlAttribute
    protected QName itemSubjectRef;

    /**
     * Gets the value of the dataState property.
     *
     * @return possible object is
     *         {@link DataState }
     */
    public DataState getDataState() {
        return dataState;
    }

    /**
     * Sets the value of the dataState property.
     *
     * @param value allowed object is
     *              {@link DataState }
     */
    public void setDataState(DataState value) {
        this.dataState = value;
    }

    /**
     * Gets the value of the itemSubjectRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getItemSubjectRef() {
        return itemSubjectRef;
    }

    /**
     * Sets the value of the itemSubjectRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setItemSubjectRef(QName value) {
        this.itemSubjectRef = value;
    }

}
