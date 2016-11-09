package au.edu.qut.structuring.ui;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;
import au.edu.qut.structuring.core.StructuringCore;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_NAME = "Setup Structuring Policy";

    final iBPStructUIResult result;

    ProMComboBox structPolicy;
    NiceIntegerSlider maxSol;
    NiceIntegerSlider maxDepth;
    NiceIntegerSlider maxChildren;
    NiceIntegerSlider maxStates;
    NiceIntegerSlider maxMinutes;

    JCheckBox forceStructuring;
    JCheckBox timeBounded;
    JCheckBox keepBisimulation;


    public iBPStructSettings() {
        super(DIALOG_NAME);

        result = new iBPStructUIResult();

        BPSItemListener bpsil = new BPSItemListener();

        LinkedList<String> policies = new LinkedList<>();
        policies.addLast("A* Search");
        policies.addLast("Limited A* Search");
        policies.addLast("Depth-First Search");
        policies.addLast("Breadth-First Search");
        policies.addLast("Fast-Depth Search");

        structPolicy = this.addComboBox("Structuring Policy", policies);
        structPolicy.addActionListener(bpsil);

        keepBisimulation = this.addCheckBox("Keep Bisimulation", true);
        keepBisimulation.addChangeListener(bpsil);

        timeBounded = this.addCheckBox("Time Bounding", true);
        timeBounded.addChangeListener(bpsil);

        forceStructuring = this.addCheckBox("Force Structuring", false);
        forceStructuring.addChangeListener(bpsil);

        maxMinutes = SlickerFactory.instance().createNiceIntegerSlider("Max Minutes", 0, 120, iBPStructUIResult.MAX_MINUTES, NiceSlider.Orientation.HORIZONTAL);
        maxMinutes.addChangeListener(bpsil);
        this.add(maxMinutes);
        maxMinutes.setVisible(true);

        maxDepth = SlickerFactory.instance().createNiceIntegerSlider("Max Depth", 0, 500, iBPStructUIResult.MAX_DEPTH, NiceSlider.Orientation.HORIZONTAL);
        maxDepth.addChangeListener(bpsil);
        this.add(maxDepth);
        maxDepth.setVisible(false);

        maxChildren = SlickerFactory.instance().createNiceIntegerSlider("Max Children", 1, 100, iBPStructUIResult.MAX_CHILDREN, NiceSlider.Orientation.HORIZONTAL);
        maxChildren.addChangeListener(bpsil);
        this.add(maxChildren);
        maxChildren.setVisible(false);

        maxStates = SlickerFactory.instance().createNiceIntegerSlider("Max States", 1, 10000, iBPStructUIResult.MAX_STATES, NiceSlider.Orientation.HORIZONTAL);
        maxStates.addChangeListener(bpsil);
        this.add(maxStates);
        maxStates.setVisible(false);

        maxSol = SlickerFactory.instance().createNiceIntegerSlider("Max Solutions", 0, 1000, iBPStructUIResult.MAX_SOL, NiceSlider.Orientation.HORIZONTAL);
        maxSol.addChangeListener(bpsil);
        this.add(maxSol);
        maxSol.setVisible(false);

        result.setPolicy(StructuringCore.Policy.ASTAR);
        result.setMaxDepth(iBPStructUIResult.MAX_DEPTH);
        result.setMaxSol(iBPStructUIResult.MAX_SOL);
        result.setMaxChildren(iBPStructUIResult.MAX_CHILDREN);
        result.setMaxStates(iBPStructUIResult.MAX_STATES);
        result.setMaxMinutes(iBPStructUIResult.MAX_MINUTES);
        result.setKeepBisimulation(true);
        result.setTimeBounded(true);
        result.setForceStructuring(false);
    }

    public iBPStructUIResult getSelections() {
        return result;
    }

    private class BPSItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            result.setMaxSol(maxSol.getValue());
            result.setMaxDepth(maxDepth.getValue());
            result.setMaxChildren(maxChildren.getValue());
            result.setMaxMinutes(maxMinutes.getValue());
            result.setTimeBounded(timeBounded.isSelected());
            result.setKeepBisimulation(keepBisimulation.isSelected());
            result.setForceStructuring(forceStructuring.isSelected());
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        result.setPolicy(StructuringCore.Policy.ASTAR);
                        maxDepth.setVisible(false);
                        maxSol.setVisible(false);
                        maxChildren.setVisible(false);
                        maxStates.setVisible(false);
                        maxMinutes.setVisible(true);
                        timeBounded.getParent().setVisible(true);
                        break;
                    case 1:
                        result.setPolicy(StructuringCore.Policy.LIM_ASTAR);
                        maxDepth.setVisible(false);
                        maxSol.setVisible(true);
                        maxChildren.setVisible(true);
                        maxStates.setVisible(true);
                        maxMinutes.setVisible(true);
                        timeBounded.getParent().setVisible(true);
                        break;
                    case 2:
                        result.setPolicy(StructuringCore.Policy.DEPTH);
                        maxDepth.setVisible(false);
                        maxSol.setVisible(false);
                        maxChildren.setVisible(false);
                        maxStates.setVisible(false);
                        maxMinutes.setVisible(false);
                        timeBounded.getParent().setVisible(false);
                        break;
                    case 3:
                        result.setPolicy(StructuringCore.Policy.BREADTH);
                        maxDepth.setVisible(true);
                        maxSol.setVisible(true);
                        maxChildren.setVisible(false);
                        maxStates.setVisible(false);
                        maxMinutes.setVisible(false);
                        timeBounded.getParent().setVisible(false);
                        break;
                    case 4:
                        result.setPolicy(StructuringCore.Policy.LIM_DEPTH);
                        maxDepth.setVisible(false);
                        maxSol.setVisible(false);
                        maxChildren.setVisible(true);
                        maxStates.setVisible(true);
                        maxMinutes.setVisible(true);
                        timeBounded.getParent().setVisible(false);
                        break;
                }
            }
        }
    }

}
