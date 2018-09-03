package au.edu.qut.processmining.miners.splitminer.ui.miner;

import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUI;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class SplitMinerUI {

    public SplitMinerUIResult showGUI(UIPluginContext context, String title) {

        DFGPUIResult dfgpUIResult = (new DFGPUI()).showGUI(context, "");

        SplitMinerSettings SplitMinerSettings = new SplitMinerSettings();
        TaskListener.InteractionResult GUI = context.showWizard(title, true, true, SplitMinerSettings);

        if( GUI == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        SplitMinerUIResult smUIResult = SplitMinerSettings.getSelections();

//      setup params for DFG+
        smUIResult.setFilterType(dfgpUIResult.getFilterType());
        smUIResult.setParallelismsThreshold(dfgpUIResult.getParallelismsThreshold());
        smUIResult.setPercentileFrequencyThreshold(dfgpUIResult.getPercentileFrequencyThreshold());
        smUIResult.setParallelismsFirst(dfgpUIResult.isParallelismsFirst());

        return smUIResult;
    }

}
