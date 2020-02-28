/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test the Name Value Pair POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NameValuePairUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NameValuePair.class);
    }


    @Test
    public void testConstructor() {
        String first = "1";
        String second = "2";

        NameValuePair obj = new NameValuePair(first, second);
        assertEquals("First param wasn't the expected value" , first, obj.getName());
        assertEquals("Second Param wasn't the expected value" , second, obj.getValue());
    }
}