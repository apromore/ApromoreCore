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

package de.hpi.bpmn2_0.model.extension.synergia;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&rt;
 *   &lt;attribute name="href" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&rt;
 * &lt;/complexType&rt;
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "configurationMapping", namespace = "http://www.processconfiguration.com")
public class ConfigurationMapping
    extends AbstractExtensionElement
{
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String href;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String}
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value  allowed object is {@link String}
     */
    public void setHref(String value) {
        this.href = value;
    }
}
