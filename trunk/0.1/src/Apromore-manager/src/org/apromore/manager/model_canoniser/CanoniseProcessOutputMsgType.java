
package org.apromore.manager.model_canoniser;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for CanoniseProcessOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanoniseProcessOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/canoniser/model_manager}ResultType"/>
 *         &lt;element name="ProcessSummary" type="{http://www.apromore.org/canoniser/model_manager}ProcessSummaryType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CanoniseProcessOutputMsgType", propOrder = {
    "result",
    "processSummary"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CanoniseProcessOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "ProcessSummary", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ProcessSummaryType processSummary;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the processSummary property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessSummaryType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ProcessSummaryType getProcessSummary() {
        return processSummary;
    }

    /**
     * Sets the value of the processSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessSummaryType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessSummary(ProcessSummaryType value) {
        this.processSummary = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:49:20+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
