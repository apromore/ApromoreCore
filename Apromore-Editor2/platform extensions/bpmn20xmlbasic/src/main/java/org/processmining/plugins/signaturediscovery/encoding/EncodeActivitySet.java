/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

// OM Ganesayanamaha
/**
 * 
 */
package org.processmining.plugins.signaturediscovery.encoding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author R. P. Jagadeesh Chandra Bose (JC)
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @date 02 July 2009
 * @version 1.0
 */
public class EncodeActivitySet {
	/*
	 * Store the encoding corresponding to the activity/element
	 * Example:
	 * ("Arrive_Start-complete","ab0")
	 * ("Triage_Request-complete","ab1")
	 * ("29060537","ab2")
	 * ("Nursing Assessment-complete","ab3")
	 * ("29062349","ab4")...
	 * 
	 */
	private Map<String, String> activityCharMap;
	
	/*
	 * Store the decoded activity corresponding to a char encoding
	 * ("ab0","Arrive_Start-complete")
	 * ("ab1","Triage_Request-complete")
	 * ("ab2","29060537")
	 * ("ab3","Nursing Assessment-complete")
	 * ("ab4","29062349")...
	 */
	private Map<String, String> charActivityMap;
	
	/*
	 * Contains set of activities from the log.
	 * The activity name usually has the format: <activity_name>-<start|complete>
	 * There is no duplication of activity name
	 * The set also contains traceID, can be mixed with activity name in no orders. See example.
	 * This is collected from all activities and traces in the log. If activity name or traceID is repeated, 
	 * they won't be duplicated here.
	 * Example:
	 * "Arrive_Start-complete","Triage_Request-complete","Triage_Start-complete","29060537",
	 * "Nursing Assessment-complete","Medical Assign_Start-complete","Blood tests","29062349",
	 * "RN Assign_Start-complete","Medical Note__final-complete","29627859"....
	 */
	private Set<String> activitySet;
	
	/*
	 * The base length for encoding
	 */
	private int encodingLength;
	private int maximumActivityLength;

	/**
	 * This method encodes a set of elements passed to it; The elements can be
	 * the set of activities along with the trace identifier. Trace identifiers
	 * need to be encoded for use in the computation of repeats (used as a
	 * distinct delimiter separating the traces).
	 * 
	 * The encoding length of each element in the set is automatically
	 * estimated.
	 * 
	 * @param activitySet
	 * @throws ActivityOverFlowException (when the number of elements to be encoded is too large)
	 */
	public EncodeActivitySet(Set<String> activitySet) throws ActivityOverFlowException {
		this.activitySet = activitySet;

		this.activityCharMap = new HashMap<String, String>();
		this.charActivityMap = new HashMap<String, String>();

		encodeActivities();
	}

	/**
	 * This method does the encoding; First it decides how many characters would be required for encoding 
	 * Input: activitySet, activityCharMap, charActivityMap
	 */
	private void encodeActivities() throws ActivityOverFlowException {
		String[] lowerCaseArray = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		String[] upperCaseArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		String[] alphaArray = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
				"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		String[] lowerCaseIntArray = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		String[] intArray = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		String[] allArray = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
				"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9" };

		int noActivities = activitySet.size();

		encodingLength = 2;
		/*
		 * If the number of activities to encode is less than 62 then use a
		 * single character encoding else check if the size is >62 and <= 260,
		 * then use lower case character followed by integer e.g., a0, b2, c9
		 * else check if the size is between 261 and 520 (both inclusive) else
		 * check if the size is between 521 and 936 (both inclusive) else check
		 * if the size is between 937 and 3844 (both inclusive) else check if
		 * the size is between 3845 and 6760 (both inclusive)
		 */
		if (noActivities <= allArray.length) {
			encodingLength = 1;
			encode(allArray);
		} else if (noActivities > allArray.length && noActivities <= lowerCaseArray.length * intArray.length) {
			encode(lowerCaseArray, intArray);
		} else if (noActivities > lowerCaseArray.length * intArray.length
				&& noActivities <= alphaArray.length * intArray.length) {
			encode(alphaArray, intArray);
		} else if (noActivities > alphaArray.length * intArray.length
				&& noActivities <= lowerCaseIntArray.length * upperCaseArray.length) {
			
                        encode(lowerCaseIntArray, upperCaseArray);
			encodingLength = 3;
			encode(lowerCaseArray, upperCaseArray, intArray);			
			
		} else if (noActivities > lowerCaseIntArray.length * upperCaseArray.length
				&& noActivities <= allArray.length * allArray.length) {

			encode(allArray, allArray);
			encodingLength = 3;
			encode(lowerCaseArray, upperCaseArray, intArray);
			
		} else if (noActivities <= lowerCaseArray.length * upperCaseArray.length * intArray.length) {
			encodingLength = 3;
			encode(lowerCaseArray, upperCaseArray, intArray);
		} else if (noActivities <= allArray.length * allArray.length * allArray.length) {
			encodingLength = 3;
			encode(allArray, allArray, allArray);
		} else {
			throw new ActivityOverFlowException();
		}

