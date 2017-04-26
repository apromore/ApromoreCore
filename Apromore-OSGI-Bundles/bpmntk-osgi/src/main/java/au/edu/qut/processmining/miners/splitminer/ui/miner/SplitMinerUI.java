package au.edu.qut.processmining.miners.splitminer.ui.miner;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class SplitMinerUI {

    public SplitMinerUIResult showGUI(UIPluginContext context, String title) {

        SplitMinerSettings SplitMinerSettings = new SplitMinerSettings();
        TaskListener.InteractionResult guiResult = context.showWizard(title, true, true, SplitMinerSettings);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return SplitMinerSettings.getSelections();
    }

}
