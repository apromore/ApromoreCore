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
