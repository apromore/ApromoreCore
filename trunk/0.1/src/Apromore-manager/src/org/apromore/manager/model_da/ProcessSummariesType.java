
package org.apromore.manager.model_da;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessSummariesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessSummariesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessSummary" type="{http://www.apromore.org/data_access/model_manager}ProcessSummaryType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessSummariesType", propOrder = {
    "processSummary"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ProcessSummariesType {

    @XmlElement(name = "ProcessSummary")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ProcessSummaryType> processSummary;

    /**
     * Gets the value of the processSummary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processSummary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessSummary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessSummaryType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T05:58:09+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ProcessSummaryType> getProcessSummary() {
        if (processSummary == null) {
            processSummary = new ArrayList<ProcessSummaryType>();
        }
        return this.processSummary;
    }

}
