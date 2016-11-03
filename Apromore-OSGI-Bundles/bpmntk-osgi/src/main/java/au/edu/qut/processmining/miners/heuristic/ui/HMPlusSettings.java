package au.edu.qut.processmining.miners.heuristic.ui;

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
public class HMPlusSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_NAME = "Setup Heuristic Miner+";

    final HMPlusUIResult result;

    NiceDoubleSlider dependencyThreshold;
    NiceDoubleSlider positiveObservations;
    NiceDoubleSlider relative2BestThreshold;

    JCheckBox enablePositiveObservations;
    JCheckBox enableRelative2Best;


    public HMPlusSettings() {
        super(DIALOG_NAME);

        result = new HMPlusUIResult();

        HMPItemListener hmpil = new HMPItemListener();

        enablePositiveObservations = this.addCheckBox("Enable Positive Observations Threshold", false);
        enablePositiveObservations.addChangeListener(hmpil);

        enableRelative2Best = this.addCheckBox("Enable Relative To Best Threshold", false);
        enableRelative2Best.addChangeListener(hmpil);

        dependencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Dependency Threshold", 0.00, 1.00, HMPlusUIResult.DEPENDENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        dependencyThreshold.addChangeListener(hmpil);
        this.add(dependencyThreshold);
        dependencyThreshold.setVisible(true);

        positiveObservations = SlickerFactory.instance().createNiceDoubleSlider("Positive Observations", 0.00, 1.00, HMPlusUIResult.POSITIVE_OBSERVATIONS, NiceSlider.Orientation.HORIZONTAL);
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
    }

    public HMPlusUIResult getSelections() {
        return result;
    }

    private class HMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setDependencyThreshold(dependencyThreshold.getValue());

            positiveObservations.setVisible(enablePositiveObservations.isSelected());
            if( enablePositiveObservations.isSelected() ) result.setPositiveObservations(positiveObservations.getValue());
            else result.disablePositiveObservations();

            relative2BestThreshold.setVisible(enableRelative2Best.isSelected());
            if( enableRelative2Best.isSelected() ) result.setRelative2BestThreshold(relative2BestThreshold.getValue());
            else result.disableRelative2BestThreshold();
        }

        @Override
        public void actionPerformed(ActionEvent e) { }
    }

}
