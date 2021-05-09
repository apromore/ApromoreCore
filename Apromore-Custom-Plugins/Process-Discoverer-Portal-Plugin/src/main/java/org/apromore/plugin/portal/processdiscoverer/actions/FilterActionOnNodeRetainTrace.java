package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zhtml.Messagebox;

public class FilterActionOnNodeRetainTrace extends FilterAction {
    
    public FilterActionOnNodeRetainTrace(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            if (analyst.filter_RetainTracesAnyValueOfEventAttribute(this.filterValue, this.filterAttributeKey)) {
                appController.updateUI(false);
                this.filterCriteria = copyCurrentFilterCriteria();
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
