
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for ReadUserOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadUserOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="User" type="{http://www.apromore.org/manager/model_portal}UserType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadUserOutputMsgType", propOrder = {
    "result",
    "user"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadUserOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "User", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected UserType user;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link UserType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public UserType getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUser(UserType value) {
        this.user = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
