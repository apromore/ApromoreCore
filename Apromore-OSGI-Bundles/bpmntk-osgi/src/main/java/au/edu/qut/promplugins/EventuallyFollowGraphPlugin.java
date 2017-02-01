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

import au.edu.qut.processmining.log.LogAnalizer;
import au.edu.qut.processmining.log.LogParser;
import au.edu.qut.processmining.log.graph.fuzzy.FuzzyNet;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 17/06/2016.
 */
@Plugin(
        name = "Discover Eventually Follow Graph from Log",
        parameterLabels = { "Event Log" },
        returnLabels = { "Fuzzy Net" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Returns the eventually follow graph of a log"
)
public class EventuallyFollowGraphPlugin {
    @UITopiaVariant(
            affiliation = "Queensland University of Technology",
            author = "Adriano Augusto",
            email = "a.augusto@qut.edu.au"
    )
    @PluginVariant(variantLabel = "Discover Eventually Follow Graph from Log", requiredParameterLabels = {0})
    public static BPMNDiagram getFuzzyNetFromLog(UIPluginContext context, XLog log) {
        LogAnalizer analizer = new LogAnalizer(log);
        analizer.runAnalysis();
        return null; //TODO
    }
}
