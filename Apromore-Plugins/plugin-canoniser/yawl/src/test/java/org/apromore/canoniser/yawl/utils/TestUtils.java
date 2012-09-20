package org.apromore.canoniser.yawl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

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
        // NoOp
    };

    public static final String TEST_RESOURCES_DIRECTORY = "src/test/resources/";

    private static final String ANF_CONTEXT = "org.apromore.anf";

    public static SpecificationSetFactsType unmarshalYAWL(final File yawlFile) throws JAXBException, FileNotFoundException, SAXException {
        return YAWLSchema.unmarshalYAWLFormat(new FileInputStream(yawlFile), false).getValue();
    }

    public static OrgDataType unmarshalYAWLOrgData(final File orgDataFile) throws JAXBException, FileNotFoundException, SAXException {
        final NamespaceFilter namespaceFilter = new NamespaceFilter("http://www.yawlfoundation.org/yawlschema/orgdata", true);
        // Create an XMLReader to use with our filter
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        namespaceFilter.setParent(reader);
        // Prepare the input, in this case a java.io.File (output)
        final InputSource is = new InputSource(new FileInputStream(orgDataFile));
        // Create a SAXSource specifying the filter
        final SAXSource source = new SAXSource(namespaceFilter, is);

        return YAWLOrgDataSchema.unmarshalYAWLOrgDataFormat(source, false).getValue();
    }

    public static void printAnf(final AnnotationsType anf, final OutputStream outputStream) throws JAXBException, IOException {
        final JAXBContext context = JAXBContext.newInstance(ANF_CONTEXT);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(new org.apromore.anf.ObjectFactory().createAnnotations(anf), outputStream);
        outputStream.flush();
    }

    public static void printCpf(final CanonicalProcessType cpf, final OutputStream outputStream) throws JAXBException, SAXException, IOException {
        CPFSchema.marshalCanoncialFormat(outputStream, cpf, true);
        outputStream.flush();
    }

    public static void printYawl(final SpecificationSetFactsType yawl, final OutputStream outputStream) throws JAXBException, SAXException,
    IOException {
        YAWLSchema.marshalYAWLFormat(outputStream, yawl, true);
        outputStream.flush();
    }

    public static void printYawlOrgData(final OrgDataType yawlOrgData, final OutputStream outputStream) throws JAXBException, SAXException,
    IOException {
        YAWLOrgDataSchema.marshalYAWLOrgDataFormat(outputStream, yawlOrgData, true);
        outputStream.flush();
    }

    public static File createTestOutputFile(final Class<?> testClass, final String fileName) {
        if (!new File("target/test/" + testClass.getName() + "/").exists()) {
            new File("target/test/" + testClass.getName() + "/").mkdirs();
        }
        return new File("target/test/" + testClass.getName() + "/" + fileName);
    }

    public static CanonicalProcessType unmarshalCPF(final File cpfFile) throws JAXBException, FileNotFoundException, SAXException {
        return CPFSchema.unmarshalCanonicalFormat(new FileInputStream(cpfFile), false).getValue();
    }

    @SuppressWarnings("unchecked")
    public static AnnotationsType unmarshalANF(final File anfFile) throws JAXBException {
        if (anfFile != null) {

            final JAXBContext jc = JAXBContext.newInstance(ANF_CONTEXT);
            final Unmarshaller u = jc.createUnmarshaller();
            return ((JAXBElement<AnnotationsType>) u.unmarshal(anfFile)).getValue();

        } else {
            return null;
        }
    }

}
