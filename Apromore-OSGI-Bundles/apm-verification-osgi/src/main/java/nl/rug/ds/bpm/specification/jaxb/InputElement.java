package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class InputElement {
    private String element, target;

    public InputElement() {}

    public InputElement(String element, String target) {
        setElement(element);
        setTarget(target);
    }

    @XmlValue
    public void setElement(String element) { this.element = element; }
    public String getElement() { return element; }

    @XmlAttribute
    public void setTarget(String target) { this.target = target; }
    public String getTarget() { return target; }
}
