package au.edu.qut.processmining.miners.heuristic.ui.net;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Adriano on 29/02/2016.
 */
public class HNMSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select Heuristic Net Miner Params";


    final HNMUIResult result;

    HNItemListener hnil = new HNItemListener();

    NiceDoubleSlider frequencyThreshold;
    NiceDoubleSlider parallelismsThreshold;

    public HNMSettings() {
        super(DIALOG_NAME);

        result = new HNMUIResult();

        frequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Frequency Threshold", 0.00, 1.00, HNMUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        frequencyThreshold.addChangeListener(hnil);
        this.add(frequencyThreshold);
        frequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 1.00, HNMUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        parallelismsThreshold.addChangeListener(hnil);
        this.add(parallelismsThreshold);
        parallelismsThreshold.setVisible(true);

        result.setFrequencyThreshold(HNMUIResult.FREQUENCY_THRESHOLD);
        result.setParallelismsThreshold(HNMUIResult.PARALLELISMS_THRESHOLD);
    }

    public HNMUIResult getSelections() {
        return result;
    }

    private class HNItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setFrequencyThreshold(frequencyThreshold.getValue());
            result.setParallelismsThreshold(parallelismsThreshold.getValue());
        }

        @Override
        public void actionPerformed(ActionEvent e) {}
    }

}
