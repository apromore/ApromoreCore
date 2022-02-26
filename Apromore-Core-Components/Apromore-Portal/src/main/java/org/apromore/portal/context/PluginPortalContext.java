/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.PortalSelection;
import org.apromore.plugin.portal.PortalUI;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

/**
 * Implementation of the PortalContext that is use by portal plug-ins to communicate with the portal.
 */
public class PluginPortalContext implements PortalContext {

    /**
     * Implementation of the PortalUI communication interface
     */
    private final static class PortalUIImpl implements PortalUI {

        @Override
        public Component createComponent(ClassLoader bundleClassLoader, String uri, Component parent, Map<?, ?> arguments) throws IOException {
            InputStream is = bundleClassLoader.getResourceAsStream(uri);
            return Executions.createComponentsDirectly(IOUtils.toString(is, "UTF-8"), "zul", parent, arguments);
        }

    }

    private final MainController mainController;
    private final PortalUI portalUI;

    /**
     * Create a new PluginPortalContext
     *
     * @param mainController
     */
    public PluginPortalContext(MainController mainController) {
        this.mainController = mainController;
        this.portalUI = new PortalUIImpl();
    }

    @Override
    public PortalSelection getSelection() {
        return new PortalSelection() {
            @Override
            public Map<SummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions() {
                return mainController.getSelectedElementsAndVersions();
            }

            @Override
            public Set<SummaryType> getSelectedProcessModels() {
                return mainController.getSelectedElements();
            }

        };
    }

    @Override
    public PortalUI getUI() {
        return new PortalUIImpl();
    }

    @Override
    public Map<String, PortalPlugin> getPortalPluginMap() {
        return mainController.getPortalPluginMap();
    }

    // Scratch area for methods required during porting

    @Override
    public void displayNewProcess(ProcessSummaryType process) {
        mainController.displayNewProcess(process);
    }

    /**
     * Bruce 17.05.2019: Do not use UserSessionManager as it does not work outside the portal ZK environment
     * Apromore has webapp bundles with its own ZK environment
     *
     * Ivo: Get current folder from the portal session
     */
    @Override
    public FolderType getCurrentFolder() {
        // return UserSessionManager.getCurrentFolder();
        // Bruce:
    	// Desktop desktop = mainController.getDesktop();
    	// return (FolderType)desktop.getSession().getAttribute(UserSessionManager.CURRENT_FOLDER);
    	return mainController.getPortalSession().getCurrentFolder();
    }

    /**
     * Bruce 17.05.2019: Do not use UserSessionManager as it does not work outside the portal ZK environment
     * Apromore has webapp bundles with its own ZK environment
     */
    @Override
    public UserType getCurrentUser() {
        //If parent window is closed, then below code block gives nullpointer
        //return UserSessionManager.getCurrentUser();
        //Desktop desktop = mainController.getDesktop();
        //Session session = desktop.getSession();

        UserType userType = UserSessionManager.getCurrentUser();
        if (UserSessionManager.getCurrentUser() == null) {
            userType = (UserType) Sessions.getCurrent().getAttribute(UserSessionManager.USER);
        }
        return userType;
    }
    
    /**
     * Get attributes stored in the user session
     * Created by Bruce 17.05.2019
     */
    @Override
    public Object getAttribute(String attribute) {
    	Desktop desktop = mainController.getDesktop();
    	return desktop.getSession().getAttribute(attribute);
    }

    @Override
    public MainController getMainController() {
        return mainController;
    }

    @Override
    public void refreshContent() {
        mainController.refresh();
    }
}
