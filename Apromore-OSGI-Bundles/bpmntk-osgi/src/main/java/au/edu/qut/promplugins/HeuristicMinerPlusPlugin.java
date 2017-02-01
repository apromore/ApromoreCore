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

import au.edu.qut.processmining.miners.heuristic.net.HeuristicNet;
import au.edu.qut.processmining.miners.heuristic.HeuristicMinerPlus;
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
        name = "Mine BPMN model with HM+",
        parameterLabels = { "Event Log" },
        returnLabels = { "HM+ output BPMN model" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Returns a BPMN model mined with Heuristic Miner Plus"
)
public class HeuristicMinerPlusPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Mine BPMN model with HM+", requiredParameterLabels = {0})
    public static BPMNDiagram mineBPMNModelWithHMP(UIPluginContext context, XLog log) {
        boolean debug = false;
        BPMNDiagram output;

        HMPlusUI gui = new HMPlusUI();
        HMPlusUIResult result = gui.showGUI(context, "Setup HM+");

        HeuristicMinerPlus hmp = new HeuristicMinerPlus();
        hmp.mineBPMNModel( log, result.getDependencyThreshold(), result.getPositiveObservations(),
                                result.getRelative2BestThreshold(), result.isReplaceIORs(),
                                result.getStructuringTime());

        HeuristicNet heuristicNet = hmp.getHeuristicNet();

        if( debug ) {
            heuristicNet.printFrequencies();
            heuristicNet.printParallelisms();
        }

        output = hmp.getBPMNDiagram();

        return output;
    }
}
