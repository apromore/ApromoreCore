
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for SearchForSimilarProcessesOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchForSimilarProcessesOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/manager/model_portal}ResultType"/>
 *         &lt;element name="Canonicals" type="{http://www.apromore.org/manager/model_portal}CanonicalsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchForSimilarProcessesOutputMsgType", propOrder = {
    "result",
    "canonicals"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class SearchForSimilarProcessesOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "Canonicals", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected CanonicalsType canonicals;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the canonicals property.
     * 
     * @return
     *     possible object is
     *     {@link CanonicalsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public CanonicalsType getCanonicals() {
        return canonicals;
    }

    /**
     * Sets the value of the canonicals property.
     * 
     * @param value
     *     allowed object is
     *     {@link CanonicalsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCanonicals(CanonicalsType value) {
        this.canonicals = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
