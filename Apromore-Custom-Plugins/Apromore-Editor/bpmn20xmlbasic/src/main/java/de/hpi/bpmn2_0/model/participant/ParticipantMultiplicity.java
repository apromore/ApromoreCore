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

package de.hpi.bpmn2_0.model.participant;

import de.hpi.bpmn2_0.model.BaseElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tParticipantMultiplicity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tParticipantMultiplicity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;attribute name="minimum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maximum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParticipantMultiplicity")
public class ParticipantMultiplicity
        extends BaseElement {

    @XmlAttribute
    protected Integer minimum;
    @XmlAttribute
    protected Integer maximum;

    /**
     * Gets the value of the minimum property.
     *
     * @return possible object is
     *         {@link Integer }
     */
    public Integer getMinimum() {
        return minimum;
    }

    /**
     * Sets the value of the minimum property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMinimum(Integer value) {
        this.minimum = value;
    }

    /**
     * Gets the value of the maximum property.
     *
     * @return possible object is
     *         {@link Integer }
     */
    public Integer getMaximum() {
        return maximum;
    }

    /**
     * Sets the value of the maximum property.
     *
     * @param value allowed object is
     *              {@link Integer }
     */
    public void setMaximum(Integer value) {
        this.maximum = value;
    }

}
