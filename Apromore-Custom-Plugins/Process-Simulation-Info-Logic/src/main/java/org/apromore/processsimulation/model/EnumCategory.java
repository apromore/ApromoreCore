package org.apromore.processsimulation.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class EnumCategory {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private double assignmentProbability;
    @XmlAttribute
    private String rawProbability;
}
