package org.apromore.canoniser.adapters;

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
		//JUnit-Tests for WoPeD integration
		suite.addTestSuite(PNML2CPFWopedCases.class);
		suite.addTestSuite(PNML2ANFWopedCases.class);
		suite.addTestSuite(PNML2PNMLWopedCases.class);
		//$JUnit-END$
		return suite;
	}

}
