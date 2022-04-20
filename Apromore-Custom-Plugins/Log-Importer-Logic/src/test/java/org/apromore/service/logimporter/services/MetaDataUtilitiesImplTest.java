/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.logimporter.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MetaDataUtilitiesImplTest {

    private final MetaDataUtilities metaDataUtilities = new MetaDataUtilitiesImpl();

    @Test
    void isTimestampTest() {

        List<List<String>> sampleLog = new ArrayList<>();
        List<String> line = new ArrayList<>();
        line.add("1");
        line.add("19-12-2019 15:13:05.9");
        sampleLog.add(line);

        String format1 = "y";
        String format2 = "dd-MM-yyyy HH:mm:ss.S";

        assertFalse(metaDataUtilities.isTimestamp(0, format1, sampleLog));
        assertTrue((metaDataUtilities.isTimestamp(1, format2, sampleLog)));
    }
}