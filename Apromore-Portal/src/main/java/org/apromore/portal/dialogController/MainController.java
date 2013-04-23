package org.apromore.portal.dialogController;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditSessionType;
import org.apromore.model.FolderType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.PluginMessage;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersFilterController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersFragmentsListboxController;
import org.apromore.portal.dialogController.similarityclusters.SimilarityClustersListboxController;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

/**
 * Main Controller for the whole application, most of the UI state is managed here.
 * It is automatically instantiated as index.zul is loaded!
 */
public class MainController extends BaseController {

    private static final long serialVersionUID = 5147685906484044300L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private HeaderController header;
    private MenuController menu;
    private SimpleSearchController simplesearch;
    private ShortMessageController shortmessageC;
    private Window shortmessageW;
    private String host;
    private List<SearchHistoriesType> searchHistory;
    private BaseListboxController baseListboxController;
    private BaseDetailController baseDetailController;
    private NavigationController navigation;

    public Html breadCrumbs;

    /**
     * onCreate is executed after the main window has been created it is
     * responsible for instantiating all necessary controllers (one for each
     * window defined in the interface)
     * see description in index.zul
     * @throws InterruptedException
     */
    public void onCreate() throws InterruptedException {
        try {
            // if client browser is not gecko3 based (such as firefox) raise an exception
            if (!Executions.getCurrent().isGecko() && !Executions.getCurrent().isGecko3()) {
                throw new Exception("Sorry, we currently support firefox only.");
            }

            Window mainW = (Window) this.getFellow("mainW");
            Hbox pagingandbuttons = (Hbox) mainW.getFellow("pagingandbuttons");
            //Html folders = (Html) mainW.getFellow("folders");

            this.shortmessageW = (Window) this.getFellow("shortmessagescomp").getFellow("shortmessage");
            this.breadCrumbs = (Html) mainW.getFellow("breadCrumbs");
            this.shortmessageC = new ShortMessageController(shortmessageW);
            this.header = new HeaderController(this);
            this.simplesearch = new SimpleSearchController(this);
            this.menu = new MenuController(this);
            this.navigation = new NavigationController(this);

            switchToProcessSummaryView();

            this.searchHistory = new ArrayList<SearchHistoriesType>();
            //String msgWhenClose = null;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);

            Properties properties = new Properties();
            properties.load(inputStream);
            this.host = properties.getProperty("Host");
            UserSessionManager.setMainController(this);
            pagingandbuttons.setVisible(true);
        } catch (Exception e) {
            String message;
            if (e.getMessage() == null) {
                message = "Please contact Apromore's administrator";
            } else {
                message = e.getMessage();
            }
            e.printStackTrace();
            Messagebox.show("Repository not available (" + message + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void loadWorkspace() {
        updateActions();
        String userId = UserSessionManager.getCurrentUser().getId();
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
     * register an event listener for the clientInfo event (to prevent user to close the browser window)
     */
    public void onClientInfo(final ClientInfoEvent event) {
        Clients.confirmClose(Constants.MSG_WHEN_CLOSE);
    }

    /**
     * Display version processes in processSummaries: if isQueryResult, the
     * query is given by version of process
     * @param processSummaries
     * @param isQueryResult
     * @throws Exception
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
        @SuppressWarnings("unchecked")
        Iterator<Component> itC = (Iterator<Component>) this.menu.getMenuB().getFellows().iterator();
        while (itC.hasNext()) {
            Component C = itC.next();
            if (C.getClass().getName().compareTo("org.zkoss.zul.Menuitem") == 0) {
                if (C.getId().equals("designPatternCr")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designReference")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designPatternCo")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designConfiguration")) {
                    ((Menuitem) C).setDisabled(true);
                } else if (C.getId().equals("designExtension")) {
                    ((Menuitem) C).setDisabled(true);
                } else {
                    ((Menuitem) C).setDisabled(!connected);
                }
            }
        }
    }

    public void reloadProcessSummaries() {
        ProcessSummariesType processSummaries = new ProcessSummariesType();
        processSummaries.getProcessSummary().clear();
        UserType user = UserSessionManager.getCurrentUser();
        FolderType currentFolder = UserSessionManager.getCurrentFolder();

        List<ProcessSummaryType> processSummaryTypes = getService().getProcesses(user.getId(), currentFolder == null ? 0 : currentFolder.getId());
        for (ProcessSummaryType processSummary : processSummaryTypes) {
            processSummaries.getProcessSummary().add(processSummary);
        }

        String message;
        if (processSummaries.getProcessSummary().size() > 1) {
            message = " processes.";
        } else {
            message = " process.";
        }
        this.displayMessage(processSummaries.getProcessSummary().size() + message);
        this.simplesearch.clearSearches();
        this.displayProcessSummaries(processSummaries, false);

        loadWorkspace();
    }

    /**
     * reset displayed informations: - short message - process summaries -
     * simple search
     * @throws Exception
     */
    public void resetUserInformation() throws Exception {
        eraseMessage();
        this.simplesearch.clearSearches();
    }

    /**
     * Forward to the controller ProcessListBoxController the request to add the
     * process to the table
     */
    public void displayNewProcess(final ProcessSummaryType returnedProcess) {
        switchToProcessSummaryView();
        ((ProcessListboxController) this.baseListboxController).displayNewProcess(returnedProcess);
        this.displayMessage(this.baseListboxController.getListModel().getSize() + " processes.");
        //loadWorkspace();
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
            ((ProcessListboxController) this.baseListboxController).unDisplay(processVersions);
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

    /**
     * Call editor to edit process version whose id is processId, name is
     * processName and version name is version. nativeType identifies language
     * to be used to edit the process version. If annotation is instantiated, it
     * identifies the annotation file to be used. If readOnly=1, annotations
     * only are editable.
     * @param version
     * @param nativeType
     * @param annotation
     * @param readOnly
     * @throws InterruptedException
     * @throws Exception
     */
    public void editProcess(final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation,
            final String readOnly) throws InterruptedException {
        String instruction = "";
        String url = getHost();
        int offsetH = 100, offsetV = 200;

        EditSessionType editSession = new EditSessionType();
        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(UserSessionManager.getCurrentUser().getUsername());

        editSession.setOriginalBranchName(version.getName());
        editSession.setVersionNumber(version.getVersionNumber());

        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        // todo: Work out is new are forcing a new branch or not.
        if (true) {
            editSession.setCreateNewBranch(false);
        } else {
            editSession.setCreateNewBranch(true);
        }
        if (annotation == null) {
            editSession.setWithAnnotation(false);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }

        try {
            // create and store an edit session
            int editSessionCode = getService().writeEditSession(editSession);            
            SignavioController.editSession = editSession;
            SignavioController.mainC = this;
            SignavioController.process = process;
            SignavioController.version = version;            

            url = "macros/OpenModelInSignavio.zul";
            
            instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV + ",height=600,width=800,scrollbars=1,resizable=1'); ";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot edit " + process.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void displayMessage(final String mes) {
        this.shortmessageC.displayMessage(mes);
    }

    public void eraseMessage() {
        this.shortmessageC.eraseMessage();
    }

    public HeaderController getHeader() {
        return header;
    }

    public void setHeader(final HeaderController header) {
        this.header = header;
    }

    public MenuController getMenu() {
        return menu;
    }

    public void setMenu(final MenuController menu) {
        this.menu = menu;
    }

    public BaseListboxController getBaseListboxController() {
        return baseListboxController;
    }

    public BaseDetailController getDetailListbox() {
        return baseDetailController;
    }

    public SimpleSearchController getSimplesearch() {
        return simplesearch;
    }

    public void setSimplesearch(final SimpleSearchController simplesearch) {
        this.simplesearch = simplesearch;
    }


    public ShortMessageController getShortmessageC() {
        return shortmessageC;
    }

    public void setShortmessageC(final ShortMessageController shortmessageC) {
        this.shortmessageC = shortmessageC;
    }

    public Window getShortmessageW() {
        return shortmessageW;
    }

    public void setShortmessageW(final Window shortmessageW) {
        this.shortmessageW = shortmessageW;
    }

    public Logger getLOG() {
        return LOGGER;
    }

    public String getHost() {
        return host;
    }

    public List<SearchHistoriesType> getSearchHistory() {
        return searchHistory;
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
     * @return
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
        HashMap<String, String> formats = new HashMap<String, String>();
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
            //ZK returns untyped Set, but we can assume it is of type ProcessSummaryType here!
            return processController.getListModel().getSelection();
        } else {
            return new HashSet<ProcessSummaryType>();
        }
    }


    /**
     * Removes the currently displayed listbox, detail and filter view
     */
    private void deattachDynamicUI() {
        getFellow("baseListbox").getFellow("tablecomp").getChildren().clear();
        getFellow("baseDetail").getFellow("detailcomp").getChildren().clear();
    }


    /**
     * Attaches the the listbox, detail and filter view
     */
    private void reattachDynamicUI() {
        getFellow("baseListbox").getFellow("tablecomp").appendChild(this.baseListboxController);
        getFellow("baseDetail").getFellow("detailcomp").appendChild(this.baseDetailController);
    }

    /**
     * Switches all dynamic UI elements to the ProcessSummaryView. Affects the
     * listbox, detail and filter view
     */
    private void switchToProcessSummaryView() {
        // TODO should replace this with TabBox!! and without all these
        // instanceof checks!
        if (this.baseListboxController != null) {
            if ((this.baseListboxController instanceof ProcessListboxController)) {
                // Everything is correctly setup
                return;
            } else {
                // Another view is currently displayed
                deattachDynamicUI();
            }
        }

        // Otherwise create new Listbox
        this.baseListboxController = new ProcessListboxController(this);
        this.baseDetailController = new ProcessVersionDetailController(this);

        reattachDynamicUI();

        ((South) getFellow("leftSouthPanel")).setTitle("Process Details");

        reloadProcessSummaries();
    }

    /**
     * Switches all dynamic UI elements to the SimilarityClusterView. Affects
     * the listbox, detail and filter view
     */
    private void switchToSimilarityClusterView() {

        // TODO should replace this with TabBox!! and without all these
        // instanceof checks!

        if (this.baseListboxController != null) {
            if ((this.baseListboxController instanceof SimilarityClustersListboxController)) {
                return;
            } else {
                deattachDynamicUI();
            }
        }

        // Otherwise create new Listbox
        SimilarityClustersFilterController simFilterController = new SimilarityClustersFilterController(this);
        this.baseDetailController = new SimilarityClustersFragmentsListboxController(this);
        this.baseListboxController = new SimilarityClustersListboxController(this, simFilterController, (SimilarityClustersFragmentsListboxController) this.baseDetailController);

        reattachDynamicUI();

        ((South) getFellow("leftSouthPanel")).setTitle("Cluster Details");
    }

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

    /* Update the List box from the folder view with what is selected and what isn't. */
    @SuppressWarnings("unchecked")
    public void updateSelectedListBox(List<Integer> processIds) {
        BaseListboxController baseListBoxController = getBaseListboxController();
        if (baseListBoxController != null) {
            baseListBoxController.getListModel().clearSelection();
            if ((baseListBoxController instanceof ProcessListboxController)) {
                for (ProcessSummaryType pst : (List<ProcessSummaryType>) baseListBoxController.getListModel()) {
                    for (Integer i : processIds) {
                        if (pst != null && pst.getId().equals(i)) {
                            baseListBoxController.getListModel().addSelection(pst);
                        }
                    }
                }
                displayProcessVersions((ProcessSummaryType) getBaseListboxController().getListModel().getSelection().iterator().next());
            }
        }
    }

}

