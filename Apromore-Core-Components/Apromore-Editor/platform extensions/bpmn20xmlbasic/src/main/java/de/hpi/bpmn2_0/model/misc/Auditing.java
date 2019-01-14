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


package de.hpi.bpmn2_0.model.misc;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Documentation;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tAuditing complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tAuditing">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAuditing")
public class Auditing
        extends BaseElement {
    /* Constructors */

    /**
     * Default constructor
     */
    public Auditing() {
    }

    /**
     * Directly sets the value of the auditing element.
     *
     * @param value
     */
    public Auditing(String value) {
        this.setId(SignavioUUID.generate());
        this.getDocumentation().add(new Documentation(value));
    }

    public String toExportString() {
        if (this.getDocumentation().size() == 0) return null;
        if (this.getDocumentation().get(0) != null)
            return this.getDocumentation().get(0).getText();
        else return null;
    }
}
