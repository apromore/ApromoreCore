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

    private TestUtils() {
    };

    public static final String TEST_RESOURCES_DIRECTORY = "src/test/resources/";

    public static SpecificationSetFactsType unmarshalYAWL(final File yawlFile) throws JAXBException, FileNotFoundException, SAXException {
        return YAWLSchema.unmarshalYAWLFormat(new BufferedInputStream(new FileInputStream(yawlFile)), false).getValue();
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
        try {
            ANFSchema.marshalAnnotationFormat(new BufferedOutputStream(outputStream), anf, true);
        } finally {
            outputStream.close();
        }
    }

    public static void printCpf(final CanonicalProcessType cpf, final OutputStream outputStream) throws JAXBException, SAXException, IOException {
        try {
            CPFSchema.marshalCanoncialFormat(new BufferedOutputStream(outputStream), cpf, true);
        } finally {
            outputStream.close();
        }
    }

    public static void printYawl(final SpecificationSetFactsType yawl, final OutputStream outputStream) throws JAXBException, SAXException,
            IOException {
        try {
            YAWLSchema.marshalYAWLFormat(new BufferedOutputStream(outputStream), yawl, true);
        } finally {
            outputStream.close();
        }
    }

    public static void printYawlOrgData(final OrgDataType yawlOrgData, final OutputStream outputStream) throws JAXBException, SAXException,
            IOException {
        try {
            YAWLOrgDataSchema.marshalYAWLOrgDataFormat(new BufferedOutputStream(outputStream), yawlOrgData, true);
        } finally {
            outputStream.close();
        }
    }

    public static File createTestOutputFile(final Class<?> testClass, final String fileName) {
        if (!new File("target/test/" + testClass.getName() + "/").exists()) {
            new File("target/test/" + testClass.getName() + "/").mkdirs();
        }
        return new File("target/test/" + testClass.getName() + "/" + fileName);
    }

}
