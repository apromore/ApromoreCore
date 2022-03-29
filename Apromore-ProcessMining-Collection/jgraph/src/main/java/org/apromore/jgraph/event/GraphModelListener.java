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
