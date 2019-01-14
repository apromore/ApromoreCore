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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Specifies a customer defined attribute and its value.
 *
 * @author Sven Wagner-Boysen
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignavioMetaData extends AbstractExtensionElement {

    @XmlAttribute
    private String metaKey;
    @XmlAttribute
    private String metaValue;

    public SignavioMetaData() {
        super();
    }

    public SignavioMetaData(String metaKey, String metaValue) {
        super();
        this.metaKey = metaKey;
        this.metaValue = metaValue;
    }

    /* Getter & Setter */

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public String getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(String metaValue) {
        this.metaValue = metaValue;
    }

}
