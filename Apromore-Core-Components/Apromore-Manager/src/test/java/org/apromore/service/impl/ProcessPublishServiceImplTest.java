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
package org.apromore.service.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessPublishRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.dao.model.User;
import org.apromore.portal.model.ProcessSummaryType;
import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessPublishServiceImplTest extends EasyMockSupport {
    private ProcessPublishRepository processPublishRepository;
    private ProcessRepository processRepository;
    private ProcessModelVersionRepository pmvRepository;

    private ProcessPublishServiceImpl processPublishService;

    @BeforeEach
    void setup() {
        processPublishRepository = createMock(ProcessPublishRepository.class);
        processRepository = createMock(ProcessRepository.class);
        pmvRepository = createMock(ProcessModelVersionRepository.class);

        processPublishService = new ProcessPublishServiceImpl(processPublishRepository, processRepository,
                pmvRepository);
    }

    @Test
    void testSavePublishDetailsNonExistentProcess() {
        int processId = 1;
        String publishId = "publishId";

        assertThrows(IllegalArgumentException.class, () ->
            processPublishService.savePublishDetails(processId, publishId, false));

    }

    @Test
    void testSavePublishDetailsExistingProcess() {
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
    void testUpdatePublishDetails() {
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
    void testGetPublishDetails() {
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

    @Test
    void testIsPublishedNullPublishRecord() {
        expect(processPublishRepository.findByPublishId(anyString())).andReturn(null);
        replayAll();

        assertFalse(processPublishService.isPublished("not a record"));
        verifyAll();
    }

    @Test
    void testIsPublishedExistingPublishedRecord() {
        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setPublished(true);

        expect(processPublishRepository.findByPublishId(anyString())).andReturn(processPublish);
        replayAll();

        assertTrue(processPublishService.isPublished("publishId"));
        verifyAll();
    }

    @Test
    void testIsPublishedExistingUnpublishedRecord() {
        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setPublished(false);

        expect(processPublishRepository.findByPublishId(anyString())).andReturn(processPublish);
        replayAll();

        assertFalse(processPublishService.isPublished("publishId"));
        verifyAll();
    }

    @Test
    void getProcessSummaryNullProcess() {
        expect(processPublishRepository.findProcessByPublishId(anyString())).andReturn(null);
        replayAll();

        assertNull(processPublishService.getSimpleProcessSummary("non-existent id"));
        verifyAll();
    }

    @Test
    void getProcessSummaryExistingProcess() {
        Process process = new Process();
        process.setId(1);
        process.setName("name");
        process.setDomain("domain");
        process.setRanking("rank");

        NativeType nativeType = new NativeType();
        nativeType.setNatType("bpmn");
        process.setNativeType(nativeType);

        User user = new User();
        user.setUsername("testUser");
        process.setUser(user);

        ProcessModelVersion processModelVersion = new ProcessModelVersion();
        processModelVersion.setVersionNumber("1.0");

        expect(processPublishRepository.findProcessByPublishId(anyString())).andReturn(process);
        expect(pmvRepository.getLatestProcessModelVersion(process.getId(), "MAIN"))
                .andReturn(processModelVersion);
        replayAll();

        ProcessSummaryType processSummaryType = processPublishService.getSimpleProcessSummary("publishId");
        assertEquals(process.getId(), processSummaryType.getId());
        assertEquals(process.getName(), processSummaryType.getName());
        assertEquals(process.getDomain(), processSummaryType.getDomain());
        assertEquals(process.getRanking(), processSummaryType.getRanking());
        assertEquals(nativeType.getNatType(), processSummaryType.getOriginalNativeType());
        assertEquals(user.getUsername(), processSummaryType.getOwner());
        assertEquals(processModelVersion.getVersionNumber(), processSummaryType.getLastVersion());
        verifyAll();
    }

    @Test
    void getProcessSummaryExistingProcessNullValues() {
        Process process = new Process();

        expect(processPublishRepository.findProcessByPublishId(anyString())).andReturn(process);
        expect(pmvRepository.getLatestProcessModelVersion(null, "MAIN"))
                .andReturn(null);
        replayAll();

        ProcessSummaryType processSummaryType = processPublishService.getSimpleProcessSummary("publishId");
        assertNull(processSummaryType.getId());
        assertNull(processSummaryType.getName());
        assertNull(processSummaryType.getDomain());
        assertNull(processSummaryType.getRanking());
        assertNull(processSummaryType.getOriginalNativeType());
        assertNull(processSummaryType.getOwner());
        assertNull(processSummaryType.getLastVersion());
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
