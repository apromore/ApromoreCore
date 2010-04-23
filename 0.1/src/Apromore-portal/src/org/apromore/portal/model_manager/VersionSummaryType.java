
package org.apromore.portal.model_manager;

import java.util.Date;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for VersionSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VersionSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last_update" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="creation_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VersionSummaryType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class VersionSummaryType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer ranking;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute(name = "last_update")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Date lastUpdate;
    @XmlAttribute(name = "creation_date")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Date creationDate;

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getRanking() {
        return ranking;
    }

    /**
     * Sets the value of the ranking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setRanking(Integer value) {
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:06:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }


 

	

	

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	/**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T08:15:03+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setLastUpdate(Date value) {
        this.lastUpdate = value;
    }

 


	/**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T08:15:03+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCreationDate(Date value) {
        this.creationDate = value;
    }

}
