package org.apromore.portal.dialogController;

import java.util.Collection;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.PluginInfo;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.zkoss.bind.annotation.Command;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * @author fauvet
 */
@SuppressWarnings("unchecked")
public class HeaderController extends SelectorComposer<Component> {

    public static final String MANAGER_SERVICE = "managerClient";

    private final MainController mainC;
    private ManagerService managerService;



    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
        }
        return managerService;
    }

    //private final Window headerW;
//    private final Toolbarbutton moreButton;
//    private final Toolbarbutton releaseNotes;
//    private final Toolbarbutton signoutButton;
//    private final Toolbarbutton installedPluginsButton;

    /*
      * called by MainController.java
      */
    public HeaderController(MainController mainController) throws Exception {
        this.mainC = mainController;

        //this.headerW = (Window) mainC.getFellow("headercomp").getFellow("header");
//        this.moreButton = (Toolbarbutton) this.headerW.getFellow("moreButton");
//        this.releaseNotes = (Toolbarbutton) this.headerW.getFellow("releaseNotes");
//        this.lblUsername = (Label) this.headerW.getFellow("lblUsername");
//        this.signoutButton = (Toolbarbutton) this.headerW.getFellow("signoutButton");
//        this.installedPluginsButton = (Toolbarbutton) this.headerW.getFellow("installedPlugins");
//
//        this.moreButton.addEventListener("onClick",
//                new EventListener() {
//                    public void onEvent(Event event) throws Exception {
//                        moreInfo();
//                    }
//                });
//        this.signoutButton.addEventListener("onClick",
//                new EventListener() {
//                    public void onEvent(Event event) throws Exception {
//                        signout();
//                    }
//                });
//        this.releaseNotes.addEventListener("onClick",
//                new EventListener() {
//                    public void onEvent(Event event) throws Exception {
//                        displayReleaseNotes();
//                    }
//                });
//        this.installedPluginsButton.addEventListener("onClick",
//                new EventListener() {
//                    @Override
//                    public void onEvent(final Event event) throws Exception {
//                        displayInstalledPlugins();
//                    }
//                });
    }

    @Command
    public void moreInfo() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.MORE_INFO + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);

    }

    @Command
    public void displayReleaseNotes() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.RELEASE_NOTES + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);
    }

    @Command
    private void signout() throws Exception {
        Messagebox.show("Are you sure you want to logout?", "Prompt", Messagebox.YES|Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        switch (((Integer)evt.getData()).intValue()) {
                            case Messagebox.YES:
                                UserSessionManager.setCurrentFolder(null);
                                UserSessionManager.setCurrentSecurityItem(0);
                                UserSessionManager.setMainController(null);
                                UserSessionManager.setPreviousFolder(null);
                                UserSessionManager.setSelectedFolderIds(null);
                                UserSessionManager.setTree(null);
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
        final Window pluginWindow = (Window) Executions.createComponents("macros/pluginInfo.zul", this.mainC, null);
        Listbox infoListBox = (Listbox) pluginWindow.getFellow("pluginInfoListBox");
        try {
            Collection<PluginInfo> installedPlugins = getService().readInstalledPlugins(null);
            infoListBox.setModel(new ListModelArray(installedPlugins.toArray(), false));
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
            buttonOk.addEventListener("onClick", new EventListener() {
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
}
