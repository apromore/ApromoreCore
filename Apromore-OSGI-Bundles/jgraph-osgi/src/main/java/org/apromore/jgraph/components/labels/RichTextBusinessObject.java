/* 
 * $Id: RichTextBusinessObject.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.components.labels;

import java.awt.Component;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * User object with a dynamic set of properties in a hashtable. The property
 * under {@link #valueKey} is used in {@link #toString()} to represent the
 * object as a string. This object supports values of type
 * {@link JGraphpadRichTextValue}.
 * 
 * @see RichTextGraphModel
 */
public class RichTextBusinessObject implements Cloneable, Serializable {

	/**
	 * Key to use for converting this object to a string.
	 */
	public static String valueKey = "value";

	/**
	 * Holds the properties as (key, value) pairs.
	 */
	protected Map properties = new Hashtable();

	/**
	 * Constructs a business object with an empty string as its value.
	 */
	public RichTextBusinessObject() {
		this("");
	}

	/**
	 * Constructs a business object with the specified value.
	 * 
	 * @param value
	 *            The value of the new object.
	 */
	public RichTextBusinessObject(Object value) {
		setValue(value);
	}

	/**
	 * Returns the properties.
	 * 
	 * @return Returns the properties.
	 */
	public Map getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 * 
	 * @param properties
	 *            The properties to set.
	 */
	public void setProperties(Map properties) {
		this.properties = properties;
	}

	/**
	 * Sets the value for {@link #valueKey}.
	 * 
	 * @param value
	 *            The value to set.
	 */
	public void setValue(Object value) {
		putProperty(valueKey, value);
	}

	/**
	 * Returns the value for {@link #valueKey}.
	 * 
	 * @return Returns the value.
	 */
	public Object getValue() {
		return getProperty(valueKey);
	}

	/**
	 * Returns true if the value is a rich text value.
	 * 
	 * @return Returns true if value is rich text.
	 */
	public boolean isRichText() {
		return getValue() instanceof RichTextValue;
	}

	/**
	 * Returns true if the value is a component.
	 * 
	 * @return Returns true if value is a component.
	 */
	public boolean isComponent() {
		return getValue() instanceof Component;
	}

	/**
	 * Sets the property under the specified key.
	 * 
	 * @param key
	 *            They key of the property.
	 * @param value
	 *            The value of the property.
	 * @return Returns the previous value.
	 */
	public Object putProperty(Object key, Object value) {
		if (key != null && value != null)
			return properties.put(key, value);
		return null;
	}

	/**
	 * Returns the property under the specified key.
	 * 
	 * @param key
	 *            The key of the property.
	 * @return Returns the specified property.
	 */
	public Object getProperty(Object key) {
		if (key != null)
			return properties.get(key);
		return null;
	}

	/**
	 * Hook for subclassers to create a custom tooltip for this user object.
	 * This is used in {@link JGraphpadGraph#getToolTipForCell(Object)}.
	 * 
	 * @return Returns a tooltip for the user object.
	 */
	public String getTooltip() {
		String html = "";
		String title = toString();
		if (title.length() > 0)
			html += "<strong>" + chopString(title, 20) + "</strong><br>";
		Iterator it = getProperties().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry element = (Map.Entry) it.next();
			if (!element.getKey().equals(valueKey))
				html += element.getKey() + ": "
						+ chopString(String.valueOf(element.getValue()), 20)
						+ "<br>";
		}
		return html;
	}

	/**
	 * A helper method that crops string to the specified length, adding 3 dots
	 * if there were more characters.
	 * 
	 * @return The chopped string.
	 */
	protected String chopString(String s, int max) {
		if (s != null) {
			if (s.length() > max)
				s = s.substring(0, max) + "...";
		}
		return s;
	}

	/**
	 * Returns the string representation of the value or an empty string if no
	 * value exists.
	 * 
	 * @return Returns the value as a string.
	 */
	public String toString() {
		Object value = getValue();
		if (value != null)
			return String.valueOf(value);
		return "";
	}

	/**
	 * Returns a clone of the object. Note: The properties are not cloned, only
	 * a clone of the containing map is used. As a special case, if the user
	 * object is a JSlider or a JTree then a new instance will be put in place
	 * of the old instance.
	 * 
	 * @return Returns a clone of this object.
	 */
	public Object clone() {
		RichTextBusinessObject clone;
		try {
			clone = (RichTextBusinessObject) super.clone();
		} catch (CloneNotSupportedException e) {
			clone = new RichTextBusinessObject();
		}
		clone.setProperties(new Hashtable(getProperties()));
		return clone;
	}

}