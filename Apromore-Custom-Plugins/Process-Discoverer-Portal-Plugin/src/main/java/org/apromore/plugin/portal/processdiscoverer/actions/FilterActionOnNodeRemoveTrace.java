package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zhtml.Messagebox;

public class FilterActionOnNodeRemoveTrace extends FilterAction {

    public FilterActionOnNodeRemoveTrace(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            if (analyst.filter_RemoveTracesAnyValueOfEventAttribute(this.filterValue, this.filterAttributeKey)) {
                appController.updateUI(false);
                return true;
            }
            else {
                showEmptyLogMessageBox();
            }
        } catch (Exception e) {
            Messagebox.show("Error during filtering. Error message: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        
    }

}
