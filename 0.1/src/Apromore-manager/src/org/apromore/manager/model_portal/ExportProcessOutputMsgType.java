
package org.apromore.manager.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportProcessOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportProcessOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="Process" type="{http://www.apromore.org/manager/model_portal}ImportProcessInputMsgType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportProcessOutputMsgType", propOrder = {
    "result",
    "process"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ExportProcessOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(name = "Process")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ImportProcessInputMsgType process;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link ImportProcessInputMsgType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ImportProcessInputMsgType getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportProcessInputMsgType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:59:49+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcess(ImportProcessInputMsgType value) {
        this.process = value;
    }

}
