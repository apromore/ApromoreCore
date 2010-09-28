
package org.apromore.manager.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadDomainsOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadDomainsOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="Domains" type="{http://www.apromore.org/manager/model_portal}DomainsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadDomainsOutputMsgType", propOrder = {
    "result",
    "domains"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadDomainsOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "Domains", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DomainsType domains;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the domains property.
     * 
     * @return
     *     possible object is
     *     {@link DomainsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DomainsType getDomains() {
        return domains;
    }

    /**
     * Sets the value of the domains property.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:20:24+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDomains(DomainsType value) {
        this.domains = value;
    }

}
