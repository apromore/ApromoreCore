/*
 * @(#)GraphModelListener.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2005 Gaudenz Alder
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.event;

import java.util.EventListener;

/**
 * Defines the interface for an object that listens to changes in a GraphModel.
 * 
 * @author Gaudenz Alder
 * @version 1.0 1/1/02
 */
public interface GraphModelListener extends EventListener {

	/**
	 * Invoked after a cell has changed in some way. The vertex/vertices may
	 * have changed bounds or altered adjacency, or other attributes have
	 * changed that may affect presentation.
	 * Note : Read the notes on the GraphModelEvent class carefully.
	 * A GraphModelEvent is the undo of the event that has just occurred,
	 * i.e. if you undo this event is executed. 
	 */
	void graphChanged(GraphModelEvent e);

}