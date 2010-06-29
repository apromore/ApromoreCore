
package org.apromore.data_access.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteProcessVersionsInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteProcessVersionsInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessVersionIdentifier" type="{http://www.apromore.org/data_access/model_manager}ProcessVersionIdentifierType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteProcessVersionsInputMsgType", propOrder = {
    "processVersionIdentifier"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:28:36+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class DeleteProcessVersionsInputMsgType {

    @XmlElement(name = "ProcessVersionIdentifier", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:28:36+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ProcessVersionIdentifierType> processVersionIdentifier;

    /**
     * Gets the value of the processVersionIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processVersionIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessVersionIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessVersionIdentifierType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:28:36+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ProcessVersionIdentifierType> getProcessVersionIdentifier() {
        if (processVersionIdentifier == null) {
            processVersionIdentifier = new ArrayList<ProcessVersionIdentifierType>();
        }
        return this.processVersionIdentifier;
    }

}
