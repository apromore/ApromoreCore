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

import au.edu.qut.bpmn.helper.DiagramHandler;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 15/06/2016.
 */
@Plugin(
        name = "Remove Parallel Flows in a BPMN Diagram",
        parameterLabels = { "BPMNDiagram" },
        returnLabels = { "Simplified Diagram" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Remove empty parallel flows in a BPMN model"
)
public class RemoveParallelFlowPlugin {


    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    @PluginVariant(
            variantLabel = "Remove Parallel Flows",
            requiredParameterLabels = {0})
    public static BPMNDiagram removeParallelFlows(PluginContext context, BPMNDiagram diagram) {
        DiagramHandler diagramHandler = new DiagramHandler();

        BPMNDiagram result = diagramHandler.copyDiagram(diagram);
        diagramHandler.removeEmptyParallelFlows(result);
        return result;
    }
}
