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
package org.apromore.plugin.portal.processdiscoverer.data;

import lombok.Getter;

import java.text.DecimalFormat;
import org.apromore.commons.datetime.DurationUtils;

@Getter
public class CaseVariantDetails {

    private final int caseVariantId;
    private final long activityInstances;
    private final double avgDuration;
    private final String avgDurationStr;
    private final long numCases;
    private final double freq;
    private final String freqStr;

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    private CaseVariantDetails(final int caseVariantId, final long occurrences, final long numCases,
                               final double duration, final double frequency) {
        this.caseVariantId = caseVariantId;
        this.activityInstances = occurrences;
        this.numCases = numCases;
        this.avgDuration = duration;
        this.avgDurationStr = DurationUtils.humanize(duration, true);
        this.freq = frequency;
        this.freqStr = decimalFormat.format(100 * freq);
    }

    public static CaseVariantDetails valueOf(final int caseVariantId, final long occurrences, final long numCases,
                                             final double duration, final double frequency) {
        return new CaseVariantDetails(caseVariantId, occurrences, numCases, duration, frequency);
    }
}
