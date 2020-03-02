/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
