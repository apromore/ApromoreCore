package au.edu.qut.processmining.miners.heuristic.ui.miner;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class HMPlusUI {

    public HMPlusUIResult showGUI(UIPluginContext context, String title) {

        HMPlusSettings hmPlusSettings = new HMPlusSettings();
        TaskListener.InteractionResult guiResult = context.showWizard(title, true, true, hmPlusSettings);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return hmPlusSettings.getSelections();
    }

}
