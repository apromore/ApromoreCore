package org.apromore.canoniser.epml;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

// Local packages
import de.epml.CorrectedEPML;
import de.epml.EPMLSchema;
import de.epml.TypeEPML;
import org.apromore.canoniser.epml.internal.EPML2Canonical;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;

/**
 * Command line tool for canonizing EPML files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Epml2Cpf {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments; either empty or a single argument, the filename of the ANF
     */
    public static void main(String[] arg) throws CanoniserException, IOException, JAXBException, SAXException, TransformerException {

        // Parse EPML from the standard input stream
        ByteArrayInputStream in = new ByteArrayInputStream(new CorrectedEPML(new StreamSource(System.in)).toByteArray());
        TypeEPML epml = EPMLSchema.unmarshalEPMLFormat(in, true /* validation enabled */).getValue();

        // Canonize EPML to CPF
        CanonicalProcessType cpf = new EPML2Canonical(epml).getCPF();

        // Serialize the CPF to the standard output stream
        CPFSchema.marshalCanonicalFormat(System.out, cpf, true /* validation enabled */);
    }
}
