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

package de.hpi.bpmn2_0.model.bpmndi.dc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Point complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Point">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Point")
public class Point {

    @XmlAttribute(name = "x", required = true)
    protected double x;
    @XmlAttribute(name = "y", required = true)
    protected double y;

    /* Constructors */

    public Point() {
    }

    /**
     * Creating a point instance based on a oryx diagram point.
     *
     * @param dockerPoint
     */
    public Point(org.oryxeditor.server.diagram.Point dockerPoint) {
        this.setX(dockerPoint.getX());
        this.setY(dockerPoint.getY());
    }

    public Point(int x, int y) {
        this.setX((new Integer(x)).doubleValue());
        this.setY((new Integer(y)).doubleValue());
    }

    /* Getter & Setter */


    /**
     * Gets the value of the x property.
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     */
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     */
    public void setY(double value) {
        this.y = value;
    }

    public org.oryxeditor.server.diagram.Point toDiagramPoint() {
        return new org.oryxeditor.server.diagram.Point(this.getX(), this.getY());
    }

}
