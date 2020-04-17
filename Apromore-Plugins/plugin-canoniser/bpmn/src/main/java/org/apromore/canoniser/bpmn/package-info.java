/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
BPMN 2.0 Canoniser.
Canonisation is the translation of BPMN models to CPF and ANF; decanonisation is the translation of CPF and ANF back to BPMN.
<p>
Apromore's canonical format (CPF) document describes a set of processes and their subprocesses.
<p>
An annotation format (ANF) document is paired with a specific CPF document and captures features other than formal process
semantics, including informal documentation text and diagram layout.
<p>
A BPMN document can describe not just collection of processes, but choreographies in which one process triggers instances
of another in a non-1:1 manner.  Diagram layouts for the process model occur as a separate section within the same document.

<h4>Canonical mapping of BPMN elements</h4>

All elements of the BPMN 2.0 Descriptive Process Modeling Conformance are mentioned in what follows.
<p>
A BPMN {@link TProcess} maps to the canonical {@link NetType} referenced by {@link CanonicalProcessType#getRootId}.
Note that this means that a separate {@link CanonicalProcessType} is produced for each BPMN {@link TProcess}.
<p>
A BPMN {@link TCallActivity} maps to a canonical {@link TaskType} with a non-<code>null</code>
{@link TaskType#getSubnetId} indicating the invoked subprocess {@link NetType}.
<p>
A BPMN {@link TSubProcess} is mapped in the same way as a BPMN {@link TCallActivity}, except that it should be
the sole referencer of the invoked subprocess.
<p>
A BPMN {@link TTask} maps to a canonical {@link TaskType}.
A BPMN {@link TServiceTask} is distinguished by...?
A BPMN {@link TUserTask} is distinguished by...?
<p>
A BPMN {@link TSequenceFlow} maps to a canonical {@link EdgeType}.
<p>
BPMN {@link MessageFlow}s are necessarily broken between distinct source and target {@link CanonicalProcessType} instances.
{@link ObjectType} instances with the same {@link ObjectType#getName} must occur in each {@link CanonicalProcessType}.
An output {@link ObjectRefType} must occur on the source element within one of the {@link CanonicalProcessType}s, and an
input {@link ObjectRefType} on the target element within the other {@link CanonicalProcessType}.
BPMN message flows are never a result of decanonisation, since only a single {@link CanonicalProcessType} is involved.
<p>
The various BPMN {@link TGateway}s map to various canonical {@link RoutingType}s.
A BPMN {@link ExclusiveGateway} maps to an {@link XORJoinType} or {@link XORSplitType}, depending on the gateway direction.
A BPMN {@link ParallelGateway} maps similarly to an {@link ANDJoinType} or {@link ANDSplitType}.
<p>
The various BPMN {@link TEvent}s map to canonical {@link EventType}s.
A canonical event with outgoing edges only decanonises as a {@link TStartEvent}.
A canonical event with incoming edges only decanonises as a {@link TEndEvent}.

Different types of event are distinguished by a {@link TCatchEvent#getEventDefinition} attribute.
If this attribute is absent, it indicates a start none or end none event type.
If this attribute is present, it means as follows:
<ul>
<li>{@link TMessageEventDefinition} maps to...?
<li>{@link TTimerEventDefinition} maps to...?
<li>{@link TTerminateEventDefinition} maps to populating the cancellation set of the {@link EventType} with
  all elements of the subprocess.  Beware that this introduces an issue if the canonical process is subsequently
  edited to add new elements: Apromore will not know that it should add these new elements to the cancellation set.
<li>Multiple start, multiple end map to...?
</ul>
<p>
BPMN participants (a.k.a. pools) and lanes map to canonical {@link ResourceTypeType}s.
The specialization relation {@link ResourceTypeType#getSpecializationIds} distinguishes participants from lanes.
Any {@link ResourceTypeType} which is a specialization of another is a lane; otherwise it is a participant.
<p>
All elements of a BPMN pool must be members of the same {@link TProcess}, but CPF {@link ResourceTypeType}s may
be referenced by {@link WorkType}s from more than one {@link NetType}.
Therefore during decanonisation, if a CPF {@link ResourceTypeType} is referenced from a number of {@link NetType}s,
it must must be mapped to that many BPMN {@link Participant}s.
<p>
A BPMN {@link LaneSet} does not map to any canonical element.
It is considered to be part of its parent BPMN process (in the case of a pool) or BPMN lane.
If present, a {link LaneSet}'s id, name, or documentation attributes are discarded during canonisation.
<p>
A BPMN {@link TDataObject}...
<p>
A BPMN {@link TDataStore}...

<h4>Annotation mapping of BPMNDI elements</h4>

BPMNDI elements are mapped exclusively to canonical annotation elements within {@link AnnotationsType}.
<p>
The BPMNDI {@link BPMNDiagram} and {@link BPMNPlane} elements are discarded during canonisation.
<p>
A BPMNDI {@link BPMNShape} maps to a canonical {@link GraphicsType}.
The {@link BPMNShape#bpmnElement bpmnElement} attribute maps to the canonical {@link AnnotationType#cpfId cpfId}.
The {@link Shape#getBounds bounds} subelement maps to a pair of canonical {@link PositionType} and {@link SizeType}.
<p>
A BPMNDI {@link BPMNEdge} maps to a canonical {@link GraphicsType}.
The {@link BPMNEdge#bpmnElement bpmnElement} attribute maps to the canonical {@link AnnotationType#cpfId cpfId} of
an {@link EdgeType} or an {@link ObjectRefType}.
The two or more {@link Edge#getWaypoints waypoints} subelements map to canonical {@link PositionType}s, ordered
from source to target.
<p>
BPMNDI {@link BPMNLabel}s and {@link BPMNLabelStyle}s are not implemented and are discarded during canonisation.

<h4>Annotation mapping of BPMN elements</h4>

The {@link AnnotationsType} contains some elements and attributes of the BPMN process semantics, in addition to the BPMNDI ones.
<p>
The BPMN documentation attributes {@link TBaseElement#getDocumentation} map to canonical {@link DocumentationType}s.
The {@link AnnotationType#cpfId cpfId} indicates which {@link TBaseElement} bears the documentation.
<p>
The various BPMN {@link TArtifact}s (that is, {@link TAssociation}, {@link TGroup}, {@link TTextAnnotation})
currently not implemented and are discarded during canonisation.

<h4>BPMN elements outside the Descriptive subclass</h4>

The following additional elements go beyond the BPMN 2.0 Descriptive Process Modeling Conformance subclass.
<p>
A BPMN {@link TDataInputObject} or {@link TDataOutputObject} maps to a canonical {@link ObjectType}.
<p>
A BPMN {@link TDataInputAssociation} does not map to any canonical element.
Instead, it maps to a {@link WorkType#getObjectRef} attribute with a {@link ObjectRefType#getType} of
{@link InputOutputType#INPUT}.
and a {@link ObjectRefType#mapsToObjectId}
<p>
A BPMN {@link TDataOutputAssociation} is mapped similarly to a {@link TDataInputAssociation}, except
that it uses {@link InputOutputType#OUTPUT}.
<p>
A BPMN {@link TTask} with a {@link TBoundaryEvent} is rewritten according to the following transformation:
<div>
<img src="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.bpmn20.svg"/> becomes
<img src="{@docRoot}"/>.
</div>
In the canonical form, each branch appears in the cancellation set of the other.

@author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
*/
package org.apromore.canoniser.bpmn;
