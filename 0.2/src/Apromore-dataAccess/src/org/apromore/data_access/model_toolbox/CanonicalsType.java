
package org.apromore.data_access.model_toolbox;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CanonicalsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanonicalsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Canonical" type="{http://www.apromore.org/data_access/model_toolbox}CanonicalType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CanonicalsType", propOrder = {
    "canonical"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:27:54+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class CanonicalsType {

    @XmlElement(name = "Canonical", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:27:54+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected List<CanonicalType> canonical;

    /**
     * Gets the value of the canonical property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the canonical property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCanonical().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CanonicalType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:27:54+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public List<CanonicalType> getCanonical() {
        if (canonical == null) {
            canonical = new ArrayList<CanonicalType>();
        }
        return this.canonical;
    }

}
