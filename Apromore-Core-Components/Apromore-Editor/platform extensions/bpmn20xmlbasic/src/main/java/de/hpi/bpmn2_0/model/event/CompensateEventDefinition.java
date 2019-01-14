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

package de.hpi.bpmn2_0.model.event;

import de.hpi.bpmn2_0.model.activity.Activity;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tCompensateEventDefinition complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCompensateEventDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEventDefinition">
 *       &lt;attribute name="waitForCompletion" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="activityRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCompensateEventDefinition")
public class CompensateEventDefinition
        extends EventDefinition {

    @XmlAttribute
    protected Boolean waitForCompletion;

    @XmlAttribute
    @XmlIDREF
    protected Activity activityRef;

    /**
     * Gets the value of the waitForCompletion property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public Boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    /**
     * Sets the value of the waitForCompletion property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setWaitForCompletion(Boolean value) {
        this.waitForCompletion = value;
    }

    /**
     * Gets the value of the activityRef property.
     *
     * @return possible object is
     *         {@link Activity }
     */
    public Activity getActivityRef() {
        return activityRef;
    }

    /**
     * Sets the value of the activityRef property.
     *
     * @param value allowed object is
     *              {@link Activity }
     */
    public void setActivityRef(Activity value) {
        this.activityRef = value;
    }

}
