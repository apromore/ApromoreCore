/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;

/**
 * Provides access to the current selection in the portal
 */
public interface PortalSelection {

    /**
     * @return a Map of the selected processes and respective versions
     */
    Map<SummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions();

    /**
     * @return the Set of currently selected process models
     */
    Set<SummaryType> getSelectedProcessModels();

    /**
     * @return the Set of currently selected event logs
     */
    List<LogSummaryType> getSelectedEventLogs();

    /**
     * @return the Set of currently selected process models
     */
    List<ProcessSummaryType> getSelectedBpmnModels();

    /**
     * @return the Set of currently selected process models
     */
    List<SummaryType> getSelectedArtifacts();

}
