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
 * @(#)GraphUI.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.plaf;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.plaf.ComponentUI;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.CellHandle;
import org.apromore.jgraph.graph.CellView;

/**
 * Pluggable look and feel interface for JGraph.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */
public abstract class GraphUI extends ComponentUI {

	/**
	 * Paints the renderer of <code>view</code> to <code>g</code>
	 * at <code>bounds</code>.
	 */
	public abstract void paintCell(
		Graphics g,
		CellView view,
		Rectangle2D bounds,
		boolean preview);

	/**
	 * Paints the renderers of <code>portViews</code> to <code>g</code>.
	 */
	public abstract void paintPorts(Graphics g, CellView[] portViews);

	/**
	 * Messaged to update the selection based on a MouseEvent for a group of
	 * cells. If the event is a toggle selection event, the cells are either
	 * selected, or deselected. Otherwise the cells are selected.
	 */
	public abstract void selectCellsForEvent(
		JGraph graph,
		Object[] cells,
		MouseEvent event);

	/**
	  * Returns the preferred size for <code>view</code>.
	  */
	public abstract Dimension2D getPreferredSize(JGraph graph, CellView view);

	/**
	  * Returns the <code>CellHandle</code> that is currently active,
	  * or <code>null</code> if no handle is active.
	  */
	public abstract CellHandle getHandle();

	/**
	  * Returns true if the graph is being edited.  The item that is being
	  * edited can be returned by getEditingCell().
	  */
	public abstract boolean isEditing(JGraph graph);

	/**
	  * Stops the current editing session.  This has no effect if the
	  * graph isn't being edited.  Returns true if the editor allows the
	  * editing session to stop.
	  */
	public abstract boolean stopEditing(JGraph graph);

	/**
	  * Cancels the current editing session. This has no effect if the
	  * graph isn't being edited.  Returns true if the editor allows the
	  * editing session to stop.
	  */
	public abstract void cancelEditing(JGraph graph);

	/**
	  * Selects the cell and tries to edit it.  Editing will
	  * fail if the CellEditor won't allow it for the selected item.
	  */
	public abstract void startEditingAtCell(JGraph graph, Object cell);

	/**
	 * Returns the cell that is being edited.
	 */
	public abstract Object getEditingCell(JGraph graph);

	/**
	 * Sets the current location for Drag-and-Drop activity. Should be set to
	 * null after a drop.
	 */
	public abstract void setInsertionLocation(Point p);

	/**
	 * Returns the insertion location for DnD operations.
	 */
	public abstract Point getInsertionLocation();

	/**
	 * Updates the handle.
	 */
	public abstract void updateHandle();
	
	/**
	 * Returns the current drop action.
	 */
	public abstract int getDropAction();

}
