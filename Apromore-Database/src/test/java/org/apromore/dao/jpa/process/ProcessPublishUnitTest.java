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
import org.apromore.dao.ProcessPublishRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessPublish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
class ProcessPublishUnitTest extends BaseTestClass {
    private Process process1;

    @Autowired
    ProcessPublishRepository processPublishRepository;

    @Autowired
    ProcessRepository processRepository;

    @BeforeEach
    void setup() {
        process1 = new Process();
        processRepository.saveAndFlush(process1);

        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setPublishId("b5f5ae43-58cd-4258-ac6d-b2bb684fb48e");
        processPublish.setPublished(true);
        processPublish.setProcess(process1);
        processPublishRepository.saveAndFlush(processPublish);
    }

    @Test
    void testFindByProcessId() {
        assertNull(processPublishRepository.findByProcessId(100));

        ProcessPublish processPublish = processPublishRepository.findByProcessId(process1.getId());
        assertEquals("b5f5ae43-58cd-4258-ac6d-b2bb684fb48e", processPublish.getPublishId());
        assertTrue(processPublish.isPublished());
        assertEquals(process1, processPublish.getProcess());
    }

    @Test
    void testFindByPublishId() {
        assertNull(processPublishRepository.findByPublishId("Invalid id"));

        ProcessPublish processPublish = processPublishRepository.findByPublishId("b5f5ae43-58cd-4258-ac6d-b2bb684fb48e");
        assertEquals("b5f5ae43-58cd-4258-ac6d-b2bb684fb48e", processPublish.getPublishId());
        assertTrue(processPublish.isPublished());
        assertEquals(process1, processPublish.getProcess());
    }

    @Test
    void testFindProcessByPublishId() {
        assertNull(processPublishRepository.findProcessByPublishId("Invalid id"));

        assertEquals(process1, processPublishRepository.findProcessByPublishId("b5f5ae43-58cd-4258-ac6d-b2bb684fb48e"));
    }
}
