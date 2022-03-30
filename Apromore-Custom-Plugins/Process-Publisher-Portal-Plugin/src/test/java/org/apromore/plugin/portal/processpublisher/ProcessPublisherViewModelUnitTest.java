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
package org.apromore.plugin.portal.processpublisher;

import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.service.ProcessPublishService;
import org.apromore.zk.notification.Notification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessPublisherViewModelUnitTest {
    @InjectMocks
    private ProcessPublisherViewModel processPublisherViewModel = new ProcessPublisherViewModel();

    @Mock private ProcessPublishService processPublishService;
    @Mock private Execution execution;
    @Mock private Session session;
    @Mock private Component component;

    MockedStatic<Executions> executionsMockedStatic = mockStatic(Executions.class);
    MockedStatic<Sessions> sessionsMockedStatic = mockStatic(Sessions.class);
    MockedStatic<Notification> notificationMockedStatic = mockStatic(Notification.class);
    MockedStatic<Clients> clientsMockedStatic = mockStatic(Clients.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void reset_mocks() {
        executionsMockedStatic.close();
        sessionsMockedStatic.close();
        notificationMockedStatic.close();
        clientsMockedStatic.close();
    }

    @Test
    void testGetBundleName() {
        assertEquals("process_publisher", processPublisherViewModel.getBundleName());
    }

    @Test
    void testInitNoPublishDetails() {
        int processId = 1;

        when(processPublishService.getPublishDetails(processId)).thenReturn(null);

        processPublisherViewModel.init(processId);
        assertEquals(processId, processPublisherViewModel.getProcessId());
        assertTrue(processPublisherViewModel.isNewPublishRecord());
        assertFalse(processPublisherViewModel.isPublish());
        assertNotEquals("", processPublisherViewModel.getPublishId());
    }

    @Test
    void testInitExistingPublishDetails() {
        int processId = 1;
        String publishId = "publishId";
        ProcessPublish processPublish = createProcessPublish(processId, publishId, true);

        when(processPublishService.getPublishDetails(processId)).thenReturn(processPublish);

        processPublisherViewModel.init(processId);
        assertEquals(processId, processPublisherViewModel.getProcessId());
        assertFalse(processPublisherViewModel.isNewPublishRecord());
        assertTrue(processPublisherViewModel.isPublish());
        assertEquals(publishId, processPublisherViewModel.getPublishId());
    }

    @Test
    void testUpdatePublishRecordNew() {
        int processId = 50;
        String publishId = "test";
        ProcessPublish processPublish = createProcessPublish(processId, publishId, true);

        when(processPublishService.savePublishDetails(processId, publishId, true))
                .thenReturn(processPublish);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);
        notificationMockedStatic.when(() -> Notification.info(anyString())).thenAnswer(invocation -> null);
        clientsMockedStatic.when(() -> Clients.evalJavaScript(anyString())).thenAnswer(invocation -> null);
        doNothing().when(component).detach();

        processPublisherViewModel.setNewPublishRecord(true);
        processPublisherViewModel.setProcessId(processId);
        processPublisherViewModel.setPublishId(publishId);
        processPublisherViewModel.setPublish(true);

        processPublisherViewModel.updatePublishRecord(component);

        assertEquals(processPublish.getPublishId(), processPublisherViewModel.getPublishId());
        assertEquals(processPublish.isPublished(), processPublisherViewModel.isPublish());
    }

    @Test
    void testUpdatePublishRecordExisting() {
        int processId = 50;
        String publishId = "test";
        ProcessPublish processPublish = createProcessPublish(processId, publishId, true);

        when(processPublishService.updatePublishStatus(publishId, true))
                .thenReturn(processPublish);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);
        notificationMockedStatic.when(() -> Notification.info(anyString())).thenAnswer(invocation -> null);
        clientsMockedStatic.when(() -> Clients.evalJavaScript(anyString())).thenAnswer(invocation -> null);
        doNothing().when(component).detach();

        processPublisherViewModel.setNewPublishRecord(false);
        processPublisherViewModel.setPublish(true);
        processPublisherViewModel.setProcessId(processId);
        processPublisherViewModel.setPublishId(publishId);

        processPublisherViewModel.updatePublishRecord(component);

        assertEquals(processPublish.getPublishId(), processPublisherViewModel.getPublishId());
        assertEquals(processPublish.isPublished(), processPublisherViewModel.isPublish());
    }

    @Test
    void testGetPublishLinkLocalhost() {
        executionsMockedStatic.when(() -> Executions.getCurrent()).thenReturn(execution);
        when(execution.getScheme()).thenReturn("http");
        when(execution.getServerName()).thenReturn("localhost");
        when(execution.getServerPort()).thenReturn(8181);

        processPublisherViewModel.setPublishId("PI123456789");

        String expectedLink = "http://localhost:8181/zkau/web/openModelInBPMNio.zul?view=true&publishId=PI123456789";
        assertEquals(expectedLink, processPublisherViewModel.getPublishLink());
    }

    @Test
    void testGetPublishLinkServer() {
        executionsMockedStatic.when(() -> Executions.getCurrent()).thenReturn(execution);
        when(execution.getScheme()).thenReturn("https");
        when(execution.getServerName()).thenReturn("remoteServer");

        processPublisherViewModel.setPublishId("PI123456789");

        String expectedLink = "https://remoteServer/zkau/web/openModelInBPMNio.zul?view=true&publishId=PI123456789";
        assertEquals(expectedLink, processPublisherViewModel.getPublishLink());
    }

    private ProcessPublish createProcessPublish(final int processId, final String publishId,
                                                final boolean published) {
        Process process = new Process();
        process.setId(processId);

        ProcessPublish processPublish = new ProcessPublish();
        processPublish.setProcess(process);
        processPublish.setPublished(true);
        processPublish.setPublishId(publishId);
        return processPublish;
    }
}
