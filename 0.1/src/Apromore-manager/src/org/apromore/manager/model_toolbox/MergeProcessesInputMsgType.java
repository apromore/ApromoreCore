
package org.apromore.manager.model_toolbox;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


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
 *         &lt;element name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cpf_ids" type="{http://www.apromore.org/toolbox/model_manager}Cpf_idsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MergeProcessesInputMsgType", propOrder = {
    "processName",
    "versionName",
    "username",
    "cpfIds"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class MergeProcessesInputMsgType {

//    @XmlElement(name = "ProcessVersion_ids", required = true)
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ProcessVersionIdsType processVersionIds;
//    @XmlElement(name = "Parameters", required = true)
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ParametersType parameters;
    @XmlElement(name = "ProcessName", required = true)
//	@XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlElement(name = "VersionName", required = true)
//	@XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlElement(name = "Username", required = true)
//	@XmlAttribute(name = "Username")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlElement(name = "Cpf_ids", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected CpfIdsType cpfIds;
//	@XmlAttribute(name = "ProcessId")
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer processId;
//    @XmlAttribute(name = "Algorithm")
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String algorithm;

    /**
     * Gets the value of the processVersionIds property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessVersionIdsType }
     *     
     */
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the cpfIds property.
     * 
     * @return
     *     possible object is
     *     {@link CpfIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public CpfIdsType getCpfIds() {
        return cpfIds;
    }

	/**
     * Sets the value of the cpfIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpfIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpfIds(CpfIdsType value) {
        this.cpfIds = value;
    }

	/**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
//    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:48:57+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:26:45+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
