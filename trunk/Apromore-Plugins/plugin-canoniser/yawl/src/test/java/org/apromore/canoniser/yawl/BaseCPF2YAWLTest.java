package org.apromore.canoniser.yawl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.Canonical2YAWL;
import org.apromore.canoniser.yawl.internal.impl.Canonical2YAWLImpl;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

/**
 * Base class for tests on CPF -> YAWL conversion
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class BaseCPF2YAWLTest {

    protected Canonical2YAWL canonical2Yawl;

    private CanonicalProcessType cpfProcess;

    public BaseCPF2YAWLTest() {
        super();
    }

    /**
     * Override and return CPF file that should be used in this test.
     *
     * @return File
     */
    protected abstract File getCPFFile();

    /**
     * Override and return ANF file that should be used in this test.
     *
     * @return File
     */
    protected abstract File getANFFile();

    @Before
    public void setUp() throws Exception {
        canonical2Yawl = new Canonical2YAWLImpl();
        try {
            System.out.println("Testing file " + getCPFFile().getName());
            cpfProcess = CPFSchema.unmarshalCanonicalFormat(new FileInputStream(getCPFFile()), false).getValue();
            createGraphImages(cpfProcess.getNet().get(0));
            try {
                final AnnotationsType anf = ANFSchema.unmarshalAnnotationFormat(new FileInputStream(getANFFile()), false).getValue();
                if (anf != null) {
                    canonical2Yawl.convertToYAWL(cpfProcess, anf);
                } else {
                    canonical2Yawl.convertToYAWL(cpfProcess);
                }
            } catch (final RuntimeException e) {
                throw new RuntimeException(getCPFFile().getName(), e);
            }
        } catch (final CanoniserException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testSaveResult() throws JAXBException, IOException, SAXException {
        TestUtils.printYawl(canonical2Yawl.getYAWL(),
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + ".yawl")));
        TestUtils.printYawlOrgData(canonical2Yawl.getOrgData(),
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + ".ybkp")));
    }

    private void createGraphImages(final NetType net) throws FileNotFoundException, IOException {
        final GraphvizVisualiser v = new GraphvizVisualiser();
        v.createImageAsDOT(net,
                new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + "-" + net.getOriginalID() + ".dot")));
        try {
            v.createImageAsPNG(net, TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + "-" + net.getOriginalID() + ".png"));
        } catch (final IOException e) {
            // Just build image if Graphviz exists
            System.out.println("WARN: " + e.getMessage());
        }
    }

    protected NetFactsType findRootNet() {
        final List<DecompositionType> decompositionList = canonical2Yawl.getYAWL().getSpecification().get(0).getDecomposition();
        for (final DecompositionType d : decompositionList) {
            if (d instanceof NetFactsType) {
                final NetFactsType net = (NetFactsType) d;
                if (net.isIsRootNet()) {
                    return net;
                }
            }
        }
        throw new IllegalStateException("Can't find a root net, invalid YAWL specification!");
    }

    protected ExternalTaskFactsType findTaskByName(final String name, final NetFactsType net) {
        for (final ExternalNetElementFactsType element : net.getProcessControlElements().getTaskOrCondition()) {
            if (element instanceof ExternalTaskFactsType) {
                if (element.getName() != null && element.getName().equals(name)) {
                    return (ExternalTaskFactsType) element;
                }
            }
        }
        return null;
    }

    protected ExternalConditionFactsType findConditonByName(final String name, final NetFactsType net) {
        for (final ExternalNetElementFactsType element : net.getProcessControlElements().getTaskOrCondition()) {
            if (element instanceof ExternalConditionFactsType) {
                if (element.getName() != null && element.getName().equals(name)) {
                    return (ExternalConditionFactsType) element;
                }
            }
        }
        return null;
    }

}
