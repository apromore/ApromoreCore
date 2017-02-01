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

package au.edu.qut.processmining.miners.heuristic.ui;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Created by Adriano on 29/02/2016.
 */
public class HMPlusSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_NAME = "Select Heuristic Net miner Params";

    final HMPlusUIResult result;

    NiceDoubleSlider dependencyThreshold;
    NiceDoubleSlider positiveObservations;
    NiceDoubleSlider relative2BestThreshold;

    JCheckBox enablePositiveObservations;
    JCheckBox enableRelative2Best;
    JCheckBox replaceIORs;

    ProMComboBox structuring;

    public HMPlusSettings() { this(DIALOG_NAME); }

    public HMPlusSettings(String title) {
        super(title);

        HMPItemListener hmpil = new HMPItemListener();

        LinkedList<String> structuringTime = new LinkedList<>();
        structuringTime.addLast("Unstructured");
        structuringTime.addLast("Post-Structuring");
        structuringTime.addLast("Pre-Structuring");

        result = new HMPlusUIResult();

        structuring = this.addComboBox("Structuring Policy", structuringTime);
        structuring.addActionListener(hmpil);

        enablePositiveObservations = this.addCheckBox("Positive Observations Threshold", false);
        enablePositiveObservations.addChangeListener(hmpil);

        enableRelative2Best = this.addCheckBox("Relative To Best Threshold", false);
        enableRelative2Best.addChangeListener(hmpil);

        replaceIORs = this.addCheckBox("Replace IORs", true);
        replaceIORs.addChangeListener(hmpil);

        dependencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Dependency Threshold", 0.00, 1.00, HMPlusUIResult.DEPENDENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        dependencyThreshold.addChangeListener(hmpil);
        this.add(dependencyThreshold);
        dependencyThreshold.setVisible(true);

        positiveObservations = SlickerFactory.instance().createNiceDoubleSlider("Positive Observations", 0.00, 0.05, HMPlusUIResult.POSITIVE_OBSERVATIONS, NiceSlider.Orientation.HORIZONTAL);
        positiveObservations.addChangeListener(hmpil);
        this.add(positiveObservations);
        positiveObservations.setVisible(false);

        relative2BestThreshold = SlickerFactory.instance().createNiceDoubleSlider("Relative to Best Threshold", 0.00, 1.00, HMPlusUIResult.RELATIVE2BEST_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        relative2BestThreshold.addChangeListener(hmpil);
        this.add(relative2BestThreshold);
        relative2BestThreshold.setVisible(false);

        result.setDependencyThreshold(HMPlusUIResult.DEPENDENCY_THRESHOLD);
        result.disablePositiveObservations();
        result.disableRelative2BestThreshold();
        result.setReplaceIORs(replaceIORs.isSelected());
        result.setStructuringTime(HMPlusUIResult.StructuringTime.NONE);
    }

    public HMPlusUIResult getSelections() {
        return result;
    }

    private class HMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setDependencyThreshold(dependencyThreshold.getValue());

            result.setReplaceIORs(replaceIORs.isSelected());

            positiveObservations.setVisible(enablePositiveObservations.isSelected());
            if( enablePositiveObservations.isSelected() ) result.setPositiveObservations(positiveObservations.getValue());
            else result.disablePositiveObservations();

            relative2BestThreshold.setVisible(enableRelative2Best.isSelected());
            if( enableRelative2Best.isSelected() ) result.setRelative2BestThreshold(relative2BestThreshold.getValue());
            else result.disableRelative2BestThreshold();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.NONE);
                        break;
                    case 1:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.POST);
                        break;
                    case 2:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.PRE);
                        break;
                }
            }
        }
    }

}
