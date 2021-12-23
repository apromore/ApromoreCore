/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.service.impl;

import org.apromore.dao.ProcessPublishRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessPublish;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessPublishServiceImplTest extends EasyMockSupport {
    private ProcessPublishRepository processPublishRepository;
    private ProcessRepository processRepository;

    private ProcessPublishServiceImpl processPublishService;

    @Before
    public void setup() {
        processPublishRepository = createMock(ProcessPublishRepository.class);
        processRepository = createMock(ProcessRepository.class);

        processPublishService = new ProcessPublishServiceImpl(processPublishRepository, processRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSavePublishDetailsNonExistentProcess() {
        int processId = 1;
        String publishId = "publishId";

        processPublishService.savePublishDetails(processId, publishId, false);
    }

    @Test
    public void testSavePublishDetailsExistingProcess() {
        int processId = 1;
        String publishId = "publishId";
        Process process = createProcess(processId);

        expect(processRepository.findUniqueByID(processId)).andReturn(process);
        expect(processPublishRepository.saveAndFlush(anyObject(ProcessPublish.class)))
                .andReturn(createProcessPublish(process, publishId, true));
        replayAll();

        ProcessPublish result = processPublishService.savePublishDetails(processId, publishId, true);
        assertEquals(publishId, result.getPublishId());
        assertEquals(process, result.getProcess());
        assertTrue(result.isPublished());
        verifyAll();
    }

    @Test
    public void testUpdatePublishDetails() {
        String publishId = "publishId";
        ProcessPublish processPublish = createProcessPublish(createProcess(1), publishId, false);

        expect(processPublishRepository.findByPublishId(publishId)).andReturn(processPublish);
        expect(processPublishRepository.saveAndFlush(anyObject(ProcessPublish.class))).andReturn(processPublish);
        replayAll();

        assertFalse(processPublish.isPublished());

        ProcessPublish result = processPublishService.updatePublishStatus(publishId, true);
        assertTrue(result.isPublished());
        verifyAll();
    }

    @Test
    public void testGetPublishDetails() {
        int processId = 1;
        String publishId = "publishId";
        Process process = createProcess(processId);

        expect(processPublishRepository.findByProcessId(processId)).andReturn(
                createProcessPublish(process, publishId, false));
        replayAll();

        ProcessPublish result = processPublishService.getPublishDetails(processId);

        assertEquals(publishId, result.getPublishId());
        assertEquals(process, result.getProcess());
        assertFalse(result.isPublished());
        verifyAll();
    }

    private ProcessPublish createProcessPublish(final Process process, final String publishId,
                                                final boolean published) {
        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setProcess(process);
        processPublish.setPublishId(publishId);
        processPublish.setPublished(published);
        return processPublish;
    }

    private Process createProcess(final int processId) {
        Process process = new Process();
        process.setId(processId);
        return process;
    }
}
