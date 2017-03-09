package au.edu.qut.processmining.miners.yam.ui.miner;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class YAMUI {

    public YAMUIResult showGUI(UIPluginContext context, String title) {

        YAMSettings YAMSettings = new YAMSettings();
        TaskListener.InteractionResult guiResult = context.showWizard(title, true, true, YAMSettings);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return YAMSettings.getSelections();
    }

}
