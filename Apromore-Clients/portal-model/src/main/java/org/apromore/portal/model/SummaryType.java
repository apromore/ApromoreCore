/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

public class SummaryType {
    protected FolderType folder;
    protected String name;
    protected Integer id;
    protected Long calendarId;
    protected String domain;
    protected String ranking;
    protected Boolean makePublic;
    protected String owner;
    protected Boolean hasRead;
    protected Boolean hasWrite;
    protected Boolean hasOwnership;
    protected String createDate;

    /**
     * Gets the value of the folder property.
     * 
     * @return
     *     possible object is
     *     {@link FolderType }
     *     
     */
    public FolderType getFolder() {
        return folder;
    }

    /**
     * Sets the value of the folder property.
     * 
     * @param value
     *     allowed object is
     *     {@link FolderType }
     *     
     */
    public void setFolder(FolderType value) {
        this.folder = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRanking() {
        return ranking;
    }

    /**
     * Sets the value of the ranking property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRanking(String value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the makePublic property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMakePublic() {
        return makePublic;
    }

    /**
     * Sets the value of the makePublic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMakePublic(Boolean value) {
        this.makePublic = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return (makePublic != null && makePublic == true) ? "public" : owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the hasRead property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasRead() {
        return hasRead;
    }

    /**
     * Sets the value of the hasRead property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasRead(Boolean value) {
        this.hasRead = value;
    }

    /**
     * Gets the value of the hasWrite property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasWrite() {
        return hasWrite;
    }

    /**
     * Sets the value of the hasWrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasWrite(Boolean value) {
        this.hasWrite = value;
    }

    /**
     * Gets the value of the hasOwnership property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasOwnership() {
        return hasOwnership;
    }

    /**
     * Sets the value of the hasOwnership property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasOwnership(Boolean value) {
        this.hasOwnership = value;
    }

    public Long getCalendarId() {
	return calendarId;
    }

    public void setCalendarId(Long calendarId) {
	this.calendarId = calendarId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

}
