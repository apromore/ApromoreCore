/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ExtendedAttributes")
public class XPDLExtendedAttributes extends XMLConvertible {

    @Element("ExtendedAttribute")
    protected ArrayList<XPDLExtendedAttribute> extendedAttributes;

    public void add(XPDLExtendedAttribute newAttribute) {
        initializeExtendedAttributes();

        getExtendedAttributes().add(newAttribute);
    }

    public ArrayList<XPDLExtendedAttribute> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(ArrayList<XPDLExtendedAttribute> newAttribute) {
        this.extendedAttributes = newAttribute;
    }

    protected void initializeExtendedAttributes() {
        if (getExtendedAttributes() == null) {
            setExtendedAttributes(new ArrayList<XPDLExtendedAttribute>());
        }
    }
}
