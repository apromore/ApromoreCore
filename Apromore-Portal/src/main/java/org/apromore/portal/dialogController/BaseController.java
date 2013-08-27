package org.apromore.portal.dialogController;

import org.apromore.manager.client.ManagerService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Window;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BaseController extends Window {

    public static final String MANAGER_SERVICE = "managerClient";
    private ManagerService managerService;


    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
            setManagerService(managerService);
        }
        return managerService;
    }

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


    private void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

}
