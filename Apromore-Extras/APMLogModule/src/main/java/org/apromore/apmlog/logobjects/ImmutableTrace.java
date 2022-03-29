/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImmutableTrace extends AbstractTraceImpl implements Serializable {

    public ImmutableTrace(int immutableIndex,
                          String caseId,
                          List<ImmutableEvent> immutableEvents,
                          List<ActivityInstance> activityInstances,
                          UnifiedMap<String, String> attributes,
                          APMLog sourceLog) {
        super(immutableIndex, caseId, immutableEvents, activityInstances, attributes, sourceLog);
    }

    // ===============================================================================================================
    // Clone methods
    // ===============================================================================================================

    @Override
    public ATrace deepClone() {
        List<ActivityInstance> activityInstanceClone = activityInstances.stream()
                .map(ActivityInstance::clone)
                .collect(Collectors.toList());

        return new ImmutableTrace(immutableIndex, caseId,
                new ArrayList<>(immutableEvents),
                activityInstanceClone,
                new UnifiedMap<>(attributes),
                sourceLog);
    }
}
