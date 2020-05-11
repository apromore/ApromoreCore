/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

/**
 *
 */
package org.apromore.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class Cluster {

    private org.apromore.dao.model.Cluster cluster;
    private List<MemberFragment> fragments = new ArrayList<MemberFragment>();

    public org.apromore.dao.model.Cluster getCluster() {
        return cluster;
    }

    public void setCluster(org.apromore.dao.model.Cluster cluster) {
        this.cluster = cluster;
    }

    public void addFragment(MemberFragment fragment) {
        fragments.add(fragment);
    }

    public List<MemberFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<MemberFragment> fragments) {
        this.fragments = fragments;
    }
}
