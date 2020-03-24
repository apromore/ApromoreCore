/*
 * $Id: ExecutableChange.java,v 1.1 2006/10/27 15:33:08 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.graph;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * The interface executable changes must adhere to
 */
public abstract class ExecutableChange extends AbstractUndoableEdit {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() {
		execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() {
		execute();
	}

	/**
	 * 
	 */
	public abstract void execute();

}
