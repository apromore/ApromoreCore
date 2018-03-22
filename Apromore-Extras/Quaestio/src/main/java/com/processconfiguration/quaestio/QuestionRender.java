/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
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
