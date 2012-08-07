package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.util.StreamUtil;
import org.zkoss.zk.ui.Executions;

/**
 * Created by IntelliJ IDEA.
 * User: Sathish
 * Date: 29/06/12
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class SignavioController extends BaseController {
//    private static final String PROCESS_ID = "processId";
//    private static final String PROCESS_NAME = "processName";
//    private static final String VERSION = "version";
//    private static final String ANNOTATION = "annotation";
//    private static final String DOMAIN = "domain";
//    private static final String PRE_VERSION = "preVersion";
//    private static final String RANKING = "ranking";
//    private static final String USERNAME = "username";
    public static ProcessSummaryType process;
    public static VersionSummaryType version;
    public static String annotation = null;
    public static MainController mainC;
    private static final String JSON_DATA = "jsonData";
    public static String nativeType;
    //public static String url;
    //public static String importPath;

    public SignavioController() {
        super();
        //String processId = Executions.getCurrent().getParameter(PROCESS_ID);
        //String ver = Executions.getCurrent().getParameter(VERSION);
        Map<String, String> param = new HashMap<String, String>();
        //ProcessSummaryType proccess = getService().readProcess(processId, versionId);
        try {
            DataHandler nativeDH = getService().exportFormat(process.getId(), process.getName(), version.getName(),
                    nativeType, annotation, false, this.mainC.getCurrentUser().getUsername());
            String data = StreamUtil.convertStreamToString(nativeDH.getInputStream());
            param.put(JSON_DATA, data.replace("\n", "").trim());
            param.put("url", getURL(nativeType));
            param.put("importPath", getImportPath(nativeType));
            Executions.getCurrent().pushArg(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setupConverter() {
    }

    public void readModel() {
    }

    public void saveModel() {
    }

    private String getURL(String nativeType) {
        String url = "";
        if (nativeType.equals("XPDL 2.1")) {
            url = "";
        } else if (nativeType.equals("BPMN 2.1")) {
            url = "http://b3mn.org/stencilset/bpmn2.0#";
        } else if (nativeType.equals("PNML 1.3.2")) {
            url = "";
        } else if (nativeType.equals("YAWL 2.2")) {
            url = "http://b3mn.org/stencilset/yawl2.2#";
        } else if (nativeType.equals("EPML 2.0")) {
            url = "http://b3mn.org/stencilset/epc#";
        }
        return url;
    }


    private String getImportPath(String nativeType) {
        String importPath = "";
        if (nativeType.equals("XPDL 2.1")) {
            importPath = "/Apromore-editor/editor/xpdlimport";
        } else if (nativeType.equals("BPMN 2.1")) {
            importPath = "/Apromore-editor/editor/bpmnimport";
        } else if (nativeType.equals("PNML 1.3.2")) {
            importPath = "/Apromore-editor/editor/pnmlimport";
        } else if (nativeType.equals("YAWL 2.2")) {
            importPath = "/Apromore-editor/editor/yawlimport";
        } else if (nativeType.equals("EPML 2.0")) {
            importPath = "/Apromore-editor/editor/epmlimport";
        }
        return importPath;
    }
}
