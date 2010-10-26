
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
 * <p>Java class for UsernamesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UsernamesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UsernamesType", propOrder = {
    "username"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class UsernamesType {

    @XmlElement(name = "Username", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T10:58:22+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<String> username;

    /**
     * Gets the value of the username property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the username property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUsername().add(newItem);
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
    public List<String> getUsername() {
        if (username == null) {
            username = new ArrayList<String>();
        }
        return this.username;
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
