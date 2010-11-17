
package org.apromore.data_access.model_toolbox;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for ReadProcessSummariesInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadProcessSummariesInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessVersions" type="{http://www.apromore.org/data_access/model_toolbox}ProcessVersionsType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadProcessSummariesInputMsgType", propOrder = {
    "processVersions"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-17T05:08:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadProcessSummariesInputMsgType {

    @XmlElement(name = "ProcessVersions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-17T05:08:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<ProcessVersionsType> processVersions;

    /**
     * Gets the value of the processVersions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processVersions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessVersions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessVersionsType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-17T05:08:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<ProcessVersionsType> getProcessVersions() {
        if (processVersions == null) {
            processVersions = new ArrayList<ProcessVersionsType>();
        }
        return this.processVersions;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-17T05:08:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
