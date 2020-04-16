/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.util.*;

// Java 2 Enterprise packages
import javax.inject.Inject;

// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.context.EditorPluginResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import org.apromore.helper.Version;
// Local packages
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.ProcessService;
import org.json.JSONException;

/**
 * This class was created to manage the BPMN model being edited in the bpmn.io editor
 * It is based on ApromoreController originally created for the same purpose with the Signavio editor
 * Basically it keeps track of the current process model (ProcessSummaryType) and version (VersionSummaryType)
 * being edited in the bpmn.io editor. 
 * This class can be called from opening an existing model in the portal or from a New Model Creation Dialog
 * It receives input data via the user session stored in the ApromoreSession object.
 * It calls to other plugins via the toolbar buttons and it will pass the BPMN XML text to those plugins
 * It also passes the EditSessionType to other plugins and receives output back via the updated EditSessionType object 
 * The process model in the editor has three possible states:
 * 	1. It is a new model being edited. In this case, it is opened from an empty model which has been pre-created in the database
 *  2. It is a model being edited from opening an existing model
 *  3. It is being saved to a totally new model from an existing model
 * @todo: ApromoreSession should be renamed to be generic (not specific to Signavio)
 * @todo: the editor requires that a process model already created in the system even if it is a new model. It  
 * is counter-intutitive that an empty model is created in the system first and then user starts editing the model
 * In case the user leaves immediately after the editor has been opened, an empty model will exist in the system
 * @author Bruce Nguyen
 *
 */
public class BPMNEditorController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNEditorController.class.getCanonicalName());

    private MainController mainC;
    private EditSessionType editSession;
    private ProcessSummaryType process;
    private VersionSummaryType vst;
    private Set<RequestParameterType<?>> params;
    boolean isNewProcess = false;

    @Inject private UserSessionManager userSessionManager;
