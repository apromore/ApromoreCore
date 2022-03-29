/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.portal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apromore.plugin.portal.PortalSelection;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;

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
    public List<SummaryType> getSelectedArtifacts() {
        List<SummaryType> summaryTypes = new ArrayList<>();
        Map<SummaryType, List<VersionSummaryType>> elements = getSelectedProcessModelVersions();
        for (Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            summaryTypes.add(entry.getKey());
        }
        return summaryTypes;
    }
}
