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

package org.apromore.plugin.portal.bpmneditor.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.common.PortalSession;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.service.AuthorizationService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.util.AccessType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.zkoss.zkplus.spring.SpringUtil;

class LinkSubProcessViewModelUnitTest {
    private static MockedStatic<UserSessionManager> userSessionManagerMockedStatic;
    private static MockedStatic<SpringUtil> springUtilMockedStatic;

    @Mock
    private SecurityService securityService;

    @Mock
    private ProcessService processService;

    @Mock
    private UserInterfaceHelper uiHelper;

    @Mock
    private MainController mainController;

    @Mock
    private PortalSession portalSession;

    @InjectMocks
    LinkSubProcessViewModel linkSubProcessViewModel;

    @BeforeAll
    static void oneTimeSetup() {
        userSessionManagerMockedStatic = mockStatic(UserSessionManager.class);
        springUtilMockedStatic = mockStatic(SpringUtil.class);
    }

    @AfterAll
    static void oneTimeTearDown() {
        userSessionManagerMockedStatic.close();
        springUtilMockedStatic.close();
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitWithNoLinkedProcess() {
        UserType userType = mock(UserType.class);

        userSessionManagerMockedStatic.when(UserSessionManager::getCurrentUser).thenReturn(userType);

        linkSubProcessViewModel.init(mainController, "test", 1);
        assertThat(linkSubProcessViewModel.getLinkType()).isEqualTo("NEW");
        assertThat(linkSubProcessViewModel.getSelectedProcess()).isNull();
        assertFalse(linkSubProcessViewModel.isProcessListEnabled());
    }

    @Test
    void testInitWithLinkedProcess() throws UserNotFoundException {
        UserType userType = new UserType();
        userType.setId("userId");
        SummariesType summariesType = buildProcessSummaryList(1);
        ProcessSummaryType linkedProcess = new ProcessSummaryType();
        linkedProcess.setId(0);

        userSessionManagerMockedStatic.when(UserSessionManager::getCurrentUser).thenReturn(userType);
        when(processService.getLinkedProcess(1, "test")).thenReturn(linkedProcess);

        when(mainController.getPortalSession()).thenReturn(portalSession);
        when(portalSession.getTree()).thenReturn(Collections.emptyList());


        when(uiHelper.buildProcessSummaryList(userType.getId(), 0, 0, LinkSubProcessViewModel.PAGE_SIZE))
            .thenReturn(summariesType);

        when(securityService.getUserById(userType.getId())).thenReturn(mock(User.class));

        AuthorizationService authorizationService = mock(AuthorizationService.class);
        springUtilMockedStatic.when(() -> SpringUtil.getBean("authorizationService")).thenReturn(authorizationService);
        when(authorizationService.getProcessAccessTypeByUser(anyInt(), any(User.class))).thenReturn(AccessType.EDITOR);

        linkSubProcessViewModel.init(mainController, "test", 1);
        List<SummaryType> processList =  linkSubProcessViewModel.getProcessList();

        linkSubProcessViewModel.init(mainController, "test", 1);

        assertThat(processList).hasSize(1);
        assertThat(linkSubProcessViewModel.getLinkType()).isEqualTo("EXISTING");
        assertThat(linkSubProcessViewModel.getSelectedProcess()).isNotNull();
        assertTrue(linkSubProcessViewModel.isProcessListEnabled());
    }

    @Test
    void testEnableProcessLinkUpdatesAfterLinkTypeChange() {
        linkSubProcessViewModel.setLinkType("NEW");
        linkSubProcessViewModel.onCheckLinkType();
        assertFalse(linkSubProcessViewModel.isProcessListEnabled());

        linkSubProcessViewModel.setLinkType("EXISTING");
        linkSubProcessViewModel.onCheckLinkType();
        assertTrue(linkSubProcessViewModel.isProcessListEnabled());

        linkSubProcessViewModel.setLinkType(null);
        linkSubProcessViewModel.onCheckLinkType();
        assertFalse(linkSubProcessViewModel.isProcessListEnabled());
    }

    @Test
    void testGetProcessListReturnsProcessInSubFolders() throws UserNotFoundException {
        UserType userType = new UserType();
        userType.setId("userId");

        userSessionManagerMockedStatic.when(UserSessionManager::getCurrentUser).thenReturn(userType);

        when(mainController.getPortalSession()).thenReturn(portalSession);
        when(portalSession.getTree()).thenReturn(buildFolderTree());
        when(uiHelper.buildProcessSummaryList(userType.getId(), 0, 0, LinkSubProcessViewModel.PAGE_SIZE))
            .thenReturn(buildProcessSummaryList(100));
        when(uiHelper.buildProcessSummaryList(userType.getId(), 1, 0, LinkSubProcessViewModel.PAGE_SIZE))
            .thenReturn(buildProcessSummaryList(10));
        when(uiHelper.buildProcessSummaryList(userType.getId(), 2, 0, LinkSubProcessViewModel.PAGE_SIZE))
            .thenReturn(buildProcessSummaryList(1));

        when(securityService.getUserById(userType.getId())).thenReturn(mock(User.class));

        AuthorizationService authorizationService = mock(AuthorizationService.class);
        springUtilMockedStatic.when(() -> SpringUtil.getBean("authorizationService")).thenReturn(authorizationService);
        when(authorizationService.getProcessAccessTypeByUser(anyInt(), any(User.class))).thenReturn(AccessType.EDITOR);

        linkSubProcessViewModel.init(mainController, "test", 1);
        List<SummaryType> processList =  linkSubProcessViewModel.getProcessList();

        assertThat(processList).hasSize(111);
        //The same list is returned each time
        assertThat(linkSubProcessViewModel.getProcessList()).isEqualTo(processList);
    }

    private List<FolderType> buildFolderTree() {
        List<FolderType> folderTypeList = new ArrayList<>();

        FolderType folderLevel1 = buildFolderTypeWithProcesses(1, "A");
        FolderType folderLevel2 = buildFolderTypeWithProcesses(2, "B");

        folderTypeList.add(folderLevel1);
        setParentFolder(folderLevel1, folderLevel2);

        return folderTypeList;
    }

    private FolderType buildFolderTypeWithProcesses(int id, String name) {
        FolderType folderType = new FolderType();
        folderType.setId(id);
        folderType.setFolderName(name);

        return folderType;
    }

    private void setParentFolder(FolderType parent, FolderType child) {
        if (parent == null || child == null) {
            return;
        }

        child.setParentId(parent.getId());
        parent.getFolders().add(child);
    }

    private SummariesType buildProcessSummaryList(int numProcesses) {
        SummariesType summariesType = new SummariesType();

        for (int i = 0; i < numProcesses; i++) {
            ProcessSummaryType processSummaryType = new ProcessSummaryType();
            processSummaryType.setId(i);
            summariesType.getSummary().add(processSummaryType);
        }

        return summariesType;
    }

}
