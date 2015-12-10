/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController.similarityclusters;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
import org.apromore.model.ClusteringParameterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.ConstrainedProcessIdsType;
import org.apromore.model.GedMatrixSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * Creates the ZK window for similarity clusters invoked through the menu.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersController extends BaseController {

    private static final float DISTANCE_RATIO = 100f;

    private static final long serialVersionUID = -4621153332593772946L;

    private MainController mainController;
    private Window scWindow;

    private Button btnOK;
    private Button btnCancel;
    private Button btnCreate;
//    private Button btnGed;
    private Listbox algorithmListbox;
    private Slider maxdistance;

    /**
     * Creates the dialog to create and show similarity clusters.
     * @param mainC the main controller
     * @throws org.zkoss.zk.ui.SuspendNotAllowedException
     * @throws InterruptedException
     */
    public SimilarityClustersController(final MainController mainC) throws SuspendNotAllowedException, InterruptedException {
        this.mainController = mainC;
        this.scWindow = (Window) Executions.createComponents("macros/similarityclusters.zul", null, null);
        this.btnOK = (Button) this.scWindow.getFellow("similarityclustersOKbutton");
        this.btnCancel = (Button) this.scWindow.getFellow("similarityclustersCancelbutton");
        Label lblBuildDate = (Label) this.scWindow.getFellow("GEDBuildDate");

        // In-Memory Clustering
        this.algorithmListbox = (Listbox) this.scWindow.getFellow("algorithm");
        this.maxdistance = (Slider) this.scWindow.getFellow("maxdistance");
        this.btnCreate = (Button) this.scWindow.getFellow("similarityclustersCreateButton");
//        this.btnGed = (Button) this.scWindow.getFellow("similarityclustersCreateGED");

        defineEventListeners();
        populateGEDMatrixBuildDate(lblBuildDate);

        this.scWindow.doModal();
    }

    /**
     * Start the create Clusters.
     */
    protected final void doCreateSimilarityClusters() {
        ClusterSettingsType settings = new ClusterSettingsType();
        initAlgorithm(settings);
        initMaxDistance(settings);
        initConstrainedProcessIds(settings);
        getService().createClusters(settings);
        Messagebox.show("Clustering Completed!");
    }

//    /**
//     * Create the GED Matrix so we can build the clusters.
//     */
//    protected void doCreateGedMatrix() {
//        getService().createGedMatrix();
//        Messagebox.show("GED Matrix Construction Completed!");
//    }

    /**
     * the cancel button was pressed. close the window.
     */
    protected final void doCancel() {
        this.scWindow.detach();
    }

    /**
     * @throws InterruptedException of Messagebox
     */
    protected final void doShowSimilarityClusters() throws InterruptedException {
        try {
            mainController.displaySimilarityClusters(initFilterConstraints());
        } catch (Exception e) {
            Messagebox.show("Search failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            this.scWindow.detach();
        }
    }


    /* Defines the Event Listeners for the class. */
    private void defineEventListeners() {
        this.btnCreate.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doCreateSimilarityClusters();
            }
        });
        this.btnOK.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doShowSimilarityClusters();
            }
        });
        this.btnOK.addEventListener("onOK", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doShowSimilarityClusters();
            }
        });
        this.btnCancel.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doCancel();
            }
        });
    }

    /* Initialises the Filter Constraints */
    private ClusterFilterType initFilterConstraints() {
        ClusterFilterType filterType = new ClusterFilterType();
        ClusteringSummaryType summary = getService().getClusteringSummary();
        filterType.setMinClusterSize(summary.getMinClusterSize());
        filterType.setMaxClusterSize(summary.getMaxClusterSize());

        filterType.setMinAvgFragmentSize(summary.getMinAvgFragmentSize());
        filterType.setMaxAvgFragmentSize(summary.getMaxAvgFragmentSize());

        filterType.setMinBCR(summary.getMinBCR());
        filterType.setMaxBCR(summary.getMaxBCR());
        return filterType;
    }

    private void initAlgorithm(ClusterSettingsType settings) {
        settings.setAlgorithm(algorithmListbox.getSelectedItem().getValue().toString());
    }

    private void initMaxDistance(ClusterSettingsType settings) {
        ClusteringParameterType param = new ClusteringParameterType();
        param.setParamName("maxdistance");
        param.setParmaValue(String.valueOf(this.maxdistance.getCurpos() / DISTANCE_RATIO));
        settings.getClusteringParams().add(param);
    }

    private void initConstrainedProcessIds(ClusterSettingsType settings) {
        ConstrainedProcessIdsType processIds = new ConstrainedProcessIdsType();
        Set<ProcessSummaryType> selectedProcesses = mainController.getSelectedProcesses();
        for (ProcessSummaryType process : selectedProcesses) {
            processIds.getProcessId().add(process.getId());
        }
        settings.setConstrainedProcessIds(processIds);
    }

    /* Populates the GED Matrix Latest Build Date */
    private void populateGEDMatrixBuildDate(Label lblBuildDate) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        GedMatrixSummaryType gedMatrixSummary = getService().getGedMatrixSummary();
        if (gedMatrixSummary.getBuildDate() == null) {
            sb.append("Never");
        } else {
            if (!gedMatrixSummary.isBuilt()) {
                sb.append("Currently Running, Started ");
            }
            sb.append(dateFormatter.format(gedMatrixSummary.getBuildDate().toGregorianCalendar().getTime()));
        }
        lblBuildDate.setValue(sb.toString());
    }

}
