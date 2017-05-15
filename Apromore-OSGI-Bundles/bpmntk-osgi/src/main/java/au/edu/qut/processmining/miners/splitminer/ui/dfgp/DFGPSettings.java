package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

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
public class DFGPSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select DFG+ Params";


    final DFGPUIResult result;

    HNItemListener hnil = new HNItemListener();

//    NiceDoubleSlider frequencyThreshold;
    NiceDoubleSlider parallelismsThreshold;
    ProMComboBox filtering;

    public DFGPSettings() {
        super(DIALOG_NAME);

        result = new DFGPUIResult();

        LinkedList<String> filterType = new LinkedList<>();
        filterType.addLast("STD");
        filterType.addLast("GUB");
        filterType.addLast("LPS");
        filterType.addLast("WTH");

        filtering = this.addComboBox("Filter Type", filterType);
        filtering.addActionListener(hnil);

//        frequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Frequency Threshold", 0.00, 1.00, DFGPUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
//        frequencyThreshold.addChangeListener(hnil);
//        this.add(frequencyThreshold);
//        frequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 1.00, DFGPUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        parallelismsThreshold.addChangeListener(hnil);
        this.add(parallelismsThreshold);
        parallelismsThreshold.setVisible(true);

        result.setFrequencyThreshold(DFGPUIResult.FREQUENCY_THRESHOLD);
        result.setParallelismsThreshold(DFGPUIResult.PARALLELISMS_THRESHOLD);
    }

    public DFGPUIResult getSelections() {
        return result;
    }

    private class HNItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
//            result.setFrequencyThreshold(frequencyThreshold.getValue());
            result.setParallelismsThreshold(parallelismsThreshold.getValue());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setFilterType(DFGPUIResult.FilterType.STD);
                        break;
                    case 1:
                        result.setFilterType(DFGPUIResult.FilterType.GUB);
                        break;
                    case 2:
                        result.setFilterType(DFGPUIResult.FilterType.LPS);
                        break;
                    case 3:
                        result.setFilterType(DFGPUIResult.FilterType.WTH);
                        break;
                }
            }
        }
    }

}
