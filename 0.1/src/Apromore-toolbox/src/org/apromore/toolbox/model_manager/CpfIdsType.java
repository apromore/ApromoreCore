
package org.apromore.toolbox.model_manager;

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
 * <p>Java class for Cpf_idsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Cpf_idsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cpf_id" type="{http://www.apromore.org/toolbox/model_manager}Cpf_idType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cpf_idsType", propOrder = {
    "cpfId"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:56:23+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CpfIdsType {

    @XmlElement(name = "Cpf_id", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:56:23+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<CpfIdType> cpfId;

    /**
     * Gets the value of the cpfId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpfId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCpfId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpfIdType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:56:23+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<CpfIdType> getCpfId() {
        if (cpfId == null) {
            cpfId = new ArrayList<CpfIdType>();
        }
        return this.cpfId;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-05T05:56:23+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
