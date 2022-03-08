package org.apromore.portal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apromore.plugin.portal.PortalSelection;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.zkoss.zhtml.Li;
import org.zkoss.zul.Messagebox;

public class PluginPortalSelection implements PortalSelection {
    private MainController mainController;

    public PluginPortalSelection(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public Map<SummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions() {
        return mainController.getSelectedElementsAndVersions();
    }

    @Override
    public Set<SummaryType> getSelectedProcessModels() {
        return mainController.getSelectedElements();
    }

    @Override
    public List<LogSummaryType> getSelectedEventLogs() {
        return null;
    }

    @Override
    public List<ProcessSummaryType> getSelectedBpmnModels() {
        return null;
    }

    @Override
    public List<SummaryType> getSelectedArtifacts() {
        List<SummaryType> summaryTypes = new ArrayList<>();
        Map<SummaryType, List<VersionSummaryType>> elements = getSelectedProcessModelVersions();
        for (Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            summaryTypes.add(entry.getKey());
        }
        return summaryTypes;
    }
}
