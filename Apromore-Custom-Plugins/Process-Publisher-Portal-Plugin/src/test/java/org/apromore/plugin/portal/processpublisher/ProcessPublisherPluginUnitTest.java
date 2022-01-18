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
package org.apromore.plugin.portal.processpublisher;

import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessPublishService;
import org.apromore.service.SecurityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessPublisherPluginUnitTest {

    @InjectMocks
    private ProcessPublisherPlugin processPublisherPlugin = new ProcessPublisherPlugin();

    @Mock private ConfigBean configBean;
    @Mock private SecurityService securityService;
    @Mock private ProcessPublishService processPublishService;
    @Mock private ProcessPublish processPublish;
    @Mock private PortalContext portalContext;
    @Mock private MainController mainController;
    @Mock private Session session;

    MockedStatic<Labels> labelsMockedStatic = mockStatic(Labels.class);
    MockedStatic<Sessions> sessionsMockedStatic = mockStatic(Sessions.class);

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void reset_mocks() {
        labelsMockedStatic.close();
        sessionsMockedStatic.close();
    }

    @Test
    public void testAvailabilityNotEnabled() {
        when(configBean.isEnableModelPublish()).thenReturn(false);
        assertEquals(PortalPlugin.Availability.UNAVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    public void testAvailabilityEnabled() {
        when(configBean.isEnableModelPublish()).thenReturn(true);
        assertEquals(PortalPlugin.Availability.AVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    public void testGetBundleName() {
        assertEquals("process_publisher", processPublisherPlugin.getBundleName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSelectedModelNothingSelected() {
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(new HashMap<>());
        when(Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        processPublisherPlugin.getSelectedModel(portalContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSelectedModelProcessTypeNotSelected() {
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(new LogSummaryType(), new ArrayList<>());

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        processPublisherPlugin.getSelectedModel(portalContext);
    }

    @Test
    public void testGetSelectedModelOneProcessTypeSelected() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);

        assertEquals(processSummaryType, processPublisherPlugin.getSelectedModel(portalContext));
    }

    @Test
    public void testGetSelectedModelFromParams() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        Map<String, Object> params = new HashMap<>();
        params.put("selectedModel", processSummaryType);
        processPublisherPlugin.setSimpleParams(params);

        assertEquals(processSummaryType, processPublisherPlugin.getSelectedModel(portalContext));
        assertTrue(processPublisherPlugin.getSimpleParams().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSelectedModelParamsWrongTypeNothingSelected() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedModel", "processSummaryType");
        processPublisherPlugin.setSimpleParams(params);

        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(new HashMap<>());
        when(Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute(Attributes.PREFERRED_LOCALE)).thenReturn(Locale.ENGLISH);

        processPublisherPlugin.getSelectedModel(portalContext);
    }

    @Test
    public void testGetIconIsPublishException() {
        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    public void testGetIconPublishedUnrecordedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute("portalContext")).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(null);

        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    public void testGetIconUnpublishedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute("portalContext")).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(processPublish);
        when(processPublish.isPublished()).thenReturn(false);

        assertEquals("unlink.svg", processPublisherPlugin.getIconPath());
    }

    @Test
    public void testGetIconPublishedProcessId() {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();
        processSummaryType.setId(1);
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        selectedProcessVersions.put(processSummaryType, new ArrayList<>());

        sessionsMockedStatic.when(() -> Sessions.getCurrent()).thenReturn(session);
        when(session.getAttribute("portalContext")).thenReturn(portalContext);
        when(portalContext.getMainController()).thenReturn(mainController);
        when(mainController.getSelectedElementsAndVersions()).thenReturn(selectedProcessVersions);
        when(processPublishService.getPublishDetails(1)).thenReturn(processPublish);
        when(processPublish.isPublished()).thenReturn(true);

        assertEquals("link.svg", processPublisherPlugin.getIconPath());
    }

}
