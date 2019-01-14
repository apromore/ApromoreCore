/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
 */

package org.apromore.plugin.portal.ltl.conformance;

// Java 2 Standard packages

import org.apromore.dao.model.Log;
import org.apromore.model.*;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.EditorPluginResolver;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.SaveAsDialogController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbar;

import javax.inject.Inject;
import java.util.*;

// Third party packages
// Local packages

/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ModelMisconformanceController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelMisconformanceController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession1;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private Set<RequestParameterType<?>> params;
    private JSONObject differences;

    private String nativeType = "BPMN 2.0";

//    private CompareService compareService;
    @Inject private UserSessionManager userSessionManager;
    @Inject private ProcessService processService;
    @Inject private CanoniserService canoniserService;
    private Component listDiff;

    public ModelMisconformanceController() {
        super();

        if (userSessionManager.getCurrentUser() == null) {
            LOGGER.warn("Faking user session with admin(!)");
            UserType user = new UserType();
            user.setId("8");
            user.setUsername("admin");
            userSessionManager.setCurrentUser(user);
        }

        String id = Executions.getCurrent().getParameter("id");
        if (id != null) {
            SignavioSession session = userSessionManager.getEditSession(id);
            if (session == null) {
                throw new AssertionError("No edit session associated with id " + id);
            }

            editSession1 = session.getEditSession();
            mainC = session.getMainC();
            process = session.getProcess();
            version = session.getVersion();
            params =  session.getParams();
        }

        Map<String, Object> param = new HashMap<>();
        try {
            String title = editSession1.getProcessName() + " (" + editSession1.getNativeType() + ")";
            this.setTitle(title);

            ExportFormatResultType exportResult1 =
                    getService().exportFormat(editSession1.getProcessId(),
                            editSession1.getProcessName(),
                            editSession1.getOriginalBranchName(),
                            editSession1.getCurrentVersionNumber(),
                            editSession1.getNativeType(),
                            editSession1.getAnnotation(),
                            editSession1.isWithAnnotation(),
                            editSession1.getUsername(),
                            params);

            title = editSession1.getProcessName();
            PluginMessages pluginMessages = exportResult1.getMessage();

            String JSON_DATA = "jsonData";
            String data1 = StreamUtil.convertStreamToString(exportResult1.getNative().getInputStream());
            param.put(JSON_DATA, data1.replace("\n", " ").replace("'", "\\u0027").trim());
            param.put("url", getURL(editSession1.getNativeType()));
            param.put("importPath", getImportPath(editSession1.getNativeType()));
            param.put("exportPath", getExportPath(editSession1.getNativeType()));
            param.put("editor", config.getSiteEditor());

            this.setTitle(title);
            if (mainC != null) {
                mainC.showPluginMessages(pluginMessages);
            }

            for (RequestParameterType<?> requestParameter: params) {
                switch (requestParameter.getId()) {
                case "m1_differences_json":
                    param.put("differences", (String) requestParameter.getValue());
                    this.differences = new JSONObject((String) requestParameter.getValue());
                    break;
                default:
                    LOGGER.warn("Unsupported request parameter \"" + requestParameter.getId() + "\" with value " + requestParameter.getValue());
                }
            }

            if (editSession1.getAnnotation() == null) {
                param.put("doAutoLayout", "true");
            } else if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(editSession1.getNativeType())) {
                param.put("doAutoLayout", "false");
            } else {
                if (editSession1.isWithAnnotation()) {
                    param.put("doAutoLayout", "false");
                } else {
                    param.put("doAutoLayout", "true");
                }
            }

            List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve();
            param.put("plugins", editorPlugins);

            Executions.getCurrent().pushArg(param);

        } catch (Exception e) {
            LOGGER.error("",e);
            e.printStackTrace();
        }