//    private ProcessService processService;

    public BPMNEditorController() {
        super();
//        processService = (ProcessService) beanFactory.getBean("processService");
        if (userSessionManager.getCurrentUser() == null) {
//            LOGGER.warn("Faking user session with admin(!)");
//            UserType user = new UserType();
//            user.setId("8");
//            user.setUsername("admin");
//            userSessionManager.setCurrentUser(user);
        	throw new AssertionError("Cannot open the editor without any login user!");
        }

        String id = Executions.getCurrent().getParameter("id");
        isNewProcess = Boolean.valueOf(Executions.getCurrent().getParameter("newProcess"));
        if (id == null) {
            throw new AssertionError("No id parameter in URL");
        }

        ApromoreSession session = userSessionManager.getEditSession(id);
        if (session == null) {
            throw new AssertionError("No edit session associated with id " + id);
        }

        editSession = session.getEditSession();
        mainC = session.getMainC();
        process = session.getProcess();
        vst = session.getVersion();
        params =  session.getParams();
        
        Map<String, Object> param = new HashMap<>();
        try {
            String title = null;
            PluginMessages pluginMessages = null;
            String bpmnXML = (String) session.get("bpmnXML");
            
            if(bpmnXML == null) {
            	if (isNewProcess) {
            		bpmnXML = "<?xml version='1.0' encoding='UTF-8'?>" +
            				  "<bpmn:definitions xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
            				                    "xmlns:bpmn='http://www.omg.org/spec/BPMN/20100524/MODEL' "  +
            				                    "xmlns:bpmndi='http://www.omg.org/spec/BPMN/20100524/DI' " +
            				                    "xmlns:dc='http://www.omg.org/spec/DD/20100524/DC' " +
            				                    "targetNamespace='http://bpmn.io/schema/bpmn' " +
            				                    "id='Definitions_1'>" +
            				    "<bpmn:process id='Process_1' isExecutable='false'>" +
            				      "<bpmn:startEvent id='StartEvent_1'/>" +
            				    "</bpmn:process>" +
            				    "<bpmndi:BPMNDiagram id='BPMNDiagram_1'>" +
            				      "<bpmndi:BPMNPlane id='BPMNPlane_1' bpmnElement='Process_1'>" +
            				        "<bpmndi:BPMNShape id='_BPMNShape_StartEvent_2' bpmnElement='StartEvent_1'>" +
            				          "<dc:Bounds height='36.0' width='36.0' x='173.0' y='102.0'/>" +
            				        "</bpmndi:BPMNShape>" +
            				      "</bpmndi:BPMNPlane>" +
            				    "</bpmndi:BPMNDiagram>" +
            				  "</bpmn:definitions>";
            	}
            	else {
            		// Note: process models created by merging are not BPMN, cannot use processService.getBPMNRepresentation 
            		//bpmnXML = processService.getBPMNRepresentation(procName, procID, branch, version);
            		String annotation = (editSession.getAnnotation() == null) ? editSession.getNativeType() : editSession.getAnnotation();
            		
                    ExportFormatResultType exportResult =
                            getService().exportFormat(editSession.getProcessId(),
                            		editSession.getProcessName(),
                            		editSession.getOriginalBranchName(),
                            		editSession.getCurrentVersionNumber(),
                            		editSession.getNativeType(),
                            		annotation,
                            		editSession.isWithAnnotation(),
                            		editSession.getUsername(),
                                    params);
                    bpmnXML = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());
            	}
            	
                title = editSession.getProcessName() + " (" + editSession.getNativeType() + ")";
                this.setTitle(title);

                param.put("bpmnXML",       escapeXML(bpmnXML));
                param.put("url",           getURL(editSession.getNativeType()));
                param.put("importPath",    getImportPath(editSession.getNativeType()));
                param.put("exportPath",    getExportPath(editSession.getNativeType()));
//                param.put("editor",        config.getSiteEditor());
                param.put("editor",        "bpmneditor");
            } else {
                param.put("bpmnXML",       bpmnXML);
                param.put("url",           getURL("BPMN 2.0"));
                param.put("importPath",    getImportPath("BPMN 2.0"));
                param.put("exportPath",    getExportPath("BPMN 2.0"));
                param.put("editor",        "bpmneditor");
                param.put("doAutoLayout", "false");
            }
            
            if (isNewProcess) {
            	param.put("doAutoLayout", "false");
            }
            else if (editSession.isWithAnnotation()) {
                param.put("doAutoLayout", "false");
            } 
            else {
                param.put("doAutoLayout", "true");
            }

            this.setTitle(title);
            if (mainC != null) {
                mainC.showPluginMessages(pluginMessages);
            }

            // We're not expecting any request parameters, so warn if we see any
            for (RequestParameterType<?> requestParameter: params) {
                switch (requestParameter.getId()) {
                default:
                    LOGGER.warn("Unsupported request parameter \"" + requestParameter.getId() + "\" with value " + requestParameter.getValue());
                }
            }

            List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("bpmnEditorPlugins");
            param.put("plugins", editorPlugins);

            Executions.getCurrent().pushArg(param);

        } catch (Exception e) {
            LOGGER.error("",e);
            e.printStackTrace();
        }
        
        //todo: the exception catching here is not effective as ZK dialogs are asynchronous
        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
            	boolean isNewProcessBackup = isNewProcess;
                try {
                	if (isNewProcess) {
                		new SaveAsDialogController(process, vst, editSession, null, eventToString(event));
                		isNewProcess = false; // to change to save current after saving as new
                	}
                	else {
                		new SaveAsDialogController(process, vst, editSession, true, eventToString(event));
                	}
                } catch (Exception ex) {
//                	Messagebox.show("Error saving model: " + ex.getMessage());
                    LOGGER.error("Error saving model.", ex.getStackTrace().toString());
                    Messagebox.show("Unable to save model! Check if a model with the same name and version number has already existed.");
                    isNewProcess = isNewProcessBackup; //change the status back in case of saving error
                }
            }
        });
        
        //todo: the exception catching here is not effective as ZK dialogs are asynchronous
        this.addEventListener("onSaveAs", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                boolean isNewProcessBackup = isNewProcess;
                try {
                	// If new model: choose Save As is the same as choose Save 
                	if (isNewProcess) {
                		new SaveAsDialogController(process, vst, editSession, null, eventToString(event));
                		isNewProcess = false; // to change to save current after saving as new
                	}
                	else {
                		new SaveAsDialogController(process, vst, editSession, false, eventToString(event));
                	}
                } catch (Exception ex) {
//                	Messagebox.show("Error saving model: " + ex.getMessage());
                    LOGGER.error("Error saving model.", ex.getStackTrace().toString());
                    Messagebox.show("Unable to save model! Check if a model with the same name and version number has already existed.");
                    isNewProcess = isNewProcessBackup; //change the status back in case of saving error
                }
            }
        });
    }


    /**
     * @param json
     * @return the <var>json</var> escaped so that it can be quoted in Javascript.
     *     Specifically, it replaces apostrophes with \\u0027 and removes embedded newlines and leading and trailing whitespace.
     */
    
    private String escapeXML(String xml) {
//    	String newline = System.getProperty("line.separator");
//        return xml.replace(newline, " ").replace("\n", " ").trim();
    	//return xml.replaceAll("(\\r|\\n|\\r\\n)+", " ").replace("'", "");
    	return xml.replaceAll("(\\r|\\n|\\r\\n)+", " ").replace("'", "\\'");
    }
    
    /**
     * YAWL models package their event data as an array of {@link String}s, EPML packages it as a {@link String}; this function
     * hides the difference.
     *
     * @param event ZK event
     * @throws RuntimeException if the data associated with <var>event</var> is neither a {@link String} nor an array of {@link String}s
     */
    private static String eventToString(final Event event) {
        if (event.getData() instanceof String[]) {
            return ((String[]) event.getData())[0];
        }
        if (event.getData() instanceof String) {
            return (String) event.getData();
        }

        throw new RuntimeException("Unsupported class of event data: " + event.getData());
    }

}
