/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.helper.Version;
import org.apromore.model.*;
import org.apromore.model.Detail;
import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersFragmentsListboxController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersListboxController;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.ext.Paginal;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Controller for the whole application, most of the UI state is managed here.
 * It is automatically instantiated as index.zul is loaded!
 */
public class MainController extends BaseController implements MainControllerInterface {

    private static final long serialVersionUID = 5147685906484044300L;
    private static MainController controller = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private EventQueue<Event> qe; // = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);

    private static final String WELCOME_TEXT = "Welcome %s. Release notes (%s)"; //Welcome %s.

    private PortalContext portalContext;
    private MenuController menu;
    private SimpleSearchController simplesearch;
    private ShortMessageController shortmessageC;
    private BaseListboxController baseListboxController;
    private BaseDetailController baseDetailController;

    private NavigationController navigation;

//    public Html breadCrumbs;
    private Paginal pg;

    private String host;
    private String majorVersionNumber;
    private String minorVersionNumber;
    private String buildDate;

    private PortalPlugin logVisualizerPlugin = null;
    
    private Execution execution = null;
	
	public static MainController getController() {
        return controller;
    }

    public MainController() {
        qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
    }

    /** Unit test constructor. */
    public MainController(ConfigBean configBean) {
        super(configBean);
    }

    /**
     * onCreate is executed after the main window has been created it is
     * responsible for instantiating all necessary controllers (one for each
     * window defined in the interface) see description in index.zul
     * @throws InterruptedException
     */
    public void onCreate() throws InterruptedException {
        try {
            loadProperties();
            UserSessionManager.initializeUser(getService());

            Window mainW = (Window) this.getFellow("mainW");
            Hbox pagingandbuttons = (Hbox) mainW.getFellow("pagingandbuttons");

            Window shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
//            this.breadCrumbs = (Html) mainW.getFellow("breadCrumbs");
            this.pg = (Paginal) mainW.getFellow("pg");
            this.shortmessageC = new ShortMessageController(shortmessageW);
            this.simplesearch = new SimpleSearchController(this);
            this.menu = new MenuController(this);
            this.portalContext = new PluginPortalContext(this);
            this.navigation = new NavigationController(this);

            switchToProcessSummaryView();
            UserSessionManager.setMainController(this);
            pagingandbuttons.setVisible(true);

            qe.subscribe(
                    new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            if (Constants.EVENT_MESSAGE_SAVE.equals(event.getName())) {
                                clearProcessVersions();
                                reloadSummaries();
                            }
                        }
                    });

