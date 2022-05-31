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

package org.apromore.dao.jpa.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.SubprocessProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SubprocessProcessUnitTest extends BaseTestClass {
    Process process1;
    Process process2;
    String subprocessId = "Test";

    @Autowired
    SubprocessProcessRepository subprocessProcessRepository;

    @Autowired
    ProcessRepository processRepository;

    @BeforeEach
    void setup() {
        process1 = new Process();
        processRepository.saveAndFlush(process1);

        process2 = new Process();
        processRepository.saveAndFlush(process2);

        SubprocessProcess subprocessProcess = new SubprocessProcess();
        subprocessProcess.setSubprocessId(subprocessId);
        subprocessProcess.setSubprocessParent(process1);
        subprocessProcess.setLinkedProcess(process2);

        subprocessProcessRepository.saveAndFlush(subprocessProcess);
    }

    @Test
    void testGetLinkedProcess() {
        assertEquals(process2, subprocessProcessRepository.getLinkedProcess(process1.getId(), subprocessId));
        assertNull(subprocessProcessRepository.getLinkedProcess(-1, subprocessId));
    }

    @Test
    void testGetExistingLink() {
        SubprocessProcess existingSubprocessProcessLink = subprocessProcessRepository.getExistingLink(process1.getId(), subprocessId);
        assertEquals(process1, existingSubprocessProcessLink.getSubprocessParent());
        assertEquals(subprocessId, existingSubprocessProcessLink.getSubprocessId());
        assertEquals(process2, existingSubprocessProcessLink.getLinkedProcess());

        assertNull(subprocessProcessRepository.getExistingLink(-1, subprocessId));
    }

    @Test
    void testGetLinkedSubprocesses() {
        assertTrue(subprocessProcessRepository.getLinkedSubProcesses(process2.getId()).isEmpty());
        assertEquals(1, subprocessProcessRepository.getLinkedSubProcesses(process1.getId()).size());
    }

}
