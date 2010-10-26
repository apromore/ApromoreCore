
package org.apromore.data_access.model_manager;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for UserType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SearchHistories" type="{http://www.apromore.org/data_access/model_manager}SearchHistoriesType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lastname" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="email" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="passwd" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserType", propOrder = {
    "searchHistories"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class UserType {

    @XmlElement(name = "SearchHistories")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<SearchHistoriesType> searchHistories;
    @XmlAttribute(name = "firstname")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String firstname;
    @XmlAttribute(name = "lastname")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String lastname;
    @XmlAttribute(name = "email")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String email;
    @XmlAttribute(name = "username")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlAttribute(name = "passwd")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String passwd;

    /**
     * Gets the value of the searchHistories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchHistories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchHistories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchHistoriesType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<SearchHistoriesType> getSearchHistories() {
        if (searchHistories == null) {
            searchHistories = new ArrayList<SearchHistoriesType>();
        }
        return this.searchHistories;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setLastname(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the passwd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getPasswd() {
        return passwd;
    }

    /**
     * Sets the value of the passwd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setPasswd(String value) {
        this.passwd = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-25T03:11:44+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
