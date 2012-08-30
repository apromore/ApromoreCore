package org.oryxeditor.server.diagram.label;

/**
 * Enumeration of positioning policies for horizontal alignment of labels
 * @author philipp.maschke
 *
 */
public enum HorizontalAlign {

	LEFT("left", 0), CENTER("center", 1), RIGHT("right", 2);
	
	/**
	 * Returns the matching object for the given string
	 * @param enumString
	 * @throws IllegalArgumentException if no matching enumeration object was found
	 * @return
	 */
	public static HorizontalAlign fromString(String enumString) {
		return fromString(enumString, true);
	}
	
	/**
	 * Returns the matching object for the given string
	 * @param enumString
	 * @param exceptionIfNoMatch whether to throw an exception if there was no match
	 * @throws IllegalArgumentException if no matching enumeration object was found and exceptionIfNoMatch is true
	 * @return
	 */
	public static HorizontalAlign fromString(String enumString, boolean exceptionIfNoMatch) {
		for (HorizontalAlign attrEnum : values()) {
			if (attrEnum.label.equals(enumString) || attrEnum.name().equals(enumString))
				return attrEnum;
		}

		if (exceptionIfNoMatch){
			throw new IllegalArgumentException("No matching enum constant found in '"
					+ HorizontalAlign.class.getSimpleName() + "' for: " + enumString);
		}else{
			return null;
		}
	}

	private String label;
	private int index;


	HorizontalAlign(String label, int index) {
		this.label = label;
		this.index = index;
	}


	/**
	 * Returns the alignment label
	 */
	@Override
	public String toString() {
		return label;
	}
	
	
	public int getIndex(){
		return index;
	}
}
