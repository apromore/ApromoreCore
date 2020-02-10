
package de.hpi.bpmn2_0.model.bpmndi.di;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

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
