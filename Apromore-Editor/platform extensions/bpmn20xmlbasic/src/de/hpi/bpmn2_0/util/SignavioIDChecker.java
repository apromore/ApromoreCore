package de.hpi.bpmn2_0.util;

import de.hpi.bpmn2_0.factory.configuration.Configuration;

public class SignavioIDChecker {
	private static boolean isValidID(String id) {
		if(id == null) {
			return false;
		}
		
		boolean testRes = true;
		
		testRes = testRes && id.length() <= 250;
		testRes = testRes && id.matches("^\\D(.)*");
		
		return testRes;
		
		
//		!bpmnEl.getId().matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}")s
	}
	
	public static boolean isValidID(String id, Configuration conf) {
		boolean result = true;
		
		if(conf != null && conf.ensureSignavioStyle && id != null) {
			result = id.matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}");
		}
		
		return result && isValidID(id);
	}
}
