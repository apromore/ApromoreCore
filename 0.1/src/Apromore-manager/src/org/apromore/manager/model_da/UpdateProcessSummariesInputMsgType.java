
package org.apromore.manager.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateProcessSummariesInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateProcessSummariesInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UpdateProcessSummaries" type="{http://www.apromore.org/data_access/model_manager}UpdateProcessSummariesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateProcessSummariesInputMsgType", propOrder = {
    "updateProcessSummaries"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class UpdateProcessSummariesInputMsgType {

    @XmlElement(name = "UpdateProcessSummaries", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected UpdateProcessSummariesType updateProcessSummaries;

    /**
     * Gets the value of the updateProcessSummaries property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateProcessSummariesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public UpdateProcessSummariesType getUpdateProcessSummaries() {
        return updateProcessSummaries;
    }

    /**
     * Sets the value of the updateProcessSummaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateProcessSummariesType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUpdateProcessSummaries(UpdateProcessSummariesType value) {
        this.updateProcessSummaries = value;
    }

}
