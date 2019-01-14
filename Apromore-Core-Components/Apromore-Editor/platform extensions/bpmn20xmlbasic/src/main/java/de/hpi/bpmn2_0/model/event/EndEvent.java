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

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p/>
 * Java class for tEndEvent complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name=&quot;tEndEvent&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://www.omg.org/bpmn20}tThrowEvent&quot;&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlRootElement(name = "endEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEndEvent")
public class EndEvent extends ThrowEvent {

    /* Constructors */

    public EndEvent() {
    }

    public EndEvent(EndEvent endEvent) {
        super(endEvent);
    }

    /* Transformation Methods */

    public void acceptVisitor(Visitor v) {
        v.visitEndEvent(this);
    }


}
