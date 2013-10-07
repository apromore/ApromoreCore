package com.processconfiguration.cmap.rcp.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class About extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION);
		msg.setText("C-Mapper v1.0 - About");
		msg.setMessage("Copyright (c) 2011, www.processconfiguration.com\n\n\n" +
				"Contributors:\n" +
				"- Asadul Islam,\n" +
				"- Marcello La Rosa.");
		msg.open();
		return null;
	}

}
