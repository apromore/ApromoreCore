/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.dao.model.User;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.EditorPluginResolver;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.util.StreamUtil;
import org.apromore.util.AccessType;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Messagebox;

/**
 * ApromoreSession and ApromoreSession.EditSessionType represent data objects of the model being
 * opened in the editor However, they don't contain the XML native model data which can be retrieved
 * from the editor Remember to update these data objects after every action on the model to keep it
 * in a consistent state. For example, after save as a new model, these data objects must be updated
 * to the new model info.
 * 
 * @todo there is a duplication between ApromoreSession and EditSessionType, they need to be clean
 *       later.
 *
 * @todo avoid thread conflict issues when setting instance data for BIMPPortalPlugin, instead it
 *       should be passed as a method parameter.
 * 
 * @author Bruce Nguyen
 *
 */
public class BPMNEditorController extends BaseController implements Composer<Component> {
  public static final String EVENT_MESSAGE_SAVE = "SaveEvent";

  private static final boolean USE_BPMNIO_MODELER = true;
  private static final String BPMNIO_MODELER_JS = "bpmn-modeler.development.js";
  private static final String BPMNIO_VIEWER_JS = "bpmn-navigated-viewer.development.js";

  private static final Logger LOGGER = PortalLoggerFactory.getLogger(BPMNEditorController.class);
  public static final String BPMN_XML = "bpmnXML";
  public static final String BPMN_2_01 = "BPMN 2.0";
  private EventQueue<Event> qeBPMNEditor =
      EventQueues.lookup(Constants.EVENT_QUEUE_BPMN_EDITOR, EventQueues.DESKTOP, true);

  private MainController mainC;
  private ApromoreSession session;
  private EditSessionType editSession;
  private ProcessSummaryType process;
  private VersionSummaryType vst;
  boolean isNewProcess = false;
  private UserType currentUserType;
  private AccessType currentUserAccessType;

  public BPMNEditorController() {
    super();
    currentUserType = UserSessionManager.getCurrentUser();
    if (currentUserType == null) {
      throw new AssertionError("Cannot open the editor without any login user!");
    }

    String id = Executions.getCurrent().getParameter("id");
    if (id == null) {
      throw new AssertionError("No id parameter in URL");
    }
    session = UserSessionManager.getEditSession(id);
    if (session == null) {
      // throw new AssertionError("No edit session associated with id " + id);
      throw new AssertionError(
          "Your session has expired. Please close this browser tab and refresh the Portal tab");
    }
    isNewProcess = Boolean.valueOf(Executions.getCurrent().getParameter("newProcess"));
    editSession = session.getEditSession();
    mainC = session.getMainC();
    process = session.getProcess();
    vst = session.getVersion();

    if (isNewProcess) {
      currentUserAccessType = AccessType.OWNER;
    } else {
      try {
        User user = mainC.getSecurityService().getUserById(currentUserType.getId());
        currentUserAccessType =
            mainC.getAuthorizationService().getProcessAccessTypeByUser(process.getId(), user);
      } catch (Exception e) {
        // currentUserAccessType = AccessType.VIEWER;
        currentUserAccessType = null;
      }
      if (currentUserAccessType == null) {
        throw new AssertionError("No valid access type for the current user");
      }
    }
    if (AccessType.VIEWER.equals(currentUserAccessType)) {
      Clients.evalJavaScript("Ap.common.injectGlobalClass(\"access-type-viewer\")");
    }

    Map<String, Object> param = new HashMap<>();
    try {
      PluginMessages pluginMessages = null;
      String bpmnXML = (String) session.get(BPMN_XML);

      if (bpmnXML == null) {
        if (isNewProcess) {
          bpmnXML = "<?xml version='1.0' encoding='UTF-8'?>"
              + "<bpmn:definitions xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
              + "xmlns:bpmn='http://www.omg.org/spec/BPMN/20100524/MODEL' "
              + "xmlns:bpmndi='http://www.omg.org/spec/BPMN/20100524/DI' "
              + "xmlns:dc='http://www.omg.org/spec/DD/20100524/DC' "
              + "targetNamespace='http://bpmn.io/schema/bpmn' " + "id='Definitions_1'>"
              + "<bpmn:process id='Process_1' isExecutable='false'>"
              + "<bpmn:startEvent id='StartEvent_1'/>" + "</bpmn:process>"
              + "<bpmndi:BPMNDiagram id='BPMNDiagram_1'>"
              + "<bpmndi:BPMNPlane id='BPMNPlane_1' bpmnElement='Process_1'>"
              + "<bpmndi:BPMNShape id='_BPMNShape_StartEvent_2' bpmnElement='StartEvent_1'>"
              + "<dc:Bounds height='36.0' width='36.0' x='173.0' y='102.0'/>"
              + "</bpmndi:BPMNShape>" + "</bpmndi:BPMNPlane>" + "</bpmndi:BPMNDiagram>"
              + "</bpmn:definitions>";
        } 
        else {
          // Note: process models created by merging are not BPMN, cannot use
          // processService.getBPMNRepresentation
          ExportFormatResultType exportResult = mainC.getManagerService().exportFormat(
              editSession.getProcessId(), editSession.getProcessName(),
              editSession.getOriginalBranchName(), editSession.getCurrentVersionNumber(),
              editSession.getNativeType(), editSession.getUsername());
          bpmnXML = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());
          param.put("doAutoLayout", "false");
        }

        param.put(BPMN_XML, escapeXML(bpmnXML));
        param.put("url", getURL(editSession.getNativeType()));
        param.put("importPath", mainC.getImportPath(editSession.getNativeType()));
        param.put("exportPath", mainC.getExportPath(editSession.getNativeType()));
        param.put("editor", "bpmneditor");
      } else {
        param.put(BPMN_XML, bpmnXML);
        param.put("url", getURL(BPMN_2_01));
        param.put("importPath", getImportPath(BPMN_2_01));
        param.put("exportPath", getExportPath(BPMN_2_01));
        param.put("editor", "bpmneditor");
        param.put("doAutoLayout", "false");
      }

      this.setTitle(
          editSession.getProcessName() + " (" + "v" + editSession.getCurrentVersionNumber() + ")");

      if (mainC != null) {
        mainC.showPluginMessages(pluginMessages);
      }
      String langTag = mainC.getI18nSession().getPreferredLangTag();
      List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("bpmnEditorPlugins");
      param.put("plugins", editorPlugins);
      param.put("langTag", langTag);
      if (USE_BPMNIO_MODELER) {
        param.put("bpmnioLib", BPMNIO_MODELER_JS);
      } else {
        param.put("bpmnioLib", AccessType.VIEWER.equals(currentUserAccessType) ? BPMNIO_VIEWER_JS : BPMNIO_MODELER_JS);
      }
      param.put("viewOnly", AccessType.VIEWER.equals(currentUserAccessType));
      PortalPlugin simulatePortalPlugin = mainC.getPortalPluginMap().get(PluginCatalog.PLUGIN_SIMULATE_MODEL);
      param.put("availableSimulateModelPlugin", simulatePortalPlugin != null &&
              simulatePortalPlugin.getAvailability() == PortalPlugin.Availability.AVAILABLE);
      Executions.getCurrent().pushArg(param);

    } catch (Exception e) {
      LOGGER.error("", e);
      e.printStackTrace();
    }

