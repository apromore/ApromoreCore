/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.bpmneditor;

import java.io.OutputStream;
// Java 2 Standard packages
import java.util.*;

// Java 2 Enterprise packages
import javax.inject.Inject;

// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.portal.context.EditorPluginResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import org.apromore.helper.Version;
// Local packages
import org.apromore.model.EditSessionType;
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
import org.apromore.service.ProcessService;
import org.json.JSONException;

public class BPMNEditorController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNEditorController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession;
    private ProcessSummaryType process;
    private VersionSummaryType vst;
    private Set<RequestParameterType<?>> params;

    @Inject private UserSessionManager userSessionManager;
    private ProcessService processService;

    public BPMNEditorController() {
        super();

        processService = (ProcessService) beanFactory.getBean("processService");
        
        if (userSessionManager.getCurrentUser() == null) {
            LOGGER.warn("Faking user session with admin(!)");
            UserType user = new UserType();
            user.setId("8");
            user.setUsername("admin");
            userSessionManager.setCurrentUser(user);
        }

        String id = Executions.getCurrent().getParameter("id");
        boolean newProcess = Boolean.valueOf(Executions.getCurrent().getParameter("newProcess"));
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
        	int procID = process.getId();
            String procName = process.getName();
            String branch = vst.getName();
            Version version = new Version(vst.getVersionNumber());

            String title = null;
            PluginMessages pluginMessages = null;
            String bpmnXML = (String) session.get("bpmnXML");
            
            if(bpmnXML == null) {
            	if (newProcess) {
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
            		bpmnXML = processService.getBPMNRepresentation(procName, procID, branch, version);
            	}
            	
                title = editSession.getProcessName() + " (" + editSession.getNativeType() + ")";
                this.setTitle(title);

                param.put("bpmnXML",       escapeXML(bpmnXML));
                param.put("url",           getURL(editSession.getNativeType()));
                param.put("importPath",    getImportPath(editSession.getNativeType()));
                param.put("exportPath",    getExportPath(editSession.getNativeType()));
//                param.put("editor",        config.getSiteEditor());
                param.put("editor",        "bpmneditor");

                if (editSession.getAnnotation() == null) {
                    param.put("doAutoLayout", "true");
                } else if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(editSession.getNativeType())) {
                    param.put("doAutoLayout", "false");
                } else {
                    if (editSession.isWithAnnotation()) {
                        param.put("doAutoLayout", "false");
                    } else {
                        param.put("doAutoLayout", "true");
                    }
                }
            } else {
                param.put("bpmnXML",       bpmnXML);
                param.put("url",           getURL("BPMN 2.0"));
                param.put("importPath",    getImportPath("BPMN 2.0"));
                param.put("exportPath",    getExportPath("BPMN 2.0"));
                param.put("editor",        config.getSiteEditor());
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
        
        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                	mainC.saveModel(process, vst, editSession, true, eventToString(event));
                } catch (InterruptedException ex) {
                    LOGGER.error("Error saving model.", ex);
                }
            }
        });
        this.addEventListener("onSaveAs", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                	mainC.saveModel(process, vst, editSession, true, eventToString(event));
                } catch (InterruptedException ex) {
                    LOGGER.error("Error saving model.", ex);
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
