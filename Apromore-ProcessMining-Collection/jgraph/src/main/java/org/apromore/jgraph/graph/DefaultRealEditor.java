/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * @(#)DefaultCellEditor.java 1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apromore.jgraph.JGraph;

/**
 * The default editor for graph cells.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */
public class DefaultRealEditor extends DefaultCellEditor
		implements
			GraphCellEditor {

	//
	//  Constructors
	//
	/**
	 * Constructs a DefaultCellEditor that uses a text field.
	 * 
	 * @param textField
	 *            a JTextField object used as the editor
	 */
	public DefaultRealEditor(final JTextField textField) {
		super(textField);
		setClickCountToStart(1);
	}

	/**
	 * Constructs a DefaultCellEditor object that uses a check box.
	 * 
	 * @param checkBox
	 *            a JCheckBox object
	 */
	public DefaultRealEditor(final JCheckBox checkBox) {
		super(checkBox);
	}

	/**
	 * Constructs a DefaultCellEditor object that uses a combo box.
	 * 
	 * @param comboBox
	 *            a JComboBox object
	 */
	public DefaultRealEditor(final JComboBox comboBox) {
		super(comboBox);
	}

	//
	//  GraphCellEditor Interface
	//
	public Component getGraphCellEditorComponent(JGraph graph, Object value,
			boolean isSelected) {
		String stringValue = graph.convertValueToString(value);
		delegate.setValue(stringValue);
		if (editorComponent instanceof JTextField)
			((JTextField) editorComponent).selectAll();
		return editorComponent;
	}
} // End of class JCellEditor
