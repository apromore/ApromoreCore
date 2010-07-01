
package org.apromore.data_access.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateProcessSummariesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateProcessSummariesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UpdateProcessSummary" type="{http://www.apromore.org/data_access/model_manager}UpdateProcessSummaryType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateProcessSummariesType", propOrder = {
    "updateProcessSummary"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:18:17+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class UpdateProcessSummariesType {

    @XmlElement(name = "UpdateProcessSummary")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:18:17+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<UpdateProcessSummaryType> updateProcessSummary;

    /**
     * Gets the value of the updateProcessSummary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the updateProcessSummary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpdateProcessSummary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpdateProcessSummaryType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:18:17+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<UpdateProcessSummaryType> getUpdateProcessSummary() {
        if (updateProcessSummary == null) {
            updateProcessSummary = new ArrayList<UpdateProcessSummaryType>();
        }
        return this.updateProcessSummary;
    }

}
