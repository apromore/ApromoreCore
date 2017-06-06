package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class SpecificationSet {
    private List<Condition> conditions;
    private List<Specification> specifications;

    public SpecificationSet() {
        conditions = new ArrayList<>();
        specifications = new ArrayList<>();
    }

    @XmlElementWrapper(name = "conditions")
    @XmlElement(name = "condition")
    public List<Condition> getConditions() { return conditions; }

    @XmlElementWrapper(name = "specifications")
    @XmlElement(name = "specification")
    public List<Specification> getSpecifications() { return specifications; }
    public void addSpecification(Specification specification) { specifications.add(specification); }
}
