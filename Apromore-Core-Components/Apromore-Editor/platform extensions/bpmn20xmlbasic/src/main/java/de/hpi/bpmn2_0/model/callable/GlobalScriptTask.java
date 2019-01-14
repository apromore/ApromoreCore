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


package de.hpi.bpmn2_0.model.callable;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tGlobalScriptTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tGlobalScriptTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tGlobalTask">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/MODEL}script" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="scriptLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tGlobalScriptTask", propOrder = {
        "script"
})
public class GlobalScriptTask
        extends GlobalTask {

    /* Constructors */
    public GlobalScriptTask() {
        super();
    }

    public GlobalScriptTask(GlobalTask gt) {
        super(gt);
    }

    @XmlElement
    protected String script;
    @XmlAttribute(name = "scriptLanguage")
    @XmlSchemaType(name = "anyURI")
    protected String scriptLanguage;

    /**
     * Gets the value of the script property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the value of the script property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setScript(String value) {
        this.script = value;
    }

    /**
     * Gets the value of the scriptLanguage property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getScriptLanguage() {
        return scriptLanguage;
    }

    /**
     * Sets the value of the scriptLanguage property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setScriptLanguage(String value) {
        this.scriptLanguage = value;
    }

}
