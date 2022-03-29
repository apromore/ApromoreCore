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
 * @(#)CellViewRenderer.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Component;

import org.apromore.jgraph.JGraph;

/**
 * Defines the requirements for objects that may be used as a
 * cell view renderer.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface CellViewRenderer {

	/**
	 * Configure and return the renderer based on the passed in
	 * components. The value is typically set from messaging the
	 * graph with <code>convertValueToString</code>.
	 * We recommend you check the value's class and throw an
	 * illegal argument exception if it's not correct.
	 *
	 * @param   graph the graph that that defines the rendering context.
	 * @param   view the view that should be rendered.
	 * @param   sel whether the object is selected.
	 * @param   focus whether the object has the focus.
	 * @param   preview whether we are drawing a preview.
	 * @return	the component used to render the value.
	 */
	Component getRendererComponent(
		JGraph graph,
		CellView view,
		boolean sel,
		boolean focus,
		boolean preview);

}
