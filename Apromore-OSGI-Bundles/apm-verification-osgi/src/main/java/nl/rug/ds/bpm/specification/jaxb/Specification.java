package nl.rug.ds.bpm.specification.jaxb;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by p256867 on 6-4-2017.
 */

@XmlRootElement
public class Specification {
    private String id, type;
    private List<InputElement> inputElements;

    @XmlTransient
    private SpecificationType specificationType;

    public Specification() {
        inputElements = new ArrayList<>();
    }

    public Specification(String type) {
        this();
        setType(type);
    }

    @XmlAttribute
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    @XmlAttribute
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    @XmlElementWrapper(name = "inputElements")
    @XmlElement(name = "inputElement")
    public List<InputElement> getInputElements() { return inputElements; }
    public void addInputElement(InputElement inputElement) { inputElements.add(inputElement); }

    @XmlTransient
    public SpecificationType getSpecificationType() {
        return specificationType;
    }
    public void setSpecificationType(SpecificationType specificationType) {
        if(specificationType.getId().equals(type))
            this.specificationType = specificationType;
    }
}
