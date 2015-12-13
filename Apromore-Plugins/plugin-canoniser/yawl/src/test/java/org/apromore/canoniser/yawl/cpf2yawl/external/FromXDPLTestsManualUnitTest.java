/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This test is just checking if any exceptions occur during conversion. Just basic structural checks are done.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
@RunWith(Parameterized.class)
public class FromXDPLTestsManualUnitTest extends WholeDirectoryUnitTest {

    public static String MODEL_DIR = TestUtils.TEST_RESOURCES_DIRECTORY + "/CPF/External/XPDL";

    @Parameters
    public static Collection<Object[]> getFiles() {
        final Collection<Object[]> params = new ArrayList<Object[]>();
        for (final File f : getCPFFiles()) {
            final File anfFile = new File(f.getPath() + (f.getName().replace(".cpf", "")) + ".anf");
            if (anfFile.exists()) {
                final Object[] arr = new Object[] { f, anfFile };
                params.add(arr);
            } else {
                final Object[] arr = new Object[] { f, null };
                params.add(arr);
            }
        }
        return params;

    }

    private static File[] getCPFFiles() {
        return new File(MODEL_DIR).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".cpf");
            }
        });
    }

    public FromXDPLTestsManualUnitTest(final File testCPFFile, final File testANFFile) {
        super(testCPFFile, testANFFile);
        shouldCanonisationFail = isName(testCPFFile, "Complex_GW.cpf");
    }

}
