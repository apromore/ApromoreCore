/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.portal.dialogController;

import java.io.IOException;
// Java 2 Standard Edition packages
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

// Third party packages
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apromore.portal.model.UserType;
import org.apromore.service.UserService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

// Local classes
import org.apromore.manager.client.ManagerService;
import org.apromore.service.EventLogService;
import org.apromore.service.SecurityService;
import org.apromore.service.AuthorizationService;
import org.apromore.service.WorkspaceService;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BaseController extends Window {

    public static final String XPDL_2_2 = "XPDL 2.2";
    public static final String BPMN_2_0 = "BPMN 2.0";
    public static final String PNML_1_3_2 = "PNML 1.3.2";
    public static final String YAWL_2_2 = "YAWL 2.2";
    public static final String EPML_2_0 = "EPML 2.0";
    private ManagerService managerService;
    private EventLogService eventLogService;
    private UserService userService;
    private SecurityService securityService;
    private AuthorizationService authorizationService;
    private WorkspaceService workspaceService;

    protected AutowireCapableBeanFactory beanFactory;
    protected ConfigBean config;

    protected BaseController() {
        beanFactory = WebApplicationContextUtils.getWebApplicationContext(
                Sessions.getCurrent().getWebApp().getServletContext()).getAutowireCapableBeanFactory();
        config = (ConfigBean) beanFactory.getBean("portalConfig");
    }

    /** Unit testing constructor. */
    protected BaseController(ConfigBean configBean) {
        this.config = configBean;
    }

    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
            setManagerService(managerService);
        }
        return managerService;
    }

    public EventLogService getEventLogService() {
        if (eventLogService == null) {
            eventLogService = (EventLogService) SpringUtil.getBean(Constants.EVENT_LOG_SERVICE);
            setEventLogService(eventLogService);
        }
        return eventLogService;
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = (UserService) SpringUtil.getBean(Constants.USER_SERVICE);
            setUserService(userService);
        }
        return userService;
    }

    public SecurityService getSecurityService() {
        if (securityService == null) {
            securityService = (SecurityService) SpringUtil.getBean(Constants.SECURITY_SERVICE);
            setSecurityService(securityService);
        }
        return securityService;
    }

    public AuthorizationService getAuthorizationService() {
        if (authorizationService == null) {
            authorizationService = (AuthorizationService) SpringUtil.getBean(Constants.AUTH_SERVICE);
            setAuthorizationService(authorizationService);
        }
        return authorizationService;
    }

    public WorkspaceService getWorkspaceService() {
        if (workspaceService == null) {
            workspaceService = (WorkspaceService) SpringUtil.getBean(Constants.WORKSPACE_SERVICE);
            setWorkspaceService(workspaceService);
        }
        return workspaceService;
    }

    public ConfigBean getConfig() {
        return config;
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
            case XPDL_2_2:
                url = "http://b3mn.org/stencilset/bpmn2.0#";
                break;
            case BPMN_2_0:
                url = "http://b3mn.org/stencilset/bpmn2.0#";
                break;
            case PNML_1_3_2:
                url = "http://b3mn.org/stencilset/petrinet#";
                break;
            case YAWL_2_2:
                url = "http://b3mn.org/stencilset/yawl2.2#";
                break;
            case EPML_2_0:
                url = "http://b3mn.org/stencilset/epc#";
                break;
        }
        return url;
    }


    protected String getImportPath(final String nativeType) {
        String importPath = "";
        switch (nativeType) {
            case XPDL_2_2:
                importPath = "/" + config.getSiteEditor() + "/editor/xpdlimport";
                break;
            case BPMN_2_0:
                importPath = "/" + config.getSiteEditor() + "/editor/bpmnimport";
                break;
            case PNML_1_3_2:
                importPath = "/" + config.getSiteEditor() + "/editor/pnmlimport";
                break;
            case YAWL_2_2:
                importPath = "/" + config.getSiteEditor() + "/editor/yawlimport";
                break;
            case EPML_2_0:
                importPath = "/" + config.getSiteEditor() + "/editor/epmlimport";
                break;
        }
        return importPath;
    }

    protected String getExportPath(final String nativeType) {
        String exportPath = "";
        switch (nativeType) {
            case XPDL_2_2:
                exportPath = "/" + config.getSiteEditor() + "/editor/xpdlexport";
                break;
            case BPMN_2_0:
                exportPath = "/" + config.getSiteEditor() + "/editor/bpmnexport";
                break;
            case PNML_1_3_2:
                exportPath = "/" + config.getSiteEditor() + "/editor/pnmlexport";
                break;
            case YAWL_2_2:
                exportPath = "/" + config.getSiteEditor() + "/editor/yawlexport";
                break;
            case EPML_2_0:
                exportPath = "/" + config.getSiteEditor() + "/editor/epmlexport";
                break;
        }
        return exportPath;
    }

    public void toggleComponentSclass(HtmlBasedComponent comp, boolean state, String stateOff, String stateOn) {
        if (comp == null || stateOn == null || stateOff == null) {
            return;
        }
        String sclass = Objects.requireNonNull(comp.getSclass(), "");
        if (state) {
            sclass = sclass.replaceAll(stateOff, "");
            if (!sclass.contains(stateOn)) {
                sclass = sclass + " " + stateOn;
            }
        } else {
            sclass = sclass.replaceAll(stateOn, "");
            if (!sclass.contains(stateOff)) {
                sclass = sclass + " " + stateOff;
            }
        }
        comp.setSclass(sclass);
    }

    private void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }
    private void setEventLogService(final EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    public String processRequest(Map<String,String[]> parameterMap) {
        return "";
    }

    private void setUserService(final UserService userService) {
        this.userService = userService;
    }

    private void setSecurityService(final SecurityService securityService) {
        this.securityService = securityService;
    }

    private void setAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    private void setWorkspaceService(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

}
