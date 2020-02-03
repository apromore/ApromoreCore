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

package de.hpi.bpmn2_0.model.extension.signavio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Indicates whether a DataObject is of type IT-System or ProcessParticipant
 *
 * @author Sven Wagner-Boysen
 */
@XmlEnum
public enum SignavioDataObjectType {
    @XmlEnumValue("Default")
    DEFAULT("default"),
    @XmlEnumValue("IT-System")
    ITSYSTEM("it-system"),
    @XmlEnumValue("ProcessParticipant")
    PROCESSPARTICIPANT("processparticipant");

    private final String value;

    SignavioDataObjectType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignavioDataObjectType fromValue(String v) {
        for (SignavioDataObjectType c : SignavioDataObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
