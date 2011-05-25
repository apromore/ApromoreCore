
package org.apromore.portal.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VersionSummaries" type="{http://www.apromore.org/manager/model_portal}VersionSummaryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="original_native_type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last_version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessSummaryType", propOrder = {
    "versionSummaries"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ProcessSummaryType {

    @XmlElement(name = "VersionSummaries", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<VersionSummaryType> versionSummaries;
    @XmlAttribute(name = "original_native_type")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String originalNativeType;
    @XmlAttribute(name = "name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlAttribute(name = "id")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer id;
    @XmlAttribute(name = "domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String domain;
    @XmlAttribute(name = "ranking")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String ranking;
    @XmlAttribute(name = "last_version")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String lastVersion;
    @XmlAttribute(name = "owner")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String owner;

    /**
     * Gets the value of the versionSummaries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the versionSummaries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVersionSummaries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VersionSummaryType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<VersionSummaryType> getVersionSummaries() {
        if (versionSummaries == null) {
            versionSummaries = new ArrayList<VersionSummaryType>();
        }
        return this.versionSummaries;
    }

    /**
     * Gets the value of the originalNativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getOriginalNativeType() {
        return originalNativeType;
    }

    /**
     * Sets the value of the originalNativeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setOriginalNativeType(String value) {
        this.originalNativeType = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setRanking(String value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the lastVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getLastVersion() {
        return lastVersion;
    }

    /**
     * Sets the value of the lastVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setLastVersion(String value) {
        this.lastVersion = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:17:50+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setOwner(String value) {
        this.owner = value;
    }

}
