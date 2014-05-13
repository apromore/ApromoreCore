package org.apromore.portal.dialogController;

import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

/**
 * Controller used to setup security for folders and processes.
 *
 * @author Igor
 */
public class SecuritySetupController extends BaseController {

    private MainController mainController;
    private SecurityFindGroupsController findGroupsController;
    private SecurityPermissionsController permissionsController;
    private SecurityFolderTreeController folderTreeController;

    public SecuritySetupController(MainController mainController) throws DialogException {
        this.mainController = mainController;
        try {
            final Window win = (Window) Executions.createComponents("/macros/securitySetup.zul", null, null);

            this.permissionsController = new SecurityPermissionsController(this, win);
            this.findGroupsController = new SecurityFindGroupsController(this, win);
            this.folderTreeController = new SecurityFolderTreeController(this, win);

            win.doModal();

            win.addEventListener("onClose", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    UserSessionManager.getMainController().loadWorkspace();
                }
            });

        } catch (Exception e) {
            throw new DialogException("Error in controller: " + e.getMessage());
        }
    }

    public SecurityPermissionsController getPermissionsController(){
        return this.permissionsController;
    }

    public SecurityFindGroupsController getFindGroupsController(){
        return this.findGroupsController;
    }

    public SecurityFolderTreeController getFolderTreeController(){
        return this.folderTreeController;
    }

    public MainController getMainController(){
        return this.mainController;
    }
}
