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

import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Adriano on 14/06/2016.
 */
public class OptimizerSettings extends ProMPropertiesPanel {

    private static final String DIALOG_NAME = "Setup Optimizer";
    private OptimizerUIResult result;

    private JCheckBox inclusiveChoice;
    private JCheckBox optionalActivities;
    private JCheckBox recurrentActivities;
    private JCheckBox unbalancedPaths;
    private JCheckBox applyCleaning;

    public OptimizerSettings() {
        super(DIALOG_NAME);

        result = new OptimizerUIResult();

        MinerSettingsListener minerListener = new MinerSettingsListener();

        unbalancedPaths = this.addCheckBox("Unbalanced Paths", true);
        unbalancedPaths.addChangeListener(minerListener);

        optionalActivities = this.addCheckBox("Optional Activities", true);
        optionalActivities.addChangeListener(minerListener);

        recurrentActivities = this.addCheckBox("Recurrent Activities", false);
        recurrentActivities.addChangeListener(minerListener);

        inclusiveChoice = this.addCheckBox("Inclusive Choice", false);
        inclusiveChoice.addChangeListener(minerListener);

        applyCleaning = this.addCheckBox("Apply Cleaning", false);
        applyCleaning.addChangeListener(minerListener);

        result.setOptionalActivities(optionalActivities.isSelected());
        result.setRecurrentActivities(recurrentActivities.isSelected());
        result.setInclusiveChoice(inclusiveChoice.isSelected());
        result.setUnbalancedPaths(unbalancedPaths.isSelected());
        result.setApplyCleaning(applyCleaning.isSelected());
    }


    public OptimizerUIResult getSelections() {
        return result;
    }

    private class MinerSettingsListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            result.setOptionalActivities(optionalActivities.isSelected());
            result.setRecurrentActivities(recurrentActivities.isSelected());
            result.setInclusiveChoice(inclusiveChoice.isSelected());
            result.setUnbalancedPaths(unbalancedPaths.isSelected());
            result.setApplyCleaning(applyCleaning.isSelected());
        }
    }
}
