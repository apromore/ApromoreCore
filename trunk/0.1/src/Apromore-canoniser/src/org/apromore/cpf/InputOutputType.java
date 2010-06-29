
package org.apromore.cpf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputOutputType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InputOutputType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="input"/>
 *     &lt;enumeration value="output"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InputOutputType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T02:48:54+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public enum InputOutputType {

    @XmlEnumValue("input")
    INPUT("input"),
    @XmlEnumValue("output")
    OUTPUT("output");
    private final String value;

    InputOutputType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InputOutputType fromValue(String v) {
        for (InputOutputType c: InputOutputType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
