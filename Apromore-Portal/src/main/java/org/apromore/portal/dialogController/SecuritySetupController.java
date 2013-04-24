package org.apromore.portal.dialogController;

import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecuritySetupController extends BaseController {

    private MainController mainController;
    private FolderTreeController folderTreeController;
    private FindUsersController findUsersController;
    private PermissionsController permissionsController;

    public SecuritySetupController(MainController mainController) throws DialogException {

        this.mainController = mainController;
        try {
            final Window win = (Window) Executions.createComponents("/macros/securitySetup.zul", null, null);

            this.permissionsController = new PermissionsController(this, win);
            this.folderTreeController = new FolderTreeController(this, win);
            this.findUsersController = new FindUsersController(this, win);

            win.doModal();

//            win.addEventListener("onClose",
//                    new EventListener() {
//                        public void onEvent(Event event) throws Exception {
//                            UserSessionManager.getMainController().loadWorkspace();
//                        }
//                    });

        } catch (Exception e) {
            throw new DialogException("Error in controller: " + e.getMessage());
        }

    }

    public PermissionsController getPermissionsController(){
        return this.permissionsController;
    }

    public FindUsersController getFindUsersController(){
        return this.findUsersController;
    }

    public MainController getMainController(){
        return this.mainController;
    }
}
