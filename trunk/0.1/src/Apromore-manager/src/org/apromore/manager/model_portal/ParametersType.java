
package org.apromore.manager.model_portal;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Parameter" type="{http://www.apromore.org/manager/model_portal}ParameterType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParametersType", propOrder = {
    "parameter"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-27T03:50:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ParametersType {

    @XmlElement(name = "Parameter", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-27T03:50:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<ParameterType> parameter;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-27T03:50:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<ParameterType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParameterType>();
        }
        return this.parameter;
    }

}
