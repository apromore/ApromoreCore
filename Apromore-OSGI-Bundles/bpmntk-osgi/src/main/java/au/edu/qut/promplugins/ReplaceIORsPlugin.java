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

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.bpmn.helper.GatewayMap;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.HashSet;

/**
 * Created by Adriano on 29/11/2016.
 */

@Plugin(
        name = "Replace IOR joins in a BPMN Diagram",
        parameterLabels = { "BPMN Diagram" },
        returnLabels = { "BPMN Diagram without IOR joins" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Remove the IOR joins in a BPMN diagram and replace them accordingly"
)
public class ReplaceIORsPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Replace IOR joins in a BPMN Diagram", requiredParameterLabels = {0})
    public static BPMNDiagram ReplaceIORs(PluginContext context, BPMNDiagram input) {
        BPMNDiagram output;

        output = (new DiagramHandler()).copyDiagram(input);
        GatewayMap gatemap = new GatewayMap(true);
        gatemap.generateMap(output);
        gatemap.detectAndReplaceIORs();

        return output;
    }
}