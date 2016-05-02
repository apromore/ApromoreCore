package org.apromore.portal.custom.gui.plugin;

import org.apromore.model.AnnotationsType;
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
            e.printStackTrace();
        }
    }

    protected ProcessSummaryType createProcessSummaryType(ProcessSummaryRowValue rowValue) {
        ProcessSummaryType processSummaryType = new ProcessSummaryType();

        int pos = 0;
        if(rowValue.size() > 7) {
            pos++;
        }
        processSummaryType.setName((String) rowValue.get(pos++));
        processSummaryType.setId((Integer) rowValue.get(pos++));
        processSummaryType.setOriginalNativeType((String) rowValue.get(pos++));
        processSummaryType.setDomain((String) rowValue.get(pos++));
        processSummaryType.setRanking((String) rowValue.get(pos++));
        processSummaryType.setLastVersion((String) rowValue.get(pos++));
        processSummaryType.setOwner((String) rowValue.get(pos++));

        return processSummaryType;
    }

    protected VersionSummaryType createVersionSummaryType(ProcessSummaryRowValue rowValue) {
        VersionSummaryType processSummaryType = new VersionSummaryType();

        int pos = 0;
        if(rowValue.size() > 7) {
            pos++;
        }
        processSummaryType.setName((String) rowValue.get(pos++));
        pos += 4;
        processSummaryType.setVersionNumber((String) rowValue.get(pos));

        return processSummaryType;
    }
}
