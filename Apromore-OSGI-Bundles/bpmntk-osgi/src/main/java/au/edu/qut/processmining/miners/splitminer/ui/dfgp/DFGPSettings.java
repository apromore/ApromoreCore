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

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;
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
public class DFGPSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select DFG+ Params";


    final DFGPUIResult result;

    DFGPItemListener dfgpil = new DFGPItemListener();

    NiceDoubleSlider percentileFrequencyThreshold;
    NiceDoubleSlider parallelismsThreshold;
//    ProMComboBox filtering;
    JCheckBox parallelismsFirst;

    public DFGPSettings() {
        super(DIALOG_NAME);

        result = new DFGPUIResult();

        LinkedList<String> filterType = new LinkedList<>();
        filterType.addLast("FWG");
        filterType.addLast("WTH");
        filterType.addLast("STD");
        filterType.addLast("NOF");

//        filtering = this.addComboBox("Filter Type", filterType);
//        filtering.addActionListener(dfgpil);

        parallelismsFirst = this.addCheckBox("Parallelisms First", false);
        parallelismsFirst.addChangeListener(dfgpil);

        percentileFrequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Percentile Frequency Threshold", 0.00, 1.00, DFGPUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        percentileFrequencyThreshold.addChangeListener(dfgpil);
        this.add(percentileFrequencyThreshold);
        percentileFrequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 1.00, DFGPUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        parallelismsThreshold.addChangeListener(dfgpil);
        this.add(parallelismsThreshold);
        parallelismsThreshold.setVisible(true);

        result.setFilterType(DFGPUIResult.FilterType.FWG);
        result.setParallelismsFirst(parallelismsFirst.isSelected());
        result.setPercentileFrequencyThreshold(percentileFrequencyThreshold.getValue());
        result.setParallelismsThreshold(parallelismsThreshold.getValue());
    }

    public DFGPUIResult getSelections() {
        return result;
    }

    private class DFGPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setPercentileFrequencyThreshold(percentileFrequencyThreshold.getValue());
            result.setParallelismsThreshold(parallelismsThreshold.getValue());
            result.setParallelismsFirst(parallelismsFirst.isSelected());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setFilterType(DFGPUIResult.FilterType.FWG);
                        percentileFrequencyThreshold.setVisible(true);
                        break;
                    case 1:
                        result.setFilterType(DFGPUIResult.FilterType.WTH);
                        percentileFrequencyThreshold.setVisible(true);
                        break;
                    case 2:
                        result.setFilterType(DFGPUIResult.FilterType.STD);
                        percentileFrequencyThreshold.setVisible(false);
                        break;
                    case 3:
                        result.setFilterType(DFGPUIResult.FilterType.NOF);
                        percentileFrequencyThreshold.setVisible(false);
                        break;
                }
            }
        }
    }
}
