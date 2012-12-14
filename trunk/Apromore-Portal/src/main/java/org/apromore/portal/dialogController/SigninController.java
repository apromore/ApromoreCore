package org.apromore.portal.dialogController;

import org.apromore.model.UserType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SigninController extends BaseController {

    private HeaderController headerC;
    private Window headerW;
    private Window signinWindow;
    private Button okWindowButton;
    private Button cancelWindowButton;
    private Button pwdRequestWindowButton;
    private Textbox username;
    private Textbox passwd;
    private MainController mainC;
    private UserType user;

    /**
     * controller of the view defined in signin.zul
     * @param headerController
     * @param mainC
     * @throws org.apromore.portal.exception.DialogException TODO clean up exceptions
     * @throws InterruptedException
     * @throws org.zkoss.zk.ui.SuspendNotAllowedException
     */
    public SigninController(HeaderController headerController, MainController mainC) throws DialogException, SuspendNotAllowedException, InterruptedException {
        this.mainC = mainC;
        this.headerC = headerController;
        this.headerW = (Window) mainC.getFellow("headercomp").getFellow("header");

        Window win = (Window) Executions.createComponents("macros/signin.zul", null, null);

        this.signinWindow = (Window) win.getFellow("signinWindow");
        this.username = (Textbox) this.signinWindow.getFellow("username");
        this.passwd = (Textbox) this.signinWindow.getFellow("passwd");
        this.okWindowButton = (Button) this.signinWindow.getFellow("okWindowButton");
        this.cancelWindowButton = (Button) this.signinWindow.getFellow("cancelWindowButton");

        okWindowButton.addEventListener("onClick", new EventListener() {
            public void onEvent(Event event) throws Exception {
                signin();
            }
        });
        this.signinWindow.addEventListener("onOK", new EventListener() {
            public void onEvent(Event event) throws Exception {
                signin();
            }
        });
        cancelWindowButton.addEventListener("onClick", new EventListener() {
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
            String passwd = hashPassword(this.passwd.getValue());
            UserType user = getService().login(username, passwd);
            if (user == null) {
                Messagebox.show("Invalid username/password", "Attention", Messagebox.OK, Messagebox.ERROR);
                this.signinWindow.setVisible(true);
            } else {
                this.user = user;
                this.headerC.userConnected(user);

                UserSessionManager.setCurrentUser(user);
                this.mainC.updateActions();

                cancel();
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
            cancel();
        }
    }

    public String hashPassword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }
}
