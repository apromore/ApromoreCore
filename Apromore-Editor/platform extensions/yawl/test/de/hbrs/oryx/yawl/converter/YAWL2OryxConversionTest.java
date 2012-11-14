/**
 * Copyright (c) 2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See: http://www.gnu.org/licenses/lgpl-3.0
 *
 */
package de.hbrs.oryx.yawl.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map.Entry;

import org.jdom2.JDOMException;
import org.json.JSONException;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.YAWLConverter.OryxResult;

/**
 * Testing Conversion from YAWL to Oryx
 */
public class YAWL2OryxConversionTest {

    @Test
    public void testOrderfulfilmentConversion() throws YSyntaxException, JDOMException, IOException, JSONException {
        YAWLConverter converter = new YAWLConverter();
        OryxResult result = converter.convertYAWLToOryx(YAWLTestData.orderFulfillmentSource);
        assertNotNull(result);
        assertEquals("There should be 9 nets in Orderfulfilment", 9, result.getDiagrams().size());
        assertNotNull(result.getRootDiagram());
        assertNotNull(result.getWarnings());
        assertNotNull(result.getRootNetId());
        assertEquals("Wrong RootNetId", "Overall", result.getRootNetId());
        assertFalse(result.hasFailed());

        BasicDiagram d = result.getRootDiagram();
        assertEquals("http://b3mn.org/stencilset/yawl2.2#", d.getStencilsetRef().getNamespace());
        assertEquals("Wrong amount of Shapes", 16, d.getChildShapesReadOnly().size());
        assertEquals("Overall", d.getProperty("yawlid"));

        for (Entry<String, BasicDiagram> diagramEntry : result.getDiagrams()) {
            FileWriter fWriter = new FileWriter(new File("target/orderfulfilment-" + diagramEntry.getKey() + ".json"));
            fWriter.write(GenericJSONBuilder.parseModel(diagramEntry.getValue()).toString(4));
            fWriter.close();
        }

    }

    @Test
    public void testFilmproductionConversion() throws IOException, JSONException, YSyntaxException, JDOMException {
        YAWLConverter converter = new YAWLConverter();
        OryxResult result = converter.convertYAWLToOryx(YAWLTestData.filmProduction);
        assertNotNull(result);
        assertEquals("There should be 1 net in FilmProduction", 1, result.getDiagrams().size());
        assertNotNull(result.getRootDiagram());
        assertNotNull(result.getWarnings());
        assertNotNull(result.getRootNetId());
        assertEquals("Wrong RootNetId", "Film_Production_Process", result.getRootNetId());
        assertFalse(result.hasFailed());

        for (Entry<String, BasicDiagram> diagramEntry : result.getDiagrams()) {
            FileWriter fWriter = new FileWriter(new File("target/filmproduction-" + diagramEntry.getKey() + ".json"));
            fWriter.write(GenericJSONBuilder.parseModel(diagramEntry.getValue()).toString(4));
            fWriter.close();
        }
    }
    
    @Test
    public void testMiscYAWLModels() throws YSyntaxException, JDOMException, IOException {
        File yawlModelDir = new File("resources/miscYawlModels");
        File[] testModels = yawlModelDir.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yawl");
            }
        });
        
        for (int i = 0; i < testModels.length; i++) {
            File model = testModels[i];
            try (Scanner modelScanner = new Scanner(model, "UTF-8")) {
                if (model.length() > 0) {
                    String modelSrc = modelScanner.useDelimiter("\\A").next();
                    YAWLConverter converter = new YAWLConverter();
                    OryxResult result = converter.convertYAWLToOryx(modelSrc);
                    assertNotNull(result);
                    assertNotNull(result.getRootDiagram());
                    assertNotNull(result.getWarnings());
                    assertNotNull(result.getRootNetId());
                    assertFalse(result.getDiagrams().isEmpty());
                    assertFalse(result.hasFailed());
                    System.out.println("Tested conversion of: "+result.getRootNetId());
                }
            }
        }
    }

}
