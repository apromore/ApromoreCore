package org.apromore.portal.custom.gui.plugin;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.ProcessSummaryRowValue;
import org.apromore.portal.custom.gui.tab.impl.TabItem;

import java.util.HashSet;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/05/2016.
 */
public class ProcessTabItemExecutor implements TabItemExecutor {

    private MainControllerInterface mainControllerInterface;

    public ProcessTabItemExecutor(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void execute(TabItem listItem) {
        ProcessSummaryType pst = createProcessSummaryType((ProcessSummaryRowValue) listItem.getTabRowValue());
        VersionSummaryType vst = createVersionSummaryType((ProcessSummaryRowValue) listItem.getTabRowValue());
        try {
            mainControllerInterface.editProcess(pst, vst, pst.getOriginalNativeType(), null, "false", new HashSet<RequestParameterType<?>>());
        } catch (InterruptedException e) {
            System.out.println(pst);
            System.out.println(vst);
            e.printStackTrace();
        }
    }

    protected ProcessSummaryType createProcessSummaryType(ProcessSummaryRowValue rowValue) {
        return rowValue.getProcessSummaryType();
    }

    protected VersionSummaryType createVersionSummaryType(ProcessSummaryRowValue rowValue) {
        return rowValue.getVersionSummaryType();
    }
}
