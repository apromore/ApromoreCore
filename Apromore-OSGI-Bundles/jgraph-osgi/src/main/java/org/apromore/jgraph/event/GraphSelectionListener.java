/*
 * @(#)GraphSelectionListener.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2005 Gaudenz Alder
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.event;

import java.util.EventListener;

/**
 * The listener that's notified when the selection in a GraphSelectionModel
 * changes.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface GraphSelectionListener extends EventListener {
	/**
	 * Called whenever the value of the selection changes.
	 * @param e the event that characterizes the change.
	 */
	void valueChanged(GraphSelectionEvent e);
}