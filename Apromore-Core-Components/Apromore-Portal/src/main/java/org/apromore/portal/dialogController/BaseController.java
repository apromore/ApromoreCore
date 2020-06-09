/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.dialogController;

// Java 2 Standard Edition packages
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

// Third party packages
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

// Local classes
import org.apromore.manager.client.ManagerService;
import org.apromore.service.EventLogService;
import org.apromore.portal.ConfigBean;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BaseController extends Window {

    public static final String MANAGER_SERVICE = "managerClient";
    public static final String EVENT_LOG_SERVICE = "eventLogService";
    private ManagerService managerService;
    private EventLogService eventLogService;

    protected AutowireCapableBeanFactory beanFactory;
    protected ConfigBean config;

    protected BaseController() {
        beanFactory = WebApplicationContextUtils.getWebApplicationContext(Sessions.getCurrent().getWebApp().getServletContext()).getAutowireCapableBeanFactory();
        config = (ConfigBean) beanFactory.getBean("portalConfig");
    }

    /** Unit testing constructor. */
    protected BaseController(ConfigBean configBean) {
        this.config = configBean;
    }

    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
            setManagerService(managerService);
        }
        return managerService;
    }

    public EventLogService getEventLogService() {
        if (eventLogService == null) {
            eventLogService = (EventLogService) SpringUtil.getBean(EVENT_LOG_SERVICE);
            setEventLogService(eventLogService);
        }
        return eventLogService;
    }

    /**
     * Turn a ZUL document into a ZK {@link Component}.
     *
     * This method requires that the ZUL document is a resource in the classpath of the
     * calling class.  It's suitable for use by portal plugins.
     * Beware that this doesn't work with ZUL in the portal's src/main/webapp directory.
     *
     * @param zulPath  path to a ZUL document within the calling bundle's classpath
     * @return a ZK {@link Component} constructed from the ZUL document at <var>zulPath</var>
     * @throws IllegalArgumentException if no resource can be read from <var>zulPath</var>
     */
    protected <T extends Component> T createComponent(String zulPath) {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(zulPath);

            return (T) Executions.createComponentsDirectly(new InputStreamReader(in), "zul", null, null);

        } catch (IOException e) {
            throw new IllegalArgumentException(zulPath + " not found", e);
        }
    }

    protected String getURL(final String nativeType) {
        String url = "";
        switch (nativeType) {
            case "XPDL 2.2":
                url = "http://b3mn.org/stencilset/bpmn2.0#";
                break;
            case "BPMN 2.0":
                url = "http://b3mn.org/stencilset/bpmn2.0#";
                break;
            case "PNML 1.3.2":
                url = "http://b3mn.org/stencilset/petrinet#";
                break;
            case "YAWL 2.2":
                url = "http://b3mn.org/stencilset/yawl2.2#";
                break;
            case "EPML 2.0":
                url = "http://b3mn.org/stencilset/epc#";
                break;
        }
        return url;
    }


    protected String getImportPath(final String nativeType) {
        String importPath = "";
        switch (nativeType) {
            case "XPDL 2.2":
                importPath = "/" + config.getSiteEditor() + "/editor/xpdlimport";
                break;
            case "BPMN 2.0":
                importPath = "/" + config.getSiteEditor() + "/editor/bpmnimport";
                break;
            case "PNML 1.3.2":
                importPath = "/" + config.getSiteEditor() + "/editor/pnmlimport";
                break;
            case "YAWL 2.2":
                importPath = "/" + config.getSiteEditor() + "/editor/yawlimport";
                break;
            case "EPML 2.0":
                importPath = "/" + config.getSiteEditor() + "/editor/epmlimport";
                break;
        }
        return importPath;
    }

    protected String getExportPath(final String nativeType) {
        String exportPath = "";
        switch (nativeType) {
            case "XPDL 2.2":
                exportPath = "/" + config.getSiteEditor() + "/editor/xpdlexport";
                break;
            case "BPMN 2.0":
                exportPath = "/" + config.getSiteEditor() + "/editor/bpmnexport";
                break;
            case "PNML 1.3.2":
                exportPath = "/" + config.getSiteEditor() + "/editor/pnmlexport";
                break;
            case "YAWL 2.2":
                exportPath = "/" + config.getSiteEditor() + "/editor/yawlexport";
                break;
            case "EPML 2.0":
                exportPath = "/" + config.getSiteEditor() + "/editor/epmlexport";
                break;
        }
        return exportPath;
    }


    private void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }
    private void setEventLogService(final EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

}