            qe.subscribe(
                    new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            if (Constants.EVENT_QUEUE_REFRESH_SCREEN.equals(event.getName())) {
                                reloadSummaries();
                            }
                        }
                    });
        } catch (Exception e) {
            String message;
            if (e.getMessage() == null) {
                message = "Please contact your Apromore Administrator";
            } else {
                message = e.getMessage();
            }
            e.printStackTrace();
            Messagebox.show("Repository not available (" + message + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
        controller=this;
    }

    // Bruce: Do not use Executions.sendRedirect as it does not work 
    // for webapp bundles with different ZK execution env.
    public void refresh() {
        try {
            //Executions.sendRedirect(null);
        	qe.publish(new Event(Constants.EVENT_QUEUE_REFRESH_SCREEN, null, Boolean.TRUE));
        } catch (NullPointerException e) {
            // The ZK documentation for sendRedirect claims that passing a null parameter is allowed
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
        updateActions();

        int currentParentFolderId = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? 0 : UserSessionManager.getCurrentFolder().getId();

        if (loadTree) {
            this.loadTree();
        }

        List<FolderType> folders = this.getService().getSubFolders(userId, currentParentFolderId);
        if (UserSessionManager.getCurrentFolder() != null) {
            FolderType folder = UserSessionManager.getCurrentFolder();
            folder.getFolders().clear();
            for (FolderType newFolder : folders) {
                folder.getFolders().add(newFolder);
            }
            UserSessionManager.setCurrentFolder(folder);
        }
    }

    private void loadTree() {
        List<FolderType> folders = this.getService().getWorkspaceFolderTree(UserSessionManager.getCurrentUser().getId());
        UserSessionManager.setTree(folders);
        this.navigation.loadWorkspace();
    }

    public void currentFolderChanged() {
        navigation.currentFolderChanged();
    }

    /**
     * Display version processes in processSummaries: if isQueryResult, the
     * query is given by version of process
     * @param processSummaries the list of process summaries to display
     * @param isQueryResult is this from a query (simsearch, clustering, etc.)
     */
    public void displayProcessSummaries(final SummariesType processSummaries, final Boolean isQueryResult) {
        int folderId;

        if (isQueryResult) {
            clearProcessVersions();
        }
        if (UserSessionManager.getCurrentFolder() == null) {
            folderId = 0;
        } else {
            folderId = UserSessionManager.getCurrentFolder().getId();
        }

        // TODO switch to process query result view
        switchToProcessSummaryView();
        List<FolderType> subFolders = getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), folderId);
        this.baseListboxController.displaySummaries(subFolders, processSummaries, isQueryResult);
    }

    // disable/enable features depending on user status
    public void updateActions() {
        Boolean connected = UserSessionManager.getCurrentUser() != null;
        List<String> blacklist = Arrays.asList("designPatternCr", "designReference", "designPatternCo", /*"designConfiguration",*/ "designExtension");

        // disable/enable menu items in menu bar
        for (Component C : this.menu.getMenuB().getFellows()) {
            if (C instanceof Menuitem) {
                ((Menuitem) C).setDisabled(!connected && !blacklist.contains(C.getId()));
            }
        }
    }

    public void reloadSummaries() {
        this.simplesearch.clearSearches();
        switchToProcessSummaryView();
        pg.setActivePage(0);

        FolderType currentFolder = UserSessionManager.getCurrentFolder();
        List<FolderType> subFolders = getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.SummaryListModel model = this.baseListboxController.displaySummaries(subFolders, false);

        this.displayMessage(
            model.getSize() + " out of " + model.getTotalCount() +
	    (model.getTotalCount() > 1 ? " elements." : " element.")
	);

        loadWorkspace(true);
    }

    public void reloadSummaries2() {
        this.simplesearch.clearSearches();
        switchToProcessSummaryView();
        pg.setActivePage(0);

        FolderType currentFolder = UserSessionManager.getCurrentFolder();
        List<FolderType> subFolders = getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.SummaryListModel model = this.baseListboxController.displaySummaries(subFolders, false);

        this.displayMessage(
            model.getSize() + " out of " + model.getTotalCount() +
	    (model.getTotalCount() > 1 ? " elements." : " element.")
	);

        loadWorkspace(false);
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
     * @param elements a selection of process versions to delete.
     * @throws InterruptedException
     */
    public void deleteElements(final Map<SummaryType, List<VersionSummaryType>> elements) throws InterruptedException {
        try {
            getService().deleteElements(elements, UserSessionManager.getCurrentUser().getUsername());
            switchToProcessSummaryView();
            this.baseListboxController.refreshContent();
            String message;
            int nb = 0;

            // to count how many process version(s) deleted
            Collection<List<VersionSummaryType>> sumTypes = elements.values();
            for (List<VersionSummaryType> sumType : sumTypes) {
                if(sumType != null) nb += sumType.size();
            }
            if (nb > 1) {
                message = nb + " process versions deleted.";
            } else {
                message = "One process version deleted.";
            }
            displayMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Deletion failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private EditSessionType createEditSession(final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation) {

        EditSessionType editSession = new EditSessionType();

        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType.equals("XPDL 2.2")?"BPMN 2.0":nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(UserSessionManager.getCurrentUser().getUsername());
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(version.getName());
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(findMaxVersion(process));
        editSession.setFolderId(portalContext.getCurrentFolder().getId());
        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
            editSession.setAnnotation(null);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }

        return editSession;
    }

    /**
     * Call editor to edit process version whose id is processId, name is
     * processName and version name is version. nativeType identifies language
     * to be used to edit the process version. If annotation is instantiated, it
     * identifies the annotation file to be used. If readOnly=1, annotations
     * only are editable.
     *
     * @param process the process summary
     * @param version the version of the process
     * @param nativeType the native type of the process
     * @param annotation the annotation of that process
     * @param readOnly is this model readonly or not
     * @param requestParameterTypes request parameters types.
     * @throws InterruptedException
     */
    public void editProcess(final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation,
            final String readOnly, Set<RequestParameterType<?>> requestParameterTypes) throws InterruptedException {
        String instruction = "";

        EditSessionType editSession = createEditSession(process, version, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();
            ApromoreSession session = new ApromoreSession(editSession, null, this, process, version, null, null, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "macros/openModelInSignavio.zul?id=" + id;
            if (annotation != null && annotation.equals(Constants.INITIAL_ANNOTATION)) url += "&newProcess=true";
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot edit " + process.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    public void editProcess2(final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation,
            final String readOnly, Set<RequestParameterType<?>> requestParameterTypes, boolean newProcess) throws InterruptedException {
        String instruction = "";

        EditSessionType editSession = createEditSession(process, version, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();
            ApromoreSession session = new ApromoreSession(editSession, null, this, process, version, null, null, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "macros/openModelInBPMNio.zul?id=" + id;
            if (newProcess) url += "&newProcess=true";
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot edit " + process.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    public void saveModel(ProcessSummaryType process, VersionSummaryType version, EditSessionType editSession,
            boolean isNormalSave, String data) throws  InterruptedException {
    	try {
    		//Window window = (Window) portalContext.getUI().createComponent(this.getClass().getClassLoader(), "macros/saveAsDialog.zul", null, null);
    		SaveAsDialogController saveDiaglog = new SaveAsDialogController(process, version, editSession, isNormalSave, data);
    	}
    	catch (Exception e) {
    		Messagebox.show("Cannot edit " + process.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
    	}
    }
    


    public void visualizeLog() {
        if(logVisualizerPlugin == null) {
            for (final PortalPlugin plugin : PortalPluginResolver.resolve()) {
                if (plugin.getName().equals("Process Discoverer")) {
                    logVisualizerPlugin = plugin;
                    break;
                }
            }
//            if(logVisualizerPlugin == null) {
//                for (final PortalPlugin plugin : PortalPluginResolver.resolve()) {
//                    if (plugin.getName().equals("Log Visualizer")) {
//                        logVisualizerPlugin = plugin;
//                        break;
//                    }
//                }
//            }
        }
        if(logVisualizerPlugin != null) {
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
        domainsType = getService().readDomains();
        return domainsType.getDomain();
    }

    /**
     * get list of users' names
     * @return the list of user names
     * @throws org.apromore.portal.exception.ExceptionAllUsers
     */
    public List<String> getUsers() throws ExceptionAllUsers {
        UsernamesType usernames = getService().readAllUsers();
        return usernames.getUsername();
    }

    /**
     * get list of formats: <k, v> belongs to getNativeTypes() <=> the file
     * extension k is associated with the native type v (<xpdl,XPDL 1.2>)
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    public HashMap<String, String> getNativeTypes() throws ExceptionFormats {
        HashMap<String, String> formats = new HashMap<>();
        NativeTypesType nativeTypesDB = getService().readNativeTypes();
        for (int i = 0; i < nativeTypesDB.getNativeType().size(); i++) {
            formats.put(nativeTypesDB.getNativeType().get(i).getExtension(), nativeTypesDB.getNativeType().get(i).getFormat());
        }
        return formats;
    }

    public void displayProcessVersions(final ProcessSummaryType data) {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).displayProcessVersions(data);
    }

    public void displayLogVersions(final LogSummaryType data) {
        //TODO
//        switchToProcessSummaryView();
//        ((ProcessVersionDetailController) this.baseDetailController).displayProcessVersions(data);
    }

    public void clearProcessVersions() {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).clearProcessVersions();
    }

//    public void clearLogVersions() {
//        //TODO
////        switchToProcessSummaryView();
////        ((ProcessVersionDetailController) this.baseDetailController).clearProcessVersions();
//    }

    public void displaySimilarityClusters(final ClusterFilterType filter) {
        switchToSimilarityClusterView();
        ((SimilarityClustersListboxController) this.baseListboxController).displaySimilarityClusters(filter);
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

//    public Set<LogSummaryType> getSelectedLogs() {
//        LogListboxController logController = (LogListboxController) getLogListboxController();
//        return logController.getListModel().getSelection();
//    }

    /**
     * @return a map with all currently selected process models and the corresponding selected versions
     * @throws ParseException
     */
    public Map<SummaryType, List<VersionSummaryType>> getSelectedElementsAndVersions() {
        Map<SummaryType, List<VersionSummaryType>> summaryTypes = new HashMap<>();

        if (getBaseListboxController() instanceof ProcessListboxController) {
            ArrayList<VersionSummaryType> versionList;

            Set<VersionDetailType> selectedVersions = ((ProcessVersionDetailController) getDetailListbox()).getListModel().getSelection();
            Set<Object> selectedProcesses = (Set<Object>) getBaseListboxController().getListModel().getSelection();
            for (Object obj : selectedProcesses) {
                if (obj instanceof ProcessSummaryType) {
                    ProcessSummaryType processSummaryType = (ProcessSummaryType) obj;
                    versionList = new ArrayList<>();
                    if (selectedVersions != null && !selectedVersions.isEmpty()) {
                        for (VersionDetailType detail: selectedVersions) {
                            versionList.add(detail.getVersion());
                        }
                    } else {
                        String versionNumber = processSummaryType.getLastVersion();
                        for (VersionSummaryType summaryType : processSummaryType.getVersionSummaries()) {
                            if (summaryType.getVersionNumber().compareTo(versionNumber) == 0) {
                                versionList.add(summaryType);
                            }
                        }
                    }
                    summaryTypes.put(processSummaryType, versionList);

                } else if (obj instanceof LogSummaryType) {
                    summaryTypes.put((LogSummaryType) obj, null);
                }
            }
        }
        LOGGER.info("Got selected elements and versions");
        return summaryTypes;
    }
    
    private VersionSummaryType getLatestVersion(List<VersionSummaryType> versionSummaries) {
        VersionSummaryType result = null;
        for (VersionSummaryType version : versionSummaries) {
            if (result == null || (version.getVersionNumber().compareTo(result.getVersionNumber()) > 0)) {
                result = version;
            }
        }
        return result;
    }


    /**
     * Show the messages we get back from plugins.
     * @param messages the messages to display to the user.
     * @throws InterruptedException if the communication was interrupted for any reason.
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
                Messagebox.show(sb.toString(), "Plugin Warnings", Messagebox.OK, Messagebox.EXCLAMATION);
            }
        }
    }

    /**
     * Update the List box from the folder view with what is selected and what isn't.
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
                displayProcessVersions((ProcessSummaryType) getBaseListboxController().getListModel().getSelection().iterator().next());
            }
        }
    }

    /* Get the Search Histories for the current logged in User. */
    public List<SearchHistoriesType> getSearchHistory() {
        return UserSessionManager.getCurrentUser().getSearchHistories();
    }

    /* Tell the portal we need to update the search history for this user. */
    public void updateSearchHistory(final List<SearchHistoriesType> searchHist) throws Exception {
        getService().updateSearchHistories(UserSessionManager.getCurrentUser(), searchHist);
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

    /* Switches all dynamic UI elements to the ProcessSummaryView. Affects the listbox, detail and filter view */
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

    /* Switches all dynamic UI elements to the SimilarityClusterView. Affects the listbox, detail and filter view */
    private void switchToSimilarityClusterView() {
        if (this.baseListboxController != null) {
            if ((this.baseListboxController instanceof SimilarityClustersListboxController)) {
                return;
            } else {
                deattachDynamicUI();
            }
        }

        // Otherwise create new Listbox
        this.baseDetailController = new SimilarityClustersFragmentsListboxController(this);
        this.baseListboxController = new SimilarityClustersListboxController(this,
                null, (SimilarityClustersFragmentsListboxController) this.baseDetailController);

        reattachDynamicUI();
    }

    /* Load the props for this app. */
    private void loadProperties() throws IOException {
        setHost("http://" + config.getSiteExternalHost() + ":" + config.getSiteExternalPort());
        String date = config.getVersionBuildDate();
        date = date.substring(0, date.indexOf("@") - 1);
        String subversion = "";
        StringTokenizer st = new StringTokenizer(date, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if(token.length() == 4) token = token.substring(2);
            subversion = token + subversion;
        }
        setMajorVersionNumber(config.getMajorVersionNumber() + "." + subversion);
        setMinorVersionNumber(config.getMinorVersionNumber());
        setBuildDate(config.getVersionBuildDate());
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

//    public BaseListboxController getLogListboxController() {
//        return baseListboxControllerLogs;
//    }

    public BaseDetailController getDetailListbox() {
        return baseDetailController;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String newHost) {
        host = newHost;
    }

    public String getMajorVersionNumber() {
        return majorVersionNumber;
    }

    public void setMajorVersionNumber(final String newMajorVersionNumber) {
        majorVersionNumber = newMajorVersionNumber;
    }

    public String getMinorVersionNumber() {
        return majorVersionNumber;
    }

    public void setMinorVersionNumber(final String newMinorVersionNumber) {
        minorVersionNumber = newMinorVersionNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(final String newBuildDate) {
        buildDate = newBuildDate;
    }

    public void addResult(List<ResultPQL> results, String userID, List<Detail> details, String query, String nameQuery) {
        TabQuery newTab = new TabQuery(nameQuery, userID, details, query, results, portalContext);
/*
        String tabName = "APQL query";
        String tabRowImage = "img/icon/bpmn-22x22.png";

        List<TabRowValue> rows = new ArrayList<>();
        for(ResultPQL resultPQL : results) {
            TabRowValue row = new TabRowValue();
            row.add("name");
            row.add("id");
            row.add("lang");
            row.add("dom");
            row.add("rank");
            row.add("version");
            row.add("own");
            rows.add(row);
        }

        List<Listheader> listheaders = new ArrayList<>();
        addListheader(listheaders, "Name",              null);
        addListheader(listheaders, "Id",                "3em");
        addListheader(listheaders, "Original language", "10em");
        addListheader(listheaders, "Domain",            "5em");
        addListheader(listheaders, "Ranking",           "6em");
        addListheader(listheaders, "Latest version",    "9em");
        addListheader(listheaders, "Owner",             "5em");

        TabItemExecutor tabItemExecutor = new ProcessTabItemExecutor(portalContext.getMainController());

        portalContext.setUser(
        PortalTabImpl newTab = new PortalTabImpl(tabName, tabRowImage, rows, listheaders, tabItemExecutor, portalContext);
*/
        SessionTab.getSessionTab(portalContext).addTabToSession(userID, newTab, true);
        updateTabs(userID);
    }

/*
    private void addListheader(final List<Listheader> listheaders, String name, String width) {
        final Listheader listheader = new Listheader(name, null, width);

        listheader.setSortAscending(new java.util.Comparator<TabItem>() {
            int position = listheaders.size();
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o1.getValue(position).compareTo(o2.getValue(position));
            }
        });

        listheader.setSortDescending(new java.util.Comparator<TabItem>() {
            int position = listheaders.size();
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o2.getValue(position).compareTo(o1.getValue(position));
            }
        });

        listheaders.add(listheader);
    }
*/

    private void updateTabs(String userId){
        Window mainW = (Window) this.getFellow("mainW");

        Tabbox tabbox = (Tabbox) mainW.getFellow("tabbox");

        int size = tabbox.getTabs().getChildren().size();
        boolean added = false;

        List<Tab> tabList = SessionTab.getSessionTab(portalContext).getTabsSession(userId);
        if(size < tabList.size() + 1) {
            for (Tab tab : tabList) {
                try {
                    if(!tabbox.getTabs().getChildren().contains(tab)) {
                        PortalTab portalTab = (PortalTab) tab.clone();
                        SessionTab.getSessionTab(portalContext).removeTabFromSession(userId, tab, false);
                        SessionTab.getSessionTab(portalContext).addTabToSession(userId, (org.zkoss.zul.Tab) portalTab, false);

                        portalTab.getTab().setParent(tabbox.getTabs());
                        if (portalTab.getTabpanel() == null) {
                            LOGGER.warn("Portal tab had no panel " + portalTab);
                        } else {
                            portalTab.getTabpanel().setParent(tabbox.getTabpanels());
                        }
                    }
                }catch (Exception e) {
                    LOGGER.warn("Couldn't update tab", e);
                    //Executions.sendRedirect(null);
                }
            }
        }
    }
    
    
    
    /**
     * Bruce added 21.05.2019
     * Display an input dialog
     * @param title: title of the dialog
     * @param message: the message regarding the input to enter
     * @param initialValue: initial value for the input
     * @param valuePattern: the expression pattern to check validity of the input
     * @returnValueHander: callback event listener, notified with onOK (containing return value as string) and onCancel event
     */
    public void showInputDialog(String title, String message, String initialValue, 
								String valuePattern,
								String allowedValues, 
								EventListener<Event> returnValueHander) {
    	Window win = (Window) Executions.createComponents("macros/inputDialog.zul", null, null);
    	Window dialog = (Window) win.getFellow("inputDialog");
    	dialog.setTitle(title);
    	Label labelMessage = (Label)dialog.getFellow("labelMessage"); 
    	Textbox txtValue = (Textbox)dialog.getFellow("txtValue");
    	Label labelError = (Label)dialog.getFellow("labelError"); 
    	labelMessage.setValue(message);
    	txtValue.setValue(initialValue);
    	labelError.setValue("");

    	dialog.doModal();

    	((Button)dialog.getFellow("btnCancel")).addEventListener("onClick", new EventListener<Event>() {
    		@Override
    		public void onEvent(Event event) throws Exception {
    			dialog.detach();
    			returnValueHander.onEvent( new Event("onCancel"));
    		}
    	});

    	((Button)dialog.getFellow("btnOK")).addEventListener("onClick", new EventListener<Event>() {
    		@Override
    		public void onEvent(Event event) throws Exception {
    			if (txtValue.getValue().trim().isEmpty()) {
    				labelError.setValue("Please enter a value!");
    			}
    			else if (!Pattern.matches(valuePattern, txtValue.getValue())) {
    				labelError.setValue("The entered value is not valid! Allowed characters: " + allowedValues);
    			}
    			else {
    				dialog.detach();
    				returnValueHander.onEvent( new Event("onOK", null, txtValue.getValue()));
    			}
    		}
    	});

    }
    

    public void setBreadcrumbs(int selectedFolderId) {
/*
        List<FolderType> breadcrumbFolders = this.getService().getBreadcrumbs(UserSessionManager.getCurrentUser().getId(), selectedFolderId);
        Collections.reverse(breadcrumbFolders);
        String content = "<table cellspacing='0' cellpadding='5' id='breadCrumbsTable'><tr>";

        int i = 0;
        for (FolderType breadcrumb : breadcrumbFolders) {
            if (i > 0) {
                content += "<td style='font-size: 9pt;'>&gt;</td>";
            }
            content += "<td><a class='breadCrumbLink' style='cursor: pointer; font-size: 9pt; color: Blue; text-decoration: underline;' id='" + breadcrumb.getId().toString() + "'>" + breadcrumb.getFolderName() + "</a></td>";
            i++;
        }

        content += "</tr></table>";
        this.breadCrumbs.setContent(content);
        Clients.evalJavaScript("bindBreadcrumbs();");
*/
    }
}

