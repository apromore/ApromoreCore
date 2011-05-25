
package org.apromore.canoniser.model_manager;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CanoniseProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanoniseProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessDescription" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/canoniser/model_manager}EditSessionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Cpf_uri" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="AddFakeEvents" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CanoniseProcessInputMsgType", propOrder = {
    "processDescription",
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CanoniseProcessInputMsgType {

    @XmlElement(name = "ProcessDescription", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler processDescription;
    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected EditSessionType editSession;
    @XmlAttribute(name = "Cpf_uri")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String cpfUri;
    @XmlAttribute(name = "AddFakeEvents")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean addFakeEvents;

    /**
     * Gets the value of the processDescription property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getProcessDescription() {
        return processDescription;
    }

    /**
     * Sets the value of the processDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessDescription(DataHandler value) {
        this.processDescription = value;
    }

    /**
     * Gets the value of the editSession property.
     * 
     * @return
     *     possible object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public EditSessionType getEditSession() {
        return editSession;
    }

    /**
     * Sets the value of the editSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

    /**
     * Gets the value of the cpfUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getCpfUri() {
        return cpfUri;
    }

    /**
     * Sets the value of the cpfUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpfUri(String value) {
        this.cpfUri = value;
    }

    /**
     * Gets the value of the addFakeEvents property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public Boolean isAddFakeEvents() {
        return addFakeEvents;
    }

    /**
     * Sets the value of the addFakeEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:43:54+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAddFakeEvents(Boolean value) {
        this.addFakeEvents = value;
    }

}
