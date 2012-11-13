package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessage;

/**
 * Command line tool for canonizing BPMN files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Bpmn2Cpf {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments, currently ignored
     */
    public static void main(String[] arg) throws CanoniserException, JAXBException, SAXException {

        List<AnnotationsType> anfs = new ArrayList<AnnotationsType>();
        List<CanonicalProcessType> cpfs = new ArrayList<CanonicalProcessType>();

        // Read BPMN from the input stream
        PluginResult result = new BPMN20Canoniser().canonise(System.in, anfs, cpfs, null);

        // Write the cpf to the output stream
        ((CpfCanonicalProcessType) cpfs.get(0)).marshal(System.out, true);

        // Write warnings to the error stream
        for (PluginMessage message : result.getPluginMessage()) {
            System.err.println(message.getMessage());
        }
    }
}
