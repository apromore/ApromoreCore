package org.apromore.portal.dialogController.similarityclusters.visualisation;

import java.util.HashMap;
import java.util.Map;

import org.apromore.model.FragmentType;
import org.apromore.portal.dialogController.BaseController;
import org.zkoss.zk.ui.Executions;

/**
 * Controller for Oryx/Signavio Window displaying Fragments
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class ClusterVisualisationFragmentController extends BaseController {

    private static final String FRAGMENT_DATA_PARAMETER = "fragmentData";
    private static final String FRAGMENT_ID_PARAMETER = "fragmentId";
    private static final long serialVersionUID = 2807381469271308301L;

    /**
     * Upon creating the Window the EPML of the Fragment is forwarded to the
     * fragment.zul file.
     */
    public ClusterVisualisationFragmentController() {
        super();

        String fragmentId = Executions.getCurrent().getParameter(FRAGMENT_ID_PARAMETER);
        Map<String, String> param = new HashMap<String, String>();
        param.put(FRAGMENT_DATA_PARAMETER, getFragmentData(fragmentId));
        param.put(FRAGMENT_ID_PARAMETER, fragmentId);
        param.put("url", getURL("EPML 2.0"));
        param.put("importPath", getImportPath("EPML 2.0"));
        Executions.getCurrent().pushArg(param);
    }

    /**
     * Retrieve the EPML for a fragment from the backend.
     *
     * @param fragmentId of fragment
     * @return EPML
     */
    private String getFragmentData(final String fragmentId) {
        if (fragmentId != null) {
            FragmentType fragment = getService().getFragment(Integer.valueOf(fragmentId));

            if (fragment != null) {
                return fragment.getContent();
            } else {
                return "";
            }

        } else {
            return "";
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
