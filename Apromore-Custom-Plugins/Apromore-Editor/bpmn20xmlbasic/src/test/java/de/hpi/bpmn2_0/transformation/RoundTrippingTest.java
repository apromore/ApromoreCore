
package de.hpi.bpmn2_0.transformation;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.IDResolver;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static com.processconfiguration.ConfigurationAlgorithmTest.testsDirectory;
import static com.processconfiguration.DefinitionsIDResolverTest.assertValidBPMN;
import com.processconfiguration.DefinitionsIDResolver;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

/**
 * Test that applies {@link BPMN2DiagramConverter} followed by {@link Diagram2BpmnConverter}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class RoundTrippingTest {

  /**
   * Round-trip a trivial BPMN model.
   */
  @Test public void testCase1() throws Exception {
      roundTrip("Case 1.bpmn");
  }

  /**
   * Confirm that a BPMN boundary event is retained during a round trip to JSON and back.
   */
  @Test public void testCase12() throws Exception {
      roundTrip("Case 12.bpmn");
  }

  /** Confirm that a BPMN boundary event within a subprocess survives a round trip. */
  @Test public void testBoundaryInSubProcess() throws Exception { roundTrip("boundary-in-subprocess.bpmn"); }

  /** Confirm that a BPMNb boundary event attached to a subprocess survives a round trip. */
  @Test public void testBoundaryOnSubProcess() throws Exception { roundTrip("boundary-on-subprocess.bpmn"); }

  /**
   * @param fileName  a BPMN file in the test/resources/data directory
   */
  private void roundTrip(String fileName) throws Exception {
    
    // Parse BPMN from XML to JAXB
    Unmarshaller unmarshaller = JAXBContext.newInstance(Definitions.class,
                                                        ConfigurationAnnotationAssociation.class,
                                                        ConfigurationAnnotationShape.class)
                                           .createUnmarshaller();
    unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
    Definitions definitions = (Definitions) unmarshaller.unmarshal(new File(new File(testsDirectory, "data"), fileName));

    // BPMN JAXB to JSON
    BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/signaviocore/editor/");
    List<BasicDiagram> diagrams = converter.getDiagramFromBpmn20(definitions);

    assertEquals(1, diagrams.size());
    File json = new File("target/test-RoundTripping." + fileName + ".json");
    FileWriter writer = new FileWriter(json);
    writer.write(diagrams.get(0).getJSON().toString());
    writer.close();

    // Yoinked from BPMNSerializationTest
    BufferedReader br = new BufferedReader(new FileReader(json));
                String bpmnJson = "";
                String line;
                while ((line = br.readLine()) != null) {
                        bpmnJson += line;
                }
    BasicDiagram diagram = BasicDiagramBuilder.parseJson(bpmnJson);

    // JSON to BPMN JAXB
    Diagram2BpmnConverter converter2 = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
    Definitions definitions2 = converter2.getDefinitionsFromDiagram();

    // Examine the resultant BPMN XML
    assertValidBPMN(definitions2, "test-RoundTripping." + fileName + ".json.bpmn");
  }
}
