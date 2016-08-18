package au.edu.qut.promplugins;

import au.edu.qut.processmining.miner.FakeMiner;
import au.edu.qut.processmining.ui.MinerUI;
import au.edu.qut.processmining.ui.MinerUIResult;
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
        MinerUI gui = new MinerUI();
        MinerUIResult result = gui.showGUI(context);

        System.out.println("Optimizer - [settings] Inclusive Choice: " + result.isInclusiveChoice());
        System.out.println("Optimizer - [settings] Optional Tasks: " + result.isOptionalTasks());
        System.out.println("Optimizer - [settings] Recurrent Tasks: " + result.isRecurrentTasks());
        System.out.println("Optimizer - [settings] Unbalanced Paths: " + result.isUnbalancedPaths());
        System.out.println("Optimizer - [settings] Apply Cleaning: " + result.isApplyCleaning());

        FakeMiner fakeMiner = new FakeMiner();
        BPMNDiagram optimizedDiagram = fakeMiner.optimize(diagram, log, result.isUnbalancedPaths(),
                                                                        result.isOptionalTasks(),
                                                                        result.isRecurrentTasks(),
                                                                        result.isInclusiveChoice(),
                                                                        result.isApplyCleaning() );

        return optimizedDiagram;
    }
}
