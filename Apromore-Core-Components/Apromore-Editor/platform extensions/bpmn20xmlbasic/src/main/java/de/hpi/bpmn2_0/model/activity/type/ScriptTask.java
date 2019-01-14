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

package de.hpi.bpmn2_0.model.activity.type;

import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.callable.GlobalScriptTask;
import de.hpi.bpmn2_0.model.callable.GlobalTask;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tScriptTask complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tScriptTask">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tTask">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}script" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="scriptLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tScriptTask", propOrder = {
        "script"
})
public class ScriptTask
        extends Task {
    /**
     * Default constructor
     */
    public ScriptTask() {

    }

    /**
     * Copy constructor
     *
     * @param scriptTask The {@link ScriptTask} to copy.
     */
    public ScriptTask(ScriptTask scriptTask) {
        super(scriptTask);
        this.setScript(scriptTask.getScript());
        this.setScriptFormat(scriptTask.getScriptFormat());
    }

    @XmlElement
    protected String script;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String scriptFormat;


    public void acceptVisitor(Visitor v) {
        v.visitScriptTask(this);
    }

    public GlobalTask getAsGlobalTask() {
        GlobalScriptTask gst = new GlobalScriptTask(super.getAsGlobalTask());

        gst.setScript(this.getScript());
        gst.setScriptLanguage(this.getScriptFormat());

        return gst;
    }

    /* Getter & Setter */

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
    public String getScriptFormat() {
        return scriptFormat;
    }

    /**
     * Sets the value of the scriptLanguage property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setScriptFormat(String value) {
        this.scriptFormat = value;
    }

}
