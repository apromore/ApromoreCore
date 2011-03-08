
package org.apromore.canoniser.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCpfUriOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCpfUriOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_canoniser}ResultType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="CpfURI" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCpfUriOutputMsgType", propOrder = {
    "result"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class GetCpfUriOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlAttribute(name = "CpfURI")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String cpfURI;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the cpfURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getCpfURI() {
        return cpfURI;
    }

    /**
     * Sets the value of the cpfURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpfURI(String value) {
        this.cpfURI = value;
    }

}
