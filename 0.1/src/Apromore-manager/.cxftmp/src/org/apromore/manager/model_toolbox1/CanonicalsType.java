
package org.apromore.manager.model_toolbox1;

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
 * <p>Java class for CanonicalsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanonicalsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CanonicalType" type="{http://www.apromore.org/toolbox/model_manager}CanonicalType" maxOccurs="unbounded" minOccurs="0"/>
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
    "canonicalType"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:22:29+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CanonicalsType {

    @XmlElement(name = "CanonicalType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:22:29+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<CanonicalType> canonicalType;

    /**
     * Gets the value of the canonicalType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the canonicalType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCanonicalType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CanonicalType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:22:29+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<CanonicalType> getCanonicalType() {
        if (canonicalType == null) {
            canonicalType = new ArrayList<CanonicalType>();
        }
        return this.canonicalType;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-16T05:22:29+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
