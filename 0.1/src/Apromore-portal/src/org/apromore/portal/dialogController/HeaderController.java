package org.apromore.portal.dialogController;


import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.UserType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;



/**
 * @author fauvet
 *
 */
public class HeaderController extends Window {

	private MainController mainC;
	private SigninController signinC;
	/*
	 * header
	 */
	private Window headerW;
	private Toolbarbutton moreButton;
	private Toolbarbutton releaseNotes;
	private Toolbarbutton signinButton;
	private Toolbarbutton signoutButton;
	private Toolbarbutton createAccountButton;
	private Toolbarbutton consultAccountButton;
	

	/*
	 * called by MainController.java
	 */
	public HeaderController(MainController mainController) throws Exception {
		
		this.mainC = mainController;
		
		/**
		 * gets components of header
		 */
		this.headerW = (Window) mainC.getFellow("headercomp").getFellow("header");
		this.releaseNotes = (Toolbarbutton) this.headerW.getFellow("releaseNotes");
		this.signinButton = (Toolbarbutton) this.headerW.getFellow("signinButton");
		this.signoutButton = (Toolbarbutton) this.headerW.getFellow("signoutButton");
		this.consultAccountButton = (Toolbarbutton) this.headerW.getFellow("consultAccountButton");
		this.createAccountButton = (Toolbarbutton) this.headerW.getFellow("createAccountButton");
		
		
		signinButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				signin(mainC);
			}
		});			
		signoutButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				signout();
			}
		});		
		createAccountButton.addEventListener("onClick", 
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				createAccount();
			}
		});
		
		this.releaseNotes.addEventListener("onClick", 
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				displayReleaseNotes();
			}
		});
	}

	protected void displayReleaseNotes() {
		String instruction ;		
		int offsetH = 100, offsetV=200;
		instruction = "window.open('" + Constants.RELEASE_NOTES + "','','top=" + offsetH + ",left=" + offsetV 
		+ ",height=600,width=800,scrollbars=1,resizable=1'); ";
		Clients.evalJavaScript(instruction);
	}

	/**
	 * Create an account
	 */
	protected void createAccount() {
		// TODO Auto-generated method stub
		
	}

	private void signout() throws Exception {
		
		/**
		 * send back to the repository the new search histories
		 */
		RequestToManager request = new RequestToManager();
		request.WriteUser(this.mainC.getCurrentUser());
		
		this.consultAccountButton.setVisible(false);
		this.signinButton.setVisible(true);
		this.signoutButton.setVisible(false);
		this.mainC.setCurrentUser(null);
		this.mainC.updateActions();
		this.mainC.resetUserInformation();
		this.mainC.refreshProcessSummaries();
		
	}
	/**
	 * Perform sign in 
	 * @param mainC 
	 * @throws InterruptedException 
	 */
	private void signin(MainController mainC) throws InterruptedException {

		try {
			this.signinC = new SigninController(this, mainC);
			
		} catch (DialogException e) {
			Messagebox.show("Error: incorrect user details",
					"Cancel", Messagebox.OK, Messagebox.ERROR);
		}
	}

	public void userConnected (UserType user) {

		
		this.mainC.setCurrentUser(user);
		this.consultAccountButton.setLabel(this.mainC.getCurrentUser().getFirstname() + " connected. ");
		this.consultAccountButton.setVisible(true);
		this.signinButton.setVisible(false);
		this.signoutButton.setVisible(true);
		this.mainC.getSimplesearch().Refresh();
	}
}
