package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.message.PluginMessage;

/**
 * Command line tool for de-canonizing BPMN files.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Cpf2Bpmn {

    /**
     * Application entry point.
     *
     * @param arg  command line arguments, currently ignored
     */
    public static void main(String[] arg) throws CanoniserException, IOException, JAXBException, SAXException {

        CanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(arg[0]), true);
        AnnotationsType      anf = null;

        // If there's a command line argument, treat it as the filename of an ANF file
        if (arg.length > 0) {
            anf = AnfAnnotationsType.newInstance(new FileInputStream(arg[1]), true);
        }

        // Read BPMN from the input stream
        PluginResult result = new BPMN20Canoniser().deCanonise(cpf, anf, System.out, null);

        // Write warnings to the error stream
        for (PluginMessage message : result.getPluginMessage()) {
            System.err.println(message.getMessage());
        }
    }
}
