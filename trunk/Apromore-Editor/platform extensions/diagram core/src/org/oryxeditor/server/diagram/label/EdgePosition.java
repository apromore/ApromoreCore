package org.oryxeditor.server.diagram.label;

/**
 * Enumeration of positions along an edge, that a label of this edge can have
 *  
 * @author philipp.maschke
 *
 */
public enum EdgePosition {
	START_TOP("starttop"),
	START_MIDDLE("startmiddle"),
	START_BOTTOM("startbottom"), 
	MID_TOP("midtop"),
	MID_BOTTOM("midbottom"),
	END_TOP("endtop"), 
	END_BOTTOM("endbottom");
	
	/**
	 * Returns the matching object for the given string
	 * @param enumString
	 * @throws IllegalArgumentException if no matching enumeration object was found
	 * @return
	 */
	public static EdgePosition fromString(String enumString) {
		return fromString(enumString, true);
	}
	
	/**
	 * Returns the matching object for the given string
	 * @param enumString
	 * @param exceptionIfNoMatch whether to throw an exception if there was no match
	 * @throws IllegalArgumentException if no matching enumeration object was found and exceptionIfNoMatch is true
	 * @return
	 */
	public static EdgePosition fromString(String enumString, boolean exceptionIfNoMatch) {
		if (enumString == null)
			return null;
		
		for (EdgePosition attrEnum : values()) {
			if (attrEnum.label.equalsIgnoreCase(enumString) || attrEnum.name().equals(enumString))
				return attrEnum;
		}

		if (exceptionIfNoMatch){
			throw new IllegalArgumentException("No matching enum constant found in '"
					+ EdgePosition.class.getSimpleName() + "' for: " + enumString);
		}else{
			return null;
		}
	}

	private String label;

	EdgePosition(String label) {
		this.label = label;
	}


	/**
	 * Returns the position label
	 */
	@Override
	public String toString() {
		return label;
	}
}
