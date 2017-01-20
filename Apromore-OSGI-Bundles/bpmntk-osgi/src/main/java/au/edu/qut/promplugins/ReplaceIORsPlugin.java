package au.edu.qut.promplugins;

import au.edu.qut.bpmn.helper.DiagramHandler;
import au.edu.qut.bpmn.helper.GatewayMap;
import au.edu.qut.processmining.miners.heuristic.ui.HMPlusUI;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

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
        GatewayMap gatemap = new GatewayMap();
        gatemap.generateMap(output);
        gatemap.detectAndReplaceIORs();

        return output;
    }
}