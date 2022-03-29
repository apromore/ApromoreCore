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
