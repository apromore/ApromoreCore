package org.apromore.util;

/**
 * @author Chathura Ekanayake
 */
public class VersionNameUtil {

	/**
	 * Constructs the next version name based on the given version name.
	 * Version sequence: 1.0, 1.1, 1.2, ..., 1.9, 2.0, 2.1, ...
	 * 
	 * @param versionName Preceding version name
	 * @return Next version name
	 */
	public static String getNextVersionName(String versionName) {
		float fvn = Float.parseFloat(versionName);
		fvn += 0.1;
        return Float.toString(fvn);
	}
}
