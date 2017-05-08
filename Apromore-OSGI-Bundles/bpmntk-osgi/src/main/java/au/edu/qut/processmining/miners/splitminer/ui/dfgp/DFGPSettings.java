package au.edu.qut.processmining.miners.splitminer.ui.dfgp;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public DFGPSettings() {
        super(DIALOG_NAME);

        result = new DFGPUIResult();

//        frequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Frequency Threshold", 0.00, 1.00, DFGPUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
//        frequencyThreshold.addChangeListener(hnil);
//        this.add(frequencyThreshold);
//        frequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 0.20, DFGPUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
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
        public void actionPerformed(ActionEvent e) {}
    }

}
