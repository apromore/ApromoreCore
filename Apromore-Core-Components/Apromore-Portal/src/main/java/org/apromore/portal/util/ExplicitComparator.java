/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Define an ordering by supplying an explicit list of elements in the desired order.
 */
public class ExplicitComparator implements Comparator<String> {

    protected List<String> examples;
    

    // Constructor

    /**
     * @param csv  Comma-separated list of strings in the desired order
     */
    public ExplicitComparator(String csv) {
        examples = Arrays.asList(csv.split(","));
    }


    // Implementation of the Comparator interface

    @Override
    public int compare(String o1, String o2) {
        return examples.indexOf(o1) - examples.indexOf(o2);
    }
}
