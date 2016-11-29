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
