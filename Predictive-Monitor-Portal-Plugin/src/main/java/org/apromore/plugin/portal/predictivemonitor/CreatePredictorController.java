/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// Third party packages
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.LogSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;

/**
 * UI for specifying the parameters of and creating a new {@link Predictor}.
 */
public class CreatePredictorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePredictorController.class.getCanonicalName());

    private final Window  window;

    private final Selectbox  predictiveMethodSelectbox;
    private final Textbox    tagTextbox;
    private final Selectbox  labelColSelectbox;
    private final Textbox    positiveLabelTextbox;
    private final Selectbox  clsMethodSelectbox;
    private final Decimalbox nEstimatorsDecimalbox;
    private final Decimalbox maxFeaturesDecimalbox;
    private final Decimalbox learningRateDecimalbox;
    private final Button     okButton;
    private final Button     cancelButton;

    private final ListModel<String> predictiveMethodModel = new ListModelArray<String>(new String[] {"Case outcome", "Remaining time"});
    public ListModel<String> getPredictiveMethodModel() { return predictiveMethodModel; }

    private final ListModel<String> labelColModel = new ListModelList<String>();
    public ListModel<String> labelColModel() { return labelColModel; }

    private final ListModel<TrainingAlgorithm> clsMethodModel = new ListModelArray<TrainingAlgorithm>(new TrainingAlgorithm[] {
        new GradientBoostingTrainingAlgorithm(),
        new RandomForestTrainingAlgorithm()});
    public ListModel<TrainingAlgorithm> clsMethodModel() { return clsMethodModel; }

    public CreatePredictorController(PortalContext portalContext, Collection<Predictor> predictors, EventLogService eventLogService, File nirdizatiPath, String pythonPath) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createPredictor.zul", null, null);

        predictiveMethodSelectbox = (Selectbox)  window.getFellow("predictiveMethod");
        tagTextbox                = (Textbox)    window.getFellow("tag");
        labelColSelectbox         = (Selectbox)  window.getFellow("labelCol");
        positiveLabelTextbox      = (Textbox)    window.getFellow("posLabel");
        clsMethodSelectbox        = (Selectbox)  window.getFellow("clsMethod");
        nEstimatorsDecimalbox     = (Decimalbox) window.getFellow("nEstimators");
        maxFeaturesDecimalbox     = (Decimalbox) window.getFellow("maxFeatures");
        learningRateDecimalbox    = (Decimalbox) window.getFellow("learningRate");
        okButton                  = (Button)     window.getFellow("ok");
        cancelButton              = (Button)     window.getFellow("cancel");

        // Find the selected log
        final Set<LogSummaryType> logSummaries = DataflowsController.findSelectedLogs(portalContext);
        if (logSummaries.size() != 1) {
            Messagebox.show("Select exactly one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        final LogSummaryType logSummary = logSummaries.iterator().next();
        final String logName = logSummary.getName();
        final XLog log = eventLogService.getXLog(logSummary.getId());

        // The log attributes are candidates to be the labelCol
        for (XAttribute attribute: log.getGlobalEventAttributes()) {
            LOGGER.info(" Event attribute: " + attribute.getKey());
            switch (attribute.getKey()) {
            case "concept:name":
            case "lifecycle:transition":
            case "org:resource":
            case "time:timestamp":
            case "event_nr":
            case "last":
                break;
            default:
                ((List<String>) labelColModel).add(attribute.getKey());
                break;
            }
        }

        // Bind window components
        ((ListModelArray<String>) predictiveMethodModel).addToSelection(predictiveMethodModel.getElementAt(0));
        predictiveMethodSelectbox.setModel(predictiveMethodModel);

        ((ListModelList<String>) labelColModel).addToSelection(labelColModel.getElementAt(0));
        labelColSelectbox.setModel(labelColModel);

        ((ListModelArray<TrainingAlgorithm>) clsMethodModel).addToSelection(clsMethodModel.getElementAt(0));
        clsMethodSelectbox.setModel(clsMethodModel);

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                LOGGER.info("Creating predictor");

                final String    tag   = tagTextbox.getValue();
                final String    label = labelColModel.getElementAt(labelColSelectbox.getSelectedIndex());
                final TrainingAlgorithm trainingAlgorithm = clsMethodModel.getElementAt(clsMethodSelectbox.getSelectedIndex());
                final Predictor predictor;

                // Assign variables that depend on the predictive method
                switch (predictiveMethodSelectbox.getSelectedIndex()) {
                case 0:  // Case outcome
                    final String positiveLabelValue = positiveLabelTextbox.getValue();
                    trainingAlgorithm.readParametersFromUI(window);
                    predictor = new CaseOutcomePredictor("Slow?", tag, label, positiveLabelValue, "slow_probability", logName, log, trainingAlgorithm, nirdizatiPath, pythonPath);
                    break;
                case 1:  // Remaining time
                    trainingAlgorithm.readParametersFromUI(window);
                    predictor = new RemainingTimePredictor(tag, logName, log, trainingAlgorithm, nirdizatiPath, pythonPath);
                    break;
                default:
                    Messagebox.show("Unsupported predictive method index: " + predictiveMethodSelectbox.getSelectedIndex(), "Attention", Messagebox.OK, Messagebox.ERROR);
                    return;
                }

                LOGGER.info("Created predictor");
                predictors.add(predictor);
                window.detach();
            }
        });

        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                window.onClose();
            }
        });

        window.doModal();
    }
}
