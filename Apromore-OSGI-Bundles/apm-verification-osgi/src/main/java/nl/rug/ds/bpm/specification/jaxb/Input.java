package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class Input {
    private String value, type;

    public Input() {}

    public Input(String value) {
        setValue(value);
        setType("or");
    }

    public Input(String value, String type) {
        setValue(value);
        setType(type);
    }

    @XmlAttribute
    public void setType(String type) {
        if(type.equalsIgnoreCase("or") || type.equalsIgnoreCase("and"))
            this.type = type;
        else
            this.type = "or";
    }
    public String getType() { return type; }

    @XmlValue
    public void setValue(String value) { this.value = value; }
    public String getValue() { return value; }
}
