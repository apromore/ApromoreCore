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
 * @(#)GraphCellEditor.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Component;

import javax.swing.CellEditor;

import org.apromore.jgraph.JGraph;

/**
  * Adds to CellEditor the extensions necessary to configure an editor
  * in a graph.
  *
  * @version 1.0 1/1/02
  * @author Gaudenz Alder
  */

public interface GraphCellEditor extends CellEditor {
	/**
	 * Sets an initial <I>value</I> for the editor.  This will cause
	 * the editor to stopEditing and lose any partially edited value
	 * if the editor is editing when this method is called. <p>
	 *
	 * Returns the component that should be added to the client's
	 * Component hierarchy.  Once installed in the client's hierarchy
	 * this component will then be able to draw and receive user input.
	 *
	 * @param	graph		the JGraph that is asking the editor to edit
	 *				This parameter can be null.
	 * @param	value		the value of the cell to be edited.
	 * @param	isSelected	true if the cell is to be rendered with
	 *				selection highlighting
	 * @return	the component for editing
	 */
	Component getGraphCellEditorComponent(
		JGraph graph,
		Object value,
		boolean isSelected);
}
