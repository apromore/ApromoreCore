package org.apromore.canoniser.yawl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.Canonical2YAWL;
import org.apromore.canoniser.yawl.internal.impl.Canonical2YAWLImpl;
import org.apromore.canoniser.yawl.utils.GraphvizVisualiser;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.NullOutputStream;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.VarMappingFactsType;
import org.yawlfoundation.yawlschema.VariableFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

/**
 * Base class for tests on CPF -> YAWL conversion
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class BaseCPF2YAWLTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCPF2YAWLTest.class);

    private static final int BUFFER_SIZE = 8192*4;

    protected boolean shouldCanonisationFail = false;

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
        canonical2Yawl = new Canonical2YAWLImpl(new NoOpMessageManager());
        LOGGER.debug("Testing file {}", getCPFFile().getName());
        BufferedInputStream canonicalFormat = new BufferedInputStream(new FileInputStream(getCPFFile()), BUFFER_SIZE);
        try {
            cpfProcess = CPFSchema.unmarshalCanonicalFormat(canonicalFormat, false).getValue();
        } finally {
            canonicalFormat.close();
        }
        try {
            AnnotationsType anf = null;
            if (getANFFile() != null) {
                BufferedInputStream annotationsFormat = new BufferedInputStream(new FileInputStream(getANFFile()), BUFFER_SIZE);
                try {
                    anf = ANFSchema.unmarshalAnnotationFormat(annotationsFormat, false).getValue();
                } finally {
                    annotationsFormat.close();
                }
            }
            if (anf != null) {
                canonical2Yawl.convertToYAWL(cpfProcess, anf);
            } else {
                canonical2Yawl.convertToYAWL(cpfProcess);
            }
        } catch (final RuntimeException e) {
            throw new RuntimeException(getCPFFile().getName(), e);
        } catch (final CanoniserException e) {
            if (!shouldCanonisationFail) {
                throw e;
            }
        }
    }

    @Test
    public void testNetInputOutputCondition() {
        if (!shouldCanonisationFail) {
            for (DecompositionType d: canonical2Yawl.getYAWL().getSpecification().get(0).getDecomposition()) {
                if (d instanceof NetFactsType) {
                    assertNotNull(((NetFactsType) d).getProcessControlElements().getInputCondition());
                    assertNotNull(((NetFactsType) d).getProcessControlElements().getOutputCondition());
                }
            }
        }
    }

    @Test
    public void testSaveResult() throws JAXBException, IOException, SAXException {
        if (!shouldCanonisationFail) {
            OutputStream yawlStream = null;
            OutputStream orgStream = null;
            if (LOGGER.isDebugEnabled()) {
                yawlStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + ".yawl"));
                orgStream = new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + ".ybkp"));
            } else {
                yawlStream = new NullOutputStream();
                orgStream = new NullOutputStream();
            }
            TestUtils.printYawl(canonical2Yawl.getYAWL(), yawlStream);
            TestUtils.printYawlOrgData(canonical2Yawl.getOrgData(), orgStream);
            if (LOGGER.isDebugEnabled()) {
                createGraphImages(cpfProcess.getNet().get(0));
            }
        }
    }

    private void createGraphImages(final NetType net) throws FileNotFoundException, IOException {
        final GraphvizVisualiser v = new GraphvizVisualiser();
        v.createImageAsDOT(net,
                new BufferedOutputStream(new FileOutputStream(TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + "-" + net.getOriginalID() + ".dot"))));
        try {
            v.createImageAsPNG(net, TestUtils.createTestOutputFile(this.getClass(), getCPFFile().getName() + "-" + net.getOriginalID() + ".png"));
        } catch (final IOException e) {
            // Just build image if Graphviz exists
            LOGGER.warn("WARN: " + e.getMessage());
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
        fail("Task "+name+" not found.");
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
        fail("Condition "+name+" not found.");
        return null;
    }

    protected VariableFactsType checkLocalVariable(final String name, final String type, final NetFactsType net) {
        for (VariableFactsType var: net.getLocalVariable()) {
            if (type.equals(var.getType()) && name.equals(var.getName())) {
                return var;
            }
        }
        fail("Local variable "+name+" with type "+type+" not found.");
        return null;
    }

    protected OutputParameterFactsType checkOutputParameter(final String name, final String type, final DecompositionFactsType d) {
        for (OutputParameterFactsType param: d.getOutputParam()) {
            if (type.equals(param.getType()) && name.equals(param.getName())) {
                return param;
            }
        }
        fail("Output parameter "+name+" with type "+type+" not found.");
        return null;
    }

    protected InputParameterFactsType checkInputParameter(final String name, final String type, final DecompositionFactsType d) {
        for (InputParameterFactsType param: d.getInputParam()) {
            if (type.equals(param.getType()) && name.equals(param.getName())) {
                return param;
            }
        }
        fail("Input parameter "+name+" with type "+type+" not found.");
        return null;
    }


    protected VarMappingFactsType checkOutputMapping(final String name, final String mapping, final ExternalTaskFactsType task) {
        String invalidMapping = null;
        if (task.getCompletedMappings() == null) {
            fail("No output mapping!");
        }
        for (VarMappingFactsType varMapping: task.getCompletedMappings().getMapping()) {
            if (name.equals(varMapping.getMapsTo()) && mapping.equals(varMapping.getExpression().getQuery())) {
                return varMapping;
            }
            if (name.equals(varMapping.getMapsTo())) {
                invalidMapping = varMapping.getExpression().getQuery();
            }
        }
        if (invalidMapping != null) {
            fail("Output mapping "+name+" invalid "+invalidMapping);
        } else {
            fail("Output mapping not found "+name+" with expression "+mapping);
        }
        return null;
    }

    protected VarMappingFactsType checkInputMapping(final String name, final String mapping, final ExternalTaskFactsType task) {
        String invalidMapping = null;
        if (task.getStartingMappings() == null) {
            fail("No input mapping!");
        }
        for (VarMappingFactsType varMapping: task.getStartingMappings().getMapping()) {
            if (name.equals(varMapping.getMapsTo()) && mapping.equals(varMapping.getExpression().getQuery())) {
                return varMapping;
            }
            if (name.equals(varMapping.getMapsTo())) {
                invalidMapping = varMapping.getExpression().getQuery();
            }
        }
        if (invalidMapping != null) {
            fail("Input mapping "+name+" invalid "+invalidMapping);
        } else {
            fail("Input mapping not found "+name+" with expression "+mapping);
        }
        return null;
    }

    protected WebServiceGatewayFactsType findDecomposition(final ExternalTaskFactsType task) {
        final List<DecompositionType> decompositionList = canonical2Yawl.getYAWL().getSpecification().get(0).getDecomposition();
        for (final DecompositionType d : decompositionList) {
            if (d instanceof WebServiceGatewayFactsType) {
                final WebServiceGatewayFactsType taskD = (WebServiceGatewayFactsType) d;
                if (taskD.getId().equals(task.getDecomposesTo().getId())) {
                    return taskD;
                }
            }
        }
        fail("Could not find decomposition for Task "+task.getId());
        return null;
    }

}
