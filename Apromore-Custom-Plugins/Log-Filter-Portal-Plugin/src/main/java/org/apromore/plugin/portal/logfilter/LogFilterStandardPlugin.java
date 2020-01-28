package org.apromore.plugin.portal.logfilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.logfilter.LogFilterService;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.logfilter.generic.GenericLogFilterPlugin;
import org.apromore.plugin.portal.logfilter.generic.LogFilterContext;
import org.apromore.plugin.portal.logfilter.generic.LogFilterInputParams;
import org.apromore.plugin.portal.logfilter.generic.LogFilterOutputResult;
import org.apromore.plugin.portal.logfilter.generic.LogFilterResultListener;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

/**
 * This plugin allows to filter log from the portal menu or 
 * other plugins to show log filter window to filter the log used in those plugins
 * This plugin provides the UI and uses the log-filter-logic plugin to do the actual
 * filtering
 * The portal will access this plugin via the DefaultPortalPlugin interface 
 * Other plugins will access this plugin via the LogFilterInterface and 
 * LogFilterResultListener interfaces.
 * @author Bruce Nguyen (29/08/2019)
 *
 */
public class LogFilterStandardPlugin extends GenericLogFilterPlugin {
	private PortalContext portalContext;
	private LogSummaryType portalItem;
	private String label = "Filter log";
	private String groupLabel = "Discoverer";
	@Inject private EventLogService eventLogService;
	@Inject private LogFilterService logFilterService;
	@Inject private LogFilterCriterionFactory logFilterCriterionFactory;

	
    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }
    
    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    
    @Override
    /**
     * This method is used by the portal when calling this plugin to filter an existing log
     * The filtered log will be saved to the portal
     */
    public void execute(PortalContext portalContext) {
    	this.portalContext = portalContext;
    	
    	// Get the current portal item
        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }
        
        if (selectedLogSummaryType.size() == 0) {
        	Clients.showNotification("Select one log!", "error", null, "top_left", 3000, true);
        }
        else if (selectedLogSummaryType.size() > 1) {
        	Clients.showNotification("Select only one log!", "error", null, "top_left", 3000, true);
        }
        else {
	        try {
	        	this.portalItem = selectedLogSummaryType.iterator().next();
	        	XLog oriLog = eventLogService.getXLog(portalItem.getId());
	        	new LogFilterController(portalContext, oriLog, logFilterService, logFilterCriterionFactory, this);
	        }
        	catch (IOException | SuspendNotAllowedException e) {
	            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
	        }
    	}
    }

    private void saveLog(PortalContext portalContext, XLog filtered_log, String logName, LogSummaryType portalItem) throws Exception {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        eventLogService.exportToStream(outputStream, filtered_log);

        int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

        eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
        		logName, new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                portalItem.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                false);
        
        Messagebox.show("A new log named '" + logName + "' has been saved in the '" + portalContext.getCurrentFolder().getFolderName() + "' folder.");

        portalContext.refreshContent();
    }

    @Override
    public void execute(LogFilterContext filterContext, LogFilterInputParams inputParams,
            LogFilterResultListener resultListener) throws Exception {
        this.portalContext = filterContext.getPortalContext();
        
        //The call has to be commented out because it causes security issue when called from another web plugin
        //portalContext.getMessageHandler().displayInfo("Execute log filter plug-in!");
        
        try {
            new LogFilterController(portalContext, logFilterService, logFilterCriterionFactory, 
                                    inputParams.getLog(), 
                                    inputParams.getClassifierAttribute(), 
                                    inputParams.getFilterCriteria(), 
                                    resultListener);
            
        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
        
    }

    @Override
    public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
        XLog filteredLog = outputParams.getLog();
        //List<LogFilterCriterion> criteria = outputParams.getFilterCriteria();
        
        if (filteredLog != null) {
            if (this.portalContext instanceof PluginPortalContext) {
                ((PluginPortalContext)portalContext).getMainController().showInputDialog(
                        "Input", 
                        "Enter a log name (no more than 60 characters)", 
                        portalItem.getName() + "_filtered", 
                        "^[a-zA-Z0-9_\\-\\s]{1,60}$",
                        "a-z, A-Z, 0-9, hyphen, underscore, and space. No more than 60 chars.",
                        new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                if (event.getName().equals("onOK")) {
                                    String logName = (String)event.getData();
                                    saveLog(portalContext, filteredLog, logName, portalItem);
                                }
                            }
                        });
            }
            else {
                Messagebox.show("The current context is not a valid plugin portal context", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        }
    }


}
