package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class Condition {
    private String condition;

    public Condition() {}

    public Condition(String condition) {
        setCondition(condition);
    }

    @XmlValue
    public void setCondition(String condition) { this.condition = condition; }
    public String getCondition() { return condition; }
}
