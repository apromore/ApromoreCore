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

import org.apromore.helper.Version;
import org.apromore.model.*;
import org.apromore.model.Detail;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.dialogController.dto.VersionDetailType;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersFilterController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersFragmentsListboxController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersListboxController;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.ext.Paginal;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.Map;
import java.util.Set;

/**
 * Main Controller for the whole application, most of the UI state is managed here.
 * It is automatically instantiated as index.zul is loaded!
 */
public class MainController extends BaseController {

    private static final long serialVersionUID = 5147685906484044300L;
	private static MainController controller = null;

    private EventQueue<Event> qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);

    private static final String WELCOME_TEXT = "Welcome %s. Release notes (%s)"; //Welcome %s.

    private PortalContext portalContext;
    private MenuController menu;
    private SimpleSearchController simplesearch;
    private ShortMessageController shortmessageC;
    private BaseListboxController baseListboxController;
    private BaseDetailController baseDetailController;
    private BaseFilterController baseFilterController;

    private NavigationController navigation;

    public Html breadCrumbs;
    private Paginal pg;

    private String host;
    private String versionNumber;
    private String buildDate;
	
	public static MainController getController() {
        return controller;
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

            Window mainW = (Window) this.getFellow("mainW");
            Hbox pagingandbuttons = (Hbox) mainW.getFellow("pagingandbuttons");

            Window shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
            this.breadCrumbs = (Html) mainW.getFellow("breadCrumbs");
            this.pg = (Paginal) mainW.getFellow("pg");
            this.shortmessageC = new ShortMessageController(shortmessageW);
            this.simplesearch = new SimpleSearchController(this);
            this.menu = new MenuController(this);
            this.portalContext = new PluginPortalContext(this);
            this.navigation = new NavigationController(this);
            Toolbarbutton moreButton = (Toolbarbutton) this.getFellow("moreButton");
            Toolbarbutton releaseNotes = (Toolbarbutton) this.getFellow("releaseNotes");
            Toolbarbutton signoutButton = (Toolbarbutton) this.getFellow("signoutButton");
            Toolbarbutton installedPluginsButton = (Toolbarbutton) this.getFellow("installedPlugins");
            Toolbarbutton webDavButton = (Toolbarbutton) this.getFellow("webDav");
            Toolbarbutton developerResourcesButton = (Toolbarbutton) this.getFellow("developerResources");


            setHeaderText(releaseNotes);
            switchToProcessSummaryView();
            UserSessionManager.setMainController(this);
            pagingandbuttons.setVisible(true);

            moreButton.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            moreInfo();
                        }
                    });
            signoutButton.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            signout();
                        }
                    });
            releaseNotes.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            displayReleaseNotes();
                        }
                    });
            installedPluginsButton.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(final Event event) throws Exception {
                            displayInstalledPlugins();
                        }
                    });
            webDavButton.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(final Event event) throws Exception {
                            displayWebDav();
                        }
                    });
            developerResourcesButton.addEventListener("onClick",
                    new EventListener<Event>() {
                        public void onEvent(final Event event) throws Exception {
                            displayDeveloperResources();
                        }
                    });
            qe.subscribe(
                    new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            if (Constants.EVENT_MESSAGE_SAVE.equals(event.getName())) {
                                clearProcessVersions();
                                reloadProcessSummaries();
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

    public void loadWorkspace() {
        setHeaderText((Toolbarbutton) this.getFellow("releaseNotes"));

		String userId = UserSessionManager.getCurrentUser().getId();
        updateTabs(userId);
        updateActions();

        setHeaderText((Toolbarbutton) this.getFellow("releaseNotes"));
        int currentParentFolderId = UserSessionManager.getCurrentFolder() == null || UserSessionManager.getCurrentFolder().getId() == 0 ? 0 : UserSessionManager.getCurrentFolder().getId();

        this.loadTree();

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

    public void loadTree() {
        List<FolderType> folders = this.getService().getWorkspaceFolderTree(UserSessionManager.getCurrentUser().getId());
        UserSessionManager.setTree(folders);
        this.navigation.loadWorkspace();
    }

    /**
     * Display version processes in processSummaries: if isQueryResult, the
     * query is given by version of process
     * @param processSummaries the list of process summaries to display
     * @param isQueryResult is this from a query (simsearch, clustering, etc.)
     */
    public void displayProcessSummaries(final ProcessSummariesType processSummaries, final Boolean isQueryResult) {
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
        ((ProcessListboxController) this.baseListboxController).displayProcessSummaries(subFolders, processSummaries, isQueryResult);
    }

    // disable/enable features depending on user status
    public void updateActions() {
        Boolean connected = UserSessionManager.getCurrentUser() != null;

        // disable/enable menu items in menu bar
        for (Component C : this.menu.getMenuB().getFellows()) {
            if (C.getClass().getName().compareTo("org.zkoss.zul.Menuitem") == 0) {
                if (C.getId().equals("designPatternCr")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designReference")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designPatternCo")) {
                    ((Menuitem) C).setDisabled(true);
//              } else if (C.getId().equals("designConfiguration")) {
//                  ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designExtension")) {
                    ((Menuitem) C).setDisabled(true);
                } else {
                    ((Menuitem) C).setDisabled(!connected);
                }
            }
        }
    }

    public void reloadProcessSummaries() {
        this.simplesearch.clearSearches();
        switchToProcessSummaryView();
        pg.setActivePage(0);

        FolderType currentFolder = UserSessionManager.getCurrentFolder();
        List<FolderType> subFolders = getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.ProcessSummaryListModel model = ((ProcessListboxController) this.baseListboxController).displayProcessSummaries(subFolders, false);

        this.displayMessage(
            model.getSize() + " out of " + model.getTotalProcessCount() +
	    (model.getTotalProcessCount() > 1 ? " processes." : " process.")
	);

        loadWorkspace();
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
     * @param processVersions a selection of process versions to delete.
     * @throws InterruptedException
     */
    public void deleteProcessVersions(final Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws InterruptedException {
        try {
            getService().deleteProcessVersions(processVersions);
            switchToProcessSummaryView();
            this.baseListboxController.refreshContent();
            String message;
            int nb = 0;

            // to count how many process version(s) deleted
            Collection<List<VersionSummaryType>> sumTypes = processVersions.values();
            for (List<VersionSummaryType> sumType : sumTypes) {
                nb += sumType.size();
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

        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
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
            SignavioSession session = new SignavioSession(editSession, null, this, process, version, null, null, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "macros/openModelInSignavio.zul?id=" + id;
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot edit " + process.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Display two process versions and allow their differences to be highlighted.
     *
     * @param process1 the process summary
     * @param version1 the version of the process
     * @param process2 the process summary
     * @param version2 the version of the process
     * @param nativeType the native type of the process
     * @param annotation the annotation of that process
     * @param readOnly is this model readonly or not
     * @param requestParameterTypes request parameters types.
     * @throws InterruptedException
     */
    public void compareProcesses(final ProcessSummaryType process1, final VersionSummaryType version1,
            final ProcessSummaryType process2, final VersionSummaryType version2,
            final String nativeType, final String annotation,
            final String readOnly, Set<RequestParameterType<?>> requestParameterTypes) throws InterruptedException {
        String instruction = "";

        EditSessionType editSession1 = createEditSession(process1, version1, nativeType, annotation);
        EditSessionType editSession2 = createEditSession(process2, version2, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();
            SignavioSession session = new SignavioSession(editSession1, editSession2, this, process1, version1, process2, version2, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "macros/compareModelsInSignavio.zul?id=" + id;
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot compare " + process1.getName() + " and " + process2.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
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

    public void clearProcessVersions() {
        switchToProcessSummaryView();
        ((ProcessVersionDetailController) this.baseDetailController).clearProcessVersions();
    }

    public void displaySimilarityClusters(final ClusterFilterType filter) {
        switchToSimilarityClusterView();
        ((SimilarityClustersListboxController) this.baseListboxController).displaySimilarityClusters(filter);
    }

    @SuppressWarnings("unchecked")
    public Set<ProcessSummaryType> getSelectedProcesses() {
        if (this.baseListboxController instanceof ProcessListboxController) {
            ProcessListboxController processController = (ProcessListboxController) getBaseListboxController();
            return processController.getListModel().getSelection();
        } else {
            return new HashSet<>();
        }
    }

    /**
     * @return a map with all currently selected process models and the corresponding selected versions
     * @throws ParseException
     */
    public Map<ProcessSummaryType, List<VersionSummaryType>> getSelectedProcessVersions() {
        Map<ProcessSummaryType, List<VersionSummaryType>> processVersions = new HashMap<>();
        String versionNumber;

        if (getBaseListboxController() instanceof ProcessListboxController) {
            ArrayList<VersionSummaryType> versionList;

            Set<VersionDetailType> selectedVersions = ((ProcessVersionDetailController) getDetailListbox()).getListModel().getSelection();
            Set<Object> selectedProcesses = (Set<Object>) getBaseListboxController().getListModel().getSelection();
            for (Object obj : selectedProcesses) {
                if (obj instanceof ProcessSummaryType) {
                    versionList = new ArrayList<>();
                    if (selectedVersions != null) {
                        for (VersionDetailType detail: selectedVersions) {
                            versionList.add(detail.getVersion());
                        }
                    } else {
                        for (VersionSummaryType summaryType : ((ProcessSummaryType) obj).getVersionSummaries()) {
                            versionNumber = ((ProcessSummaryType) obj).getLastVersion();
                            if (summaryType.getVersionNumber().compareTo(versionNumber) == 0) {
                                versionList.add(summaryType);
                            }
                        }
                    }
                    processVersions.put((ProcessSummaryType) obj, versionList);
                }
            }
        }
        return processVersions;
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



    @Command
    protected void moreInfo() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.MORE_INFO + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);

    }

    @Command
    protected void displayReleaseNotes() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.RELEASE_NOTES + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);
    }


    @Command
    protected void displayWebDav() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.WEB_DAV + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);
    }

    @Command
    protected void displayDeveloperResources() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.DEVELOPER_RESOURCES + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);
    }



    @Command
    protected void signout() throws Exception {
        Messagebox.show("Are you sure you want to logout?", "Prompt", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener<Event>() {
                    public void onEvent(Event evt) throws Exception {
                        switch ((Integer) evt.getData()) {
                            case Messagebox.YES:
                                UserSessionManager.setCurrentFolder(null);
                                UserSessionManager.setCurrentSecurityItem(0);
                                UserSessionManager.setMainController(null);
                                UserSessionManager.setPreviousFolder(null);
                                UserSessionManager.setSelectedFolderIds(null);
                                UserSessionManager.setTree(null);
                                //getService().writeUser(UserSessionManager.getCurrentUser());
                                Executions.sendRedirect("/j_spring_security_logout");
                                break;
                            case Messagebox.NO:
                                break;
                        }
                    }
                }
        );
    }

    @Command
    protected void displayInstalledPlugins() throws InterruptedException {
        final Window pluginWindow = (Window) Executions.createComponents("macros/pluginInfo.zul", this, null);
        Listbox infoListBox = (Listbox) pluginWindow.getFellow("pluginInfoListBox");
        try {
            List<PluginInfo> installedPlugins = new ArrayList<>(getService().readInstalledPlugins(null));
            infoListBox.setModel(new ListModelList<>(installedPlugins, false));
            infoListBox.setItemRenderer(new ListitemRenderer() {
                @Override
                public void render(final Listitem item, final Object data, final int index) throws Exception {
                    if (data != null && data instanceof PluginInfo) {
                        PluginInfo info = (PluginInfo) data;
                        item.appendChild(new Listcell(info.getName()));
                        item.appendChild(new Listcell(info.getVersion()));
                        item.appendChild(new Listcell(info.getType()));
                        Listcell dCell = new Listcell();
                        Label dLabel = new Label(info.getDescription());
                        dLabel.setWidth("100px");
                        dLabel.setMultiline(true);
                        dCell.appendChild(dLabel);
                        item.appendChild(dCell);
                        item.appendChild(new Listcell(info.getAuthor()));
                        item.appendChild(new Listcell(info.getEmail()));
                    }
                }
            });
            Button buttonOk = (Button) pluginWindow.getFellow("ok");
            buttonOk.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    pluginWindow.detach();
                }
            });
            pluginWindow.doModal();
        } catch (Exception e) {
            Messagebox.show("Error retrieving installed Plugins: "+e.getMessage(), "Error", Messagebox.OK,
                    Messagebox.ERROR);
        }
    }

    /* Removes the currently displayed listbox, detail and filter view */
    private void deattachDynamicUI() {
        this.getFellow("baseListbox").getFellow("tablecomp").getChildren().clear();
        this.getFellow("baseDetail").getFellow("detailcomp").getChildren().clear();
        this.getFellow("baseFilter").getFellow("filtercomp").getChildren().clear();
    }

    /* Attaches the the listbox, detail and filter view */
    private void reattachDynamicUI() {
        this.getFellow("baseListbox").getFellow("tablecomp").appendChild(baseListboxController);
        this.getFellow("baseDetail").getFellow("detailcomp").appendChild(baseDetailController);
        this.getFellow("baseFilter").getFellow("filtercomp").appendChild(baseFilterController);
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
        this.baseFilterController = new BaseFilterController(this);

        reattachDynamicUI();
        reloadProcessSummaries();
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
        this.baseFilterController = new SimilarityClustersFilterController(this);
        this.baseListboxController = new SimilarityClustersListboxController(this,
                (SimilarityClustersFilterController) this.baseFilterController,
                (SimilarityClustersFragmentsListboxController) this.baseDetailController);

        reattachDynamicUI();
    }

    /* Load the props for this app. */
    private void loadProperties() throws IOException {
        setHost("http://" + config.getSiteExternalHost() + ":" + config.getSiteExternalPort());
        setVersionNumber(config.getVersionNumber());
        setBuildDate(config.getVersionBuildDate());
    }

    /* From the data in the properties automagically update the label in the header. */
    private void setHeaderText(Toolbarbutton releaseNotes) {
        releaseNotes.setLabel(String.format(WELCOME_TEXT, UserSessionManager.getCurrentUser().getFirstName(), versionNumber));
    }

    /* From a list of version summary types find the max version number. */
    private String findMaxVersion(ProcessSummaryType process) {
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

    public String getHost() {
        return host;
    }

    public void setHost(final String newHost) {
        host = newHost;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(final String newVersionNumber) {
        versionNumber = newVersionNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(final String newBuildDate) {
        buildDate = newBuildDate;
    }

    public void addResult(List<ResultPQL> results, String userID, List<Detail> details, String query, String nameQuery) {
        TabQuery newTab = new TabQuery(nameQuery, userID, details, query, portalContext);
        newTab.setTabpanel(results);
    }

    private void updateTabs(String userId){
        Window mainW = (Window) this.getFellow("mainW");

        Tabbox tabbox = (Tabbox) mainW.getFellow("tabbox");

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(userId);
        if(tabs.isEmpty()) {
            for(Component component : tabbox.getTabs().getChildren()) {
                Tab tab = (Tab) component;
                if(tab.isClosable()) {
                    SessionTab.getSessionTab(portalContext).addTabToSession(userId, tab);
                }
            }
        }else {
            for(Tab tab : tabs) {
                if(tab instanceof PortalTab && ((PortalTab) tab).isNew()){
                    ((PortalTab) tab).setNew(false);
                    tabbox.getTabs().appendChild(tab);
                    ((PortalTab) tab).getTabpanel().setParent(tabbox.getTabpanels());
                }
            }
        }
    }

}

