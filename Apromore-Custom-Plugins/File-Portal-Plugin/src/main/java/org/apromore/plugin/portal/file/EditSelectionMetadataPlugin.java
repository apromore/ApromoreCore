package org.apromore.plugin.portal.file;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.file.impl.EditListMetadataController;
import org.apromore.portal.dialogController.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

public class EditSelectionMetadataPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(EditSelectionMetadataPlugin.class);

    private String label = "Edit metadata";
    private String groupLabel = "File";


    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "meta-edit.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();

            mainC.eraseMessage();
            Map<SummaryType, List<VersionSummaryType>> selectedElements = mainC.getSelectedElementsAndVersions();

            if (selectedElements.size() != 0) {
                new EditListMetadataController(mainC, selectedElements);

            } else {
                mainC.displayMessage("No process versions or event logs selected.");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit selection metadata", e);
            Messagebox.show("Unable to edit selection metadata");
        }
    }
}
