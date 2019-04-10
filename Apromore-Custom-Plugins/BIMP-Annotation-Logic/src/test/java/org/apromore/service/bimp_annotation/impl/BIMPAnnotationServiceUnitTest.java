/*
 * Copyright © 2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.bimp_annotation.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("You must set the simo.* properties in site.properties before enabling this test suite.")
public class BIMPAnnotationServiceUnitTest {

    private BIMPAnnotationService service = null;
    private Transformer transformer;

    @Before
    public void setUp() throws IOException, TransformerConfigurationException {
        Properties site = new Properties();
        site.load(new FileInputStream("../../site.properties"));

        service = new BIMPAnnotationServiceImpl(
            site.getProperty("simo.python"),
            new File(site.getProperty("simo.backend")),
            Integer.parseInt(site.getProperty("simo.timeout"))
        );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer(new StreamSource(ClassLoader.getSystemResourceAsStream("strip-qbp-ids.xsl")));
    }

    @Test
    public void testAnnotateBPMNModelForBIMP() throws Exception {
        ClassLoader cl = BIMPAnnotationServiceUnitTest.class.getClassLoader();
        String model = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("Production.bpmn")))
            .lines()
            .collect(Collectors.joining("\n"));
        XLog log = (new XesXmlGZIPParser())
            .parse(cl.getResourceAsStream("Production.xes.gz"))
            .listIterator()
            .next();
        String expected = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("Production.bpmn+bimp")))
            .lines()
            .collect(Collectors.joining("\n"));

        String result = service.annotateBPMNModelForBIMP(model, log, new BIMPAnnotationService.Context() {
            public void setDescription(String description) {}
            public void setFractionComplete(Double fractionComplete) {}
        });

        Assert.assertNotNull(result);
        Assert.assertEquals(stripQBPids(expected), stripQBPids(result));
    }

    /**
     * Remove all UUID ids from the QBP elements of an annotated BPMN document.
     *
     * Every execution of {@link BIMPAnnotationServiceImpl#annotateBPMNModelForBIMP}
     * produces a unique result due to the UUIDs.
     * Removing the UUIDs allows simple file comparison with the expected result.
     * 
     * @param bpmn  a BPMN XML with QBP annotations
     * @return <i>bpmn</i> with all UUID ids removed
     * @throws TransformerException if XML processing fails
     */
    private String stripQBPids(String bpmn) throws TransformerException {
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(bpmn)), new StreamResult(writer));

        return writer.toString();
    }
}
