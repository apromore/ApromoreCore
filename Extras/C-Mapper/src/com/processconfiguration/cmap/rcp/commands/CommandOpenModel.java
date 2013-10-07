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

public class CommandOpenModel extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fd  = new FileDialog(parent, SWT.OPEN);
		fd.setFilterExtensions(new String[]{"*.epml"});
		String mfileName = fd.open();
		
		if(mfileName != null){
			if(Util.isCorrectModelFile(mfileName)){
				Application.modelFileName = mfileName;
				Connection.RefreshView(Application.Views_ModelView_ID);
			}
			else {
				//--Message: The Model file is not in correct format
			}
		}
		return null;
	}

}
