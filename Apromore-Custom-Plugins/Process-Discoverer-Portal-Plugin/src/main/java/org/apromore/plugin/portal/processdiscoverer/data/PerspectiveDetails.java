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

import java.text.DecimalFormat;

public class PerspectiveDetails {

    private final String value;
    private final long occurrences;
    private final double freq;
    private final String freqStr;

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    private PerspectiveDetails(String value, long occurrences, double freq) {
        this.value = value;
        this.occurrences = occurrences;
        this.freq = freq;
        this.freqStr = decimalFormat.format(100 * freq);
    }

    public static PerspectiveDetails valueOf (String value, long occurrences, double freq) {
        return new PerspectiveDetails(value, occurrences, freq);
    }

    public String getValue() {
        return value;
    }

    public long getOccurrences() {
        return occurrences;
    }

    public double getFreq() {
        return freq;
    }

    public String getFreqStr() {
        return freqStr;
    }
}
