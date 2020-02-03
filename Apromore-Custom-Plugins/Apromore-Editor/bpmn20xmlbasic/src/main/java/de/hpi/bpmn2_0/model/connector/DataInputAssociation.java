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

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tDataInputAssociation complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tDataInputAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tDataAssociation">
 *       &lt;sequence>
 *         &lt;element name="sourceRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded"/>
 *         &lt;element name="targetRef" type="{http://www.w3.org/2001/XMLSchema}IDREF"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataInputAssociation")
public class DataInputAssociation
        extends DataAssociation {

    /**
     * Default constructor
     */
    public DataInputAssociation() {
        super();
    }

    /**
     * Constructor creates an data input association base on a data association.
     *
     * @param dataAssociation
     */
    public DataInputAssociation(DataAssociation dataAssociation) {
        this.id = SignavioUUID.generate();
        this.assignment = dataAssociation.getAssignment();
        this.documentation = dataAssociation.getDocumentation();
        this.transformation = dataAssociation.getTransformation();
        this.process = dataAssociation.getProcess();
    }

    public void acceptVisitor(Visitor v) {
        v.visitDataInputAssociation(this);
    }


    public FlowElement getTargetRef() {
        if (parent != null) {
            return parent;
        }

        return super.getTargetRef();
    }
}
