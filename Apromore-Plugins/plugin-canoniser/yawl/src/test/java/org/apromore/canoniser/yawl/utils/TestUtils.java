/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.yawl.internal.utils.NamespaceFilter;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSchema;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.YAWLOrgDataSchema;

public final class TestUtils {

    private static final int BUFFER_SIZE = 8192*4;

    private TestUtils() {
    };

    public static final String TEST_RESOURCES_DIRECTORY = "src/test/resources/";

    public static SpecificationSetFactsType unmarshalYAWL(final File yawlFile) throws JAXBException, SAXException, IOException {
        BufferedInputStream yawlFormat = new BufferedInputStream(new FileInputStream(yawlFile), BUFFER_SIZE);
        try {
            return YAWLSchema.unmarshalYAWLFormat(yawlFormat, false).getValue();
        } finally {
            yawlFormat.close();
        }
    }

    public static OrgDataType unmarshalYAWLOrgData(final File orgDataFile) throws JAXBException, FileNotFoundException, SAXException {
        final NamespaceFilter namespaceFilter = new NamespaceFilter("http://www.yawlfoundation.org/yawlschema/orgdata", true);
        // Create an XMLReader to use with our filter
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        namespaceFilter.setParent(reader);
        // Prepare the input, in this case a java.io.File (output)
        final InputSource is = new InputSource(new BufferedInputStream(new FileInputStream(orgDataFile)));
        // Create a SAXSource specifying the filter
        final SAXSource source = new SAXSource(namespaceFilter, is);

        return YAWLOrgDataSchema.unmarshalYAWLOrgDataFormat(source, false).getValue();
    }

    public static void printAnf(final AnnotationsType anf, final OutputStream outputStream) throws JAXBException, IOException, SAXException {
        BufferedOutputStream annotationFormat = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        try {
            ANFSchema.marshalAnnotationFormat(annotationFormat, anf, true);
        } finally {
            annotationFormat.close();
        }
    }

    public static void printCpf(final CanonicalProcessType cpf, final OutputStream outputStream) throws JAXBException, SAXException, IOException {
        BufferedOutputStream canonicalFormat = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        try {
            CPFSchema.marshalCanonicalFormat(canonicalFormat, cpf, true);
        } finally {
            canonicalFormat.close();
        }
    }

    public static void printYawl(final SpecificationSetFactsType yawl, final OutputStream outputStream) throws JAXBException, SAXException,
            IOException {
        BufferedOutputStream yawlFormat = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        try {
            YAWLSchema.marshalYAWLFormat(yawlFormat, yawl, true);
        } finally {
            yawlFormat.close();
        }
    }

    public static void printYawlOrgData(final OrgDataType yawlOrgData, final OutputStream outputStream) throws JAXBException, SAXException,
            IOException {
        BufferedOutputStream yawlOrgDataFormat = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        try {
            YAWLOrgDataSchema.marshalYAWLOrgDataFormat(yawlOrgDataFormat, yawlOrgData, true);
        } finally {
            yawlOrgDataFormat.close();
        }
    }

    public static File createTestOutputFile(final Class<?> testClass, final String fileName) {
        if (!new File("target/test/" + testClass.getName() + "/").exists()) {
            new File("target/test/" + testClass.getName() + "/").mkdirs();
        }
        return new File("target/test/" + testClass.getName() + "/" + fileName);
    }

}
