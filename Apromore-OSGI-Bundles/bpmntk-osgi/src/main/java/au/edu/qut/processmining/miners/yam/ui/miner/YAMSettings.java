package au.edu.qut.processmining.miners.yam.ui.miner;

import au.edu.qut.processmining.miners.yam.ui.dfgp.DFGPUIResult;
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
public class YAMSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select YAM Params";

    final YAMUIResult result;

//    NiceDoubleSlider frequencyThreshold;
    NiceDoubleSlider parallelismsThreshold;
    JCheckBox replaceIORs;
//    ProMComboBox structuring;

    public YAMSettings() {
        super(DIALOG_NAME);

        HMPItemListener hmpil = new HMPItemListener();

        LinkedList<String> structuringTime = new LinkedList<>();
        structuringTime.addLast("NONE");
        structuringTime.addLast("PRE");
        structuringTime.addLast("POST");

        result = new YAMUIResult();

//        structuring = this.addComboBox("Structuring Time", structuringTime);
//        structuring.addActionListener(hmpil);

        replaceIORs = this.addCheckBox("Replace IORs", true);
        replaceIORs.addChangeListener(hmpil);

//        frequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Frequency Threshold", 0.00, 1.00, DFGPUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
//        frequencyThreshold.addChangeListener(hmpil);
//        this.add(frequencyThreshold);
//        frequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 0.20, DFGPUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        parallelismsThreshold.addChangeListener(hmpil);
        this.add(parallelismsThreshold);
        parallelismsThreshold.setVisible(true);

        result.setFrequencyThreshold(DFGPUIResult.FREQUENCY_THRESHOLD);
        result.setParallelismsThreshold(DFGPUIResult.PARALLELISMS_THRESHOLD);
        result.setReplaceIORs(replaceIORs.isSelected());
        result.setStructuringTime(YAMUIResult.STRUCT_POLICY);
    }

    public YAMUIResult getSelections() {
        return result;
    }

    private class HMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
//            result.setFrequencyThreshold(frequencyThreshold.getValue());
            result.setParallelismsThreshold(parallelismsThreshold.getValue());
            result.setReplaceIORs(replaceIORs.isSelected());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setStructuringTime(YAMUIResult.StructuringTime.NONE);
                        break;
                    case 1:
                        result.setStructuringTime(YAMUIResult.StructuringTime.PRE);
                        break;
                    case 2:
                        result.setStructuringTime(YAMUIResult.StructuringTime.POST);
                        break;
                }
            }
        }
    }

}
