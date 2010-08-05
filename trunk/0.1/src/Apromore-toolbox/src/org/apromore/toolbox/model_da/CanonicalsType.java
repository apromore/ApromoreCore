
package org.apromore.toolbox.model_da;

import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
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
 *         &lt;element name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary" maxOccurs="unbounded" minOccurs="0"/>
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
    "processId",
    "versionName",
    "cpf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CanonicalsType {

    @XmlElement(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected int processId;
    @XmlElement(name = "VersionName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlElement(name = "Cpf")
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<DataHandler> cpf;

    /**
     * Gets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public int getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessId(int value) {
        this.processId = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets the value of the versionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCpf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataHandler }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public List<DataHandler> getCpf() {
        if (cpf == null) {
            cpf = new ArrayList<DataHandler>();
        }
        return this.cpf;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-08-04T03:40:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
