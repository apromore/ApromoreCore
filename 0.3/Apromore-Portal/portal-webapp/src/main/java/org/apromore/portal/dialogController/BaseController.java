package org.apromore.portal.dialogController;

import org.apromore.manager.client.ManagerService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Window;

/**
 * Created by IntelliJ IDEA.
 * User: lappie
 * Date: 28/11/11
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
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

    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

}
