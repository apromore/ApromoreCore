
package org.apromore.manager.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadCanonicalOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadCanonicalOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadCanonicalOutputMsgType", propOrder = {
    "result",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadCanonicalOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler cpf;
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler anf;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public DataHandler getCpf() {
        return cpf;
    }

    /**
     * Sets the value of the cpf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCpf(DataHandler value) {
        this.cpf = value;
    }

    /**
     * Gets the value of the anf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public DataHandler getAnf() {
        return anf;
    }

    /**
     * Sets the value of the anf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T02:42:59+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

}
