
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
 * <p>Java class for ProcessVersionIdentifierType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessVersionIdentifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Processid" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessVersionIdentifierType", propOrder = {
    "versionName"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ProcessVersionIdentifierType {

    @XmlElement(name = "VersionName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<String> versionName;
    @XmlAttribute(name = "Processid")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processid;

    /**
     * Gets the value of the versionName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the versionName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVersionName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<String> getVersionName() {
        if (versionName == null) {
            versionName = new ArrayList<String>();
        }
        return this.versionName;
    }

    /**
     * Gets the value of the processid property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getProcessid() {
        return processid;
    }

    /**
     * Sets the value of the processid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-23T09:54:06+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessid(Integer value) {
        this.processid = value;
    }

}
