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
package de.hpi.bpmn2_0.model.bpmndi.di;

import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNPlane;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Plane complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Plane">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/DD/20100524/DI}Node">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/spec/DD/20100524/DI}DiagramElement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "diagramElement"
})
@XmlSeeAlso({
        BPMNPlane.class
})
public abstract class Plane extends Node {

    @XmlElementRef(name = "DiagramElement")
    protected List<DiagramElement> diagramElement;

    /**
     * Gets the value of the diagramElement property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diagramElement property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiagramElement().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link BPMNShape }
     * {@link BPMNEdge }
     * {@link DiagramElement }
     */
    public List<DiagramElement> getDiagramElement() {
        if (diagramElement == null) {
            diagramElement = new ArrayList<DiagramElement>();
        }
        return this.diagramElement;
    }

}
