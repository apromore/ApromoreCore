/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.toolbox.clustering.dissimilarity;

/**
 * The Dissimilarity Matrix Interface.
 */
public interface DissimilarityMatrix {

    public static final Double GED_THRESHOLD = 0.45D;
    public static final Double LED_CUTOFF = 0.6D;
    public static final int LARGE_FRAGMENTS = 60;
    public static final int SMALL_FRAGMENTS = 6;

    /**
     * Computer the Dissimilarity matrix for the repo.
     */
    void computeDissimilarity();

    /**
     * Returns the Dissimilarity of two fragments.
     * @param index1 fragment one
     * @param index2 fragment two
     * @return the resulting dissimilarity value
     */
    Double getDissimilarity(Integer index1, Integer index2);

    /**
     * Add a Dissimilarity Calculator to help with the process.
     * @param calc a calculator that implements the DissimilarityCalc.
     */
    void addDissimCalc(DissimilarityCalc calc);

    /**
     * Add a GED Calculator to help with the process.
     * @param calc a calculator that implements the GEDMatrixCalc.
     */
    void addGedCalc(GEDMatrixCalc calc);

    /**
     * Sets the dissimilarity threshold. within this threshold we store the value. outside we disregard.
     * @param dissThreshold the threshold.
     */
    void setDissThreshold(double dissThreshold);

}
