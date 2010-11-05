
package org.apromore.toolbox.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadCanonicalsInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadCanonicalsInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cpf_ids" type="{http://www.apromore.org/data_access/model_toolbox}Cpf_idsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadCanonicalsInputMsgType", propOrder = {
    "cpfIds"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadCanonicalsInputMsgType {

    @XmlElement(name = "Cpf_ids", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected CpfIdsType cpfIds;

    /**
     * Gets the value of the cpfIds property.
     * 
     * @return
     *     possible object is
     *     {@link CpfIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public CpfIdsType getCpfIds() {
        return cpfIds;
    }

    /**
     * Sets the value of the cpfIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpfIdsType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpfIds(CpfIdsType value) {
        this.cpfIds = value;
    }

}
