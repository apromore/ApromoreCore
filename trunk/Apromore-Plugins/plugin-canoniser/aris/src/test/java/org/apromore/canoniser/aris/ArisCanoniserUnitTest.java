package org.apromore.canoniser.aris;

// Java 2 Standard packages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.output.NullOutputStream;
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginRequestImpl;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// Third party packages
// Local classes

/**
 * Test suite for {@link ArisCanoniser}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class ArisCanoniserUnitTest {

    /**
     * Directory from which to read input files.
     *
     * In order to comply with the the nondisclosure agreement on this dataset, it isn't included in the source repository.
     * The Maven test target needs to be invoked with a <code>-Dnda.data.dir=<i>directory</i></code> to indicate the
     * directory containing the data.  If this system property is absent, the test will be skipped.
     *
     * Additionally, the test dataset is very large and will exceed the default JAXP limit on allowed XML entities.
     * The limit can be raised to a sufficent value by passing <code>-DentityExpansionLimit=200000</code>.
     *
     * To summarize, this test is run by invoking Maven with a command similar to the following:
     * <pre>
     * mvn -DentityExpansionLimit=200000 -Dnda.data.dir=/Volumes/NDA_DATA/ clean test
     * </pre>
     */
    private static File AML_DIRECTORY = System.getProperty("nda.data.dir") == null ? null : new File(System.getProperty("nda.data.dir"));

    /** Directory in which to create output files. */
    private static File OUTPUT_DIRECTORY = new File("target");

    /** Logger.  This is named after the class. */
    private Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /** Exercise the {@link ArisCanoniser#canonise} method. */
    @Ignore
    @Test
    public void testCanonise_GI() throws Exception {

        // Skip this test if the NDA'ed dataset location wasn't configured at runtime
        if (AML_DIRECTORY == null) {
            logger.info("Property nda.data.dir not set; skipping test of NDA'ed dataset.");
            return;
        }

        ArisCanoniser canoniser = new ArisCanoniser();
        List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();
        List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();
        PluginRequest request = new PluginRequestImpl();
        canoniser.canonise(new FileInputStream(new File(AML_DIRECTORY, "GI - Building Blocks - ClaimCenter.xml")), anfList, cpfList, request);

        assertEquals(1, cpfList.size());
        CPFSchema.marshalCanonicalFormat(new FileOutputStream(new File(OUTPUT_DIRECTORY, "GI.cpf")), cpfList.get(0), false);
        CPFSchema.marshalCanonicalFormat(new NullOutputStream(), cpfList.get(0), true);
    }

    /** Exercise the {@link ArisCanoniser#canonise} method. */
    @Ignore
    @Test
    public void testCanoniseFragment_gp2() throws Exception {

        // Skip this test if the NDA'ed dataset location wasn't configured at runtime
        if (AML_DIRECTORY == null) {
            logger.info("Property nda.data.dir not set; skipping test of NDA'ed dataset.");
            return;
        }

        ArisCanoniser canoniser = new ArisCanoniser();
        List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();
        List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();
        PluginRequest request = new PluginRequestImpl();
        canoniser.canonise(new FileInputStream(new File(AML_DIRECTORY, "Model.gp2---10----u--.xml")), anfList, cpfList, request);

        assertEquals(1, cpfList.size());
        CPFSchema.marshalCanonicalFormat(new FileOutputStream(new File(OUTPUT_DIRECTORY, "gp2.cpf")), cpfList.get(0), false);
        ANFSchema.marshalAnnotationFormat(new FileOutputStream(new File(OUTPUT_DIRECTORY, "gp2.anf")), anfList.get(0), false);
        CPFSchema.marshalCanonicalFormat(new NullOutputStream(), cpfList.get(0), true);
        ANFSchema.marshalAnnotationFormat(new NullOutputStream(), anfList.get(0), true);
    }
}
