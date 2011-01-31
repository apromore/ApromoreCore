
package org.apromore.toolbox.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessVersion_idsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessVersion_idsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessVersion_id" type="{http://www.apromore.org/toolbox/model_manager}ProcessVersion_idType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessVersion_idsType", propOrder = {
    "processVersionId"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-31T07:11:41+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ProcessVersionIdsType {

    @XmlElement(name = "ProcessVersion_id", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-31T07:11:41+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<ProcessVersionIdType> processVersionId;

    /**
     * Gets the value of the processVersionId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processVersionId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessVersionId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessVersionIdType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-31T07:11:41+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<ProcessVersionIdType> getProcessVersionId() {
        if (processVersionId == null) {
            processVersionId = new ArrayList<ProcessVersionIdType>();
        }
        return this.processVersionId;
    }

}
