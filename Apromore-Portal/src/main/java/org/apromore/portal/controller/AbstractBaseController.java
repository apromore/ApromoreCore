package org.apromore.portal.controller;

import org.apromore.manager.client.ManagerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public abstract class AbstractBaseController extends SelectorComposer<Component> {

    public static final String MANAGER_SERVICE = "managerClient";

    private ManagerService managerService;


    /**
     * Returns the Client Spring bean pinting to the Manager Webservices.
     * @return the manager client Webservice spring managed bean or null if service not available.
     */
    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
            setManagerService(managerService);
        }
        return managerService;
    }

    /**
     * Returns the Currently Logged in User or null.
     * @return the authentication object or null if not authenticated.
     */
    protected Authentication getAuthenticatedUser() {
        Authentication auth = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            auth = (Authentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return auth;
    }

    /**
     * The path used for the Editor to invoke the correct layout tools.
     * @param nativeType the type of the model we want to visualise.
     * @return the namespace url or an empty string if the type is unknown.
     */
    protected String getURL(final String nativeType) {
        String url = "";
        switch (nativeType) {
            case "XPDL 2.1":
                url = "http://b3mn.org/stencilset/bpmn1.1#";
                break;
            case "BPMN 2.0":
                url = "http://b3mn.org/stencilset/bpmn2.0#";
                break;
            case "PNML 1.3.2":
                url = "";
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

    /**
     * The path the editor used to import models for different model languages.
     * @param nativeType the format of the model we want to visualise.
     * @return the import path to the editor servlet or and empty string if the type isn't known.
     */
    protected String getImportPath(final String nativeType) {
        String importPath = "";
        switch (nativeType) {
            case "XPDL 2.1":
                importPath = "/editor/editor/xpdlimport";
                break;
            case "BPMN 2.0":
                importPath = "/editor/editor/bpmnimport";
                break;
            case "PNML 1.3.2":
                importPath = "/editor/editor/pnmlimport";
                break;
            case "YAWL 2.2":
                importPath = "/editor/editor/yawlimport";
                break;
            case "EPML 2.0":
                importPath = "/editor/editor/epmlimport";
                break;
        }
        return importPath;
    }

    /**
     * The path the editor used to export models for different model languages.
     * @param nativeType the format of the model we want to retrieve from the editor.
     * @return the export path to the editor servlet or and empty string if the type isn't known.
     */
    protected String getExportPath(final String nativeType) {
        String exportPath = "";
        switch (nativeType) {
            case "XPDL 2.1":
                exportPath = "/editor/editor/xpdlexport";
                break;
            case "BPMN 2.0":
                exportPath = "/editor/editor/bpmnexport";
                break;
            case "YAWL 2.2":
                exportPath = "/editor/editor/yawlexport";
                break;
            case "EPML 2.0":
                exportPath = "/editor/editor/epmlexport";
                break;
        }
        return exportPath;
    }



    /* Spring method used to populate the manager service. */
    private void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

}
