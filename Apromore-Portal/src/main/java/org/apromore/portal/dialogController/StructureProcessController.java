package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.StructureBPMNProcessOutputMsgType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Adriano on 11/11/2015.
 */
public class StructureProcessController extends BaseController  {

    private static final Logger LOGGER = LoggerFactory.getLogger(StructureProcessController.class);

    public StructureProcessController(final MainController mainC, HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions) {

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
                        Messagebox.show("Process: " + processName + " structured successful!");
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
