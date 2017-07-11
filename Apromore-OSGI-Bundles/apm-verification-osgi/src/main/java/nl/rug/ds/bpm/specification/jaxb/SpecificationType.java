package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class SpecificationType {
    private String id;
    private List<Input> inputs;
    private List<Formula> formulas;

    public SpecificationType() {
        inputs = new ArrayList<>();
        formulas = new ArrayList<>();
    }

    public SpecificationType(String id) {
        this();
        setId(id);
    }

    @XmlAttribute(required = true)
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    @XmlElementWrapper(name = "inputs")
    @XmlElement(name = "input")
    public List<Input> getInputs() { return inputs; }
    public void addInput(Input input) { inputs.add(input); }

    @XmlElementWrapper(name = "formulas")
    @XmlElement(name = "formula")
    public List<Formula> getFormulas() { return formulas; }
    public void addFormula(Formula formula) { formulas.add(formula); }
}
