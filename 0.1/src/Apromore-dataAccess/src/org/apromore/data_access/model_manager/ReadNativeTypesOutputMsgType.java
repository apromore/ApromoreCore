
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for ReadNativeTypesOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadNativeTypesOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="NativeTypes" type="{http://www.apromore.org/data_access/model_manager}NativeTypesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadNativeTypesOutputMsgType", propOrder = {
    "result",
    "nativeTypes"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadNativeTypesOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "NativeTypes", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected NativeTypesType nativeTypes;

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
     * Gets the value of the nativeTypes property.
     * 
     * @return
     *     possible object is
     *     {@link NativeTypesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public NativeTypesType getNativeTypes() {
        return nativeTypes;
    }

    /**
     * Sets the value of the nativeTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link NativeTypesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeTypes(NativeTypesType value) {
        this.nativeTypes = value;
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
