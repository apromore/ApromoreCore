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
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.loganimation.recording;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.service.loganimation.impl.AnimationException;
import org.deckfour.xes.model.XLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

public class ModelValidityTest extends TestDataSetup {

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid_diagram_no_diagram.bpmn",
            "invalid_diagram_no_elements.bpmn",
            "invalid_diagram_disconnected.bpmn",
            "invalid_diagram_two_start_end_events.bpmn",
            "invalid_diagram_start_event_multi_outgoing.bpmn",
            "invalid_diagram_end_event_multi_incoming.bpmn",
            "invalid_diagram_task_multi_outgoing.bpmn",
            "invalid_diagram_task_multi_incoming.bpmn",
            "invalid_diagram_one_pool_lanes.bpmn",
            "invalid_diagram_two_lanes.bpmn"
    })
    void test_InvalidModel(String modelFilename) throws Exception {
        XLog log = readXESFile("src/test/logs/ab.xes");
        String diagram = readBPMNDiagramAsString("src/test/logs/" + modelFilename);
        AnimationException thrown = Assertions.assertThrows(AnimationException.class, () -> {
            replay(List.of(log), diagram);
        }, "AnimationException was expected");
    }
}
