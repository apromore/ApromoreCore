/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.StructureBPMNProcessOutputMsgType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

import java.util.Map;
import java.util.List;

/**
 * Created by Adriano on 11/11/2015.
 */
public class StructureProcessController extends BaseController  {

    private static final Logger LOGGER = LoggerFactory.getLogger(StructureProcessController.class);

    public StructureProcessController(final MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) {

        try {
            for (ProcessSummaryType process : processVersions.keySet()) {
                for (VersionSummaryType version : processVersions.get(process)) {
                    int processId = process.getId();
                    String processName = process.getName();
                    String branchName = version.getName();
                    String versionNumber = version.getVersionNumber();

                    int folderId = 0;
                    if( UserSessionManager.getCurrentFolder() != null )
                        folderId = UserSessionManager.getCurrentFolder().getId();

                    String username = UserSessionManager.getCurrentUser().getUsername();
                    List<String> domains = mainC.getDomains();
                    String domain = (new SelectDynamicListController(domains)).getValue();

                    StructureBPMNProcessOutputMsgType result = getService().structureBPMNProcess(processId, processName, branchName, versionNumber, username, folderId, domain);

                    if( result.getResult().getCode() == 0 ) {
                        mainC.displayNewProcess(result.getProcessSummary());
                        Messagebox.show("Structuring result: " + result.getResult().getMessage());
                    } else {
                        Messagebox.show(result.getResult().getMessage() + " - failed to structure process: " + processName, "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while structuring the process.", e);
            Messagebox.show(e.getClass().getName() + " - failed to structure the process.", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
