package au.edu.qut.processmining.miners.splitminer.ui.miner;

import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
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
public class SplitMinerSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select Split Miner Params";

    final SplitMinerUIResult result;

    JCheckBox replaceIORs;
    JCheckBox removeSelfLoops;
//    ProMComboBox structuring;

    public SplitMinerSettings() {
        super(DIALOG_NAME);

        SMPItemListener smpil = new SMPItemListener();

        LinkedList<String> structuringTime = new LinkedList<>();
        structuringTime.addLast("NONE");
        structuringTime.addLast("PRE");
        structuringTime.addLast("POST");

        result = new SplitMinerUIResult();

//        structuring = this.addComboBox("Structuring Time", structuringTime);
//        structuring.addActionListener(smpil);

        replaceIORs = this.addCheckBox("Remove OR-joins", false);
        replaceIORs.addChangeListener(smpil);

        removeSelfLoops = this.addCheckBox("Remove Self-loops", true);
        removeSelfLoops.addChangeListener(smpil);

        result.setReplaceIORs(replaceIORs.isSelected());
        result.setRemoveSelfLoops(removeSelfLoops.isSelected());
        result.setStructuringTime(SplitMinerUIResult.STRUCT_POLICY);
    }

    public SplitMinerUIResult getSelections() {
        return result;
    }

    private class SMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setReplaceIORs(replaceIORs.isSelected());
            result.setRemoveSelfLoops(removeSelfLoops.isSelected());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setStructuringTime(SplitMinerUIResult.StructuringTime.NONE);
                        break;
                    case 1:
                        result.setStructuringTime(SplitMinerUIResult.StructuringTime.PRE);
                        break;
                    case 2:
                        result.setStructuringTime(SplitMinerUIResult.StructuringTime.POST);
                        break;
                }
            }
        }
    }

}
