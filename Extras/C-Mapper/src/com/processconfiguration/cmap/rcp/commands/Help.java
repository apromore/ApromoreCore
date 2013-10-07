package com.processconfiguration.cmap.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class Help extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION);
		msg.setText("C-Mapper v1.0 - Help");
		msg.setMessage("Please go to the website http://www.processconfiguration.com for more detail information" +
				"and documentation.");
		msg.open();
		return null;
	}

}
