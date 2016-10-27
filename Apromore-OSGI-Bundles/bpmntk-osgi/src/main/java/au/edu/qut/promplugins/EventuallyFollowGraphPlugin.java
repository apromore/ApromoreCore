package au.edu.qut.promplugins;

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
        FuzzyNet graph = LogParser.getFuzzyNet(log);
        return graph.getEventuallyFollowGraph();
    }
}
