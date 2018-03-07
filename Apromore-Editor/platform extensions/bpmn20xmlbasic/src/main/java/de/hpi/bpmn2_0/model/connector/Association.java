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

package de.hpi.bpmn2_0.model.connector;

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tAssociation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tArtifact">
 *       &lt;attribute name="sourceRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="targetRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="associationDirection" type="{http://www.omg.org/bpmn20}tAssociationDirection" default="none" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAssociation")
public class Association
        extends Edge {
    @XmlAttribute
    protected AssociationDirection associationDirection;

    @XmlTransient
    public boolean _containedInProcess;

    public Association() {
        super();
    }

    public Association(DataAssociation dataAssociation) {
        super(dataAssociation);

        /*
           * Determine association direction
           */
        if (dataAssociation instanceof DataInputAssociation || dataAssociation instanceof DataOutputAssociation) {
            this.setAssociationDirection(AssociationDirection.ONE);
        }
    }

    public void acceptVisitor(Visitor v) {
        v.visitAssociation(this);
    }


    /* Getter & Setter */

    /**
     * Gets the value of the associationDirection property.
     *
     * @return possible object is
     *         {@link AssociationDirection }
     */
    public AssociationDirection getAssociationDirection() {
        if (associationDirection == null) {
            return AssociationDirection.NONE;
        } else {
            return associationDirection;
        }
    }

    /**
     * Sets the value of the associationDirection property.
     *
     * @param value allowed object is
     *              {@link AssociationDirection }
     */
    public void setAssociationDirection(AssociationDirection value) {
        this.associationDirection = value;
    }

}
