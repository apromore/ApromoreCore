package org.apromore.portal.dialogController;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

public class EditController extends Window {

	public EditController(Window win, int processId, String versionName) throws SuspendNotAllowedException, InterruptedException {
		
		win.setId(processId+versionName);
		win.setTitle("Edit process " + processId + "version (" + versionName +")");
				
		Executions.sendRedirect("http://www.google.com/search?q=" + processId	+ ".." + versionName) ;
		}
}
