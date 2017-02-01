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

import au.edu.qut.processmining.repairing.Optimizer;
import au.edu.qut.processmining.repairing.ui.OptimizerUI;
import au.edu.qut.processmining.repairing.ui.OptimizerUIResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 15/06/2016.
 */
@Plugin(
        name = "Optimize BPMN Diagram",
        parameterLabels = { "BPMNDiagram", "Event Log" },
        returnLabels = { "Optimized Diagram" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Optimize a BPMNDiagram replaying traces of the event log used for its discovery."
)
public class OptimizerPlugin {

    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    @PluginVariant(variantLabel = "Optimize BPMNDiagram", requiredParameterLabels = {0, 1})
    public static BPMNDiagram optimizeDiagram(UIPluginContext context, BPMNDiagram diagram, XLog log) {
        OptimizerUI gui = new OptimizerUI();
        OptimizerUIResult result = gui.showGUI(context);

        System.out.println("Optimizer - [settings] Inclusive Choice: " + result.isInclusiveChoice());
        System.out.println("Optimizer - [settings] Optional Tasks: " + result.isOptionalActivities());
        System.out.println("Optimizer - [settings] Recurrent Tasks: " + result.isRecurrentActivities());
        System.out.println("Optimizer - [settings] Unbalanced Paths: " + result.isUnbalancedPaths());
        System.out.println("Optimizer - [settings] Apply Cleaning: " + result.isApplyCleaning());

        Optimizer optimizer = new Optimizer();
        BPMNDiagram optimizedDiagram = optimizer.optimize(diagram, log, result.isUnbalancedPaths(),
                                                                        result.isOptionalActivities(),
                                                                        result.isRecurrentActivities(),
                                                                        result.isInclusiveChoice(),
                                                                        result.isApplyCleaning() );

        return optimizedDiagram;
    }
}
