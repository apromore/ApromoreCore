package org.apromore.fxes.log;

/**
 * 
 * @author Alireza Ostovar (Alirezaostovar@gmail.com)
 *
 */
public enum FXAttributeType {
	
	LITERAL, 
	DISCRETE, 
	CONTINUOUS, 
	BOOLEAN, 
	TIMESTAMP;
	
	/**
	 * Returns the type of the attribute
	 * @param a
	 * @return
	 */
	public static FXAttributeType getAttributeType(String a)
	{
		if (a.equalsIgnoreCase("string"))
			return LITERAL;
		if(a.equalsIgnoreCase("date"))
			return TIMESTAMP;
		if(a.equalsIgnoreCase("int"))
			return DISCRETE;
		if(a.equalsIgnoreCase("float"))
			return CONTINUOUS;
		if(a.equalsIgnoreCase("boolean"))
			return BOOLEAN;
		
		return LITERAL;
	}
}
