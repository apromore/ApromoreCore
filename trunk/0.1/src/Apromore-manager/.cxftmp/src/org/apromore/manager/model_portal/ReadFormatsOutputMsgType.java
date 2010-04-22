
package org.apromore.manager.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadFormatsOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadFormatsOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="Formats" type="{http://www.apromore.org/manager/model_portal}FormatsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadFormatsOutputMsgType", propOrder = {
    "result",
    "formats"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadFormatsOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(name = "Formats", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected FormatsType formats;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the formats property.
     * 
     * @return
     *     possible object is
     *     {@link FormatsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public FormatsType getFormats() {
        return formats;
    }

    /**
     * Sets the value of the formats property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormatsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:25:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setFormats(FormatsType value) {
        this.formats = value;
    }

}
