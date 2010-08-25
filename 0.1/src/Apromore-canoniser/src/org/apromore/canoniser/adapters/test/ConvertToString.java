package org.apromore.canoniser.adapters.test;

import java.io.File;
import java.io.FileInputStream;

public class ConvertToString {
	
	public static String readFileAsString(String filePath) throws java.io.IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    FileInputStream f = new FileInputStream(filePath);
	    f.read(buffer);
	    return new String(buffer);
	}

}

