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
