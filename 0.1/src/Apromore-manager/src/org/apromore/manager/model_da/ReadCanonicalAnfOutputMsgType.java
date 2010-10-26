
package org.apromore.manager.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for ReadCanonicalAnfOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadCanonicalAnfOutputMsgType">
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
@XmlType(name = "ReadCanonicalAnfOutputMsgType", propOrder = {
    "result",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadCanonicalAnfOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler cpf;
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler anf;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T04:46:33+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
