/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
