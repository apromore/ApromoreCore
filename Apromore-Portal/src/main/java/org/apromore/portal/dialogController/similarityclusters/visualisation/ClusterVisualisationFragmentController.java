package org.apromore.portal.dialogController.similarityclusters.visualisation;

import java.util.HashMap;
import java.util.Map;

import org.apromore.model.GetFragmentOutputMsgType;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.util.StreamUtil;
import org.zkoss.zk.ui.Executions;

/**
 * Controller for Oryx/Signavio Window displaying Fragments
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class ClusterVisualisationFragmentController extends BaseController {

    private static final String JSON_DATA = "jsonData";
    private static final String FRAGMENT_ID_PARAMETER = "fragmentId";

    /**
     * Upon creating the Window the EPML of the Fragment is forwarded to the
     * fragment.zul file.
     */
    public ClusterVisualisationFragmentController() {
        super();

        Map<String, String> param = new HashMap<>();

        try {
            String fragmentId = Executions.getCurrent().getParameter(FRAGMENT_ID_PARAMETER);
            GetFragmentOutputMsgType exportResult = getService().getFragment(Integer.valueOf(fragmentId));
            String data = StreamUtil.convertStreamToString(exportResult.getFragmentResult().getNative().getInputStream());

            param.put(JSON_DATA, data.replace("\n", " ").trim());
            param.put("url", getURL(exportResult.getNativeType()));
            param.put("importPath", getImportPath(exportResult.getNativeType()));
            param.put("doAutoLayout", "true");

            Executions.getCurrent().pushArg(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
