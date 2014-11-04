package org.processmining.plugins.signaturediscovery.encoding;

/**
 * @author R.P. Jagadeesh Chandra 'JC' Bose
 * @date 14 July 2010 
 * @since 01 July 2010
 * @version 1.0
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @copyright R.P. Jagadeesh Chandra 'JC' Bose
 * 			  Architecture of Information Systems Group (AIS) 
 * 			  Department of Mathematics and Computer Science
 * 			  University of Technology, Eindhoven, The Netherlands
 */

public class InstanceProfile {
	/**
	 * The name of the trace
	 */
	private String name;
	
	/**
	 * the class label for the trace
	 */
	private String label;

	/**
	 * the encoded trace: "ab1cd2di3ss3"
	 */
	private String encodedTrace;

	public InstanceProfile(String name, String encodedTrace, String label){
		this.name = name;
		this.encodedTrace = encodedTrace;
		this.label = label;
	}

	public String toString() {
		String text = "Instance Profile: "+name+"\n";
		text = text + "encodedTrace:" + encodedTrace+" Label: " + label;
		return text;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public String getEncodedTrace() {
		return encodedTrace;
	}
}
