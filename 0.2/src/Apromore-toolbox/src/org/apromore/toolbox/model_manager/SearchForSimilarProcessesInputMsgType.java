
package org.apromore.toolbox.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchForSimilarProcessesInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchForSimilarProcessesInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Algorithm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LatestVersions" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Parameters" type="{http://www.apromore.org/toolbox/model_manager}ParametersType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchForSimilarProcessesInputMsgType", propOrder = {
    "processId",
    "versionName",
    "algorithm",
    "latestVersions",
    "parameters"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class SearchForSimilarProcessesInputMsgType {

    @XmlElement(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected int processId;
    @XmlElement(name = "VersionName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String versionName;
    @XmlElement(name = "Algorithm", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String algorithm;
    @XmlElement(name = "LatestVersions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected boolean latestVersions;
    @XmlElement(name = "Parameters", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected ParametersType parameters;

    /**
     * Gets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public int getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setProcessId(int value) {
        this.processId = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the latestVersions property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public boolean isLatestVersions() {
        return latestVersions;
    }

    /**
     * Sets the value of the latestVersions property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setLatestVersions(boolean value) {
        this.latestVersions = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParametersType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T11:59:21+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setParameters(ParametersType value) {
        this.parameters = value;
    }

}
