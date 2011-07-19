
package org.apromore.data_access.model_toolbox;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadCanonicalsInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadCanonicalsInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessVersion" type="{http://www.apromore.org/data_access/model_toolbox}ProcessVersionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="LatestVersions" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadCanonicalsInputMsgType", propOrder = {
    "processVersion"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class ReadCanonicalsInputMsgType {

    @XmlElement(name = "ProcessVersion")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected List<ProcessVersionType> processVersion;
    @XmlAttribute(name = "LatestVersions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Boolean latestVersions;

    /**
     * Gets the value of the processVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessVersionType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public List<ProcessVersionType> getProcessVersion() {
        if (processVersion == null) {
            processVersion = new ArrayList<ProcessVersionType>();
        }
        return this.processVersion;
    }

    /**
     * Gets the value of the latestVersions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public Boolean isLatestVersions() {
        return latestVersions;
    }

    /**
     * Sets the value of the latestVersions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:30:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setLatestVersions(Boolean value) {
        this.latestVersions = value;
    }

}
