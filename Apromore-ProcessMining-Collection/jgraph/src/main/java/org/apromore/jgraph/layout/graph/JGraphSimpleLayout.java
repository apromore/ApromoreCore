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
 * $Id: JGraphSimpleLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2005, Gaudenz Alder
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.graph;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;

/**
 * Three simple layouts in one class. Circl, tilt and randomize. The latter two
 * take into accounts the x and y parameters.
 */
public class JGraphSimpleLayout implements JGraphLayout {

	public static final int TYPE_CIRCLE = 0;

	public static final int TYPE_TILT = 1;

	public static final int TYPE_RANDOM = 2;

	protected int type = TYPE_CIRCLE;

	protected int maxx, maxy;

	public JGraphSimpleLayout(int type) {
		this(type, 20, 20);
	}

	public JGraphSimpleLayout(int type, int maxx, int maxy) {
		this.type = type;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	public void run(JGraphFacade graph) {
		switch (type) {
		case (TYPE_CIRCLE): {
			graph.circle(graph.getVertices());
			break;
		}
		case (TYPE_TILT): {
			graph.tilt(graph.getVertices(), maxx, maxy);
			break;
		}
		case (TYPE_RANDOM): {
			graph.randomize(graph.getVertices(), maxx, maxy);
			break;
		}
		}
	}

	/**
	 * @return Returns the maxx.
	 */
	public int getMaxx() {
		return maxx;
	}

	/**
	 * @param maxx
	 *            The maxx to set.
	 */
	public void setMaxx(int maxx) {
		this.maxx = maxx;
	}

	/**
	 * @return Returns the maxy.
	 */
	public int getMaxy() {
		return maxy;
	}

	/**
	 * @param maxy
	 *            The maxy to set.
	 */
	public void setMaxy(int maxy) {
		this.maxy = maxy;
	}

	/**
	 * Returns a name for this algorithm based on the type. This may be one of
	 * <code>Circle</code>,<code>Tilt</code> or <code>Random</code>.
	 * <code>Unknown</code> is returned for all unimplemented types.
	 * 
	 */
	public String toString() {
		switch (type) {
		case (TYPE_CIRCLE):
			return "Circle";
		case (TYPE_TILT):
			return "Tilt";
		case (TYPE_RANDOM):
			return "Random";
		default:
			return "Unknown";
		}
	}

}