		//Free Memory
		lowerCaseArray = null;
		upperCaseArray = null;
		intArray = null;
		alphaArray = null;
		lowerCaseIntArray = null;
		allArray = null;
		System.gc();
	}

	protected void encode(String[] strArray) {
		int currentactivityIndex = 0;
		String charEncoding;
		
		maximumActivityLength = 0;
		for (String activity : activitySet) {
			if(activity.length() > maximumActivityLength)
				maximumActivityLength = activity.length();
			
			charEncoding = strArray[currentactivityIndex];
			activityCharMap.put(activity, charEncoding);
			if (charActivityMap.containsKey(charEncoding)) {
				System.out.println("Something wrong with encoding: Already present charEncoding");
				System.exit(0);
			} else {
				charActivityMap.put(charEncoding, activity);
			}
			currentactivityIndex++;
		}

		//Free Memory
		charEncoding = null;
	}

	protected void encode(String[] strArray, String[] intArray) {
		int currentactivityIndex = 0;

		int firstCharIndex, secondCharIndex;
		String charEncoding;
		maximumActivityLength = 0;
		for (String activity : activitySet) {
			if(activity.length() > maximumActivityLength)
				maximumActivityLength = activity.length();
			
			firstCharIndex = currentactivityIndex / intArray.length;
			secondCharIndex = currentactivityIndex % intArray.length;

			charEncoding = strArray[firstCharIndex] + intArray[secondCharIndex];
			activityCharMap.put(activity, charEncoding);
			if (charActivityMap.containsKey(charEncoding)) {
				System.out.println("Something wrong with encoding: Already present charEncoding");
				System.exit(0);
			} else {
				charActivityMap.put(charEncoding, activity);
			}
			currentactivityIndex++;
		}

		//Free Memory
		charEncoding = null;
	}

	protected void encode(String[] strArray1, String[] strArray2, String[] strArray3) {
		int currentactivityIndex = 0;

		int firstCharIndex, secondCharIndex, thirdCharIndex;
		String charEncoding;
		maximumActivityLength = 0;
		for (String activity : activitySet) {
			if(activity.length() > maximumActivityLength)
				maximumActivityLength = activity.length();
			
			thirdCharIndex = currentactivityIndex % strArray3.length;
			secondCharIndex = (currentactivityIndex / strArray3.length) % (strArray2.length);
			firstCharIndex = currentactivityIndex / (strArray3.length * strArray2.length);

			charEncoding = strArray1[firstCharIndex] + strArray2[secondCharIndex] + strArray3[thirdCharIndex];
			activityCharMap.put(activity, charEncoding);
			if (charActivityMap.containsKey(charEncoding)) {
				System.out.println("Something wrong with encoding: Already present charEncoding");
				System.exit(0);
			} else {
				charActivityMap.put(charEncoding, activity);
			}
			currentactivityIndex++;
		}

		//Free Memory
		charEncoding = null;
	}

	public Map<String, String> getActivityCharMap() {
		return activityCharMap;
	}

	public Map<String, String> getCharActivityMap() {
		return charActivityMap;
	}

	public int getEncodingLength() {
		return encodingLength;
	}

	public int getMaximumActivityLength() {
		return maximumActivityLength;
	}
}
