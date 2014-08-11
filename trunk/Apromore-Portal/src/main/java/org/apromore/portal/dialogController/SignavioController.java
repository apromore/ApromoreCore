/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SignavioController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignavioController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private Set<RequestParameterType<?>> params;


    public SignavioController() {
        super();

        if (Executions.getCurrent().getParameter("id") != null) {
            SignavioSession session = UserSessionManager.getEditSession(Executions.getCurrent().getParameter("id"));

            editSession = session.getEditSession();
            mainC = session.getMainC();
            process = session.getProcess();
            version = session.getVersion();
            params =  session.getParams();
        }

        Map<String, String> param = new HashMap<>();
        try {
            this.setTitle(editSession.getProcessName() + " (" + editSession.getNativeType() + ")");

            ExportFormatResultType exportResult =
                    getService().exportFormat(editSession.getProcessId(),
                            editSession.getProcessName(),
                            editSession.getOriginalBranchName(),
                            editSession.getCurrentVersionNumber(),
                            editSession.getNativeType(),
                            editSession.getAnnotation(),
                            editSession.isWithAnnotation(),
                            editSession.getUsername(),
                            params);

            String data = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());

            // Run the document through a pass-through XML transformation because we have ZK/Signavio issues if the native document used apostrophes to quote attributes
            // If there's ever a non-XML process model format, we'll have to detect that and skip the XML parsing
//            StringWriter stringWriter = new StringWriter();
//            TransformerFactory.newInstance().newTransformer().transform(new StreamSource(new StringReader(data)), new StreamResult(stringWriter));
//            data = stringWriter.toString();


            mainC.showPluginMessages(exportResult.getMessage());
            this.setTitle(editSession.getProcessName());
            String JSON_DATA = "jsonData";
            param.put(JSON_DATA, data.replace("\n", " ").trim());
            param.put("url", getURL(editSession.getNativeType()));
            param.put("importPath", getImportPath(editSession.getNativeType()));
            param.put("exportPath", getExportPath(editSession.getNativeType()));

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
            Executions.getCurrent().pushArg(param);
        } catch (Exception e) {
            LOGGER.error("",e);
            e.printStackTrace();
        }

        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(process, version, editSession, true, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
        this.addEventListener("onSaveAs", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(process, version, editSession, false, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });

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
