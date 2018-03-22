/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ConversionUUIDGeneratorUnitTest {

    @Test
    public void testGetUUID() {
        final ConversionUUIDGenerator gen = new ConversionUUIDGenerator();
        assertNotNull(gen.getUUID(null));
        final String id1 = gen.getUUID("test");
        final String id2 = gen.getUUID("test");
        assertEquals(id1, id2);

        final String id3 = gen.getUUID("test_");
        assertFalse(id3.contains("_"));

        final String id4 = gen.getUUID("test ");
        assertFalse(id4.contains(" "));

    }

}
