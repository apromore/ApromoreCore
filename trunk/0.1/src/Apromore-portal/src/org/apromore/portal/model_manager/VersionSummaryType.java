
package org.apromore.portal.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for VersionSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VersionSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Annotations" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last_update" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creation_date" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VersionSummaryType", propOrder = {
    "annotations"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class VersionSummaryType {

    @XmlElement(name = "Annotations")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<String> annotations;
    @XmlAttribute(name = "ranking")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String ranking;
    @XmlAttribute(name = "name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlAttribute(name = "last_update")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String lastUpdate;
    @XmlAttribute(name = "creation_date")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String creationDate;
    @XmlAttribute(name = "documentation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String documentation;

    /**
     * Gets the value of the annotations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<String> getAnnotations() {
        if (annotations == null) {
            annotations = new ArrayList<String>();
        }
        return this.annotations;
    }

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setRanking(String value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setLastUpdate(String value) {
        this.lastUpdate = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCreationDate(String value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-06T11:09:41+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
