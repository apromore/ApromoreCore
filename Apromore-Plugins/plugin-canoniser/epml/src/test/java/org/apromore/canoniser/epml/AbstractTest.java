/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.canoniser.epml;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.apache.commons.io.output.NullOutputStream;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.xml.sax.SAXException;

public class AbstractTest {

    /**
     * @param resource  the name of a resource within the <code>EPML/</code> directory of the classpath, minus its <code>.epml</code> extension
     */
    public void testCanonise(final String resource) throws CanoniserException, PropertyException, JAXBException, SAXException, FileNotFoundException, IOException {
        EPML20Canoniser c = new EPML20Canoniser();
        ArrayList<AnnotationsType> anfList = new ArrayList<>();
        ArrayList<CanonicalProcessType> cpfList = new ArrayList<>();
        c.canonise(ClassLoader.getSystemResourceAsStream("EPML/" + resource + ".epml"), anfList, cpfList, new PluginRequestImpl());

        assertFalse(anfList.isEmpty());
        assertFalse(cpfList.isEmpty());

        try (FileOutputStream cpfOut = new FileOutputStream("target/" + resource + ".epml.cpf");
             FileOutputStream anfOut = new FileOutputStream("target/" + resource + ".epml.anf")) {

            // Unvalidated write to the target directory           
            CPFSchema.marshalCanonicalFormat(cpfOut, cpfList.get(0), false);
            ANFSchema.marshalAnnotationFormat(anfOut, anfList.get(0), false);

            // Validation pass
            CPFSchema.marshalCanonicalFormat(new NullOutputStream(), cpfList.get(0), true);
            ANFSchema.marshalAnnotationFormat(new NullOutputStream(), anfList.get(0), true);
        }
    }

    /**
     * @param resource  the name of a resource within both the <code>CPF/</code> and <code>ANF/</code> directories of the classpath, minus
     *     the respective <code>.cpf</code> and <code>.anf</code> extensions
     */
    public void testDeCanonise(final String resource) throws CanoniserException, JAXBException, SAXException, IOException {
        EPML20Canoniser c = new EPML20Canoniser();

        try (OutputStream epmlStream = new FileOutputStream(new File("target/" + resource + ".epml"));
                InputStream cpfStream = ClassLoader.getSystemResourceAsStream("CPF/" + resource + ".cpf");
                InputStream anfStream = ClassLoader.getSystemResourceAsStream("ANF/" + resource + ".anf");) {
            c.deCanonise(CPFSchema.unmarshalCanonicalFormat(cpfStream, true).getValue(), ANFSchema.unmarshalAnnotationFormat(anfStream, true)
                    .getValue(), epmlStream, new PluginRequestImpl());
            epmlStream.flush();
        }
    }
}
