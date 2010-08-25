package org.apromore.canoniser.adapters.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(EPML2CPF.class);
		suite.addTestSuite(XPDL2CPF.class);
		suite.addTestSuite(EPML2XPDL.class);
		suite.addTestSuite(XPDL2EPML.class);
		//$JUnit-END$
		return suite;
	}

}
