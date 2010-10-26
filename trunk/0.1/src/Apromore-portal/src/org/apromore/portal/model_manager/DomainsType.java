
package org.apromore.portal.model_manager;

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
 * <p>Java class for DomainsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Domain" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainsType", propOrder = {
    "domain"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class DomainsType {

    @XmlElement(name = "Domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<String> domain;

    /**
     * Gets the value of the domain property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the domain property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDomain().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<String> getDomain() {
        if (domain == null) {
            domain = new ArrayList<String>();
        }
        return this.domain;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
