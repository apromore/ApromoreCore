package au.edu.qut.processmining.ui;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 14/06/2016.
 */
public class MinerUI {

    public MinerUIResult showGUI(UIPluginContext context) {

        MinerSettings minerSettings = new MinerSettings();
        TaskListener.InteractionResult guiResult = context.showWizard("Select Optimizer Settings", true, true, minerSettings);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return minerSettings.getSelections();
    }
}
