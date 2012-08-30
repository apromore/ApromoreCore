package de.hpi.diagram;

public class SignavioUUID {
	public static String generate(){
		return "sid-" + java.util.UUID.randomUUID().toString();
	}
}