/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
// import org.apromore.portal.context.EditorPluginResolver;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionManager;
import org.apromore.service.loganimation.LogAnimationService2;
import org.apromore.service.loganimation.modelmapping.OldBpmnModelMapping;
import org.apromore.service.loganimation.recording.AnimationContext;
import org.apromore.service.loganimation.recording.AnimationIndex;
import org.apromore.service.loganimation.recording.FrameRecorder;
import org.apromore.service.loganimation.recording.ModelMapping;
import org.apromore.service.loganimation.recording.Movie;
import org.apromore.service.loganimation.recording.Stats;
import org.apromore.service.loganimation.replay.AnimationLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Messagebox;
import de.hpi.bpmn2_0.model.Definitions;

/**
 * Java bound to the <code>animateLogInSignavio.zul</code> ZUML document.
 */
public class LogAnimationController extends BaseController implements Composer<Component> {

  private static final Logger LOGGER =
      PortalLoggerFactory.getLogger(LogAnimationController.class.getCanonicalName());
  private String pluginSessionId;
  private String pluginExecutionId = "";
  private AnimationContext animateContext;
  private Movie animationMovie;
  private final String BPMN_EDITOR_KEY = "bpmneditor";
  private MainController mainController;

  public LogAnimationController() {

    pluginSessionId = Executions.getCurrent().getParameter("id");
    if (pluginSessionId == null) {
      throw new AssertionError("No id parameter in URL");
    }

    ApromoreSession session = UserSessionManager.getEditSession(pluginSessionId);
    if (session == null) {
      throw new AssertionError(
          "Your session has expired. Please close this browser tab and refresh the Portal tab");
    }

    Map<String, Object> pageParams = new HashMap<>();
    try {
      // Set the title of the page
      List<LogAnimationService2.Log> logs = (List<LogAnimationService2.Log>) session.get("logs");
      String title = "";
      for (LogAnimationService2.Log log : logs) {
        title += log.fileName + " | ";
      }
      this.setTitle(title.substring(0, title.length() - 3));

      List<AnimationLog> animationLogs = (List<AnimationLog>) session.get("animationLogs");
      animateContext = new AnimationContext(animationLogs);
      ModelMapping modelMapping = new OldBpmnModelMapping((Definitions) session.get("model"));

      long timer = System.currentTimeMillis();
      List<AnimationIndex> animationIndexes = new ArrayList<>();
      JSONArray logStartFrameIndexes = new JSONArray();
      JSONArray logEndFrameIndexes = new JSONArray();
      for (AnimationLog log : animationLogs) {
        animationIndexes.add(new AnimationIndex(log, modelMapping, animateContext));
        logStartFrameIndexes
            .put(animateContext.getFrameIndexFromLogTimestamp(log.getStartDate().getMillis()));
        logEndFrameIndexes
            .put(animateContext.getFrameIndexFromLogTimestamp(log.getEndDate().getMillis()));
      }
      LOGGER.debug(
          "Create animation index: " + (System.currentTimeMillis() - timer) / 1000 + " seconds.");

      LOGGER.debug("Start recording frames");
      timer = System.currentTimeMillis();
      animationMovie = FrameRecorder.record(animationIndexes, animateContext);
      LOGGER.debug("Finished recording frames: " + (System.currentTimeMillis() - timer) / 1000
          + " seconds.");

      JSONObject setupData = (JSONObject) session.get("setupData");
      setupData.put("recordingFrameRate", animateContext.getRecordingFrameRate());
      setupData.put("recordingDuration", animateContext.getRecordingDuration());
      setupData.put("logStartFrameIndexes", logStartFrameIndexes);
      setupData.put("logEndFrameIndexes", logEndFrameIndexes);
      setupData.put("elementIndexIDMap", modelMapping.getElementJSON());
      setupData.put("caseCountsByFrames", Stats.computeCaseCountsJSON(animationMovie));

      // Create page parameters, these parameters will be injected into the ZUL file by ZK
      pluginExecutionId = PluginExecutionManager.registerPluginExecution(new PluginExecution(this),
          Sessions.getCurrent());
      pageParams.put("bpmnXML", escapeQuotedJavascript((String) session.get("bpmnXML")));
      pageParams.put("url", getURL("BPMN 2.0"));
      // pageParams.put("importPath", mainController.getImportPath("BPMN 2.0"));
      // pageParams.put("exportPath", mainController.getExportPath("BPMN 2.0"));
      pageParams.put("editor", BPMN_EDITOR_KEY);
      pageParams.put("doAutoLayout", "false");
      pageParams.put("setupData", escapeQuotedJavascript(setupData.toString()));
      pageParams.put("pluginExecutionId", pluginExecutionId);
      Executions.getCurrent().pushArg(pageParams);

    } catch (Exception e) {
      Messagebox.show(e.getMessage());
      LOGGER.error(e.getMessage());
    }

    this.addEventListener("onFrameSkipChanged", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        try {
          animateContext.setFrameSkip(Integer.valueOf((event.getData().toString())));
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
    init(comp);

    Desktop desktop = comp.getDesktop();

    desktop.setAttribute("pluginSessionId", pluginSessionId);
  }

  @Override
  public String processRequest(Map<String, String[]> parameterMap) {
    String startFrameIndex = parameterMap.get("startFrameIndex")[0];
    String chunkSize = parameterMap.get("chunkSize")[0];
    try {
      String chunkJSON = animationMovie
          .getChunkJSON(Integer.parseInt(startFrameIndex), Integer.parseInt(chunkSize)).toString();
      return escapeQuotedJavascript(chunkJSON);
    } catch (NumberFormatException | JSONException e) {
      return "Error: " + e.getMessage();
    }
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

  @Override
  public void doAfterCompose(Component comp) throws Exception {
    onCreate(comp);

  }
}
