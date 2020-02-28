/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
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
    Map<SummaryType, List<VersionSummaryType>> getSelectedProcessModelVersions();

    /**
     * @return the Set of currently selected process models
     */
    Set<SummaryType> getSelectedProcessModels();

}