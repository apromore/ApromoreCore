package org.apromore.canoniser.yawl.internal.impl.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.internal.impl.context.YAWLConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.SpecificationHandler;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

public class YAWLConversionFactoryUnitTest extends BaseYAWL2CPFUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC1Sequence.yawl");
    }

    @Test
    public void testCreateHandler() throws JAXBException, CanoniserException, SAXException, IOException {
        final SpecificationSetFactsType yawlSpec = TestUtils.unmarshalYAWL(getYAWLFile());
        final YAWLSpecificationFactsType specification = yawlSpec.getSpecification().get(0);
        final YAWLConversionFactory factory = new YAWLConversionFactory(new YAWLConversionContext(specification, yawlSpec.getLayout(),
                TestUtils.unmarshalYAWLOrgData(getYAWLOrgDataFile()), new NoOpMessageManager()));
        assertNotNull(factory.getContext());
        assertNotNull(factory.createHandler(specification.getDecomposition().get(0), null, null));
        assertNotNull(factory.createHandler(specification, null, null, SpecificationHandler.class));
        try {
            factory.createHandler(null, null, null);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

}
