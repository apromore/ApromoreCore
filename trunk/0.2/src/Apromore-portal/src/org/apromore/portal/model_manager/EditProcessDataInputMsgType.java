
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditProcessDataInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditProcessDataInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="processName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="newName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="preName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditProcessDataInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class EditProcessDataInputMsgType {

    @XmlAttribute(name = "processName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String processName;
    @XmlAttribute(name = "id")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Integer id;
    @XmlAttribute(name = "domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String domain;
    @XmlAttribute(name = "owner")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String owner;
    @XmlAttribute(name = "ranking")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String ranking;
    @XmlAttribute(name = "newName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String newName;
    @XmlAttribute(name = "preName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String preName;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public String getProcessName() {
        return processName;
    }

    /**
     * Sets the value of the processName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setRanking(String value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the newName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public String getNewName() {
        return newName;
    }

    /**
     * Sets the value of the newName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setNewName(String value) {
        this.newName = value;
    }

    /**
     * Gets the value of the preName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public String getPreName() {
        return preName;
    }

    /**
     * Sets the value of the preName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:04:27+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setPreName(String value) {
        this.preName = value;
    }

}
