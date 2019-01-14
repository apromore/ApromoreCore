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

package de.hpi.bpmn2_0.model.bpmndi.di;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipantBandKind.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="ParticipantBandKind">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="top_initiating"/>
 *     &lt;enumeration value="middle_initiating"/>
 *     &lt;enumeration value="bottom_initiating"/>
 *     &lt;enumeration value="top_non_initiating"/>
 *     &lt;enumeration value="middle_non_initiating"/>
 *     &lt;enumeration value="bottom_non_initiating"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "ParticipantBandKind")
@XmlEnum
public enum ParticipantBandKind {

    @XmlEnumValue("top_initiating")
    TOP_INITIATING("top_initiating"),
    @XmlEnumValue("middle_initiating")
    MIDDLE_INITIATING("middle_initiating"),
    @XmlEnumValue("bottom_initiating")
    BOTTOM_INITIATING("bottom_initiating"),
    @XmlEnumValue("top_non_initiating")
    TOP_NON_INITIATING("top_non_initiating"),
    @XmlEnumValue("middle_non_initiating")
    MIDDLE_NON_INITIATING("middle_non_initiating"),
    @XmlEnumValue("bottom_non_initiating")
    BOTTOM_NON_INITIATING("bottom_non_initiating");
    private final String value;

    ParticipantBandKind(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParticipantBandKind fromValue(String v) {
        for (ParticipantBandKind c : ParticipantBandKind.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * @return boolean stating whether the participant is initiating
     */
    public boolean isInitiating() {
        switch (this) {
            case TOP_INITIATING: {
                return true;
            }
            case TOP_NON_INITIATING: {
                return false;
            }
            case MIDDLE_INITIATING: {
                return true;
            }
            case MIDDLE_NON_INITIATING: {
                return false;
            }
            case BOTTOM_INITIATING: {
                return true;
            }
            case BOTTOM_NON_INITIATING: {
                return false;
            }
            default:
                return false;
        }
    }

    public boolean isBottom() {
        switch (this) {
            case BOTTOM_INITIATING: {
                return true;
            }
            case BOTTOM_NON_INITIATING: {
                return true;
            }
            default:
                return false;
        }
    }

    public boolean isTop() {
        switch (this) {
            case TOP_INITIATING: {
                return true;
            }
            case TOP_NON_INITIATING: {
                return true;
            }
            default:
                return false;
        }
    }

}
