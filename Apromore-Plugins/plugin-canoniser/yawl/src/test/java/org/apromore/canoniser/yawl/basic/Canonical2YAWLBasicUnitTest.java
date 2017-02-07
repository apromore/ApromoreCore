/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;

public class Canonical2YAWLBasicUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/EmptyNet.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/EmptyNet.yawl.anf");
    }

    /**
     * Some basic checks on the YAWL layout
     */
    @Test
    public void testLayoutBasics() {
        final SpecificationSetFactsType spec = canonical2Yawl.getYAWL();
        assertTrue(spec.getLayout().getSpecification().size() == 1);
        assertNotNull(spec.getLayout().getLocale());
        assertEquals("DE", spec.getLayout().getLocale().getCountry());
        assertEquals("de", spec.getLayout().getLocale().getLanguage());
        assertNotNull(spec.getLayout().getSpecification().get(0).getNet().size() > 0);

        LayoutNetFactsType rootNetLayout = spec.getLayout().getSpecification().get(0).getNet().get(0);
        assertNotNull(rootNetLayout);
        assertEquals(13421772, rootNetLayout.getBgColor().intValue());
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

    @Test
    public void testMetaData() {
        final SpecificationSetFactsType spec = canonical2Yawl.getYAWL();
        MetaDataType metaData = spec.getSpecification().get(0).getMetaData();
        assertTrue(metaData.getContributor().size() == 1);
        assertEquals("Firstname Lastname", metaData.getContributor().get(0));
        // TODO Duplicate??
        assertEquals("EmptyNet Workfow", metaData.getTitle());
        assertEquals("EmptyNet Workfow", spec.getSpecification().get(0).getName());
        assertEquals(new BigDecimal("0.1"), metaData.getVersion());
        assertEquals("UID_d1f45fda-5536-4bc7-b938-a2fbdacb2fb7", metaData.getIdentifier());
    }

}
