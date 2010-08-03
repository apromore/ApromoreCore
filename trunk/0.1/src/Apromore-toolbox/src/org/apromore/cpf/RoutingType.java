
package org.apromore.cpf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoutingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoutingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/CPF}NodeType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoutingType")
@XmlSeeAlso({
    StateType.class,
    JoinType.class,
    SplitType.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class RoutingType
    extends NodeType
{


}
