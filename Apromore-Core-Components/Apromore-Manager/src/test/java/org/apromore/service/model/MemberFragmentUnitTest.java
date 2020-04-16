/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test the Member Fragment POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class MemberFragmentUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(MemberFragment.class);
    }

    @Test
    public void testConstructor() {
        Integer first = 1;

        MemberFragment obj = new MemberFragment(first);
        assertEquals("First param wasn't the expected value" , first, obj.getFragmentId());
    }
}
