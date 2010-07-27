
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadProcessSummariesOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadProcessSummariesOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="ProcessSummaries" type="{http://www.apromore.org/data_access/model_manager}ProcessSummariesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadProcessSummariesOutputMsgType", propOrder = {
    "result",
    "processSummaries"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadProcessSummariesOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(name = "ProcessSummaries", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ProcessSummariesType processSummaries;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the processSummaries property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessSummariesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ProcessSummariesType getProcessSummaries() {
        return processSummaries;
    }

    /**
     * Sets the value of the processSummaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessSummariesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-27T10:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessSummaries(ProcessSummariesType value) {
        this.processSummaries = value;
    }

}
