/* 
 * $Id: CellConstants.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.components.labels;

import java.util.Map;

import org.apromore.jgraph.graph.GraphConstants;

/**
 * JGraphpad graph constants. Contains special constants supported by the
 * renderers or other functionality.
 */
public class CellConstants extends GraphConstants {

	/**
	 * Key for the <code>stretchImage</code> attribute. This special attribute
	 * contains a Boolean instance indicating whether the background image
	 * should be stretched.
	 */
	public final static String STRETCHIMAGE = "stretchImage";

	/**
	 * Key for the <code>vertexShape</code> attribute. This special attribute
	 * contains an Integer instance indicating which shape should be drawn by
	 * the renderer.
	 */
	public final static String VERTEXSHAPE = "vertexShape";

	/**
	 * Returns true if stretchImage in this map is true. Default is false.
	 */
	public static final boolean isStretchImage(Map map) {
		Boolean boolObj = (Boolean) map.get(STRETCHIMAGE);
		if (boolObj != null)
			return boolObj.booleanValue();
		return false;
	}

	/**
	 * Sets stretchImage in the specified map to the specified value.
	 */
	public static final void setStretchImage(Map map, boolean stretchImage) {
		map.put(STRETCHIMAGE, new Boolean(stretchImage));
	}

	/**
	 * Sets vertexShape in the specified map to the specified value.
	 */
	public static final void setVertexShape(Map map, int shape) {
		map.put(VERTEXSHAPE, new Integer(shape));
	}

	/**
	 * Returns vertexShape from the specified map.
	 */
	public static final int getVertexShape(Map map) {
		Integer intObj = (Integer) map.get(VERTEXSHAPE);
		if (intObj != null)
			return intObj.intValue();
		return 0;
	}
}