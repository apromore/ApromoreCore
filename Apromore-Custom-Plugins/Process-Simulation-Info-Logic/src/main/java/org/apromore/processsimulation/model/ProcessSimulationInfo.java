/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */


package org.apromore.processsimulation.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessSimulationInfo {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private long processInstances;
    @XmlAttribute
    private Currency currency;
    @XmlAttribute
    private String startDateTime;

    @XmlElement(name = "qbp:errors")
    private Errors errors;

    @XmlElement(name = "qbp:arrivalRateDistribution")
    private Distribution arrivalRateDistribution;

    @XmlElementWrapper(name = "qbp:elements")
    @XmlElement(name = "qbp:element")
    private List<Element> tasks;

    @XmlElementWrapper(name = "qbp:timetables")
    @XmlElement(name = "qbp:timetable")
    private List<Timetable> timetables;

    @XmlElementWrapper(name = "qbp:resources")
    @XmlElement(name = "qbp:resource")
    private List<Resource> resources;

    @XmlElementWrapper(name = "qbp:sequenceFlows")
    @XmlElement(name = "qbp:sequenceFlow")
    private List<SequenceFlow> sequenceFlows;

}
