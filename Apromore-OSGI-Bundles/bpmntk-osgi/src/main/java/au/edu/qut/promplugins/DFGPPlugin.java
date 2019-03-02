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
import au.edu.qut.processmining.miners.splitminer.dfgp.DirectlyFollowGraphPlus;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUI;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
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
        name = "Generate DFG+",
        parameterLabels = { "Event Log" },
        returnLabels = { "DFG+" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Returns the DFG+ of the input log"
)
public class DFGPPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Generate DFG+", requiredParameterLabels = {0})
    public static BPMNDiagram generateDFGP(UIPluginContext context, XLog log) {
        boolean debug = false;

        DFGPUI gui = new DFGPUI();
        DFGPUIResult result = gui.showGUI(context, "Setup for DFG+");

        SimpleLog sLog = LogParser.getSimpleLog(log, new XEventNameClassifier());
        DirectlyFollowGraphPlus net = new DirectlyFollowGraphPlus(  sLog, result.getPercentileFrequencyThreshold(),
                                                                    result.getParallelismsThreshold(),
                                                                    result.getFilterType(), result.isParallelismsFirst());
        net.buildDFGP();

        if( debug ) {
            net.printNodes();
            net.printParallelisms();
        }

        return net.getDFGP(true);
    }
}