/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.loganimation;

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
import org.zkoss.zk.ui.util.Clients;

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
import org.apromore.portal.dialogController.SaveAsDialogController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.loganimation.LogAnimationService;

/**
 * Java bound to the <code>animateLogInSignavio.zul</code> ZUML document.
 */
public class LogAnimationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAnimationController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private Set<RequestParameterType<?>> params;

    public LogAnimationController() {
        super();

        String id = Executions.getCurrent().getParameter("id");
        if (id == null) {
            throw new AssertionError("No id parameter in URL");
        }

        ApromoreSession session = UserSessionManager.getEditSession(id);
        if (session == null) {
            throw new AssertionError("No edit session associated with id " + id);
        }

        editSession = session.getEditSession();
        mainC = session.getMainC();
        process = session.getProcess();
        version = session.getVersion();
        params =  session.getParams();

        LogAnimationService logAnimationService = (LogAnimationService) session.get("logAnimationService");

        Map<String, Object> param = new HashMap<>();
        try {
            String bpmnXML = (String) session.get("bpmnXML");
            String title = null;
            PluginMessages pluginMessages = null;
            if(bpmnXML == null) {
                title = editSession.getProcessName() + " (" + editSession.getNativeType() + ")";
                this.setTitle(title);

                ExportFormatResultType exportResult1 =
                        getService().exportFormat(editSession.getProcessId(),
                                editSession.getProcessName(),
                                editSession.getOriginalBranchName(),
                                editSession.getCurrentVersionNumber(),
                                editSession.getNativeType(),
                                editSession.getAnnotation(),
                                editSession.isWithAnnotation(),
                                editSession.getUsername(),
                                params);

                title = editSession.getProcessName();
                pluginMessages = exportResult1.getMessage();

                bpmnXML = StreamUtil.convertStreamToString(exportResult1.getNative().getInputStream());

                param.put("bpmnXML",      escapeQuotedJavascript(bpmnXML));
                param.put("url",           getURL(editSession.getNativeType()));
                param.put("importPath",    getImportPath(editSession.getNativeType()));
                param.put("exportPath",    getExportPath(editSession.getNativeType()));
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
                param.put("bpmnXML",      escapeQuotedJavascript(bpmnXML));
                param.put("url",           getURL("BPMN 2.0"));
                param.put("importPath",    getImportPath("BPMN 2.0"));
                param.put("exportPath",    getExportPath("BPMN 2.0"));
                param.put("editor",        "bpmneditor");
                param.put("doAutoLayout", "true");
            }

            String animationData = (String) session.get("animationData");
            if(animationData == null) {
                if (logAnimationService != null) {  // logAnimationService is null if invoked from the editor toobar
                    List<LogAnimationService.Log> logs = (List<LogAnimationService.Log>) session.get("logs");
                    animationData = logAnimationService.createAnimation(bpmnXML, logs);
                    param.put("animationData", escapeQuotedJavascript(animationData));
                }
            }else {
                param.put("animationData", escapeQuotedJavascript(animationData));
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

            List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("editorPluginsBPMN");
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
                    //new SaveAsDialogController(process, version, editSession, true, eventToString(event));
                	mainC.saveModel(process, version, editSession, true, eventToString(event));
                } catch (Exception e) {
                    LOGGER.error("Error saving model.", e);
                }
            }
        });
        this.addEventListener("onSaveAs", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    //new SaveAsDialogController(process, version, editSession, false, eventToString(event));
                	mainC.saveModel(process, version, editSession, false, eventToString(event));
                } catch (Exception e) {
                    LOGGER.error("Error saving model.", e);
                }
            }
        });
    }

    /*
    public void onCreate() throws InterruptedException {

        //Window window = (Window) this.getFellowIfAny("win");  // If we needed a dynamic ZK UI, we'd be able to mutate it here

        //Clients.evalJavaScript("initialize()");
    }
    */

    /**
     * @param json
     * @return the <var>json</var> escaped so that it can be quoted in Javascript.
     *     Specifically, it replaces apostrophes with \\u0027 and removes embedded newlines and leading and trailing whitespace.
     */
    private String escapeQuotedJavascript(String json) {
        return json.replace("\n", " ").replace("'", "\\u0027").trim();
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
