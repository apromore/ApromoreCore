package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.util.StreamUtil;
import org.zkoss.zk.ui.Executions;

/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SignavioController extends BaseController {

    public static ProcessSummaryType process;
    public static VersionSummaryType version;
    public static String annotation = null;
    public static MainController mainC;
    private static final String JSON_DATA = "jsonData";
    public static String nativeType;

    public SignavioController() {
        super();
        Map<String, String> param = new HashMap<String, String>();
        try {
            DataHandler nativeDH = getService().exportFormat(process.getId(), process.getName(), version.getName(),
                    nativeType, annotation, false, this.mainC.getCurrentUser().getUsername());
            String data = StreamUtil.convertStreamToString(nativeDH.getInputStream());
            this.setTitle(process.getName());
            param.put(JSON_DATA, data.replace("\n", "").trim());
            param.put("url", getURL(nativeType));
            param.put("importPath", getImportPath(nativeType));
            Executions.getCurrent().pushArg(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getURL(String nativeType) {
        String url = "";
        switch (nativeType) {
            case "XPDL 2.1":
                url = "http://b3mn.org/stencilset/bpmn1.1#";
                break;
            case "BPMN 2.1":
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


    private String getImportPath(String nativeType) {
        String importPath = "";
        switch (nativeType) {
            case "XPDL 2.1":
                importPath = "/editor/editor/xpdlimport";
                break;
            case "BPMN 2.1":
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
}
