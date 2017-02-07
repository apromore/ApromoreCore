package au.edu.qut.processmining.miners.heuristic.ui.net;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 23/01/2017.
 */
public class HNMUI {

    public HNMUIResult showGUI(UIPluginContext context, String title) {

        HNMSettings hnmSettings = new HNMSettings();
        TaskListener.InteractionResult guiResult = context.showWizard(title, true, true, hnmSettings);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return hnmSettings.getSelections();
    }
}
