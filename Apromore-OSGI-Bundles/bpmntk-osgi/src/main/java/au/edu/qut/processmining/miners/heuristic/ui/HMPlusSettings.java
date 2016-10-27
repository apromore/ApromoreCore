package au.edu.qut.processmining.miners.heuristic.ui;

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
public class HMPlusSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_NAME = "Setup Heuristic Miner+";

    final HMPlusUIResult result;

    NiceDoubleSlider dependencyThreshold;
    NiceDoubleSlider positiveObservations;
    NiceDoubleSlider relative2bestThreshold;


    public HMPlusSettings() {
        super(DIALOG_NAME);

        result = new HMPlusUIResult();

        HMPItemListener hmpil = new HMPItemListener();

        dependencyThreshold = SlickerFactory.instance().createNiceDoubleSlider("Dependency Threshold", 0.00, 1.00, HMPlusUIResult.DEPENDENCY_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        dependencyThreshold.addChangeListener(hmpil);
        this.add(dependencyThreshold);
        dependencyThreshold.setVisible(true);

        positiveObservations = SlickerFactory.instance().createNiceDoubleSlider("Positive Observations", 0.00, 1.00, HMPlusUIResult.POSITIVE_OBSERVATIONS, NiceSlider.Orientation.HORIZONTAL);
        positiveObservations.addChangeListener(hmpil);
        this.add(positiveObservations);
        positiveObservations.setVisible(true);

        relative2bestThreshold = SlickerFactory.instance().createNiceDoubleSlider("Relative to Best Threshold", 0.00, 1.00, HMPlusUIResult.RELATIVE2BEST_THRESHOLD, NiceSlider.Orientation.HORIZONTAL);
        relative2bestThreshold.addChangeListener(hmpil);
        this.add(relative2bestThreshold);
        relative2bestThreshold.setVisible(true);

        result.setDependencyThreshold(HMPlusUIResult.DEPENDENCY_THRESHOLD);
        result.setPositiveObservations(HMPlusUIResult.POSITIVE_OBSERVATIONS);
        result.setRelative2BestThreshold(HMPlusUIResult.RELATIVE2BEST_THRESHOLD);
    }

    public HMPlusUIResult getSelections() {
        return result;
    }

    private class HMPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setDependencyThreshold(dependencyThreshold.getValue());
            result.setPositiveObservations(positiveObservations.getValue());
            result.setRelative2BestThreshold(relative2bestThreshold.getValue());
        }

        @Override
        public void actionPerformed(ActionEvent e) { }
    }

}
