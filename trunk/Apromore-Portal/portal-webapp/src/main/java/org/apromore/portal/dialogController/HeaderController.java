package org.apromore.portal.dialogController;

import java.util.Collection;

import org.apromore.model.PluginInfo;
import org.apromore.model.UserType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * @author fauvet
 */
public class HeaderController extends BaseController {

    private final MainController mainC;
    private SigninController signinC;
    /*
      * header
      */
    private final Window headerW;
    private final Toolbarbutton moreButton;
    private final Toolbarbutton releaseNotes;
    private final Toolbarbutton signinButton;
    private final Toolbarbutton signoutButton;
    private final Toolbarbutton createAccountButton;
    private final Toolbarbutton consultAccountButton;
    private final Toolbarbutton installedPluginsButton;

    /*
      * called by MainController.java
      */
    public HeaderController(final MainController mainController) throws Exception {
        this.mainC = mainController;
        /**
         * gets components of header
         */
        this.headerW = (Window) mainC.getFellow("headercomp").getFellow("header");
        this.moreButton = (Toolbarbutton) this.headerW.getFellow("moreButton");
        this.releaseNotes = (Toolbarbutton) this.headerW.getFellow("releaseNotes");
        this.signinButton = (Toolbarbutton) this.headerW.getFellow("signinButton");
        this.signoutButton = (Toolbarbutton) this.headerW.getFellow("signoutButton");
        this.consultAccountButton = (Toolbarbutton) this.headerW.getFellow("consultAccountButton");
        this.createAccountButton = (Toolbarbutton) this.headerW.getFellow("createAccountButton");
        this.installedPluginsButton = (Toolbarbutton) this.headerW.getFellow("installedPlugins");

        moreButton.addEventListener("onClick",
                new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        moreInfo();
                    }
                });
        signinButton.addEventListener("onClick",
                new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        signin(mainC);
                    }
                });
        signoutButton.addEventListener("onClick",
                new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        signout();
                    }
                });
        createAccountButton.addEventListener("onClick",
                new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        createAccount();
                    }
                });

        this.releaseNotes.addEventListener("onClick",
                new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        displayReleaseNotes();
                    }
                });

        this.installedPluginsButton.addEventListener("onClick",
                new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                displayInstalledPlugins();
            }
        });

    }

    /**
     * Displays the Plugin information
     *
     * @throws InterruptedException
     */
    protected void displayInstalledPlugins() throws InterruptedException {
        final Window pluginWindow = (Window) Executions.createComponents("macros/pluginInfo.zul",
                this.mainC, null);
        Listbox infoListBox = (Listbox) pluginWindow.getFellow("pluginInfoListBox");
        try {
            Collection<PluginInfo> installedPlugins = getService().readInstalledPlugins(null);
            infoListBox.setModel(new ListModelArray(installedPlugins.toArray(), false));
            infoListBox.setItemRenderer(new ListitemRenderer() {

                @Override
                public void render(final Listitem item, final Object data) throws Exception {
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

    protected void moreInfo() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.MORE_INFO + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);

    }

    protected void displayReleaseNotes() {
        String instruction;
        int offsetH = 100, offsetV = 200;
        instruction = "window.open('" + Constants.RELEASE_NOTES + "','','top=" + offsetH + ",left=" + offsetV
                + ",height=600,width=800,scrollbars=1,resizable=1'); ";
        Clients.evalJavaScript(instruction);
    }

    /**
     * Create an account
     *
     * @throws InterruptedException
     */
    protected void createAccount() throws InterruptedException {
        Messagebox.show("Not yet available...", "Attention", Messagebox.OK,
                Messagebox.INFORMATION);
    }

    private void signout() throws Exception {
        getService().writeUser(this.mainC.getCurrentUser());

        this.consultAccountButton.setVisible(false);
        this.signinButton.setVisible(true);
        this.signoutButton.setVisible(false);
        this.mainC.setCurrentUser(null);
        this.mainC.updateActions();
        this.mainC.resetUserInformation();
        this.mainC.reloadProcessSummaries();

    }

    /**
     * Perform sign in
     *
     * @param mainC
     * @throws InterruptedException
     */
    private void signin(final MainController mainC) throws InterruptedException {

        try {
            this.signinC = new SigninController(this, mainC);

        } catch (DialogException e) {
            Messagebox.show("Error: incorrect user details",
                    "Cancel", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void userConnected(final UserType user) {


        this.mainC.setCurrentUser(user);
        this.consultAccountButton.setLabel(this.mainC.getCurrentUser().getFirstname() + " connected. ");
        this.consultAccountButton.setVisible(true);
        this.signinButton.setVisible(false);
        this.signoutButton.setVisible(true);
        this.mainC.getSimplesearch().Refresh();
    }
}
