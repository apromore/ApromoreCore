package org.oryxeditor.server.diagram.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BasicDiagramTest.class,
	BasicNodeTest.class,
	BasicEdgeTest.class,
	BasicDiagramBuilderTest.class,
	BasicJSONBuilderTest.class
})
public class AllTests {

}
