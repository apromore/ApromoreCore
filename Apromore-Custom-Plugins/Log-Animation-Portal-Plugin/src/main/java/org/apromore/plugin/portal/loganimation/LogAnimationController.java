/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
// import org.apromore.portal.context.EditorPluginResolver;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.loganimation.LogAnimationService;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * Java bound to the <code>animateLogInSignavio.zul</code> ZUML document.
 */
public class LogAnimationController extends BaseController implements Composer<Component> {

  private static final Logger LOGGER = PortalLoggerFactory.getLogger(LogAnimationController.class);

  private MainController mainC;
  private String pluginSessionId;
  private EditSessionType editSession;
  private ProcessSummaryType process;
  private VersionSummaryType version;
  private Set<RequestParameterType<?>> params;

  /*
   * The initialization must be put inside the constructor as it will push the result to Execution's
   * arguments to be used inside the ZUL file with the $arg.$ object.
   */
  public LogAnimationController() {
    super();

    pluginSessionId = Executions.getCurrent().getParameter("id");
    if (pluginSessionId == null) {
      throw new AssertionError("No id parameter in URL");
    }

    ApromoreSession session = UserSessionManager.getEditSession(pluginSessionId);
    if (session == null) {
      // throw new AssertionError("No edit session associated with id " + pluginSessionId);
      throw new AssertionError(
          "Your session has expired. Please close this browser tab and refresh the Portal tab");

    }

    editSession = session.getEditSession();
    mainC = session.getMainC();
    process = session.getProcess();
    version = session.getVersion();
    params = session.getParams();

    LogAnimationService logAnimationService =
        (LogAnimationService) session.get("logAnimationService");

    Map<String, Object> param = new HashMap<>();
    try {
      String bpmnXML = (String) session.get("bpmnXML");
      String title = null;
      PluginMessages pluginMessages = null;
      if (bpmnXML == null) {
        title = editSession.getProcessName() + " (" + editSession.getNativeType() + ")";
        this.setTitle(title);

        ExportFormatResultType exportResult1 = mainC.getManagerService().exportFormat(
            editSession.getProcessId(), editSession.getProcessName(),
            editSession.getOriginalBranchName(), editSession.getCurrentVersionNumber(),
            editSession.getNativeType(), editSession.getUsername());

        title = editSession.getProcessName();
        pluginMessages = exportResult1.getMessage();

        bpmnXML = StreamUtil.convertStreamToString(exportResult1.getNative().getInputStream());

        param.put("bpmnXML", escapeQuotedJavascript(bpmnXML));
        param.put("url", getURL(editSession.getNativeType()));
        param.put("importPath", getImportPath(editSession.getNativeType()));
        param.put("exportPath", getExportPath(editSession.getNativeType()));
        param.put("editor", "bpmneditor");

        if (editSession.getAnnotation() == null) {
          param.put("doAutoLayout", "true");
        } else if (process.getOriginalNativeType() != null
            && process.getOriginalNativeType().equals(editSession.getNativeType())) {
          param.put("doAutoLayout", "false");
        } else {
          if (editSession.isWithAnnotation()) {
            param.put("doAutoLayout", "false");
          } else {
            param.put("doAutoLayout", "true");
          }
        }
      } else {
        param.put("bpmnXML", escapeQuotedJavascript(bpmnXML));
        param.put("url", getURL("BPMN 2.0"));
        param.put("importPath", getImportPath("BPMN 2.0"));
        param.put("exportPath", getExportPath("BPMN 2.0"));
        param.put("editor", "bpmneditor");
        param.put("doAutoLayout", "true");
      }

      String animationData = (String) session.get("animationData");
      if (animationData == null) {
        if (logAnimationService != null) { // logAnimationService is null if invoked from the editor
                                           // toobar
          List<LogAnimationService.Log> logs = (List<LogAnimationService.Log>) session.get("logs");
          animationData = logAnimationService.createAnimation(bpmnXML, logs);
          param.put("animationData", escapeQuotedJavascript(animationData));
        }
      } else {
        param.put("animationData", escapeQuotedJavascript(animationData));
      }

      this.setTitle(title);
      if (mainC != null) {
        mainC.showPluginMessages(pluginMessages);
      }

      // We're not expecting any request parameters, so warn if we see any
      for (RequestParameterType<?> requestParameter : params) {
        switch (requestParameter.getId()) {
          default:
            LOGGER.warn("Unsupported request parameter \"" + requestParameter.getId()
                + "\" with value " + requestParameter.getValue());
        }
      }

      List<EditorPlugin> editorPlugins = (List<EditorPlugin>) SpringUtil.getBean("editorPlugins");
      // List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("editorPluginsBPMN");
      param.put("plugins", editorPlugins);

      Executions.getCurrent().pushArg(param);

    } catch (Exception e) {
      LOGGER.error("", e);
      e.printStackTrace();
    }

    this.addEventListener("onSave", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        try {
          // new SaveAsDialogController(process, version, editSession, true, eventToString(event));
          // mainC.saveModel(process, version, editSession, true, eventToString(event));
        } catch (Exception e) {
          LOGGER.error("Error saving model.", e);
        }
      }
    });
    this.addEventListener("onSaveAs", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        try {
          // new SaveAsDialogController(process, version, editSession, false, eventToString(event));
          // mainC.saveModel(process, version, editSession, false, eventToString(event));
        } catch (Exception e) {
          LOGGER.error("Error saving model.", e);
        }
      }
    });


  }

  /*
   * Once the constructor has been executed, the onCreate method will be called from the ZUL file.
   * The ZK Desktop can only be active inside the onCreate method once the ZUL file has been
   * processed by ZK.
   */
  public void onCreate(Component comp) throws InterruptedException {
    // Store objects to be cleaned up when the session timeouts
    Desktop desktop = comp.getDesktop();
    desktop.setAttribute("pluginSessionId", pluginSessionId);
  }

  /**
   * @param json
   * @return the <var>json</var> escaped so that it can be quoted in Javascript. Specifically, it
   *         replaces apostrophes with \\u0027 and removes embedded newlines and leading and
   *         trailing whitespace.
   */
  private String escapeQuotedJavascript(String json) {
    return json.replace("\n", " ").replace("'", "\\u0027").trim();
  }

  /**
   * YAWL models package their event data as an array of {@link String}s, EPML packages it as a
   * {@link String}; this function hides the difference.
   *
   * @param event ZK event
   * @throws RuntimeException if the data associated with <var>event</var> is neither a
   *         {@link String} nor an array of {@link String}s
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

  @Override
  public void doAfterCompose(Component comp) throws Exception {
    onCreate(comp);

  }

}
