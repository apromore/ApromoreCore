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

package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

import static au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult.FilterType.WTH;

/**
 * Created by Adriano on 23/01/2017.
 */
public class DFGPUIResult {
    public enum FilterType{STD, NOF, FWG, WTH}

    public static final double FREQUENCY_THRESHOLD = 0.40;
    public static final double PARALLELISMS_THRESHOLD = 0.10;
    public static final FilterType STD_FILTER = WTH;
    public static final boolean PARALLELISMS_FIRST = false;

    private double percentileFrequencyThreshold;
    private double parallelismsThreshold;
    private FilterType filterType;
    private boolean parallelismsFirst;

    public DFGPUIResult() {
        percentileFrequencyThreshold = FREQUENCY_THRESHOLD;
        parallelismsThreshold = PARALLELISMS_THRESHOLD;
        filterType = STD_FILTER;
        parallelismsFirst = PARALLELISMS_FIRST;
    }

    public boolean isParallelismsFirst() { return parallelismsFirst; }
    public void setParallelismsFirst(boolean parallelismsFirst) { this.parallelismsFirst = parallelismsFirst; }

    public FilterType getFilterType() { return filterType; }
    public void setFilterType(FilterType filterType) { this.filterType = filterType; }

    public double getPercentileFrequencyThreshold() {
        return percentileFrequencyThreshold;
    }
    public void setPercentileFrequencyThreshold(double percentileFrequencyThreshold) {
        this.percentileFrequencyThreshold = percentileFrequencyThreshold;
    }

    public double getParallelismsThreshold() {
        return parallelismsThreshold;
    }
    public void setParallelismsThreshold(double parallelismsThreshold) {
        this.parallelismsThreshold = parallelismsThreshold;
    }
}
