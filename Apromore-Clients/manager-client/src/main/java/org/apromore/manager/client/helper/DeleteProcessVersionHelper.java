/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.manager.client.helper;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.SummaryType;
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
     * @param elements the list of models
     * @return the object to be sent to the Service
     */
    public static Collection<ProcessVersionIdentifierType> setElements(Map<SummaryType, List<VersionSummaryType>> elements) {
        ProcessVersionIdentifierType processVersionId;
        List<VersionSummaryType> versionSummaries;
        Set<SummaryType> keys = elements.keySet();

        Collection<ProcessVersionIdentifierType> payload = new ArrayList<>();

        for (SummaryType summaryType : keys) {
            if(summaryType instanceof ProcessSummaryType) {
                ProcessSummaryType processSummary = (ProcessSummaryType) summaryType;
                versionSummaries = elements.get(processSummary);

                processVersionId = new ProcessVersionIdentifierType();
                processVersionId.setProcessId(processSummary.getId());
                processVersionId.setProcessName(processSummary.getName());
                for (VersionSummaryType versionSummary : versionSummaries) {
                    processVersionId.setBranchName(versionSummary.getName());
                    processVersionId.setVersionNumber(versionSummary.getVersionNumber());
                }

                payload.add(processVersionId);
            }
        }

        return payload;
    }

}
