package au.edu.qut.processmining.ui;

import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Adriano on 14/06/2016.
 */
public class MinerSettings extends ProMPropertiesPanel {

    private static final String DIALOG_NAME = "Setup Optimizer";
    private MinerUIResult result;

    private JCheckBox inclusiveChoice;
    private JCheckBox optionalTasks;
    private JCheckBox recurrentTasks;
    private JCheckBox unbalancedPaths;
    private JCheckBox applyCleaning;

    public MinerSettings() {
        super(DIALOG_NAME);

        result = new MinerUIResult();

        MinerSettingsListener minerListener = new MinerSettingsListener();

        unbalancedPaths = this.addCheckBox("Unbalanced Paths", true);
        unbalancedPaths.addChangeListener(minerListener);

        optionalTasks = this.addCheckBox("Optional Activities", true);
        optionalTasks.addChangeListener(minerListener);

        recurrentTasks = this.addCheckBox("Recurrent Activities", false);
        recurrentTasks.addChangeListener(minerListener);

        inclusiveChoice = this.addCheckBox("Inclusive Choice", false);
        inclusiveChoice.addChangeListener(minerListener);

        applyCleaning = this.addCheckBox("Apply Cleaning", false);
        applyCleaning.addChangeListener(minerListener);

        result.setOptionalTasks(optionalTasks.isSelected());
        result.setRecurrentTasks(recurrentTasks.isSelected());
        result.setInclusiveChoice(inclusiveChoice.isSelected());
        result.setUnbalancedPaths(unbalancedPaths.isSelected());
        result.setApplyCleaning(applyCleaning.isSelected());
    }


    public MinerUIResult getSelections() {
        return result;
    }

    private class MinerSettingsListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            result.setOptionalTasks(optionalTasks.isSelected());
            result.setRecurrentTasks(recurrentTasks.isSelected());
            result.setInclusiveChoice(inclusiveChoice.isSelected());
            result.setUnbalancedPaths(unbalancedPaths.isSelected());
            result.setApplyCleaning(applyCleaning.isSelected());
        }
    }
}
