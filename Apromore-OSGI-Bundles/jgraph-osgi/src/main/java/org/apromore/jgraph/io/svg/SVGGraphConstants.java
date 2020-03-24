package org.apromore.jgraph.io.svg;

import java.util.Map;

public class SVGGraphConstants {

	/** Represents the lack of allocation of a shape */
	public static final int NO_SHAPE_SPECIFIED = -1;

	/** Represents a rectangle shape type */
	public static final int SHAPE_RECTANGLE = 0;

	/** Represents an ellipse shape type */
	public static final int SHAPE_ELLIPSE = 1;

	/** Represents an rounded rectangle shape type */
	public static final int SHAPE_ROUNDRECT = 2;
	
	/** Represents an cylinder shape type */
	public static final int SHAPE_CYLINDER = 3;

	/** Represents an diamond shape type */
	public static final int SHAPE_DIAMOND = 4;

	/** The current default shape to be used */
	protected static int defaultShape = SHAPE_RECTANGLE;
	
	/**
	 * Key for the <code>vertexShape</code> attribute. Use instances of int as
	 * values for this key.
	 */
	public final static String VERTEXSHAPE = "vertexShape";

	/**
	 * Key for the <code>hexBorderColor</code> attribute. Use instances of
	 * String as values for this key.
	 */
	public final static String VERTEXSHADOW = "vertexShadow";

	/**
	 * Sets the value attribute in the specified map to the specified shape type
	 * value.
	 * 
	 * @param map
	 *            The map to store the shape attribute in.
	 * @param shapeType
	 *            The value to set the share tpye attribute to. The possible
	 *            values for this are defined near the top of this file
	 */
	public static void setShape(Map map, int shapeType) {
		Integer wrapperInt = new Integer(shapeType);
		map.put(VERTEXSHAPE, wrapperInt);
	}

	/**
	 * Returns the font for the specified attribute map. Uses default font if no
	 * font is specified in the attribute map.
	 */
	public static int getShape(Map map) {
		Integer wrapperInt = (Integer) map.get(VERTEXSHAPE);
		if (wrapperInt != null) {
			return wrapperInt.intValue();
		}
		return NO_SHAPE_SPECIFIED;
	}

	/**
	 * Sets the value attribute in the specified map to the specified shadow
	 * value.
	 * 
	 * @param map
	 *            The map to store the shape attribute in.
	 * @param isShadow
	 *            The value to set the shadow attribute to.
	 */
	public static void setShadow(Map map, boolean isShadow) {
		map.put(VERTEXSHADOW, new Boolean(isShadow));
	}

	/**
	 * Returns the shadow for the specified attribute map. Uses no shadow by
	 * default if none set
	 */
	public static boolean isShadow(Map map) {
		Boolean bool = (Boolean) map.get(VERTEXSHADOW);
		if (bool != null)
			return bool.booleanValue();
		return false;
	}

}