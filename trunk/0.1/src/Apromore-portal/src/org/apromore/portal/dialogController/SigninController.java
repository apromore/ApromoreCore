package org.apromore.portal.dialogController;


import org.apromore.portal.exception.DialogException;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_portal.UserType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;




public class SigninController extends Window {

	private HeaderController headerC;
	private Window headerW;
	private Window signinWindow;
	private Button okWindowButton;
	private Button cancelWindowButton;
	private Button pwdRequestWindowButton;
	private Textbox username ;
	private Textbox passwd;
	private MainController mainC;
	private UserType user;

	/**
	 * controller of the view defined in signin.zul
	 * 
	 * @param headerController
	 * @param mainC
	 * @throws DialogException
	 * 
	 * TODO clean up exceptions
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 */
	public SigninController(HeaderController headerController, MainController mainC) 
	throws DialogException, SuspendNotAllowedException, InterruptedException {
		this.mainC = mainC;
		/**
		 * get components
		 */
		this.headerC = headerController;
		this.headerW = (Window) mainC.getFellow("headercomp").getFellow("header");


		final Window win = (Window) Executions.createComponents(
				"macros/signin.zul", null, null);

		this.signinWindow = (Window) win.getFellow("signinWindow");
		this.username = (Textbox) this.signinWindow.getFellow("username");
		this.passwd = (Textbox) this.signinWindow.getFellow("passwd");
		this.okWindowButton = (Button) this.signinWindow.getFellow("okWindowButton");
		this.cancelWindowButton = (Button) this.signinWindow.getFellow("cancelWindowButton");

		okWindowButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				signin();
			}
		});
		this.signinWindow.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				signin();
			}
		});
		cancelWindowButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	

		win.doModal();

	}


	/**
	 * Perform sign out of the current user
	 */
	private void cancel() {
		this.signinWindow.detach();
	}

	private void signin() throws InterruptedException {
		try {			
			String username = this.username.getValue();
			String passwd = this.passwd.getValue();
			// TODO: check credentials
			RequestToManager request = new RequestToManager();
			UserType user = request.ReadUser(username);
			this.user = user;
			this.headerC.userConnected(user);
			this.mainC.updateActions();
		} catch (Exception e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			cancel();
		}
	}
}
