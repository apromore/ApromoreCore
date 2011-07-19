
package org.apromore.manager.model_portal;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImportProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImportProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessDescription" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/manager/model_portal}EditSessionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="AddFakeEvents" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImportProcessInputMsgType", propOrder = {
    "processDescription",
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class ImportProcessInputMsgType {

    @XmlElement(name = "ProcessDescription", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected DataHandler processDescription;
    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected EditSessionType editSession;
    @XmlAttribute(name = "AddFakeEvents")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Boolean addFakeEvents;

    /**
     * Gets the value of the processDescription property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

    /**
     * Gets the value of the addFakeEvents property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:06:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setAddFakeEvents(Boolean value) {
        this.addFakeEvents = value;
    }

}
