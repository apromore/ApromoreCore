package org.apromore.portal.dialogController;

import java.util.List;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.UserType;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Window;

/**
 * Base Controller that all controllers inherit.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BaseController extends Window {

    public static final String MANAGER_SERVICE = "managerClient";

    private ManagerService managerService;


    public ManagerService getService() {
        if (managerService == null) {
            managerService = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
            setManagerService(managerService);
        }
        return managerService;
    }

    private void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }

}
