/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

import static org.apromore.common.Constants.TRUNK_NAME;

import org.apromore.commons.config.ConfigBean;
import org.apromore.commons.item.ItemNameUtils;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.PortalSession;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.i18n.I18nConfig;
import org.apromore.portal.common.i18n.I18nSession;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.controller.SortMenuController;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.workspaceOptions.CopyAndPasteController;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.DomainsType;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.NativeTypesType;
import org.apromore.portal.model.PluginMessage;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.UsernamesType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.types.EventQueueTypes;
import org.apromore.portal.util.CostTable;
import org.apromore.portal.util.StreamUtil;
import org.apromore.zk.ApromoreDesktopCleanup;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.ext.Paginal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Main Controller for the whole application, most of the UI state is managed
 * here. It is automatically instantiated as index.zul is loaded!
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MainController extends BaseController implements MainControllerInterface, Composer<Component> {

    private static final long serialVersionUID = 5147685906484044300L;
    public static final String APROMORE = "Apromore";
    public static final String ON_CLICK = "onClick";

    private static MainController controller = null;
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(MainController.class);

    private static String encKey;

    private EventQueue<Event> qe;

    private static final String WELCOME_TEXT = "Welcome %s. Release notes (%s)"; // Welcome %s.

    private static final String KEY_ALIAS = "apseckey";
    private static final int KEY_COPY = 67;
    private static final int KEY_PASTE = 86;
    private static final int KEY_CUT = 88;
    private static final int KEY_CTRL_A_LO = 65;
    private static final int KEY_CTRL_A_BG = 97;

    private PortalContext portalContext;
    private MenuController menu;
    private SimpleSearchController simplesearch;
    private ShortMessageController shortmessageC;
    private BaseListboxController baseListboxController;
    private BaseDetailController baseDetailController;
    private NavigationController navigation;
    public Html breadCrumbs;
    public Tabbox tabBox;
    public Tab tabCrumbs;
    private Paginal pg;
    private String host;
    private PortalPlugin logVisualizerPlugin = null;
    public PortalSession portalSession;
    private Map<String, PortalPlugin> portalPluginMap;
    private I18nSession i18nSession;
    private LangChooserController langChooserController;
    private Component mainComponent;
    private CopyAndPasteController copyAndPasteController;

    public static MainController getController() {
        return controller;
    }

    public MainController() {
        setupLocale();
        portalSession = new PortalSession(this);

        qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
        portalSession = new PortalSession(this);

        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
    }

    private void setupLocale() {
        I18nConfig config = (I18nConfig) SpringUtil.getBean("i18nConfig");
        i18nSession = new I18nSession(config);
        UserSessionManager.setCurrentI18nSession(i18nSession);
        i18nSession.applyLocaleFromClient();
    }

    public I18nSession getI18nSession() {
        return i18nSession;
    }

    private void setupUserDynamically(final UserType userType) {
        LOGGER.debug("DYNAMICALLY Setting*** userType {} as currentUser", userType);
        UserSessionManager.setCurrentUser(userType);
        LOGGER.debug("DONE DYNAMICALLY setting*** userType {} as currentUser", userType);
    }

    /**
     * Unit test constructor.
     */
    public MainController(ConfigBean configBean) {
        super(configBean);

        portalSession = new PortalSession(this);
    }

    public PortalSession getPortalSession() {
        return portalSession;
    }

    public Map<String, PortalPlugin> getPortalPluginMap() {
        return portalPluginMap;
    }

    /**
     * onCreate is executed after the main window has been created it is responsible
     * for instantiating all necessary controllers (one for each window defined in
     * the interface) see description in index.zul
     *
     * @throws InterruptedException
     */
    public void onCreate(Component comp) throws InterruptedException {
        try {
            init(comp);
            loadProperties();
            this.mainComponent = comp;
            Window mainW = (Window) comp.getFellow("mainW");
            Hbox pagingandbuttons = (Hbox) mainW.getFellow("pagingandbuttons");

            Window shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
            this.breadCrumbs = (Html) mainW.getFellow("breadCrumbs");
            this.tabCrumbs = (Tab) mainW.getFellow("tabCrumbs");
            this.tabBox = (Tabbox) mainW.getFellow("tabbox");
            this.pg = (Paginal) mainW.getFellow("pg");
            Menupopup orderListItemPopup= (Menupopup) mainW.getFellow("orderListItemPopup");
            Button orderListSortBtn= (Button) mainW.getFellow("orderListSortBtn");
            SortMenuController sortMenuController=new SortMenuController(this, orderListItemPopup);
            orderListSortBtn.addEventListener(Events.ON_CLICK, sortMenuController::showSortMenu);

            this.shortmessageC = new ShortMessageController(shortmessageW);
            this.simplesearch = new SimpleSearchController(this, comp);
            this.portalContext = new PluginPortalContext(this);
            this.copyAndPasteController =
                    new CopyAndPasteController(this, UserSessionManager.getCurrentUser());

            this.navigation = new NavigationController(this, comp);

            Combobox langChooser = (Combobox) mainW.getFellow("langChooser");
            if (i18nSession.getConfig().isEnabled()) {
                langChooserController = new LangChooserController(langChooser, this);
                langChooserController.populate();
                langChooser.setVisible(true);
            } else {
                langChooser.setVisible(false);
            }

            controller = this;
            MainController self = this;

            // portalContext_ will be set from one place
            Sessions.getCurrent().setAttribute("portalContext_" + this.mainComponent.getDesktop().getId(), portalContext);
            //We are keeping it's own ID to retrieve the portal context from anywhere of the portal
            this.mainComponent.getDesktop().setAttribute("PORTAL_REF_ID",this.mainComponent.getDesktop().getId());
            this.mainComponent.getDesktop().addListener(new ApromoreDesktopCleanup());

            this.breadCrumbs.addEventListener("onSelectFolder", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    int selectedFolderId = Integer.parseInt(event.getData().toString());
                    self.selectBreadcrumFolder(selectedFolderId);
                    self.tabBox.setSelectedIndex(0);
                }
            });

            this.breadCrumbs.addEventListener("onReloadBreadcrumbs", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    self.reloadBreadcrumbs();
                }
            });

            if (qe == null) {
                qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
            }

            qe.subscribe(new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    switch (event.getName()) {
                        case Constants.EVENT_MESSAGE_SAVE:
                            clearProcessVersions();
                            reloadSummaries();
                            break;

                        case Constants.EVENT_QUEUE_REFRESH_SCREEN:
                            reloadSummaries();
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + event.getName());
                    }
                }
            });

            // Updates from Access Controller
            EventQueue<Event> eqAccessRight =
                EventQueues.lookup(EventQueueTypes.UPDATE_USERMETADATA, EventQueues.APPLICATION, true);
            eqAccessRight.subscribe((Event event) -> {
                if ("update_usermetadata".equals(event.getName())) {
                    String userName = UserSessionManager.getCurrentUser().getUsername();
                    String evUserName = (String) event.getData();
                    if (userName.equals(evUserName)) {
                        reloadSummaries();
                    }
                }
            });

            // Move this to constructor so that inner component can access
            // UserSessionManager data
            // UserSessionManager.initializeUser(getService(), config);
            switchToProcessSummaryView();
            pagingandbuttons.setVisible(true);

            mainW.addEventListener("onCtrlPress", new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws InterruptedException {
                    try {
                        Integer keycode = (Integer) event.getData();
                        switch (keycode) {
                            case KEY_COPY:
                                baseListboxController.copy();
                                break;
                            case KEY_PASTE:
                                baseListboxController.paste();
                                break;
                            case KEY_CUT:
                                baseListboxController.cut();
                                break;
                            case KEY_CTRL_A_LO:
                            case KEY_CTRL_A_BG:
                                baseListboxController.selectAll();
                                break;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Wrong Command Key", e);
                    }
                }
            });

            final String COST_KEY = "costTable";
            final String CURRENCY_KEY = "currency";
            mainW.addEventListener("onCostTableInit", e -> {
                String jsonString = (String)e.getData();
                Map<String, Double> costRates = new HashMap<>();
                String currency = "USD";
                if (jsonString != null) {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                    costRates = (Map<String, Double>) jsonObject.getOrDefault(COST_KEY, costRates);
                    currency = (String) jsonObject.getOrDefault(CURRENCY_KEY, currency);
                }

                Sessions.getCurrent().setAttribute(COST_KEY, CostTable.builder()
                    .currency(currency)
                    .costRates(costRates)
                    .build());
            });
            // force/explicit page titling due to an issue with ZK stale page title
            Executions.getCurrent().getDesktop().getFirstPage().setTitle(
                Labels.getLabel("brand_shortName") + " - " + Labels.getLabel("portal_title_text")
            );
        } catch (final Exception e) {
            LOGGER.error("Repository NOT available", e);

            e.printStackTrace();

            String message;
            if (e.getMessage() == null) {
                message = "Please contact your Apromore Administrator";
            } else {
                message = e.getMessage();
            }
            e.printStackTrace();
            Messagebox.show(Labels.getLabel("portal_repoUnvailable_message"), "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        }

    }

    // Bruce: Do not use Executions.sendRedirect as it does not work
    // for webapp bundles with different ZK execution env.
    public void refresh() {
        try {
            // Executions.sendRedirect(null);
            qe.publish(new Event(Constants.EVENT_QUEUE_REFRESH_SCREEN, null, Boolean.TRUE));
        } catch (NullPointerException e) {
            // The ZK documentation for sendRedirect claims that passing a null parameter is
            // allowed
            // https://www.zkoss.org/javadoc/latest/zk/org/zkoss/zk/ui/Executions.html#sendRedirect(java.lang.String)
            LOGGER.warn("ZK default redirection failed", e);
        }
    }

    public PortalContext getPortalContext() {
        return this.portalContext;
    }

    public void loadWorkspace() {
        loadWorkspace(true);
    }

    private void loadWorkspace(boolean loadTree) {
        String userId = UserSessionManager.getCurrentUser().getId();
        updateTabs(userId);
        // updateActions();

        if (loadTree) {
            this.loadTree();
        }

        reloadBreadcrumbs();
    }

    public void reloadBreadcrumbs() {
        String userId = UserSessionManager.getCurrentUser().getId();
        FolderType currentFolder = this.portalSession.getCurrentFolder();
        int currentParentFolderId = currentFolder == null || currentFolder.getId() == 0 ? 0 : currentFolder.getId();
        List<FolderType> folders = this.getManagerService().getSubFolders(userId, currentParentFolderId);
        if (currentFolder != null) {
            FolderType folder = currentFolder;
            folder.getFolders().clear();
            for (FolderType newFolder : folders) {
                folder.getFolders().add(newFolder);
            }
            this.portalSession.setCurrentFolder(currentFolder);
        }
    }

    private void loadTree() {
        List<FolderType> folders = this.getManagerService()
                .getWorkspaceFolderTree(UserSessionManager.getCurrentUser().getId());
        this.portalSession.setTree(folders);
        this.navigation.loadWorkspace();
    }

    public void currentFolderChanged() {
        navigation.currentFolderChanged();
    }

    /**
     * Display version processes in processSummaries: if isQueryResult, the query is
     * given by version of process
     *
     * @param processSummaries the list of process summaries to display
     * @param isQueryResult    is this from a query (simsearch, clustering, etc.)
     */
    public void displayProcessSummaries(final SummariesType processSummaries, final Boolean isQueryResult) {
        int folderId;

        if (isQueryResult) {
            clearProcessVersions();
        }
        FolderType currentFolder = this.portalSession.getCurrentFolder();
        if (currentFolder == null) {
            folderId = 0;
        } else {
            folderId = currentFolder.getId();
        }

        // TODO switch to process query result view
        switchToProcessSummaryView();
        List<FolderType> subFolders = getManagerService().getSubFolders(UserSessionManager.getCurrentUser().getId(),
                folderId);
        this.baseListboxController.displaySummaries(subFolders, processSummaries, isQueryResult);
    }

    public void displaySearchResult(final SummariesType summaries) {
        clearProcessVersions();
        switchToProcessSummaryView();
        this.baseListboxController.displaySummaries(new ArrayList<FolderType>(), summaries, true);
    }

    public void reloadSummaries() {
        this.simplesearch.clearSearches();
        switchToProcessSummaryView();
        pg.setActivePage(0);

        FolderType currentFolder = this.portalSession.getCurrentFolder();
        List<FolderType> subFolders = getManagerService().getSubFolders(UserSessionManager.getCurrentUser().getId(),
                currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.SummaryListModel model = this.baseListboxController.displaySummaries(subFolders,
                false);

        this.displayMessage(model.getSize() + " out of " + model.getTotalCount()
                + (model.getTotalCount() > 1 ? " elements." : " element."));

        loadWorkspace(true);
    }

    public void reloadSummaries2() {
        this.simplesearch.clearSearches();
        switchToProcessSummaryView();
        pg.setActivePage(0);

        FolderType currentFolder = this.portalSession.getCurrentFolder();
        List<FolderType> subFolders = getManagerService().getSubFolders(UserSessionManager.getCurrentUser().getId(),
                currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.SummaryListModel model = this.baseListboxController.displaySummaries(subFolders,
                false);

        this.displayMessage(model.getSize() + " out of " + model.getTotalCount()
                + (model.getTotalCount() > 1 ? " elements." : " element."));

        loadWorkspace(false);

        this.baseListboxController.getListBox().setFocus(true); //To handle event on empty list
    }

    public void reloadSummariesWithOpenTreeItems(List<Integer> folderIds) {
        this.reloadSummaries2(); //Reload without Tree
        List<FolderType> folders = this.getManagerService()
            .getWorkspaceFolderTree(UserSessionManager.getCurrentUser().getId());
        this.portalSession.setTree(folders);
        this.navigation.loadTreeSpace(folderIds); //Reload tree with Existing Open Items
    }

    /**
     * Forward to the controller ProcessListBoxController the request to add the
     * process to the table
     */
    public void displayNewProcess(final ProcessSummaryType returnedProcess) {
        switchToProcessSummaryView();
        ((ProcessListboxController) this.baseListboxController).displayNewProcess(returnedProcess);
        this.displayMessage(this.baseListboxController.getListModel().getSize() + " processes.");
    }

    /**
     * Send request to Manager: deleted process versions given as parameter
     *
     * @param elements a selection of process versions to delete.
     * @throws InterruptedException
     */
    public void deleteElements(final Map<SummaryType, List<VersionSummaryType>> elements) throws InterruptedException {
        try {
            getManagerService().deleteElements(elements, UserSessionManager.getCurrentUser().getUsername());
            switchToProcessSummaryView();
            this.baseListboxController.refreshContent();
            String message;
            int nb = 0;

            // to count how many process version(s) deleted
            Collection<List<VersionSummaryType>> sumTypes = elements.values();
            for (List<VersionSummaryType> sumType : sumTypes) {
                if (sumType != null)
                    nb += sumType.size();
            }
            if (nb > 1) {
                message = nb + " process versions deleted.";
            } else {
                message = "One process version deleted.";
            }
            displayMessage(message);
        } catch (Exception e) {
            LOGGER.warn("Unable to delete elements", e);
            Messagebox.show(Labels.getLabel("portal_failedDeleteNonOwner_message"), APROMORE, Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    private EditSessionType createEditSession(final ProcessSummaryType process, final VersionSummaryType version,
                                              final String nativeType) {
        EditSessionType editSession = new EditSessionType();
        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType.equals("XPDL 2.2") ? "BPMN 2.0" : nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(UserSessionManager.getCurrentUser().getUsername());
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(TRUNK_NAME); // Note: version name is the branch name
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(findMaxVersion(process));
        editSession.setFolderId(portalContext.getCurrentFolder().getId());
        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        editSession.setWithAnnotation(false);
        editSession.setAnnotation(null);

        return editSession;
    }

    // TO DO: Consolidate these private functions into common
    private VersionSummaryType getLatestVersion(List<VersionSummaryType> versionSummaries) {
        VersionSummaryType result = null;
        for (VersionSummaryType version : versionSummaries) {
            if (result == null || (version.getVersionNumber().compareTo(result.getVersionNumber()) > 0)) {
                result = version;
            }
        }
        return result;
    }

    private String getNativeType(String origNativeType) {
        String nativeType = origNativeType;
        if (origNativeType == null || origNativeType.isEmpty()) {
            nativeType = "BPMN 2.0";
        }
        return nativeType;
    }

    public void openProcess(ProcessSummaryType process, VersionSummaryType version) throws Exception {
        String nativeType = getNativeType(process.getOriginalNativeType());
        LOGGER.info("Open process model {} version {}", process.getName(), version.getVersionNumber());
        editProcess2(process, version, nativeType, new HashSet<RequestParameterType<?>>(), false);
    }

    public ProcessSummaryType openNewProcess() throws Exception {

        String username = UserSessionManager.getCurrentUser().getUsername();
        String userId = UserSessionManager.getCurrentUser().getId();

        Integer folderId = 0;
        FolderType currentFolder = getPortalSession().getCurrentFolder();
        if (currentFolder != null) {
            folderId = currentFolder.getId();
        }

        Pageable wholePage = Pageable.unpaged();
        Page<Process> processes =  this.getWorkspaceService().getProcesses(userId, folderId, wholePage);
        LOGGER.info("Find {} processes in current folder", processes.getSize());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());

        String bpmnXML = "<?xml version='1.0' encoding='UTF-8'?>"
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

        ImportProcessResultType importResult = getManagerService().importProcess(
                username, folderId, BPMN_2_0, UNTITLED_PROCESS_NAME, VERSION_1_0, new ByteArrayInputStream(bpmnXML.getBytes()), "",
                "", now, null, false);

        Integer processId = importResult.getProcessSummary().getId();

        ProcessSummaryType process = importResult.getProcessSummary();
        VersionSummaryType version = process.getVersionSummaries().get(0);
        LOGGER.info("Create process model {} version {}", process.getName(), version.getVersionNumber());

        // Create draft to associated with new model
        ProcessModelVersion draft = getManagerService().createDraft(processId, process.getName(),
                version.getVersionNumber(), process.getOriginalNativeType(),
                new ByteArrayInputStream(bpmnXML.getBytes()), username);
        LOGGER.info("Create draft version id: {} for model {} version {}", draft.getId(), process.getName(),
                version.getVersionNumber());

        qe.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));

        editProcess2(process, version, process.getOriginalNativeType(), new HashSet<>(),
                true);

        return process;
    }

    /**
     * Call editor to edit process version whose id is processId, name is
     * processName and version name is version. nativeType identifies language to be
     * used to edit the process version. If annotation is instantiated, it
     * identifies the annotation file to be used. If readOnly=1, annotations only
     * are editable.
     *
     * @param process               the process summary
     * @param version               the version of the process
     * @param nativeType            the native type of the process
     * @param requestParameterTypes request parameters types.
     * @throws InterruptedException
     */
    @Override
    public void editProcess(final ProcessSummaryType process, final VersionSummaryType version, final String nativeType,
                            Set<RequestParameterType<?>> requestParameterTypes, boolean newProcess) throws InterruptedException {
        String instruction = "";

        EditSessionType editSession = createEditSession(process, version, nativeType);

        try {
            String id = UUID.randomUUID().toString();
            ApromoreSession session = new ApromoreSession(editSession, null, this, process, version, null, null,
                    requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "macros/openModelAlternative.zul?id=" + id;
            if (newProcess)
                url += "&newProcess=true";
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            LOGGER.error("Cannot edit", e.getMessage());
            Messagebox.show(MessageFormat.format(Labels.getLabel("portal_cannotEdit_message"), process.getName()),
                    APROMORE, Messagebox.OK, Messagebox.ERROR);
        }
    }

    @Override
    public void editProcess2(final ProcessSummaryType process, final VersionSummaryType version,
                             final String nativeType, Set<RequestParameterType<?>> requestParameterTypes, boolean newProcess)
            throws InterruptedException {

        String instruction = "";
        EditSessionType editSession = createEditSession(process, version, nativeType);

        try {
            String id = UUID.randomUUID().toString();
            requestParameterTypes.add(new RequestParameterType<>("versions", process.getVersionSummaries()));
            ApromoreSession session = new ApromoreSession(editSession, null, this, process, version, null, null,
                    requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "openModelInBPMNio.zul?id=" + id + "&REFER_ID="+ Executions.getCurrent().getDesktop().getId();
            if (newProcess)
                url += "&newProcess=true";
            instruction += "window.open('" + url + "');";


            ExportFormatResultType exportResult = this.getManagerService().exportFormat(
                    editSession.getProcessId(), editSession.getProcessName(),
                    editSession.getOriginalBranchName(), editSession.getCurrentVersionNumber(),
                    editSession.getNativeType(), editSession.getUsername());
            String bpmnXML = StreamUtil.convertStreamToString(exportResult.getNative().getInputStream());

            // If no draft associated with specified model, version and user
            if (this.getProcessService().getProcessModelVersionByUser(process.getId(),
                    org.apromore.common.Constants.DRAFT_BRANCH_NAME, version.getVersionNumber(),
                    getSecurityService().getUserById(portalContext.getCurrentUser().getId()).getId()) == null) {
                this.getManagerService().createDraft(process.getId(), process.getName(), version.getVersionNumber(),
                        nativeType, new ByteArrayInputStream(bpmnXML.getBytes()),
                        UserSessionManager.getCurrentUser().getUsername());
            }
            ExportFormatResultType exportResultDraft = this.getManagerService().exportFormat(
                    editSession.getProcessId(), editSession.getProcessName(),
                    org.apromore.common.Constants.DRAFT_BRANCH_NAME, editSession.getCurrentVersionNumber(),
                    editSession.getNativeType(), editSession.getUsername());
            String bpmnXmlDraft = StreamUtil.convertStreamToString(exportResultDraft.getNative().getInputStream());

            if (!Objects.equals(bpmnXML, bpmnXmlDraft)) {
                String finalInstruction = instruction;
                Messagebox.show(
                    MessageFormat.format(Labels.getLabel("portal_unsavedDraftExisted_message"), editSession.getCurrentVersionNumber()),
                    Labels.getLabel("brand_name"),
                    new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
                    Messagebox.QUESTION, e -> {
                        switch (e.getButton()) {
                            case YES:
                                Clients.evalJavaScript(finalInstruction);
                                break;
                            case NO: // Cancel is clicked
                                this.getProcessService().updateDraft(editSession.getProcessId(),
                                    editSession.getCurrentVersionNumber(), editSession.getNativeType(),
                                    new ByteArrayInputStream(bpmnXML.getBytes()),
                                    editSession.getUsername());
                                Clients.evalJavaScript(finalInstruction);
                                break;
                            default: // if the Close button is clicked, e.getButton() returns null
                        }
                    });
            } else {
                Clients.evalJavaScript(instruction);
            }

        } catch (Exception e) {
            LOGGER.warn("Unable to edit process model " + process.getName() + " version " + version.getVersionNumber(),
                    e);
            Messagebox.show(MessageFormat.format(Labels.getLabel("portal_cannotEdit_message"), process.getName()),
                    APROMORE, Messagebox.OK, Messagebox.ERROR);
        }
    }

    public FolderType getBreadcrumFolders(int selectedFolderId) {
        FolderType selectedFolder = null;
        List<FolderType> breadcrumbFolders = this.getManagerService()
                .getBreadcrumbs(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

        for (FolderType folder : breadcrumbFolders) {
            if (folder.getId() == selectedFolderId) {
                selectedFolder = folder;
                break;
            }
        }
        return selectedFolder;
    }

    public void selectBreadcrumFolder(int selectedFolderId) {
        FolderType selectedFolder = this.getBreadcrumFolders(selectedFolderId);

        if (selectedFolder == null) {
            selectedFolder = new FolderType();
            selectedFolder.setId(0);
            selectedFolder.setFolderName("Home");
        }

        List<FolderType> availableFolders = this.getManagerService()
                .getSubFolders(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

        if (selectedFolder.getFolders().size() == 0) {
            for (FolderType folderType : availableFolders) {
                selectedFolder.getFolders().add(folderType);
            }
        }
        this.portalSession.setPreviousFolder(this.portalSession.getCurrentFolder());
        this.portalSession.setCurrentFolder(selectedFolder);

        this.reloadSummaries2();
        navigation.selectCurrentFolder();
        this.clearProcessVersions();

        if (selectedFolder.getId() != 0)
            this.displayFolderVersions(selectedFolder);

    }

    public void visualizeLog() {
        if (logVisualizerPlugin == null) {
            for (final PortalPlugin plugin : PortalPluginResolver.resolve()) {
                if (plugin.getName().equals("Process Discoverer")) {
                    logVisualizerPlugin = plugin;
                    break;
                }
            }
        }
        if (logVisualizerPlugin != null) {
            logVisualizerPlugin.execute(new PluginPortalContext(this));
        }
    }

    public void displayMessage(final String mes) {
        this.shortmessageC.displayMessage(mes);
    }

    public void eraseMessage() {
        this.shortmessageC.eraseMessage();
    }

    /**
     * get list of domains
     */
    public List<String> getDomains() throws ExceptionDomains {
        DomainsType domainsType;
        domainsType = getManagerService().readDomains();
        return domainsType.getDomain();
    }

    /**
     * get list of users' names
     *
     * @return the list of user names
     * @throws org.apromore.portal.exception.ExceptionAllUsers
     */
    public List<String> getUsers() throws ExceptionAllUsers {
        UsernamesType usernames = getManagerService().readAllUsers();
        return usernames.getUsername();
    }

    /**
     * get list of formats: <k, v> belongs to getNativeTypes() <=> the file
     * extension k is associated with the native type v (<xpdl,XPDL 1.2>)
     *
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    public HashMap<String, String> getNativeTypes() throws ExceptionFormats {
        HashMap<String, String> formats = new HashMap<>();
        NativeTypesType nativeTypesDB = getManagerService().readNativeTypes();
        for (int i = 0; i < nativeTypesDB.getNativeType().size(); i++) {
            formats.put(nativeTypesDB.getNativeType().get(i).getExtension(),
                    nativeTypesDB.getNativeType().get(i).getFormat());
        }
        return formats;
    }

    public void displayProcessVersions(final ProcessSummaryType data) {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).displayProcessVersions(data);
    }

    public void displayLogVersions(final LogSummaryType data) {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).displayLogVersions(data);
    }

    public void displayFolderVersions(final FolderType data) {
        Folder folder = this.getWorkspaceService().getFolder(data.getId());
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).displayFolderVersions(folder);
    }

    public void clearProcessVersions() {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).clearProcessVersions();
    }

    @SuppressWarnings("unchecked")
    public Set<SummaryType> getSelectedElements() {
        if (this.baseListboxController instanceof ProcessListboxController) {
            ProcessListboxController processController = (ProcessListboxController) getBaseListboxController();
            return processController.getListModel().getSelection();
        } else {
            return new HashSet<>();
        }
    }

    /**
     * @return a map with all currently selected process models and the
     * corresponding selected versions
     * @throws ParseException
     */
    public Map<SummaryType, List<VersionSummaryType>> getSelectedElementsAndVersions() {
        Map<SummaryType, List<VersionSummaryType>> summaryTypes = new LinkedHashMap<>();

        if (getBaseListboxController() instanceof ProcessListboxController) {
            ArrayList<VersionSummaryType> versionList;

            Set<VersionDetailType> selectedVersions = ((ProcessVersionDetailController) getDetailListbox())
                    .getListModel().getSelection();
            Set<Object> selectedProcesses = getBaseListboxController().getListModel().getSelection();
            for (Object obj : selectedProcesses) {
                if (obj instanceof ProcessSummaryType) {
                    ProcessSummaryType processSummaryType = (ProcessSummaryType) obj;
                    versionList = new ArrayList<>();
                    if (selectedVersions != null && !selectedVersions.isEmpty()) {
                        for (VersionDetailType detail : selectedVersions) {
                            versionList.add(detail.getVersion());
                        }
                    } else {
                        String versionNumber = processSummaryType.getLastVersion();
                        for (VersionSummaryType summaryType : processSummaryType.getVersionSummaries()) {
                            if (summaryType.getVersionNumber().compareTo(versionNumber) == 0 &&
                                TRUNK_NAME.equals(summaryType.getName())) {
                                versionList.add(summaryType);
                                break;
                            }
                        }
                    }
                    summaryTypes.put(processSummaryType, versionList);

                } else if (obj instanceof LogSummaryType) {
                    summaryTypes.put((LogSummaryType) obj, null);
                }
            }
        }
        LOGGER.debug("Got selected elements and versions");
        return summaryTypes;
    }

    /**
     * Show the messages we get back from plugins.
     *
     * @param messages the messages to display to the user.
     * @throws InterruptedException if the communication was interrupted for any
     *                              reason.
     */
    public void showPluginMessages(final PluginMessages messages) throws InterruptedException {
        if (messages != null) {
            StringBuilder sb = new StringBuilder();
            Iterator<PluginMessage> iter = messages.getMessage().iterator();
            while (iter.hasNext()) {
                sb.append(iter.next().getValue());
                if (iter.hasNext()) {
                    sb.append("\n\n");
                }
            }
            if (sb.length() > 0) {
                Messagebox.show(sb.toString(), "Apromore Plugin", Messagebox.OK, Messagebox.EXCLAMATION);
            }
        }
    }

    /**
     * Update the List box from the folder view with what is selected and what
     * isn't.
     *
     * @param processIds update the list box of processes with these processes.
     */
    @SuppressWarnings("unchecked")
    public void updateSelectedListBox(List<Integer> processIds) {
        BaseListboxController baseListBoxController = getBaseListboxController();
        if (baseListBoxController != null) {
            baseListBoxController.getListModel().clearSelection();
            if ((baseListBoxController instanceof ProcessListboxController)) {
                for (ProcessSummaryType pst : (List<ProcessSummaryType>) baseListBoxController.getListModel()) {
                    for (Integer i : processIds) {
                        if (pst != null && pst.getId().equals(i)) {
                            baseListBoxController.getListModel().addToSelection(pst);
                        }
                    }
                }
                displayProcessVersions((ProcessSummaryType) getBaseListboxController().getListModel().getSelection()
                        .iterator().next());
            }
        }
    }

    /* Removes the currently displayed listbox, detail and filter view */
    private void deattachDynamicUI() {
        this.getFellow("baseListboxProcesses").getFellow("tablecomp").getChildren().clear();
        this.getFellow("baseDetail").getFellow("detailcomp").getChildren().clear();
    }

    /* Attaches the the listbox, detail and filter view */
    private void reattachDynamicUI() {
        this.getFellow("baseListboxProcesses").getFellow("tablecomp").appendChild(baseListboxController);
        this.getFellow("baseDetail").getFellow("detailcomp").appendChild(baseDetailController);
    }

    /*
     * Switches all dynamic UI elements to the ProcessSummaryView. Affects the
     * listbox, detail and filter view
     */
    private void switchToProcessSummaryView() {
        if (this.baseListboxController != null) {
            if ((this.baseListboxController instanceof ProcessListboxController)) {
                return;
            } else {
                deattachDynamicUI();
            }
        }

        // Otherwise create new Listbox
        this.baseListboxController = new ProcessListboxController(this);
        this.baseDetailController = new ProcessVersionDetailController(this);

        reattachDynamicUI();
        reloadSummaries();
    }

    /* Load the props for this app. */
    private void loadProperties() throws IOException {
        LOGGER.trace("Loading properties of webapp");
    }

    public String getContactEmail() {
        return getConfig().getSite().getContactEmail();
    }

    /* From a list of version summary types find the max version number. */
    private static String findMaxVersion(ProcessSummaryType process) {
        Version versionNum;
        Version max = new Version(0, 0);
        for (VersionSummaryType version : process.getVersionSummaries()) {
            versionNum = new Version(version.getVersionNumber());
            if (versionNum.compareTo(max) > 0) {
                max = versionNum;
            }
        }
        return max.toString();
    }

    public MenuController getMenu() {
        return menu;
    }

    public BaseListboxController getBaseListboxController() {
        return baseListboxController;
    }

    public BaseDetailController getDetailListbox() {
        return baseDetailController;
    }

    private void updateTabs(String userId) {
        Window mainW = (Window) this.getFellow("mainW");

        Tabbox tabbox = (Tabbox) mainW.getFellow("tabbox");

        int size = tabbox.getTabs().getChildren().size();
        boolean added = false;

        List<Tab> tabList = SessionTab.getSessionTab(portalContext).getTabsSession(userId);
        if (size < tabList.size() + 1) {
            for (Tab tab : tabList) {
                try {
                    if (!tabbox.getTabs().getChildren().contains(tab)) {
                        PortalTab portalTab = (PortalTab) tab.clone();
                        SessionTab.getSessionTab(portalContext).removeTabFromSession(userId, tab, false);
                        SessionTab.getSessionTab(portalContext).addTabToSession(userId, (org.zkoss.zul.Tab) portalTab,
                                false);

                        portalTab.getTab().setParent(tabbox.getTabs());
                        if (portalTab.getTabpanel() == null) {
                            LOGGER.warn("Portal tab had no panel " + portalTab);
                        } else {
                            portalTab.getTabpanel().setParent(tabbox.getTabpanels());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("Couldn't update tab", e);
                    // Executions.sendRedirect(null);
                }
            }
        }
    }

    /**
     * Bruce added 21.05.2019 Display an input dialog
     *
     * @param title:        title of the dialog
     * @param message:      the message regarding the input to enter
     * @param initialValue: initial value for the input
     * @param valuePattern: the expression pattern to check validity of the input
     * @returnValueHander: callback event listener, notified with onOK (containing
     * return value as string) and onCancel event
     */
    public void showInputDialog(String title, String message, String initialValue, String valuePattern,
                                String allowedValues, EventListener<Event> returnValueHander) {
        Window win = (Window) Executions.createComponents("macros/inputDialog.zul", null, null);
        Window dialog = (Window) win.getFellow("inputDialog");
        dialog.setTitle(title);
        Label labelMessage = (Label) dialog.getFellow("labelMessage");
        Textbox txtValue = (Textbox) dialog.getFellow("txtValue");
        Label labelError = (Label) dialog.getFellow("labelError");
        labelMessage.setValue(message);
        txtValue.setValue(initialValue);
        labelError.setValue("");

        dialog.doModal();

        ((Button) dialog.getFellow("btnCancel")).addEventListener(ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                dialog.detach();
                returnValueHander.onEvent(new Event("onCancel"));
            }
        });

        ((Button) dialog.getFellow("btnOK")).addEventListener(ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (txtValue.getValue().trim().isEmpty()) {
                    labelError.setValue("Please enter a value!");
                } else if (!Pattern.matches(valuePattern, txtValue.getValue())) {
                    labelError.setValue("The entered value is not valid! Allowed characters: " + allowedValues);
                } else {
                    dialog.detach();
                    returnValueHander.onEvent(new Event("onOK", null, txtValue.getValue()));
                }
            }
        });

        win.addEventListener("onOK", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Events.sendEvent(ON_CLICK, dialog.getFellow("btnOK"), null);
            }
        });

    }

    public void setBreadcrumbs(int selectedFolderId) {
        List<FolderType> breadcrumbFolders = this.getManagerService()
                .getBreadcrumbs(UserSessionManager.getCurrentUser().getId(), selectedFolderId);
        Collections.reverse(breadcrumbFolders);
        String content = "";
        String crumb;
        int breadcrumbLength = breadcrumbFolders.size();

        int i = 0;
        for (FolderType breadcrumb : breadcrumbFolders) {
            String folderName = breadcrumb.getFolderName();
            String id = breadcrumb.getId().toString();
            String onClick = "Ap.portal.clickBreadcrumb('" + id + "')";
            if (i == breadcrumbLength - 1) {
                crumb = "<a data-last=\"true\" data-id=\"" + id + "\" onclick=\"" + onClick + "\">" + folderName
                        + "</a>";
            } else {
                crumb = "<a data-id=\"" + id + "\" onclick=\"" + onClick + "\">" + folderName + "</a>";
            }
            content += "&gt;<span class=\"ap-portal-crumb\">" + crumb + "</span>";
            i++;
        }
        this.breadCrumbs.setContent(content);
        Clients.evalJavaScript("Ap.portal.updateBreadcrumbs();");
    }

    public String deriveName(ProcessSummaryType processSummaryType, String suffix) {
        String processName = processSummaryType == null ? "untitled" : processSummaryType.getName();
        List<Log> existingLogs = getWorkspaceService().getLogsByPrefix(processName + suffix);
        List<String> existingNames = new ArrayList<>();
        for (Log log : existingLogs) {
            existingNames.add(log.getName());
        }
        return ItemNameUtils.deriveName(existingNames, processName, suffix);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        onCreate(comp);
    }

    @Override
    public Component getFellow(String id) {
        return mainComponent.getFellow(id);

    }

    @Override
    public Desktop getDesktop() {
        return mainComponent.getDesktop();
    }

    public CopyAndPasteController getCopyPasteController() {
        return this.copyAndPasteController;
    }

    public NavigationController getNavigationController() {
        return this.navigation;
    }

}
