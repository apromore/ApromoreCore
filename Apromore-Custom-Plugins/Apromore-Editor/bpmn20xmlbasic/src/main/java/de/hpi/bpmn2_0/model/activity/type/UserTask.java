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

package de.hpi.bpmn2_0.model.activity.type;

import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.UserTaskImplementation;
import de.hpi.bpmn2_0.model.activity.resource.Rendering;
import de.hpi.bpmn2_0.model.callable.GlobalTask;
import de.hpi.bpmn2_0.model.callable.GlobalUserTask;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tUserTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tUserTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tTask">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}rendering" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="implementation" type="{http://www.omg.org/bpmn20}tUserTaskImplementation" default="unspecified" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tUserTask", propOrder = {
        "rendering"
})
public class UserTask
        extends Task {

    /* Constructors */

    /**
     * Default constructor
     */
    public UserTask() {
    }


    /**
     * Copy constructor based on a {@link UserTask}
     *
     * @param task
     */
    public UserTask(UserTask task) {
        super(task);

        this.getRendering().addAll(task.getRendering());
        this.setImplementation(task.getImplementation());
    }

    protected List<Rendering> rendering;
    @XmlAttribute
    protected UserTaskImplementation implementation;

    public void acceptVisitor(Visitor v) {
        v.visitUserTask(this);
    }

    public GlobalTask getAsGlobalTask() {
        GlobalUserTask gut = new GlobalUserTask(super.getAsGlobalTask());
        gut.getRendering().addAll(this.getRendering());
        gut.setImplementation(this.getImplementation());
        return gut;
    }

    /* Getter & Setter */

    /**
     * Gets the value of the rendering property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rendering property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRendering().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Rendering }
     */
    public List<Rendering> getRendering() {
        if (rendering == null) {
            rendering = new ArrayList<Rendering>();
        }
        return this.rendering;
    }

    /**
     * Gets the value of the implementation property.
     *
     * @return possible object is
     *         {@link UserTaskImplementation }
     */
    public UserTaskImplementation getImplementation() {
        if (implementation == null) {
            return UserTaskImplementation.UNSPECIFIED;
        } else {
            return implementation;
        }
    }

    /**
     * Sets the value of the implementation property.
     *
     * @param value allowed object is
     *              {@link UserTaskImplementation }
     */
    public void setImplementation(UserTaskImplementation value) {
        this.implementation = value;
    }

}
