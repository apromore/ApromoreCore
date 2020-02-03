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

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores label's positions information.
 *
 * @author Sven Wagner-Boysen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignavioLabel extends AbstractExtensionElement {

    @XmlAnyAttribute
    private Map<QName, String> labelAttributes;

    /*
      * Constructors
      */
    public SignavioLabel() {
        super();
    }

    public SignavioLabel(Map<String, String> labelInfo) {
        for (String key : labelInfo.keySet()) {
            getLabelAttributes().put(new QName(key), labelInfo.get(key));
        }
    }

    /* Getter & Setter */

    public Map<QName, String> getLabelAttributes() {
        if (labelAttributes == null) {
            labelAttributes = new HashMap<QName, String>();
        }
        return labelAttributes;
    }

}
