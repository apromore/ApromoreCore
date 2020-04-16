/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.service.model;

import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Cluster POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ClusterUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(Cluster.class);
    }

    @Test
    public void testAddingAFragment() {
        Cluster obj = new Cluster();
        obj.addFragment(new MemberFragment(1));
        obj.addFragment(new MemberFragment(2));

        Assert.assertThat(obj.getFragments().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(obj.getFragments().get(0).getFragmentId(), CoreMatchers.equalTo(1));
    }
}
