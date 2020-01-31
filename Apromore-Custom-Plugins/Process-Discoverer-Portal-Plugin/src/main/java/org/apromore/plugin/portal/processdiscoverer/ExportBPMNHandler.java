package org.apromore.plugin.portal.processdiscoverer;

import java.io.ByteArrayInputStream;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.UIManager;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.helper.Version;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

/**
 * This class provides a facility to export BPMN models from ProcessDiscoverer and save
 * the models to the portal.
 * It shows a progress window with progress bar indicating the completion percent for long-running task
 * To do that, it uses an EventQueue at the session scope where other parties can post events to 
 * or listen to events during this process.
 * Note that instances of this class can only be used once at a time, i.e. one instance is used
 * and then finished, then another instance is used and then finished.
 * If multiple instances concurrently exist and run, it may happen that one instance may respond
 * to events occuring in another instance. 
 * 
 * @author Simon Rabozi
 * @modified Bruce Nguyen
 *
 */
public class ExportBPMNHandler implements EventListener<Event> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDiscovererController.class);
    private static final String EVENT_QUEUE = ExportBPMNHandler.class.getCanonicalName();
    private static final String CHANGE_DESCRIPTION = "CHANGE_DESCRIPTION";
    private static final String CHANGE_FRACTION_COMPLETE = "CHANGE_FRACTION_COMPLETE";
    private static final String MINING_COMPLETE = "MINING_COMPLETE";
    private static final String MINING_EXCEPTION = "MINING_EXCEPTION";
    private static final String ANNOTATION_EXCEPTION = "ANNOTATION_EXCEPTION";
    private EventQueue<Event> eventQueue = null;

    private boolean annotateForBIMP;
    private ProcessDiscovererController controller = null;
    private PortalContext portalContext = null;
    private String minedModel = null; 
    
    //Progress window
    private Window window;
    private Label descriptionLabel;
    private Progressmeter fractionCompleteProgressmeter;
    private ProgressEventListener progressListener = null;
    private boolean showProgressBar = false;

    public ExportBPMNHandler(PortalContext portalContext, ProcessDiscovererController controller, boolean annotateForBIMP, boolean showProgressBar) {
    	this.portalContext = portalContext;
    	this.controller = controller;
        this.annotateForBIMP = annotateForBIMP;
        this.showProgressBar = showProgressBar;
        this.progressListener = new ProgressEventListener();
        eventQueue = EventQueues.lookup(EVENT_QUEUE, EventQueues.SESSION, true);
    }
    
    /*
     * Note: forget to unsubscribe may lead to unexpected behavior
     * if multiple ExportBPMNHander objects exist.
     */
    class ProgressEventListener implements EventListener<Event> {
        public void onEvent(Event event) throws Exception {
            switch (event.getName()) {
                case CHANGE_DESCRIPTION:
                    if (descriptionLabel != null) descriptionLabel.setValue((String) event.getData());
                    break;
    
                case CHANGE_FRACTION_COMPLETE:
                    if (fractionCompleteProgressmeter != null) fractionCompleteProgressmeter.setValue((int) Math.round(100.0 * (Double) event.getData()));
                    break;
    
                case MINING_COMPLETE:
                	if (fractionCompleteProgressmeter != null) fractionCompleteProgressmeter.setValue(100);
                    if (descriptionLabel != null) descriptionLabel.setValue("Saving BPMN model");
                    try {
                    	ExportBPMNHandler.this.save();
                    	if (window != null) window.detach();
                        eventQueue.unsubscribe(this); //unsubscribe after finishing work
                    } catch (Exception e) {
                        e.printStackTrace();
                        Messagebox.show("Process mining failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                        eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    }
                    break;

                case MINING_EXCEPTION:
                    Exception e = (Exception) event.getData();
                    if (window != null) window.detach();
                    Messagebox.show("Process mining failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                    eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    break;

                case ANNOTATION_EXCEPTION:
                    Exception e2 = (Exception) event.getData();
                    Messagebox.show("Unable to annotate BPMN model for BIMP simulation (" + e2.getMessage() + ")\n\nModel will be created without annotations.", "Attention", Messagebox.OK, Messagebox.EXCLAMATION);
                    eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    break;
                }
        }
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        //window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "/zul/mineAndSave.zul", null, null);
    	if (this.showProgressBar) {
	    	window = (Window) Executions.createComponents("/zul/mineAndSave.zul", null, null);
	        ((Button) window.getFellow("cancel")).addEventListener("onClick", new EventListener<Event>() {
	            public void onEvent(Event event) throws Exception {
	                window.detach();
	            }
	        });
	        
	        descriptionLabel = (Label) window.getFellow("description");
	        fractionCompleteProgressmeter = (Progressmeter) window.getFellow("fractionComplete");
	        window.doModal();
    	}
        
        eventQueue.subscribe(progressListener); // subscribe to start listening to events

        new Thread() {
            public void run() {
                try {
                    mine();
                    eventQueue.publish(new Event(MINING_COMPLETE, null, null));

                } catch (Exception e) {
                    e.printStackTrace();
                    eventQueue.publish(new Event(MINING_EXCEPTION, null, e));
                }
            }
        }.start();
    }
    
    private void mine() throws Exception {
    	BPMNDiagram newDiagram = controller.getBPMNDiagram();

        for(BPMNEdge edge : newDiagram.getEdges()) {
            edge.setLabel("");
        }

        for (Flow flow : newDiagram.getFlows()) {
            flow.setLabel("");
        }
        for (org.processmining.models.graphbased.directed.bpmn.elements.Event event1 : newDiagram.getEvents()) {
            event1.getAttributeMap().put("ProM_Vis_attr_label", "");
        }

        UIContext context = new UIContext();
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        UIPluginContext uiPluginContext = context.getMainPluginContext();
        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, newDiagram);
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

        minedModel = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">") +
                definitions.exportElements() +
                "</definitions>";

        if (annotateForBIMP) {
            try {
            	minedModel = controller.getBIMPAnnotationService().annotateBPMNModelForBIMP(minedModel, controller.getInitialLog(), 
                		new BIMPAnnotationService.Context() {
		                    public void setDescription(String description) {
		                        eventQueue.publish(new Event(CHANGE_DESCRIPTION, null, description));
		                    }
		
		                    public void setFractionComplete(Double fractionComplete) {
		                        eventQueue.publish(new Event(CHANGE_FRACTION_COMPLETE, null, fractionComplete));
		                    }
                		});

            } catch (Exception e) {
            	LOGGER.warn("Unable to annotate BPMN model for BIMP simulation", e);
            	eventQueue.publish(new Event(ANNOTATION_EXCEPTION, null, e));
            }
        }
    }

    private void save() throws Exception {
        String defaultProcessName = null;
        if (controller.getLogName() != null) {
            defaultProcessName = controller.getLogName().split("\\.")[0];
        }
        
        controller.showInputDialog(
			"Save BPMN model",
			"Enter a name for the BPMN model (no more than 60 characters)", 
			defaultProcessName, 
			"^[a-zA-Z0-9_\\(\\)\\-\\s]{1,60}$",
			"a-z, A-Z, 0-9, hyphen, underscore, and space. No more than 60 chars.",
			new EventListener<Event>() {
				@Override
            	public void onEvent(Event event) throws Exception {
					if (event.getName().equals("onOK")) {
    				   String modelName = (String)event.getData();
				       String user = portalContext.getCurrentUser().getUsername();
				        Version version = new Version(1, 0);
				        Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
				        String now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
				        boolean publicModel = false;

				        List<String> domains = controller.getDomainService().findAllDomains();
				        SelectDynamicListController domainCB = new SelectDynamicListController(domains);
				        domainCB.setReference(domains);
				        domainCB.setAutodrop(true);
				        domainCB.setWidth("85%");
				        domainCB.setHeight("100%");
				        domainCB.setAttribute("hflex", "1");

				        controller.getProcessService().importProcess(user,
				        		portalContext.getCurrentFolder() == null ? 0 : controller.getContainingFolderId(), //portalContext.getCurrentFolder().getId(),
				        		modelName,
				                version,
				                "BPMN 2.0",
				                controller.getCanoniserService().canonise("BPMN 2.0", new ByteArrayInputStream(minedModel.getBytes()), canoniserProperties),
				                domainCB.getValue(),
				                "Model generated by the Apromore BPMN process mining service.",
				                now,  // creation timestamp
				                now,  // last update timestamp
				                publicModel);
				        Messagebox.show("A new BPMN model named '" + modelName + "' has been saved in the '" + controller.getContainingFolderName() + "' folder.", "Apromore", Messagebox.OK, Messagebox.NONE); //portalContext.getCurrentFolder().getFolderName() + "' folder.");
				        portalContext.refreshContent();
					}
            	}
			});

 
    };

}
