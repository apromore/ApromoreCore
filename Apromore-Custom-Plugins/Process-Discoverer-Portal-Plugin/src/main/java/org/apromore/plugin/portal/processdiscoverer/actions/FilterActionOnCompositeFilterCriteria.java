package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

/*
 * This filter action is used with the LogFilter window.
 * It is a special action because the filtering is actually executed inside the window (OK button) instead of by this action.
 * Therefore, this action's execute() only does some data preparation to support undo and redo.
 */
public class FilterActionOnCompositeFilterCriteria extends FilterAction {
    public FilterActionOnCompositeFilterCriteria(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            analyst.filter(actionFilterCriteria);
            appController.updateUI(false);
            return true;
        } catch (Exception e) {
            Messagebox.show("Error in filtering. Error message: " + e.getMessage());
            return false;
        }
    }

}
