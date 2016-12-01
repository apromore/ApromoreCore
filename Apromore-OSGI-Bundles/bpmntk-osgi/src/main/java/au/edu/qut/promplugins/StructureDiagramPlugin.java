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
