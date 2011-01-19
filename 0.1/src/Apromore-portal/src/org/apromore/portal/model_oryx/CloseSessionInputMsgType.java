
package org.apromore.portal.model_oryx;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseSessionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloseSessionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseSessionInputMsgType", propOrder = {
    "code"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CloseSessionInputMsgType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected int code;

    /**
     * Gets the value of the code property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public int getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCode(int value) {
        this.code = value;
    }

}
