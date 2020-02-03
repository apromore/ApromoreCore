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

package de.hpi.bpmn2_0.model.choreography;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tChoreographyLoopType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="tChoreographyLoopType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Standard"/>
 *     &lt;enumeration value="MultiInstanceSequential"/>
 *     &lt;enumeration value="MultiInstanceParallel"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "tChoreographyLoopType")
@XmlEnum
public enum ChoreographyLoopType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Standard")
    STANDARD("Standard"),
    @XmlEnumValue("MultiInstanceSequential")
    MULTI_INSTANCE_SEQUENTIAL("MultiInstanceSequential"),
    @XmlEnumValue("MultiInstanceParallel")
    MULTI_INSTANCE_PARALLEL("MultiInstanceParallel");
    private final String value;

    ChoreographyLoopType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChoreographyLoopType fromValue(String v) {
        for (ChoreographyLoopType c : ChoreographyLoopType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
