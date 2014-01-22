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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


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
