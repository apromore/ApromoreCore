package au.edu.qut.bpmn.structuring.ui;

import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;

import java.util.concurrent.CancellationException;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructUI {

    public iBPStructUIResult showGUI(UIPluginContext context) {

        iBPStructSettings ibpsParam = new iBPStructSettings();
        TaskListener.InteractionResult guiResult = context.showWizard("Select iBPStruct strategy", true, true, ibpsParam);

        if( guiResult == TaskListener.InteractionResult.CANCEL ) {
            context.getFutureResult(0).cancel(true);
            throw new CancellationException("The wizard has been cancelled.");
        }

        return ibpsParam.getSelections();
    }

}
