package org.processmining.models.graphbased;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AttributeMap {
	private final static String PREFIX = "ProM_Vis_attr_";

	public enum ArrowType {
		ARROWTYPE_CLASSIC(PREFIX + "arrow_classic"), //
		ARROWTYPE_TECHNICAL(PREFIX + "arrow_tech"), //
		ARROWTYPE_SIMPLE(PREFIX + "arrow_simple"), //
		ARROWTYPE_DIAMOND(PREFIX + "arrow_diamond"), //
		ARROWTYPE_CIRCLE(PREFIX + "arrow_circle"), //
		ARROWTYPE_LINE(PREFIX + "arrow_line"), //
		ARROWTYPE_DOUBLELINE(PREFIX + "arrow_double"), //
		ARROWTYPE_NONE(PREFIX + "arrow_none"), //
		ARROW_CROSS(PREFIX + "arrow_cross"), //
		ARROW_TECHNICAL_CIRCLE(PREFIX + "arrow_technical_circle");

		ArrowType(String s) {
		}
	}

	public final static String SHAPE = PREFIX + "shape";
	public static final String SHAPEDECORATOR = PREFIX + "ShapeDecorator";

	public final static String FILLCOLOR = PREFIX + "fillcolor";
	public final static String GRADIENTCOLOR = PREFIX + "gradientcolor";
	public final static String ICON = PREFIX + "icon";
	public final static String BORDERWIDTH = PREFIX + "border";
	public final static String LABEL = PREFIX + "label";
	public final static String TOOLTIP = PREFIX + "tooltip";

	public final static String EDGESTART = PREFIX + "edgestart";
	public final static String EDGESTARTFILLED = PREFIX + "edgeStartFilled";

	public final static String EDGEEND = PREFIX + "edge end";
	public final static String EDGEENDFILLED = PREFIX + "edgeEndFilled";

	public final static String EDGEMIDDLE = PREFIX + "edge middle";
	public final static String EDGEMIDDLEFILLED = PREFIX + "edgeMiddleFilled";

	public final static String LABELVERTICALALIGNMENT = PREFIX + "labelVerticalAlignment";
	public final static String EDGECOLOR = PREFIX + "edgeColor"; // added by arya
	public final static String STROKECOLOR = PREFIX + "strokeColor"; // added by arya

	public final static String INSET = PREFIX + "inset"; // added by jribeiro
	public final static String STROKE = PREFIX + "stroke"; // added by jribeiro
	public final static String DASHPATTERN = PREFIX + "dashPattern"; // added by jribeiro
	public final static String DASHOFFSET = PREFIX + "dashOffset"; // added by jribeiro
	public final static String LABELCOLOR = PREFIX + "labelColor"; // added by jribeiro
	public final static String LABELALONGEDGE = PREFIX + "labelAlongEdge"; // added by jribeiro

	/**
	 * A Float representing the linewidth of a line.
	 */
	public final static String LINEWIDTH = PREFIX + "lineWidth";
	public final static String NUMLINES = PREFIX + "numLines";

	public final static String STYLE = PREFIX + "style";

	public final static String POLYGON_POINTS = PREFIX + "polygonpoints";

	public static final String SQUAREBB = PREFIX + "squareBB";
	public static final String RESIZABLE = PREFIX + "resizable";
	public static final String AUTOSIZE = PREFIX + "autosize";
	public static final String SHOWLABEL = PREFIX + "showLabel";
	public static final String MOVEABLE = PREFIX + "movable"; // added by arya

	/**
	 * This should be set to SwingConstants.SOUTH, SwingConstants.WEST and so
	 * on. SwingConstants.NORTH means the graph prefers drawn Top-Down
	 * SwingConstants.WEST means the graph prefers drawn Left to Right
	 */
	public static final String PREF_ORIENTATION = PREFIX + "orientation";
	public static final String LABELHORIZONTALALIGNMENT = PREFIX + "horizontal alignment";
	public static final String SIZE = "size";
	public static final String PORTOFFSET = "portoffset";

	/**
	 * The value of this attribute should be an array of type Point2D[]
	 * (size>0), as used in the method GraphConstants.setExtraLabelPositions()
	 */
	public static final String EXTRALABELPOSITIONS = "Label positions";
	/**
	 * The value of this attribute should be an array of type String[] (size>0),
	 * as used in the method GraphConstants.setExtraLabels()
	 */
	public static final String EXTRALABELS = "Extra Labels";

	/**
	 * Renderer to be used.
	 */
	public static final String RENDERER = "Renderer";

	private final Map<String, Object> mapping = new LinkedHashMap<String, Object>();

	public AttributeMap() {
	}

	public Object get(String key) {
		return mapping.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		synchronized (mapping) {
			Object o = mapping.get(key);
			if (o != null) {
				return (T) o;
			}
			if (mapping.containsKey(key)) {
				return null;
			} else {
				return defaultValue;
			}
		}
	}

	public void clear() {
		mapping.clear();
	}

	public Set<String> keySet() {
		return mapping.keySet();
	}

	/**
	 * This method updates the map and signals the owner. The origin is passed
	 * in this update, to make sure that no unnecessary updates are performed
	 * 
	 * @param key
	 * @param value
	 * @param origin
	 * @return
	 */
	public boolean put(String key, Object value) {
		Object old;
		synchronized (mapping) {
			old = mapping.get(key);
			mapping.put(key, value);
		}
		if (value == old) {
			return false;
		}
		if ((value == null) || (old == null) || !value.equals(old)) {
			return true;
		}
		return false;
	}

	public void remove(String key) {
		synchronized (mapping) {
			mapping.remove(key);
		}
	}

	public boolean containsKey(String key) {
		return mapping.containsKey(key);
	}

}