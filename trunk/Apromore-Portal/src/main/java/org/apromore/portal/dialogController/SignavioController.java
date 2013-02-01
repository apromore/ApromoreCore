package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.util.StreamUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;


/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SignavioController extends BaseController {

    private static final String JSON_DATA = "jsonData";
    public static EditSessionType editSession;
    public static MainController mainC;

    private static final Logger logger = Logger.getLogger(SignavioController.class.getCanonicalName());

    public SignavioController() {
        super();

        this.addEventListener("onSave", new EventListener() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                event.getData();
            }
        });

        this.addEventListener("onSaveAs", new EventListener() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                event.getData();
            }
        });

        Map<String, String> param = new HashMap<String, String>();
        try {
            ExportFormatResultType exportResult =
                    getService().exportFormat(editSession.getProcessId(),
                            editSession.getProcessName(),
                            editSession.getOriginalBranchName(),
                            editSession.getVersionNumber(),
                            editSession.getNativeType(),
                            editSession.getAnnotation(),
                            editSession.isWithAnnotation(),
                            editSession.getUsername(), new HashSet<RequestParameterType<?>>());
            String data = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());

            this.mainC.showPluginMessages(exportResult.getMessage());
            this.setTitle(editSession.getProcessName());
            param.put(JSON_DATA, data.replace("\n", "").trim());
            param.put("url", getURL(editSession.getNativeType()));
            param.put("importPath", getImportPath(editSession.getNativeType()));
			param.put("doAutoLayout", editSession.isWithAnnotation());
            Executions.getCurrent().pushArg(param);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    private String getURL(final String nativeType) {
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


    private String getImportPath(final String nativeType) {
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

}
