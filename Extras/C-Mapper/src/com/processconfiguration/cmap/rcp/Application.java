package com.processconfiguration.cmap.rcp;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	
	public static String Views_QuestionView_ID = "com.processconfiguration.cmap.rcp.views.qmlview";
	public static String Views_ModelView_ID = "com.processconfiguration.cmap.rcp.views.modelview";
	public static String Views_CMapView_ID = "com.processconfiguration.cmap.rcp.views.cmap";
	public static String Views_DispConditionView_ID = "com.processconfiguration.cmap.rcp.views.dispcondition";
	
	public static String qmlFileName = "";
	public static String modelFileName = "";
	public static String cmapFileName = ""; //This is actuall holds the whole path
	
	public static String qmlFilePath = "";
	public static String modelFilePath = "";

	
	//public final static String qmlSchema = "C:\\Asadul\\synergia_example\\" + "QML.xsd";
	public final static String qmlSchema = "/schemas/QML.xsd";
	public final static String modelSchema = "/schemas/EPML_2.0.xsd";
	public final static String cmapSchema = "/schemas/CMAP.xsd";
	
	public final static boolean ValidateSchema = false;
	
	@Override
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
