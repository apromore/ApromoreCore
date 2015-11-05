package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.model.Detail;
import org.deckfour.xes.model.XLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by corno on 2/07/2014.
 */
public interface BPMNMinerService {

    String discoverBPMNModel(XLog log, boolean sortLog, boolean structProcess, int miningAlgorithm, int dependencyAlgorithm, double interruptingEventTolerance, double timerEventPercentage,
                             double timerEventTolerance, double multiInstancePercentage, double multiInstanceTolerance,
                             double noiseThreshold, List<String> listCandidates, Map<Set<String>, Set<String>> primaryKeySelections);

}
