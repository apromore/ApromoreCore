/**
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.plugin.portal.processdiscoverer.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Builder
@Getter
public class SimulationData {
    private long caseCount;
    private long resourceCount;
    private long startTime;
    private long endTime;

    @Getter(AccessLevel.NONE)
    private Map<String, Double> nodeWeights; // nodeId => mean duration

    public Collection<String> getDiagramNodeIDs() {
        return Collections.unmodifiableSet(nodeWeights.keySet());
    }

    public double getDiagramNodeDuration(@NonNull String nodeId) {
        return nodeWeights.getOrDefault(nodeId, 0d);
    }
}
