/*
 * Copyright © 2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.transformation;

import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.IDResolver;
import org.junit.Ignore;
import org.junit.Test;

import static com.processconfiguration.ConfigurationAlgorithmTest.testsDirectory;
import static com.processconfiguration.DefinitionsIDResolverTest.assertValidBPMN;
import com.processconfiguration.DefinitionsIDResolver;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import org.oryxeditor.server.diagram.basic.BasicDiagram;

/**
 * Test that applies {@link BPMN2DiagramConverter} followed by {@link Diagram2BpmnConverter}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class RoundTrippingTest {

  /**
   * Confirm that a BPMN boundary event is retained during a round trip to JSON and back.
   */
  @Ignore("Boundary events can't be round-tripped yet")
  @Test public void testCase12() throws Exception {

    // Parse BPMN from XML to JAXB
    Unmarshaller unmarshaller = JAXBContext.newInstance(Definitions.class,
                                                        ConfigurationAnnotationAssociation.class,
                                                        ConfigurationAnnotationShape.class)
                                           .createUnmarshaller();
    unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
    Definitions definitions = (Definitions) unmarshaller.unmarshal(new File(new File(testsDirectory, "data"), "Case 12.bpmn"));

    // BPMN JAXB to JSON
    BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/signaviocore/editor/");
    List<BasicDiagram> diagrams = converter.getDiagramFromBpmn20(definitions);

    // JSON to BPMN JAXB
    Diagram2BpmnConverter converter2 = new Diagram2BpmnConverter(diagrams.get(0), AbstractBpmnFactory.getFactoryClasses());
    Definitions definitions2 = converter2.getDefinitionsFromDiagram();

    // Examine the resultant BPMN XML
    assertValidBPMN(definitions2, "test-RoundTripping.testCase12.bpmn");
  }
}
