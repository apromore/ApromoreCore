
package org.apromore.toolbox.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StoreCpfOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoreCpfOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_toolbox}ResultType"/>
 *         &lt;element name="ProcessSummary" type="{http://www.apromore.org/data_access/model_toolbox}ProcessSummaryType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreCpfOutputMsgType", propOrder = {
    "result",
    "processSummary"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class StoreCpfOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected ResultType result;
    @XmlElement(name = "ProcessSummary")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected ProcessSummaryType processSummary;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setProcessSummary(ProcessSummaryType value) {
        this.processSummary = value;
    }

}
