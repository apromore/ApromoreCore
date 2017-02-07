package au.edu.qut.processmining.miners.heuristic.ui.miner;

import au.edu.qut.processmining.miners.heuristic.ui.net.HNMUIResult;
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
    private static final String DIALOG_NAME = "Select Heuristic Miner Params";

    final HMPlusUIResult result;

    NiceDoubleSlider frequencyThreshold;
    NiceDoubleSlider parallelismsThreshold;
    JCheckBox replaceIORs;
    ProMComboBox structuring;

    public HMPlusSettings() {
        super(DIALOG_NAME);

        HMPItemListener hmpil = new HMPItemListener();

        LinkedList<String> structuringTime = new LinkedList<>();
        structuringTime.addLast("NONE");
        structuringTime.addLast("PRE");
        structuringTime.addLast("POST");

        result = new HMPlusUIResult();

        structuring = this.addComboBox("Structuring Time", structuringTime);
        structuring.addActionListener(hmpil);

        replaceIORs = this.addCheckBox("Replace IORs", true);
        replaceIORs.addChangeListener(hmpil);

        frequencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Frequency Threshold", 0.00, 1.00, HNMUIResult.FREQUENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        frequencyThreshold.addChangeListener(hmpil);
        this.add(frequencyThreshold);
        frequencyThreshold.setVisible(true);

        parallelismsThreshold = SlickerFactory.instance().createNiceDoubleSlider("Parallelisms Threshold", 0.00, 1.00, HNMUIResult.PARALLELISMS_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        parallelismsThreshold.addChangeListener(hmpil);
        this.add(parallelismsThreshold);
        parallelismsThreshold.setVisible(true);

        result.setFrequencyThreshold(HNMUIResult.FREQUENCY_THRESHOLD);
        result.setParallelismsThreshold(HNMUIResult.PARALLELISMS_THRESHOLD);
        result.setReplaceIORs(replaceIORs.isSelected());
        result.setStructuringTime(HMPlusUIResult.STRUCT_POLICY);
    }

    public HMPlusUIResult getSelections() {
        return result;
    }

    private class HMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setFrequencyThreshold(frequencyThreshold.getValue());
            result.setParallelismsThreshold(parallelismsThreshold.getValue());
            result.setReplaceIORs(replaceIORs.isSelected());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.NONE);
                        break;
                    case 1:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.PRE);
                        break;
                    case 2:
                        result.setStructuringTime(HMPlusUIResult.StructuringTime.POST);
                        break;
                }
            }
        }
    }

}