//        this.addEventListener("onRepair", new EventListener<Event>() {
//            @Override
//            public void onEvent(final Event event) throws InterruptedException {
//                String bpmnString = eventToString(event);
//                try {
//                    ModelAbstractions model = new ModelAbstractions(bpmnString.getBytes(Charset.forName("UTF-8")));
//                    DifferencesML differencesML = compareService.discoverBPMNModel(model, log, new HashSet<String>());
//                    differences = new JSONObject(DifferencesML.toJSON(differencesML));
//                    LOGGER.info("Obtained differences: " + differences);
//                    onCreate();
//
//                } catch (Exception e) {
//                    LOGGER.error("Unable to obtain differences", e);
//                }
//            }
//        });
        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    System.out.println(userSessionManager.getCurrentUser());
                    new SaveAsDialogController(process, version, editSession1, true, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
        this.addEventListener("onSaveAs", new EventListener<Event>() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(process, version, editSession1, false, eventToString(event));
                    System.out.println("saven print");
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
    }

    private String buttonCSSStyle(boolean isSelected) {
        return "background: " + (isSelected ? "#DDEEFF" : "inherit") + "; " +
               "border: " + (isSelected ? "1px solid red" : "none") + "; " +
               "margin: 5px; text-align: initial; white-space: normal";
    }

    public void onCreate() throws InterruptedException {
        LOGGER.info("Obtained differences-2: ");
        Toolbar hbox = (Toolbar) this.getFellowIfAny("differences");
        if (hbox != null) {
            Component parent = (Component) hbox.getParent();
            listDiff = parent;
            
            // Remove any pre-extant list items
            Component sibling = hbox.getNextSibling();
            while (sibling != null) {
                parent.removeChild(sibling);
                sibling = hbox.getNextSibling();
            }

            // Add the current differences
            try {
                Iterator<?> keys = this.differences.keys();
                int i = 0;
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    LOGGER.info("key: " +key);
                    final Button button = new Button(key);
                    button.setStyle(buttonCSSStyle(false));

                    button.addEventListener("onClick", new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            for (Component component: button.getParent().getChildren()) {
                                if (component instanceof Button) {
                                    Button b = (Button) component;
                                    b.setStyle(buttonCSSStyle(button == b));
                                }
                            }
                        }
                    });
                    button.setWidgetListener("onClick", differenceToJavascript(i, this.differences.getJSONArray(key)));
                    parent.appendChild(button);
                    i++;
                }

            } catch (Exception e) {
                InterruptedException ie = new InterruptedException("Unable to parse differences JSON");
                ie.initCause(e);
                ie.printStackTrace();
                throw ie;
            }
        }

        Button applyButton = (Button) this.getFellowIfAny("apply");
        if (applyButton != null) {
            // The repairMLDifference function will send an onRepair event to the ZK asynchronous updater when it completes
            applyButton.setWidgetListener("onClick", "oryxEditor1.repairDifference()");
        }
    }

    private String differenceToJavascript(int buttonIndex, JSONArray difference) throws JSONException {
        LOGGER.info("differenceToJavascript: " + difference);

        String elements = "[";
        String legends = "[";
        String colors = "[";

        String removals = "[";
        String startEnd = "[";

        String addTasks = "false";
        String newTasksLabel = "[";
        String sourceTargetsNew = "[";

        for(int i = 0; i < difference.length(); i++){
            JSONObject differenceJSON = ((JSONObject)difference.get(i));

            elements += "'" + differenceJSON.get("elementId") + "'" ;
            legends += "'" + differenceJSON.get("legend") + "'";
            colors += "'" + differenceJSON.get("color") + "'";

            if(i != (difference.length()-1)){
                elements+=",";
                legends+=",";
                colors+=",";
            }

            JSONArray toRemove = differenceJSON.getJSONArray("toRemove");
            for(int j = 0; j < toRemove.length(); j++){
                if (removals.length() > 1)
                    removals += ",";

                removals += "'" + toRemove.get(j) + "'";
            }

            if(differenceJSON.has("start") && differenceJSON.has("end")) {
                String st = differenceJSON.getString("start");
                String en = differenceJSON.getString("end");
                if (startEnd.length() > 1)
                    startEnd += ",";
                startEnd += "['" + st + "','" + en + "']";
            }

            if(differenceJSON.has("type") && differenceJSON.getString("type").equals("addTask")){
                int k =0;
                addTasks = "true";
                JSONArray newTasks = differenceJSON.getJSONArray("newTasks");
                JSONArray sourcesTargets = differenceJSON.getJSONArray("sourceTargetNew");

                for(int l = 0; l < sourcesTargets.length() - 1; l += 2){
                    if (sourceTargetsNew.length() > 1) {
                        sourceTargetsNew += ",";
                        newTasksLabel += ",";
                    }

                    newTasksLabel += "'" + newTasks.getString(k) + "'";
                    sourceTargetsNew += "['" + sourcesTargets.getString(l) + "','" + sourcesTargets.getString(l+1) + "']";

                    k++;
                }
            }
        }

        elements += "]";
        legends += "]";
        colors += "]";
        removals += "]";
        startEnd += "]";
        newTasksLabel += "]";
        sourceTargetsNew += "]";

        System.out.println("oryxEditor1.displayDiscrepancy(" + buttonIndex + "," + elements + "," + legends + "," + colors + "," + removals + "," + startEnd + "," + addTasks + "," + newTasksLabel + "," + sourceTargetsNew + ")");
        return "oryxEditor1.displayDiscrepancy(" + buttonIndex + "," + elements + "," + legends + "," + colors + "," + removals + "," + startEnd + "," + addTasks + "," + newTasksLabel + "," + sourceTargetsNew + ")";
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
