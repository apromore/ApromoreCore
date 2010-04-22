package org.apromore.portal.dialogController;


import org.apromore.portal.exception.DialogException;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_portal.UserType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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
		this.mainC.resetDisplayedInformation();
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
		this.consultAccountButton.setLabel(this.mainC.getCurrentUser().getFirstname() + " connected ");
		this.consultAccountButton.setVisible(true);
		this.signinButton.setVisible(false);
		this.signoutButton.setVisible(true);
		this.mainC.getSimplesearch().Refresh();
	}
}
