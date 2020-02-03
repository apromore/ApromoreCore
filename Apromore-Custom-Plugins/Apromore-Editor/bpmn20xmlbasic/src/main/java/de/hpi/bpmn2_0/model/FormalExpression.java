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

package de.hpi.bpmn2_0.model;

import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tFormalExpression complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tFormalExpression">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tExpression">
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="evaluatesToTypeRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFormalExpression", propOrder = {
        "language",
        "evaluatesToTypeRef"
})
public class FormalExpression extends Expression {

    public FormalExpression() {
        super();
    }

    public FormalExpression(String expressionString) {
        super(expressionString);
    }

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String language;

    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String evaluatesToTypeRef;

    /* Getter & Setter */

    /**
     * Gets the value of the language property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the evaluatesToTypeRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public String getEvaluatesToTypeRef() {
        return evaluatesToTypeRef;
    }

    /**
     * Sets the value of the evaluatesToTypeRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setEvaluatesToTypeRef(String value) {
        this.evaluatesToTypeRef = value;
    }
}
