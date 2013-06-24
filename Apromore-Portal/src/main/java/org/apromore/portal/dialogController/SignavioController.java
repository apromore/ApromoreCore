package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;


/**
 * The Signavio Controller. This controls opening the signavio editor in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class SignavioController extends BaseController {

    private final String JSON_DATA = "jsonData";
    public static EditSessionType editSession;
    public static MainController mainC;
    public static ProcessSummaryType process;
    public static VersionSummaryType version;
    private boolean isNormalSave;


    private static final Logger LOGGER = LoggerFactory.getLogger(SignavioController.class.getCanonicalName());

    public SignavioController() {
        super();

        Map<String, String> param = new HashMap<>();
        try {
            ExportFormatResultType exportResult =
                    getService().exportFormat(editSession.getProcessId(),
                            editSession.getProcessName(),
                            editSession.getOriginalBranchName(),
                            editSession.getVersionNumber(),
                            editSession.getNativeType(),
                            editSession.getAnnotation(),
                            editSession.isWithAnnotation(),
                            editSession.getUsername(), new HashSet<RequestParameterType<?>>());
            String data = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());

            mainC.showPluginMessages(exportResult.getMessage());
            this.setTitle(editSession.getProcessName());
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

        this.addEventListener("onSave", new EventListener() {

            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    LOGGER.info("Event type " + event.getData().getClass() + ": " + event.getData());
                    new SaveAsDialogController(mainC, process, version, editSession, true /* a normal save */, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    exceptionFormats.printStackTrace();
                }
            }
        });

        this.addEventListener("onSaveAs", new EventListener() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(mainC, process, version, editSession, false /* not a normal save */, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    exceptionFormats.printStackTrace();
                }
            }
        });

    }

    /**
     * YAWL models package their event data as an array of {@link String}s, EPML packages it as a {@link String}; this function
     * hides the difference.
     *
     * @param event
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
