/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.util.Comparator;
import org.apromore.portal.model.SummaryType;
/*
 * @author Mohammad Ali
 */
public class SummaryTypeComparator implements Comparator<SummaryType> {
    @Override
    public int compare(SummaryType summaryType1, SummaryType summaryType2) {
        if (summaryType1 == null || summaryType2 == null) {
            return 0;
        }
        return AlphaNumericComparator.compareTo(summaryType1.getName(), summaryType2.getName());
    }
}
