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

package de.hpi.bpmn2_0.model.gateway;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for tGatewayDirection.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="tGatewayDirection">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="unspecified"/>
 *     &lt;enumeration value="converging"/>
 *     &lt;enumeration value="diverging"/>
 *     &lt;enumeration value="mixed"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlEnum
public enum GatewayDirection {

    @XmlEnumValue("Unspecified")
    UNSPECIFIED("unspecified"),
    @XmlEnumValue("Converging")
    CONVERGING("converging"),
    @XmlEnumValue("Diverging")
    DIVERGING("diverging"),
    @XmlEnumValue("Mixed")
    MIXED("mixed");
    private final String value;

    GatewayDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GatewayDirection fromValue(String v) {
        for (GatewayDirection c : GatewayDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
