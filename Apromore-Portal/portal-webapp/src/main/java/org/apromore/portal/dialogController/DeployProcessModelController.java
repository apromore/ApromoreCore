package org.apromore.portal.dialogController;

import java.util.List;
import java.util.Map.Entry;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

public class DeployProcessModelController extends BaseController {

    private static final long serialVersionUID = 7136271898811065214L;

    private final MainController mainC;
    private final Window deployProcessW;

    public DeployProcessModelController(final MainController mainController, final MenuController menuController, final Entry<ProcessSummaryType, List<VersionSummaryType>> process) {
        this.mainC = mainController;
        this.deployProcessW = (Window) Executions.createComponents("macros/deployProcess.zul", null, null);

        try {
            this.deployProcessW.doModal();
        } catch (SuspendNotAllowedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
