
package org.apromore.portal.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="SearchExpression" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchExpressionType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:52:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class SearchExpressionType {

    @XmlAttribute(name = "SearchExpression")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:52:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String searchExpression;

    /**
     * Gets the value of the searchExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:52:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getSearchExpression() {
        return searchExpression;
    }

    /**
     * Sets the value of the searchExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:52:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setSearchExpression(String value) {
        this.searchExpression = value;
    }

}
