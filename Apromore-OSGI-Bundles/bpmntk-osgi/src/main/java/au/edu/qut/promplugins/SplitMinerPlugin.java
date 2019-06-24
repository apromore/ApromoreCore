/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package au.edu.qut.promplugins;

import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.SimpleLog;
import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUI;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 25/10/2016.
 */

@Plugin(
        name = "Discover BPMN model with Split Miner",
        parameterLabels = { "Event Log" },
        returnLabels = { "SplitMiner output" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Returns a BPMN model mined with Split Miner"
)
public class SplitMinerPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Discover BPMN model with Split Miner", requiredParameterLabels = {0})
    public static BPMNDiagram discoverBPMNModelWithSplitMiner(UIPluginContext context, XLog log) {
        boolean debug = false;
        BPMNDiagram output;

        SplitMinerUI gui = new SplitMinerUI();
        SplitMinerUIResult result = gui.showGUI(context, "Setup HM+");

        SplitMiner sm = new SplitMiner();
//        output = sm.mineBPMNModel(log, new XEventNameClassifier(), result.getPercentileFrequencyThreshold(), result.getParallelismsThreshold(),
//                                        result.getFilterType(), result.isParallelismsFirst(),
//                                        result.isReplaceIORs(), result.isRemoveLoopActivities(), result.getStructuringTime());
        SimpleLog simpleLog = LogParser.getSimpleLog(log, new XEventNameClassifier());
        output = sm.mineBPMNModel(simpleLog, new XEventNameClassifier(), result.getPercentileFrequencyThreshold(), result.getParallelismsThreshold(),
                                        result.getFilterType(), result.isParallelismsFirst(),
                                        result.isReplaceIORs(), result.isRemoveLoopActivities(), result.getStructuringTime());

        return output;
    }
    
    @PluginVariant(variantLabel = "Discover BPMN model with Split Miner via SimpleLog", requiredParameterLabels = {0})
    public static BPMNDiagram discoverBPMNModelWithSplitMiner2(UIPluginContext context, XLog log) {
        boolean debug = false;
        BPMNDiagram output;

        SplitMinerUI gui = new SplitMinerUI();
        SplitMinerUIResult result = gui.showGUI(context, "Setup HM+");

        SplitMiner sm = new SplitMiner();
        SimpleLog simpleLog = LogParser.getSimpleLog(log, new XEventNameClassifier());
        output = sm.mineBPMNModel(simpleLog, new XEventNameClassifier(), result.getPercentileFrequencyThreshold(), result.getParallelismsThreshold(),
                                        result.getFilterType(), result.isParallelismsFirst(),
                                        result.isReplaceIORs(), result.isRemoveLoopActivities(), result.getStructuringTime());

        return output;
    }
}
