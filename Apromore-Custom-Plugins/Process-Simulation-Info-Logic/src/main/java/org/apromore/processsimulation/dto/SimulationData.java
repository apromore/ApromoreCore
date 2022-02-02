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
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */

package org.apromore.processsimulation.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class SimulationData {
    private long caseCount;
    private long resourceCount;
    private long startTime;
    private long endTime;

    @Getter(AccessLevel.NONE)
    private Map<String, Double> nodeWeights; // nodeId => mean duration

    @Getter(AccessLevel.NONE)
    private Map<String, List<EdgeFrequency>> edgeFrequencies; // gatewayId => edge frequencies

    public Collection<String> getDiagramNodeIDs() {
        return Collections.unmodifiableSet(nodeWeights.keySet());
    }

    public double getDiagramNodeDuration(@NonNull String nodeId) {
        return nodeWeights.getOrDefault(nodeId, 0d);
    }

    public Map<String, List<EdgeFrequency>> getEdgeFrequencies() {
        return edgeFrequencies != null ? Collections.unmodifiableMap(edgeFrequencies) : null;
    }
}
