package com.processconfiguration.cmap.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.processconfiguration.cmap.rcp.Application;
import com.processconfiguration.cmap.rcp.Util;
import com.processconfiguration.cmap.rcp.model.Connection;

public class CommandOpenCmap extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fd  = new FileDialog(parent, SWT.OPEN);
		fd.setFilterExtensions(new String[]{"*.cmap"});
		
		String cmapFileName = fd.open();
		if(cmapFileName != null){
			if(Util.isCorrectCmapFile(cmapFileName)){
				Application.cmapFileName = cmapFileName;
				Connection.FileUpload = true;
				Connection.RefreshView(Application.Views_CMapView_ID);
				Connection.HideView(Application.Views_DispConditionView_ID);
				//displayTree(parent, cmapFileName);
			}
		}
		
		return null;
		
	}

}
