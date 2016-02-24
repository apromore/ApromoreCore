package org.apromore.plugin.portal;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides access to the current selection in the portal
 */
public interface PortalSelection {

    /**
     * @return a Map of the selected processes and respective versions
     */
    Map<ProcessSummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions();

    /**
     * @return the Set of currently selected process models
     */
    Set<ProcessSummaryType> getSelectedProcessModels();

}