
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
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.bind.IDResolver;
import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

import com.processconfiguration.ConfigurationAlgorithmTest;
import com.processconfiguration.DefinitionsIDResolver;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;

/**
 * Test harness for {@link BPMN2DiagramConverter}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BPMN2DiagramConverterTest {

    static final Logger logger = Logger.getLogger(BPMN2DiagramConverterTest.class.getCanonicalName());

    /**
     * The <a href="{@docRoot}/../tests/dataGetDiagramFromBpmn20_1-expected.json/">JSON document</a>
     * expected from converting {@link ConfigurationAlgorithmTest#test1File}.
     */
    public static final File expectedJSONFile =
        new File(new File(ConfigurationAlgorithmTest.testsDirectory, "data"), "GetDiagramFromBpmn20_1-expected.json");

    /**
     * Test the {@link BPMN2DiagramConverter#getDiagramFromBpmn20} method on
     * {@link ConfigurationAlgorithmTest#test1File}.
     */
    @Ignore("Need to write a more robust comparison for JSON objects that ignores property ordering")
    @Test public void testGetDiagramFromBpmn20_1() throws IOException, JAXBException, JSONException {

        // Parse BPMN from XML to JAXB
        Unmarshaller unmarshaller = JAXBContext.newInstance(Definitions.class,
                                                            ConfigurationAnnotationAssociation.class,
                                                            ConfigurationAnnotationShape.class)
                                               .createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(ConfigurationAlgorithmTest.test1File);

        // Convert BPMN to JSON
        BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/signaviocore/editor/");
        List<BasicDiagram> diagrams = converter.getDiagramFromBpmn20(definitions);

        // Read the expected JSON
        String expectedJSON = toString(expectedJSONFile);

        // Inspect the JSON
        assertEquals(1, diagrams.size());
        String actualJSON = diagrams.get(0).getString();

        // Write out a copy of the generated JSON
        FileWriter writer = new FileWriter(new File(new File("target"), "GetDiagramFromBpmn20_1.json"));
        writer.write(actualJSON);
        writer.close();

        assertEquals(expectedJSON, actualJSON);
    }

    /**
     * Test the {@link #toString} method.
     */
    @Test public void testToString1() throws IOException {
        String string = toString(expectedJSONFile);
    }

    /**
     * @param file  a file short enough to fit into memory
     * @return the contents of the <var>file</var> as a string in the platform encoding
     */
    private static String toString(File file) throws IOException {

        StringBuffer fileData = new StringBuffer((int) file.length());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }       
}
