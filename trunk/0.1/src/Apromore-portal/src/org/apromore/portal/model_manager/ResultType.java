
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ResultType {

    @XmlAttribute(name = "message")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String message;
    @XmlAttribute(name = "code")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer code;

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Integer getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCode(Integer value) {
        this.code = value;
    }

}
