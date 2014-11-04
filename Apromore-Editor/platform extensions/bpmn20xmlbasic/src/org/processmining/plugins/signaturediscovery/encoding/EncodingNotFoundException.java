package org.processmining.plugins.signaturediscovery.encoding;

/**
 * @author  R. P. Jagadeesh Chandra Bose (JC)
 * @date    02 July 2009
 * @email   j.c.b.rantham.prabhakara@tue.nl
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class EncodingNotFoundException extends Exception {
	String str;
	
	EncodingNotFoundException(String a){
		str = a;
	}
	
	public String toString(){
		return "Encoding Not Found For "+str;
	}

}
