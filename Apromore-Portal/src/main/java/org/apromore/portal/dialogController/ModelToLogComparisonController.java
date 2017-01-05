/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.portal.context.EditorPluginResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;

/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ModelToLogComparisonController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignavioController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession1, editSession2;
    private ProcessSummaryType process, process2;
    private VersionSummaryType version, version2;
    private Set<RequestParameterType<?>> params;
    private JSONObject differences;

    public ModelToLogComparisonController() {
        super();

        String id = Executions.getCurrent().getParameter("id");
        if (id != null) {
            SignavioSession session = UserSessionManager.getEditSession(id);
            if (session == null) {
                throw new AssertionError("No edit session associated with id " + id);
            }

            editSession1 = session.getEditSession();
            editSession2 = session.getEditSession2();
            mainC = session.getMainC();
            process = session.getProcess();
            version = session.getVersion();
            process2 = session.getProcess2();
            version2 = session.getVersion2();
            params =  session.getParams();
        }

        Map<String, Object> param = new HashMap<>();
        try {
            String title = editSession1.getProcessName() + " (" + editSession1.getNativeType() + ")";
            if (editSession2 != null) {
                title += " vs " + editSession2.getProcessName() + " (" + editSession2.getNativeType() + ")";
            }
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

            if (editSession2 != null) {
                ExportFormatResultType exportResult2 =
                    getService().exportFormat(editSession2.getProcessId(),
                            editSession2.getProcessName(),
                            editSession2.getOriginalBranchName(),
                            editSession2.getCurrentVersionNumber(),
                            editSession2.getNativeType(),
                            editSession2.getAnnotation(),
                            editSession2.isWithAnnotation(),
                            editSession2.getUsername(),
                            params);

                String data2 = StreamUtil.convertStreamToString(exportResult2.getNative().getInputStream());
                param.put("jsonData2", data2.replace("\n", " ").replace("'", "\\u0027").trim());

                title += " differences with " + editSession2.getProcessName();
                pluginMessages = null;
                if (exportResult1.getMessage() != null) {
                    pluginMessages = new PluginMessages();
                    pluginMessages.getMessage().addAll(exportResult1.getMessage().getMessage());
                }
                if (exportResult2.getMessage() != null) {
                    if (pluginMessages == null) {
                        pluginMessages = new PluginMessages();
                    }
                    pluginMessages.getMessage().addAll(exportResult2.getMessage().getMessage());
                }
            }

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

        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
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
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
    }

    public void onCreate() throws InterruptedException {
        Treechildren treechildren = (Treechildren) this.getFellowIfAny("differences");
        if (treechildren != null) {
            try {
                JSONArray array = this.differences.getJSONArray("differences");
                for (int i=0; i < array.length(); i++) {
                    JSONObject difference = array.getJSONObject(i);

                    // Add UI for this difference
                    Treeitem item = new Treeitem();

                    Treerow row = new Treerow();
                    item.appendChild(row);

                    Treecell cell = new Treecell(difference.getString("sentence"));
                    row.appendChild(cell);

                    cell.setWidgetListener("onClick", differenceToJavascript(difference));

                    treechildren.appendChild(item);
                }
            } catch (JSONException e) {
                InterruptedException ie = new InterruptedException("Unable to parse differences JSON");
                ie.initCause(e);
                ie.printStackTrace();
                throw ie;
            }
        }
    }

    private String differenceToJavascript(JSONObject difference) throws JSONException {
            LOGGER.info("differenceToJavascript: " + difference);

            return "oryxEditor1.displayMLDifference(" +
                    "\"" + difference.optString("type") + "\"," +
                    difference.optJSONArray("start")    + "," +
                    difference.optJSONArray("a")        + "," +
                    difference.optJSONArray("b")        + "," +
                    difference.optJSONArray("newTasks") + "," +
                    difference.optJSONArray("end")      + "," +
                    difference.optJSONArray("greys")    + ")";
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
