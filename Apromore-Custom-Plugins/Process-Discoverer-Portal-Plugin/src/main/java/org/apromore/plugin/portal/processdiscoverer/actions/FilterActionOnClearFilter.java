package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

/**
 * This action handles clearing the filter criteria
 */
public class FilterActionOnClearFilter extends FilterAction {
    public FilterActionOnClearFilter(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            this.setPreviousFilterCriteria(analyst.copyCurrentFilterCriteria());
            analyst.clearFilter();
            appController.updateUI(false);
            return true;
        } catch (Exception e) {
            Messagebox.show("Error in clearing filter. Error message: " + e.getMessage());
            return false;
        }
    }

}
