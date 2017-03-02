/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package au.edu.qut.processmining.repairing.ui;

/**
 * Created by Adriano on 14/06/2016.
 */
public class OptimizerUIResult {
    private boolean recurrentActivities;
    private boolean optionalActivities;
    private boolean inclusiveChoice;
    private boolean setUnbalancedPaths;
    private boolean applyCleaning;

    public boolean isOptionalActivities() { return optionalActivities; }
    public void setOptionalActivities(boolean optionalActivities) { this.optionalActivities = optionalActivities; }

    public boolean isInclusiveChoice() {
        return inclusiveChoice;
    }
    public void setInclusiveChoice(boolean inclusiveChoice) { this.inclusiveChoice = inclusiveChoice; }

    public boolean isRecurrentActivities() { return recurrentActivities; }
    public void setRecurrentActivities(boolean recurrentActivities) { this.recurrentActivities = recurrentActivities; }

    public boolean isUnbalancedPaths() { return setUnbalancedPaths; }
    public void setUnbalancedPaths(boolean setUnbalancedPaths) { this.setUnbalancedPaths = setUnbalancedPaths; }

    public boolean isApplyCleaning() { return applyCleaning; }
    public void setApplyCleaning(boolean applyCleaning) { this.applyCleaning = applyCleaning; }
}
