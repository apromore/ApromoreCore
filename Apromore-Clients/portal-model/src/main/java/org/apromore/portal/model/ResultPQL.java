/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.07.16 at 11:52:41 AM AEST 
//


package org.apromore.portal.model;

import java.util.ArrayList;
import java.util.List;

public class ResultPQL {
    protected List<Boolean> attributesToShow;
    protected ProcessSummaryType pst;
    protected VersionSummaryType vst;

    /**
     * Gets the value of the attributesToShow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributesToShow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributesToShow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * 
     * 
     */
    public List<Boolean> getAttributesToShow() {
        if (attributesToShow == null) {
            attributesToShow = new ArrayList<Boolean>();
        }
        return this.attributesToShow;
    }

    /**
     * Gets the value of the pst property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessSummaryType }
     *     
     */
    public ProcessSummaryType getPst() {
        return pst;
    }

    /**
     * Sets the value of the pst property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessSummaryType }
     *     
     */
    public void setPst(ProcessSummaryType value) {
        this.pst = value;
    }

    /**
     * Gets the value of the vst property.
     * 
     * @return
     *     possible object is
     *     {@link VersionSummaryType }
     *     
     */
    public VersionSummaryType getVst() {
        return vst;
    }

    /**
     * Sets the value of the vst property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionSummaryType }
     *     
     */
    public void setVst(VersionSummaryType value) {
        this.vst = value;
    }

}
