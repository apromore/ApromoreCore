package org.apromore.canoniser.yawl.cpf2yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;

@Ignore
public class BasicTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/EmptyNet.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/EmptyNet.yawl.anf");
    }

    /**
     * Some basic checks on the YAWL layout
     */
    @Test
    public void testLayoutBasics() {
        final SpecificationSetFactsType spec = canonical2Yawl.getYAWL();
        assertTrue(spec.getLayout().getSpecification().size() == 1);
        assertNotNull(spec.getLayout().getLocale());
        assertNotNull(spec.getLayout().getLocale().getCountry());
        assertNotNull(spec.getLayout().getLocale().getLanguage());
        assertNotNull(spec.getLayout().getSpecification().get(0).getNet().size() > 0);
    }

    @Test
    public void testLayoutNetColor() {
        final SpecificationSetFactsType spec = canonical2Yawl.getYAWL();
        final Specification layout = spec.getLayout().getSpecification().get(0);
        final LayoutNetFactsType netLayout = layout.getNet().get(0);

        final int rgb = (int) netLayout.getBgColor().longValue();

        final int red = (rgb >> 16) & 0x0ff;
        final int green = (rgb >> 8) & 0x0ff;
        final int blue = (rgb) & 0x0ff;

        assertEquals(204, red);
        assertEquals(204, green);
        assertEquals(204, blue);
    }

}
