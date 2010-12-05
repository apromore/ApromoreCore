
package org.apromore.manager.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MergeProcessesInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MergeProcessesInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessVersion_ids" type="{http://www.apromore.org/manager/model_portal}ProcessVersion_idsType"/>
 *         &lt;element name="Parameters" type="{http://www.apromore.org/manager/model_portal}ParametersType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Algorithm" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MergeProcessesInputMsgType", propOrder = {
    "processVersionIds",
    "parameters"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class MergeProcessesInputMsgType {

    @XmlElement(name = "ProcessVersion_ids", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ProcessVersionIdsType processVersionIds;
    @XmlElement(name = "Parameters", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ParametersType parameters;
    @XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlAttribute(name = "Username")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer processId;
    @XmlAttribute(name = "Algorithm")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String algorithm;

    /**
     * Gets the value of the processVersionIds property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessVersionIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ProcessVersionIdsType getProcessVersionIds() {
        return processVersionIds;
    }

    /**
     * Sets the value of the processVersionIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessVersionIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessVersionIds(ProcessVersionIdsType value) {
        this.processVersionIds = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParametersType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ParametersType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametersType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setParameters(ParametersType value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets the value of the versionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Integer getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:06:39+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

}