    this.addEventListener("onSave", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        if (currentUserAccessType == AccessType.VIEWER) {
          Notification.error(Labels.getLabel("portal_noPrivilegeSaveEdit_message"));
          return;
        }
        if (isNewProcess) {
          new SaveAsDialogController(process, vst, session, false, eventToString(event), mainC);
        } else {
          new SaveAsDialogController(process, vst, session, true, eventToString(event), mainC);
        }
      }
    });

    this.addEventListener("onSaveAs", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        if (currentUserAccessType == AccessType.VIEWER) {
          Notification.error(Labels.getLabel("portal_noPrivilegeSaveEdit_message"));
          return;
        }
        new SaveAsDialogController(process, vst, session, false, eventToString(event), mainC);
      }
    });

    this.addEventListener("onSimulateModel", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        PortalContext portalContext = mainC.getPortalContext();
        Map<String, PortalPlugin> portalPluginMap = portalContext.getPortalPluginMap();
        PortalPlugin simulateModelPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_SIMULATE_MODEL);

        //Since simulate model is an EE feature, it may not be available
        if (simulateModelPlugin == null) {
          Messagebox.show(Labels.getLabel("portal_simModelUnavailable_message"),
                  Labels.getLabel("portal_simModelUnavailable_title"),
                  Messagebox.OK, Messagebox.INFORMATION);
          return;
        }

        if (currentUserAccessType == AccessType.VIEWER) {
          Notification.error(Labels.getLabel("portal_noPrivilegeSaveEdit_message"));
          return;
        }

        Map arg = new HashMap<>();
        arg.put("selectedModel", process);
        arg.put("modelData", eventToString(event));
        simulateModelPlugin.setSimpleParams(arg);
        simulateModelPlugin.execute(portalContext);
      }
    });

    this.addEventListener("onShare", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        PortalPlugin accessControlPlugin;
        if (currentUserAccessType != AccessType.OWNER) {
          Notification.error(Labels.getLabel("portal_noPrivilegeShare_message"));
          return;
        }

        if (isNewProcess || process == null) {
          Notification.error(Labels.getLabel("portal_saveModelFirst_message"));
        } else {
          PortalContext portalContext = mainC.getPortalContext();
          try {
            Map<String, PortalPlugin> portalPluginMap = portalContext.getPortalPluginMap();
            Object selectedItem = process;
            accessControlPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_ACCESS_CONTROL);
            Map arg = new HashMap<>();
            arg.put("withFolderTree", false);
            arg.put("selectedItem", selectedItem);
            arg.put("currentUser", UserSessionManager.getCurrentUser());
            arg.put("autoInherit", true);
            arg.put("showRelatedArtifacts", true);
            arg.put("enablePublish", mainC.getConfig().isEnablePublish());
            accessControlPlugin.setSimpleParams(arg);
            accessControlPlugin.execute(portalContext);
          } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Apromore", Messagebox.OK, Messagebox.ERROR);
          }
        }
      }
    });

    BPMNEditorController editorController = this;
    qeBPMNEditor.subscribe(new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        if (EVENT_MESSAGE_SAVE.equals(event.getName())) {
          String[] data = (String[]) event.getData();
          setTitle(data[0], data[1]);
          process = session.getProcess();
          editorController.isNewProcess = false;
        }
      }
    });
  }

  private void setTitle(String processName, String versionNumber) {
    this.setTitle(processName + " (" + "v" + versionNumber + ")");
  }

  /**
   * @param xml
   * @return the <var>json</var> escaped so that it can be quoted in Javascript. Specifically, it
   *         replaces apostrophes with \\u0027 and removes embedded newlines and leading and
   *         trailing whitespace.
   */
  private String escapeXML(String xml) {
    return xml.replaceAll("(\\r|\\n|\\r\\n)+", " ").replace("'", "\\'");
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
	// TODO Auto-generated method stub
	
}

}
