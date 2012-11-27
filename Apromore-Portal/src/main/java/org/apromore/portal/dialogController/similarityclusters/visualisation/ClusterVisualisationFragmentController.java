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

}
