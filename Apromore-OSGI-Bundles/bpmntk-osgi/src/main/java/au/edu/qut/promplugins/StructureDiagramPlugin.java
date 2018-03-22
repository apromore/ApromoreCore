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

import au.edu.qut.bpmn.metrics.ComplexityCalculator;
import au.edu.qut.bpmn.structuring.StructuringService;
import au.edu.qut.bpmn.structuring.ui.iBPStructUI;
import au.edu.qut.bpmn.structuring.ui.iBPStructUIResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 15/06/2016.
 */

@Plugin(
        name = "Structure BPMN Diagram",
        parameterLabels = { "BPMNDiagram" },
        returnLabels = { "Structured Diagram" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Structure a BPMN Diagram and possibly repair its soundness"
)
public class StructureDiagramPlugin {

    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    @PluginVariant(variantLabel = "Structure BPMNDiagram", requiredParameterLabels = {0})
    public static BPMNDiagram structureDiagram(UIPluginContext context, BPMNDiagram diagram) {
        BPMNDiagram structuredDiagram;
        iBPStructUI gui = new iBPStructUI();
        iBPStructUIResult result = gui.showGUI(context);
        StructuringService ss = new StructuringService();

        try {
            structuredDiagram = ss.structureDiagram(diagram,
                    result.getPolicy().toString(),
                    result.getMaxDepth(),
                    result.getMaxSol(),
                    result.getMaxChildren(),
                    result.getMaxStates(),
                    result.getMaxMinutes(),
                    result.isTimeBounded(),
                    result.isKeepBisimulation(),
                    result.isForceStructuring());
        } catch(Exception e) {
            context.log(e);
            System.err.print(e);
            return diagram;
        }

        ComplexityCalculator cc = new ComplexityCalculator(structuredDiagram);
        System.out.println("COMPLEXITY - SIZE: " + cc.computeSize());
        System.out.println("COMPLEXITY - CFC: " + cc.computeCFC());
        System.out.println("COMPLEXITY - STRUCTUREDNESS: " + cc.computeStructuredness());
        System.out.println("COMPLEXITY - DUPLICATES: " + cc.computeDuplicates());

        return structuredDiagram;
    }
}
