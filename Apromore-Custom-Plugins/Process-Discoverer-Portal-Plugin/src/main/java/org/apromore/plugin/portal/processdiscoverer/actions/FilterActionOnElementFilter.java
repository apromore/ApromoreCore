package org.apromore.plugin.portal.processdiscoverer.actions;

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zhtml.Messagebox;

/**
 * This action is used for filtering on one single element (one at a time).
 * It can be categorized into actions implemented by subclasses: retain or remove trace or events
 */
public abstract class FilterActionOnElementFilter extends FilterAction {
    protected String elementValue;
    protected String attributeKey;
    
    public FilterActionOnElementFilter(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            setPreviousFilterCriteria(analyst.copyCurrentFilterCriteria());
            if (performFiltering(this.elementValue, this.attributeKey)) {
                appController.updateUI(false);
                return true;
            }
            else {
                Messagebox.show("The log is empty after applying all filter criteria! Please use different criteria.",
                        "Process Discoverer",
                        Messagebox.OK,
                        Messagebox.INFORMATION);
                return false;
            }
        } catch (Exception e) {
            Messagebox.show("Error in filtering. Error message: " + e.getMessage());
            return false;
        }
    }
    
    public void setExecutionParams(String elementValue, String attributeKey) {
        this.elementValue = elementValue;
        this.attributeKey = attributeKey;
    }
    
    public abstract boolean performFiltering(String elementValue, String attributeKey) throws Exception;
}
