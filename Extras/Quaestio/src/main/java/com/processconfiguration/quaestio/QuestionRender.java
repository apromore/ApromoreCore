/*******************************************************************************
 * Copyright © 2006-2011, www.processconfiguration.com
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *      Marcello La Rosa - initial API and implementation, subsequent revisions
 *      Florian Gottschalk - individualizer for YAWL
 *      Possakorn Pitayarojanakul - integration with Configurator and Individualizer
 ******************************************************************************/
/**
 * Copyright © 2006-2009, Marcello La Rosa (marcello.larosa@gmail.com)
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *      Marcello La Rosa - initial API and implementation
 */
package com.processconfiguration.quaestio;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.processconfiguration.qml.QuestionType;



public class QuestionRender extends DefaultListCellRenderer {
	
	public Component getListCellRendererComponent(
		JList list,
		Object value,            // value to display
		int index,               // cell index
		boolean isSelected,      // is the cell selected
		boolean cellHasFocus){    // the list and the cell have the focus

		super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		QuestionType question = (QuestionType)value;
		
		if (question.isSkippable()){
			setForeground(Color.GRAY);//OLD: new java.awt.Color(Color(51,94,168))
			setToolTipText(question.getId()+" (skipped)");
		}
		else
			setToolTipText(question.getId());
//		if (isSelected) {
//		    setBackground(list.getSelectionBackground());
//		    setForeground(list.getSelectionForeground());
//		}
//		else {
//		    setBackground(list.getBackground());
//		    setForeground(list.getForeground());
//		}
//		setText(question.toString());
//		setFont(list.getFont());
		return this;
	}

}
