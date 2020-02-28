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

package de.hpi.bpmn2_0.replay;

public class Metrics {
    private int missingTokenCount = 0;
    private int consumedTokenCount = 0;
    private int remainingTokenCount = 0;
    private int producedTokenCount = 0;
    private double tracePercent = 0;
    
    public int getMissingTokenCount() {
        return this.missingTokenCount;
    }
    
    public void setMissingTokenCount(int missingToken) {
        this.missingTokenCount = missingToken;
    }
    
    public int getConsumedTokenCount() {
        return this.consumedTokenCount;
    }
    
    public void setConsumedTokenCount(int consumedToken) {
        this.consumedTokenCount = consumedToken;
    }    
    
    public int getRemainingTokenCount() {
        return this.remainingTokenCount;
    }
    
    public void setRemainingTokenCount(int remainingToken) {
        this.remainingTokenCount = remainingToken;
    }
    
    public int getProducedTokenCount() {
        return this.producedTokenCount;
    }
    
    public void setProducedTokenCount(int producedToken) {
        this.producedTokenCount = producedToken;
    }
    
    public double getTokenFitness() {
        if (consumedTokenCount > 0 && producedTokenCount > 0) {
            return 1.0*(1/2*(1-missingTokenCount/consumedTokenCount) + 1/2*(1-remainingTokenCount/producedTokenCount));
        }
        else {
            return 0;
        }
    }
    
    public double getTraceFitness() {
        return this.tracePercent;
    }
    
    public void setTraceFitness(double tracePercent) {
        this.tracePercent = tracePercent;
    }    
}