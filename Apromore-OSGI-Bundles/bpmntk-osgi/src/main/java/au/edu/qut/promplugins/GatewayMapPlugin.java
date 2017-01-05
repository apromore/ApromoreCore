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
        name = "Get Gateway Map of a BPMN Diagram",
        parameterLabels = { "BPMN Diagram" },
        returnLabels = { "Gateway Map" },
        returnTypes = { BPMNDiagram.class },
        userAccessible = true,
        help = "Display a BPMN as only its gateways"
)
public class GatewayMapPlugin {

    @UITopiaVariant(
            affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee"
    )
    @PluginVariant(variantLabel = "Get Gateway Map of a BPMN Diagram", requiredParameterLabels = {0})
    public static BPMNDiagram GetGatewayMap(PluginContext context, BPMNDiagram input) {
        BPMNDiagram copy;
        BPMNDiagram output;

        copy = (new DiagramHandler()).copyDiagram(input);

        GatewayMap gatemap = new GatewayMap();
        gatemap.generateMap(copy);
        gatemap.detectIORs();

        output = gatemap.getGatewayMap();

        return output;
    }
}
