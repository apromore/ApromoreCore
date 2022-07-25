/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.i18n.I18nConfig;
import org.apromore.portal.common.i18n.I18nSession;
import org.apromore.portal.context.EditorPluginResolver;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.helper.Version;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.ProcessPublishService;
import org.apromore.service.ProcessService;
import org.apromore.util.AccessType;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import static org.apromore.common.Constants.DRAFT_BRANCH_NAME;
import static org.apromore.common.Constants.TRUNK_NAME;
import static org.apromore.plugin.portal.PortalContexts.getPageDefinition;
import static org.apromore.portal.common.LabelConstants.MESSAGEBOX_DEFAULT_TITLE;

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
  private static final String BPMN_CURRENT_EDITOR_ID_LIST = "BPMN_CURRENT_EDITOR_ID_LIST";

  private static final Logger LOGGER = PortalLoggerFactory.getLogger(BPMNEditorController.class);
  public static final String BPMN_XML = "bpmnXML";
  public static final String PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY = "portal_saveModelFirst_message";
  private static final String PROCESS_SERVICE_BEAN = "processService";
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
  private boolean isViewLink = false;
  @Wire
  private Popup popup;

  public BPMNEditorController() {
    super();

    currentUserType = UserSessionManager.getCurrentUser();
    if (currentUserType == null) {
      throw new AssertionError("Cannot open the editor without any login user!");
    }

    isViewLink = Boolean.valueOf(Executions.getCurrent().getParameter("view"));
    if (isViewLink) {
      openViewLink();
      return;
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

    Map<String, Object> param = new HashMap<>();
    if (isEditorIdExistInCurrentSession(id)) {
      isNewProcess = false;
    }

    if (isNewProcess) {
      currentUserAccessType = AccessType.OWNER;
      param.put("isNewProcess", true);
    } else {
      param.put("isNewProcess", false);
      try {
        User user = mainC.getSecurityService().getUserById(currentUserType.getId());
        currentUserAccessType = mainC.getAuthorizationService().getProcessAccessTypeByUser(process.getId(), user);
        UserType userType = mainC.getPortalContext().getCurrentUser();

        if (currentUserAccessType != null && !userType.hasAnyPermission(PermissionType.MODEL_EDIT)
                && userType.hasAnyPermission(PermissionType.MODEL_VIEW)) {
          currentUserAccessType = AccessType.VIEWER;
        } else if (!userType.hasAnyPermission(PermissionType.MODEL_EDIT)) {
          currentUserAccessType = null;
        }

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

    try {
      PluginMessages pluginMessages = null;
      String bpmnXML = (String) session.get(BPMN_XML);

      if (bpmnXML == null) {

        // Note: process models created by merging are not BPMN, cannot use
        // processService.getBPMNRepresentation
        ExportFormatResultType exportResult = mainC.getManagerService().exportFormat(editSession.getProcessId(),
                editSession.getProcessName(), TRUNK_NAME, editSession.getCurrentVersionNumber(),
                editSession.getNativeType(), editSession.getUsername());
        bpmnXML = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());

        ExportFormatResultType exportResultDraft = mainC.getManagerService().exportFormat(editSession.getProcessId(),
                editSession.getProcessName(), DRAFT_BRANCH_NAME, editSession.getCurrentVersionNumber(),
                editSession.getNativeType(), editSession.getUsername());
        String bpmnXmlDraft = StreamUtil.convertStreamToString(exportResultDraft.getNative().getInputStream());

        param.put("doAutoLayout", "false");

        param.put(BPMN_XML, AccessType.VIEWER.equals(currentUserAccessType) ? escapeXML(bpmnXML) :
                escapeXML(bpmnXmlDraft));
        param.put("url", getURL(editSession.getNativeType()));
        param.put("importPath", mainC.getImportPath(editSession.getNativeType()));
        param.put("exportPath", mainC.getExportPath(editSession.getNativeType()));
        param.put("editor", "bpmneditor");
      } else {
        param.put(BPMN_XML, escapeXML(bpmnXML));
        param.put("url", getURL(BPMN_2_0));
        param.put("importPath", getImportPath(BPMN_2_0));
        param.put("exportPath", getExportPath(BPMN_2_0));
        param.put("editor", "bpmneditor");
        param.put("doAutoLayout", "false");
      }

      param.put("nativeType", process.getOriginalNativeType());

      String title = editSession.getProcessName() + " (" + "v" + editSession.getCurrentVersionNumber() + ")";
      this.setTitle(title);
      this.setClientAttribute("title", title);
      if (mainC != null) {
        mainC.showPluginMessages(pluginMessages);
      }

      String langTag = mainC.getI18nSession().getPreferredLangTag();
      List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("bpmnEditorPlugins");
      param.put("plugins", editorPlugins);
      param.put("langTag", langTag);
      param.put("username", currentUserType.getUsername());
      param.put("processName", editSession.getProcessName());
      if (USE_BPMNIO_MODELER) {
        param.put("bpmnioLib", BPMNIO_MODELER_JS);
      } else {
        param.put("bpmnioLib", AccessType.VIEWER.equals(currentUserAccessType) ? BPMNIO_VIEWER_JS : BPMNIO_MODELER_JS);
      }
      param.put("viewOnly", AccessType.VIEWER.equals(currentUserAccessType));
      PortalPlugin simulatePortalPlugin = mainC.getPortalPluginMap().get(PluginCatalog.PLUGIN_SIMULATE_MODEL);
      param.put("availableSimulateModelPlugin", simulatePortalPlugin != null &&
              simulatePortalPlugin.getAvailability() == PortalPlugin.Availability.AVAILABLE);
      PortalPlugin processPublishPlugin = mainC.getPortalPluginMap().get(PluginCatalog.PLUGIN_PUBLISH_MODEL);
      param.put("availablePublishModelPlugin", processPublishPlugin != null
          && processPublishPlugin.getAvailability() == PortalPlugin.Availability.AVAILABLE);
      param.put("isPublished", isProcessPublished());
      Executions.getCurrent().pushArg(param);

      populateLinkedProcessesList();
      cacheCurrentEditorId(id);

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
          new SaveAsDialogController(process, vst, session, null, eventToString(event), mainC);
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

    this.addEventListener("onForceSave", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws InterruptedException {
        Map<String, Object> arg = (Map<String, Object>) event.getData();
        String xml = arg.get("xml").toString();
        String flowOnEvent = arg.get("flowOnEvent").toString();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        saveCurrentModelVersion(
          editSession.getProcessId(),
          editSession.getProcessName(),
          editSession.getCurrentVersionNumber(),
          editSession.getNativeType(),
          is,
          editSession.getUsername()
        );
        Notification.info(
          MessageFormat.format(
            Labels.getLabel("bpmnEditor_afterSave_message"),
            editSession.getProcessName(),
            editSession.getCurrentVersionNumber()
          )
        );
        callAfterCheckUnsaved(flowOnEvent);
      }
    });

    this.addEventListener("onCheckUnsaved", (Event event) -> {
      String flowOnEvent = (String) event.getData();
      if (isNewProcess) {
        Messagebox.show(
          Labels.getLabel("bpmnEditor_mustSave_message"),
          Labels.getLabel(MESSAGEBOX_DEFAULT_TITLE),
          Messagebox.OK,
          Messagebox.EXCLAMATION
        );
        return;
      }
      boolean isUpToDate = mainC.getManagerService().isProcessUpdatedWithUserDraft(
          editSession.getProcessId(),
          editSession.getProcessName(),
          editSession.getCurrentVersionNumber(),
          editSession.getNativeType(),
          editSession.getUsername()
      );
      if (isUpToDate) {
        callAfterCheckUnsaved(flowOnEvent);
      } else {
        Messagebox.show(
          Labels.getLabel("bpmnEditor_unsavedChanges_message"),
          Labels.getLabel(MESSAGEBOX_DEFAULT_TITLE),
          Messagebox.OK | Messagebox.CANCEL,
          Messagebox.QUESTION,
          (Event e) -> {
            if (Messagebox.ON_OK.equals(e.getName())) {
              callAfterCheckUnsaved(flowOnEvent);
            }
          }
        );
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
          Notification.error(Labels.getLabel(PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY));
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
            Messagebox.show(e.getMessage(), Labels.getLabel(MESSAGEBOX_DEFAULT_TITLE), Messagebox.OK, Messagebox.ERROR);
          }
        }
      }
    });

    this.addEventListener("onPublishModel", event -> {
      if (isNewProcess || process == null) {
        Notification.error(Labels.getLabel(PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY));
        return;
      }

      PortalContext portalContext = mainC.getPortalContext();
      Map<String, PortalPlugin> portalPluginMap = portalContext.getPortalPluginMap();
      PortalPlugin publishModelPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_PUBLISH_MODEL);

      Map<String, Object> arg = new HashMap<>();
      arg.put("selectedModel", process);
      publishModelPlugin.setSimpleParams(arg);
      publishModelPlugin.execute(portalContext);
    });

    this.addEventListener("onClickSubprocessBtn", event -> {
      if (isNewProcess || process == null) {
        Notification.error(Labels.getLabel(PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY));
        return;
      }

      boolean isViewer = AccessType.VIEWER.equals(currentUserAccessType);
      String elementId =  (String) event.getData();

      ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);
      ProcessSummaryType linkedProcess = processService.getLinkedProcess(process.getId(), elementId);
      User user = mainC.getSecurityService().getUserById(currentUserType.getId());
      boolean hasLinkedProcessAccess = (linkedProcess != null) &&
          (mainC.getAuthorizationService().getProcessAccessTypeByUser(linkedProcess.getId(), user) != null);

      if (isViewer && !hasLinkedProcessAccess) {
        Notification.error(Labels.getLabel("bpmnEditor_subProcessLinkNoEdit_message",
            "Only owner/editor and add or edit a link"));
      } else if (hasLinkedProcessAccess) {
        viewLinkedSubprocess(elementId);
      } else {
        linkSubprocess(elementId);
      }
    });

    this.addEventListener("onViewSubprocess", event -> {
      String elementId =  (String) event.getData();
      viewLinkedSubprocess(elementId);
    });

    this.addEventListener("onLinkSubprocess", event -> {
      String elementId =  (String) event.getData();
      linkSubprocess(elementId);
    });

    this.addEventListener("onDeleteSubprocess", event -> {
      String elementId =  (String) event.getData();
      unlinkSubprocess(elementId);
    });

    this.addEventListener("onUnlinkSubprocess", event -> {
      String elementId =  (String) event.getData();
      unlinkSubprocess(elementId);
      Notification.info("Process successfully unlinked");
    });

    this.addEventListener("onDownloadXML", event -> {
      String xml = (String) event.getData();
      //Show window if there is a linked subprocess. Otherwise, download.
      ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);
      if (process == null || !processService.hasLinkedProcesses(process.getId(), currentUserType.getUsername())) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        Filedownload.save(is, "text/xml", getProcessName() + ".bpmn");
      } else {
        Map<String, Object> args = new HashMap<>();
        args.put("process", process);
        args.put("version", vst);
        Window downloadBPMNPrompt = (Window) Executions.createComponents(
            getPageDefinition("static/bpmneditor/downloadBPMN.zul"), null, args);
        downloadBPMNPrompt.doModal();
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
          Clients.evalJavaScript("Apromore.BPMNEditor.afterSave()");
        }
      }
    });

    this.addEventListener("onChangeFont", event -> {
      Component win = event.getTarget();
      popup=(Popup)win.getFellow("popup");
      popup.open(event.getTarget(),"at_pointer");
    });

  }

  private String getProcessName() {
    return StringUtils.defaultIfEmpty(editSession.getProcessName(), "untitled");
  }

  private void cacheCurrentEditorId(String editorId) {
    Session currentSession = Executions.getCurrent().getSession();
    if (currentSession.getAttribute(BPMN_CURRENT_EDITOR_ID_LIST) != null) {
      Set<String> currentBpmnEditorIds = (Set<String>) currentSession.getAttribute(BPMN_CURRENT_EDITOR_ID_LIST);
      currentBpmnEditorIds.add(editorId);
      currentSession.setAttribute(BPMN_CURRENT_EDITOR_ID_LIST, currentBpmnEditorIds);
    } else {
      Set<String> currentBpmnEditorIds = new HashSet<>();
      currentBpmnEditorIds.add(editorId);
      currentSession.setAttribute(BPMN_CURRENT_EDITOR_ID_LIST, currentBpmnEditorIds);
    }
  }

  private boolean isEditorIdExistInCurrentSession(String editorId) {
    Session currentSession = Executions.getCurrent().getSession();
    if (currentSession.getAttribute(BPMN_CURRENT_EDITOR_ID_LIST) != null) {
      Set<String> currentBpmnEditorIds = (Set<String>) currentSession.getAttribute(BPMN_CURRENT_EDITOR_ID_LIST);
      return currentBpmnEditorIds != null && currentBpmnEditorIds.contains(editorId);
    } else {
      return false;
    }
  }

  private void saveCurrentModelVersion(Integer processId, String processName, String versionNumber,
                                       String nativeType, InputStream nativeStream, String userName) {
    try {
      String bpmnXml = new String(nativeStream.readAllBytes(), StandardCharsets.UTF_8);

      ProcessModelVersion newVersion = mainC.getManagerService().updateProcessModelVersion(
          processId, editSession.getOriginalBranchName(), versionNumber, userName, "", nativeType,
          new ByteArrayInputStream(bpmnXml.getBytes()));
      mainC.getManagerService().updateDraft(processId,
          editSession.getOriginalVersionNumber(), nativeType, new ByteArrayInputStream(bpmnXml.getBytes()),
          userName);
      editSession.setOriginalVersionNumber(versionNumber);
      editSession.setCurrentVersionNumber(versionNumber);
      editSession.setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setVersionNumber(versionNumber);

      qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null,
          new String[] {processName, versionNumber}));
    } catch (Exception e) {
      Messagebox.show(Labels.getLabel("portal_unableSave_message"), null, Messagebox.OK,
          Messagebox.ERROR);
    }
  }

  private void callAfterCheckUnsaved(String flowOnEvent) {
    Clients.evalJavaScript("Apromore.BPMNEditor.afterCheckUnsaved('" + flowOnEvent + "')");
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
    return xml
            .replaceAll("(\\r|\\n|\\r\\n)+", " ")
            // remove LSEP (line separator), that breaks XML code embedding in ZK's inline JS script
            .replaceAll("\u2028", " ")
            .replace("'", "\\'");
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

  private boolean isProcessPublished() {
    if (process == null) {
      return false;
    }
    ProcessPublishService processPublishService = (ProcessPublishService) SpringUtil.getBean("processPublishService");
    ProcessPublish publishDetails = processPublishService.getPublishDetails(process.getId());
    return publishDetails != null && publishDetails.isPublished();
  }

  private void openViewLink() {
    if (isViewLink) {
      String publishId = Executions.getCurrent().getParameter("publishId");
      ProcessPublishService processPublishService = (ProcessPublishService) SpringUtil.getBean("processPublishService");
      ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);

      //Check if link is published. If not, show an error.
      if (!processPublishService.isPublished(publishId)) {
        try {
          Executions.forward("./macros/invalidLink.zul");
          return;
        } catch (IOException e) {
          throw new AssertionError("This link is inactive");
        }
      }

      //Get process from publish id
      process = processPublishService.getSimpleProcessSummary(publishId);
      String nativeType = process.getOriginalNativeType();
      String version = process.getLastVersion();
      setTitle(process.getName(), process.getLastVersion());

      //get bpmnXML from process
      try {
        ExportFormatResultType exportResult = processService.exportProcess(
                process.getName(), process.getId(), "MAIN", new Version(version), nativeType, currentUserType.getUsername());
        String bpmnXML = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());
        pushViewModeParameters(bpmnXML);
      } catch (Exception e) {
        LOGGER.error("", e);
        throw new AssertionError("Could not get bpmn xml");
      }
    }
  }

  private void pushViewModeParameters(final String bpmnXML) {
    Clients.evalJavaScript("Ap.common.injectGlobalClass(\"access-type-viewer\")");
    Map<String, Object> param = new HashMap<>();

    param.put(BPMN_XML, escapeXML(bpmnXML));
    param.put("editor", "bpmneditor");
    List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve("bpmnEditorPlugins");
    param.put("plugins", editorPlugins);
    param.put("availableSimulateModelPlugin", false);
    param.put("availablePublishModelPlugin", false);
    param.put("bpmnioLib", BPMNIO_MODELER_JS);
    param.put("isPublished", true);
    param.put("viewOnly", true);
    param.put("langTag", getLanguageTag());
    param.put("doAutoLayout", "false");
    param.put("username", currentUserType.getUsername());
    param.put("isNewProcess", false);
    Executions.getCurrent().pushArg(param);
  }

  private String getLanguageTag() {
    I18nSession i18nSession = UserSessionManager.getCurrentI18nSession();

    if (i18nSession == null) {
      I18nConfig i18nConfig = (I18nConfig) SpringUtil.getBean("i18nConfig");
      i18nSession = new I18nSession(i18nConfig);
      UserSessionManager.setCurrentI18nSession(i18nSession);
      i18nSession.applyLocaleFromClient();
    }

    return i18nSession.getPreferredLangTag();
  }

  private void linkSubprocess(String elementId) throws IOException {
    if (isNewProcess || process == null) {
      Notification.error(Labels.getLabel(PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY));
      return;
    }

    boolean isViewer = AccessType.VIEWER.equals(currentUserAccessType);

    if (isViewer) {
      Notification.error(Labels.getLabel("bpmnEditor_subProcessLinkNoEdit_message",
          "Only owner/editor and add or edit a link"));
    } else {
      Map<String, Object> args = new HashMap<>();
      args.put("mainController", mainC);
      args.put("parentProcessId", process.getId());
      args.put("elementId", elementId);
      String linkProcessWindowPath = "static/bpmneditor/linkSubProcess.zul";
      for (Page page : Executions.getCurrent().getDesktop().getPages()) {
        if (page.getFellowIfAny("winLinkSubprocess") != null) {
          // DO Nothing
          return;
        }
      }
      Window linkSubProcessModal =
          (Window) Executions.createComponents(getPageDefinition(linkProcessWindowPath), null, args);
      linkSubProcessModal.doModal();
    }
  }

  private void viewLinkedSubprocess(String elementId) throws UserNotFoundException {
    if (isNewProcess || process == null) {
      Notification.error(Labels.getLabel(PORTAL_SAVE_MODEL_FIRST_MESSAGE_KEY));
      return;
    }

    ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);
    ProcessSummaryType linkedProcess = processService.getLinkedProcess(process.getId(), elementId);
    User user = mainC.getSecurityService().getUserById(currentUserType.getId());
    boolean hasLinkedProcessAccess = (linkedProcess != null) &&
        (mainC.getAuthorizationService().getProcessAccessTypeByUser(linkedProcess.getId(), user) != null);

    if (!hasLinkedProcessAccess) {
      Notification.error("No process is linked");
      return;
    }

    VersionSummaryType version = linkedProcess.getVersionSummaries().stream()
        .filter(v -> v.getVersionNumber().equals(linkedProcess.getLastVersion()))
        .findFirst().orElse(null);
    try {
      mainC.editProcess2(linkedProcess, version, linkedProcess.getOriginalNativeType(), new HashSet<>(), false);
    } catch (InterruptedException e) {
      Notification.error("Unable to view linked process");
      LOGGER.error("Unable to view linked process", e);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
  }

  private void unlinkSubprocess(String elementId) {
    if (isNewProcess || process == null) {
      //If the process isn't saved, there are no link details.
      return;
    }

    ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);
    processService.unlinkSubprocess(process.getId(), elementId);
  }

  private void populateLinkedProcessesList() throws UserNotFoundException {
    if (isNewProcess || process == null) {
      //If the process isn't saved, there are no link details.
      return;
    }

    ProcessService processService = (ProcessService) SpringUtil.getBean(PROCESS_SERVICE_BEAN);
    Map<String, Integer> linkedProcesses = processService.getLinkedProcesses(process.getId(), currentUserType.getUsername());

    for (String subProcessId : linkedProcesses.keySet()) {
      ProcessSummaryType linkedProcess = processService.getLinkedProcess(process.getId(), subProcessId);
      String linkedProcessName = linkedProcess.getName() + " (v" + linkedProcess.getLastVersion() + ")";

      Clients.evalJavaScript("setLinkedSubProcess('" + subProcessId + "','" + linkedProcessName + "');");
    }
  }

  @Override
  public void doAfterCompose(Component comp) throws Exception {
      // TODO Auto-generated method stub

  }



}
