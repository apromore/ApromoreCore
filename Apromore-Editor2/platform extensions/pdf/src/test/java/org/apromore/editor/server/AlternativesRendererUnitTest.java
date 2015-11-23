package org.apromore.editor.server;

import org.junit.Test;

/**
 * Test suite for {@link AlternativesRenderer}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AlternativesRendererUnitTest extends AlternativesRenderer {

  /** Dummy test. */
  @Test public void testMakePDF() throws Exception {
      AlternativesRenderer.makePDF("src/test/resources/makePDF.svg", "target/makePDF.pdf");
  }
}
