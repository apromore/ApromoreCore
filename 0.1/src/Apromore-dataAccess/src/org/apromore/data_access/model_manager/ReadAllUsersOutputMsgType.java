
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for ReadAllUsersOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadAllUsersOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="Usernames" type="{http://www.apromore.org/data_access/model_manager}UsernamesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadAllUsersOutputMsgType", propOrder = {
    "result",
    "usernames"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadAllUsersOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "Usernames", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected UsernamesType usernames;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the usernames property.
     * 
     * @return
     *     possible object is
     *     {@link UsernamesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public UsernamesType getUsernames() {
        return usernames;
    }

    /**
     * Sets the value of the usernames property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsernamesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsernames(UsernamesType value) {
        this.usernames = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
