package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement(name = "bpmSpecification")
public class BPMSpecification {
    private List<SpecificationType> specificationTypes;
    private List<SpecificationSet> specificationSets;
    private List<Group> groups;

    public BPMSpecification() {
        specificationTypes = new ArrayList<>();
        specificationSets = new ArrayList<>();
        groups = new ArrayList<>();
    }

    @XmlElementWrapper(name = "specificationTypes")
    @XmlElement(name = "specificationType")
    public List<SpecificationType> getSpecificationTypes() { return specificationTypes; }
    public void addSpecificationType(SpecificationType specificationType) { specificationTypes.add(specificationType); }

    @XmlElementWrapper(name = "specificationSets")
    @XmlElement(name = "specificationSet")
    public List<SpecificationSet> getSpecificationSets() { return specificationSets; }
    public void addSpecificationSet(SpecificationSet specificationSet) { specificationSets.add(specificationSet); }
    
    @XmlElementWrapper(name = "elementGroups")
    @XmlElement(name = "group")
    public List<Group> getGroups() { return groups; }
    public void addGroup(Group group) { groups.add(group); }
}
