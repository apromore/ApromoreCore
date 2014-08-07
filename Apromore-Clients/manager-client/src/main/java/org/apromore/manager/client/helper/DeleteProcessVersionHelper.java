/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.manager.client.helper;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.VersionSummaryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to help construct the client's message to the WebService.
 */
public class DeleteProcessVersionHelper {

    public static final String GREEDY_ALGORITHM = "Greedy";

    /**
     * Sets up the process models that are to be merged.
     *
     * @param selectedProcessVersions the list of models
     * @return the object to be sent to the Service
     */
    public static Collection<ProcessVersionIdentifierType> setProcessModels(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) {
        ProcessVersionIdentifierType processVersionId;
        List<VersionSummaryType> versionSummaries;
        Set<ProcessSummaryType> keys = selectedProcessVersions.keySet();

        Collection<ProcessVersionIdentifierType> payload = new ArrayList<>();

        for (ProcessSummaryType processSummary : keys) {
            versionSummaries = selectedProcessVersions.get(processSummary);

            processVersionId = new ProcessVersionIdentifierType();
            processVersionId.setProcessId(processSummary.getId());
            processVersionId.setProcessName(processSummary.getName());
            for (VersionSummaryType versionSummary : versionSummaries) {
                processVersionId.setBranchName(versionSummary.getName());
                processVersionId.setVersionNumber(versionSummary.getVersionNumber());
            }

            payload.add(processVersionId);
        }

        return payload;
    }

}
