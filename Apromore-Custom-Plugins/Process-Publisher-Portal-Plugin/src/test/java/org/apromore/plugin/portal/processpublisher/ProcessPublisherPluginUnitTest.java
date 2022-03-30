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

import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessPublishService;
import org.apromore.service.SecurityService;
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
import org.zkoss.util.resource.Labels;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessPublisherPluginUnitTest {

    @InjectMocks
    private ProcessPublisherPlugin processPublisherPlugin = new ProcessPublisherPlugin();

    @Mock private ConfigBean configBean;
    @Mock private SecurityService securityService;
    @Mock private ProcessPublishService processPublishService;
    @Mock private ProcessPublish processPublish;
    @Mock private PortalContext portalContext;
    @Mock private MainController mainController;
    @Mock private Session session;
    @Mock private UserType user;

    MockedStatic<Labels> labelsMockedStatic = mockStatic(Labels.class);
    MockedStatic<Sessions> sessionsMockedStatic = mockStatic(Sessions.class);
    MockedStatic<UserSessionManager> userSessionManagerMockedStatic = mockStatic(UserSessionManager.class);
    MockedStatic<PortalContexts> portalContextHolderMockedStatic = mockStatic(PortalContexts.class);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void reset_mocks() {
        labelsMockedStatic.close();
        sessionsMockedStatic.close();
        userSessionManagerMockedStatic.close();
        portalContextHolderMockedStatic.close();
    }

    @Test
    void testAvailabilityNotEnabled() {
        when(configBean.isEnableModelPublish()).thenReturn(false);
        assertEquals(PortalPlugin.Availability.UNAVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    void testAvailabilityEnabledNoPermission() {
        when(configBean.isEnableModelPublish()).thenReturn(true);
        userSessionManagerMockedStatic.when(() -> UserSessionManager.getCurrentUser()).thenReturn(user);
        when(user.hasAnyPermission(PermissionType.PUBLISH_MODELS)).thenReturn(false);
        assertEquals(PortalPlugin.Availability.UNAVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    void testAvailabilityEnabledWithPermission() {
        when(configBean.isEnableModelPublish()).thenReturn(true);
        userSessionManagerMockedStatic.when(() -> UserSessionManager.getCurrentUser()).thenReturn(user);
        when(user.hasAnyPermission(PermissionType.PUBLISH_MODELS)).thenReturn(true);
        assertEquals(PortalPlugin.Availability.AVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    void testGetBundleName() {
        assertEquals("process_publisher", processPublisherPlugin.getBundleName());
    }

    @Test
    void testGetSelectedModelNothingSelected() {
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(new HashMap<>());
        when(Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        assertThrows(IllegalArgumentException.class, () -> processPublisherPlugin.getSelectedModel(portalContext));
    }

    @Test
    void testGetSelectedModelProcessTypeNotSelected() {
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(new LogSummaryType(), new ArrayList<>());

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        assertThrows(IllegalArgumentException.class, () -> processPublisherPlugin.getSelectedModel(portalContext));
    }

    @Test
    void testGetSelectedModelOneProcessTypeSelected() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);

        assertEquals(processSummaryType, processPublisherPlugin.getSelectedModel(portalContext));
    }

    @Test
    void testGetSelectedModelFromParams() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        Map<String, Object> params = new HashMap<>();
        params.put("selectedModel", processSummaryType);
        processPublisherPlugin.setSimpleParams(params);

        assertEquals(processSummaryType, processPublisherPlugin.getSelectedModel(portalContext));
        assertTrue(processPublisherPlugin.getSimpleParams().isEmpty());
    }

    @Test
    void testGetSelectedModelParamsWrongTypeNothingSelected() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedModel", "processSummaryType");
        processPublisherPlugin.setSimpleParams(params);

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(new HashMap<>());
        when(Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        assertThrows(IllegalArgumentException.class, () -> processPublisherPlugin.getSelectedModel(portalContext));
    }

    @Test
    void testGetIconIsPublishException() {
        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    void testGetIconPublishedUnrecordedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        portalContextHolderMockedStatic.when(() -> PortalContexts.getActivePortalContext()).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(null);

        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    void testGetIconUnpublishedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        portalContextHolderMockedStatic.when(() -> PortalContexts.getActivePortalContext()).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(processPublish);
        when(processPublish.isPublished()).thenReturn(false);

        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    void testGetIconPublishedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        portalContextHolderMockedStatic.when(() -> PortalContexts.getActivePortalContext()).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(processPublish);
        when(processPublish.isPublished()).thenReturn(true);

        assertEquals("link.svg", processPublisherPlugin.getIconPath());
    }

}
