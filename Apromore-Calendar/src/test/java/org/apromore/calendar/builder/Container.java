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
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.calendar.builder;

import java.util.List;


public class Container {

    List<Long> start;
    List<Long> end;
    List<Long> diff;

    public List<Long> getStart() {
        return start;
    }

    public void setStart(List<Long> start) {
        this.start = start;
    }

    public List<Long> getEnd() {
        return end;
    }

    public void setEnd(List<Long> end) {
        this.end = end;
    }

    public List<Long> getDiff() {
        return diff;
    }

    public void setDiff(List<Long> diff) {
        this.diff = diff;
    }


}
