/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import au.edu.qut.processmining.miners.heuristic.ui.HMPlusUI;
import au.edu.qut.processmining.miners.heuristic.ui.HMPlusUIResult;
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
        name = "Mine Heuristic Net from a Log",
        parameterLabels = { "Event Log" },
        returnLabels = { "Heuristic Net" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Returns the Heuristic net mined from the input log"
)
public class HeuristicNetPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Mine Heuristic Net from a Log", requiredParameterLabels = {0})
    public static BPMNDiagram mineHeuristicNet(UIPluginContext context, XLog log) {
        boolean debug = true;

        HMPlusUI gui = new HMPlusUI();
        HMPlusUIResult result = gui.showGUI(context, "Setup");

        SimpleLog sLog = LogParser.getSimpleLog(log);
        HeuristicNet net = new HeuristicNet(sLog, result.getDependencyThreshold(), result.getPositiveObservations(), result.getRelative2BestThreshold());
        net.generateHeuristicNet();

        if( debug ) {
            net.printFrequencies();
            net.printParallelisms();
        }

        return net.getHeuristicDiagram(true);
    }
}