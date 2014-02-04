package de.hpi.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Philipp
 * Helper CLass to convert the LibConfig Format into JSON
 */
public class LibConfigToJsonConvert {
//	BNF for LibConfig
// configuration = setting-list | empty
//  
//  setting-list = setting | setting-list setting
//  
//  setting = name (":" | "=") value ";"
//  
//  value = scalar-value | array | list | group
//  
//  value-list = value | value-list "," value
//  
//  scalar-value = boolean | integer | integer64 | hex | hex64 | float
//                 | string
//  
//  scalar-value-list = scalar-value | scalar-value-list "," scalar-value
//  
//  array = "[" (scalar-value-list | empty) "]"
//  
//  list = "(" (value-list | empty) ")"
//  
//  group = "{" (setting-list | empty) "}"
	/**
	 * Converts the given LibConfig as String to a JSONObject
	 * NOTE: 	Integer64 and Hex64 are not supported
	 * 			And differencs between groups and list will get lost
	 * @param string
	 * @return
	 * @throws org.json.JSONException
	 */
	static public JSONObject parseString(String string) throws JSONException{
		String s1 = string.replaceAll(";", ",");
		/*
		 * transform all lists to Arrays
		 */
		s1 = s1.replaceAll("\\(", "[");
		s1 = s1.replaceAll("\\)", "]");
		
		/*
		 * replace all = by :, due to semantical similiarity
		 */
		s1 = s1.replaceAll("=", ":");
		
		/*
		 * Normalize dataTypes
		 */
		s1 = s1.replaceAll("[Tt][Rr][Uu][Ee]", "true");
		s1 = s1.replaceAll("[Ff][Aa][Ll][Ss][Ee]", "false");
		/*
		 * integer64 is unsupported
		 */
		s1 = s1.replaceAll("[-+]?[0-9]+L(L)?", "-1");
		/*
		 * hex64 is not supported
		 */
		s1 = s1.replaceAll("0[Xx][0-9A-Fa-f]+L(L)?", "0x0");
		/*
		 * add brackets for root container
		 */
		s1 = "{"+s1+"}";
		return new JSONObject(s1);
	}
	

}
