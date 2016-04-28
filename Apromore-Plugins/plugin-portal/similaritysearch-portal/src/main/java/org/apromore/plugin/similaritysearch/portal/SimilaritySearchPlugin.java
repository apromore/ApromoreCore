/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.plugin.similaritysearch.portal;

import org.apromore.model.*;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.similaritysearch.logic.SimilarityService;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.portal.dialogController.FolderTreeController;
import org.apromore.portal.exception.DialogException;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

import javax.inject.Inject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * An example Portal Plugin, which display an Hello World dialog
 */
@Component
public class SimilaritySearchPlugin extends PluginCustomGui {

    @Inject
    private SimilarityService similarityService;

    private final String GREEDY_ALGORITHM = "Greedy";

    private int count = 1;
    private PortalContext context;
    private Window similaritySearchW;
    private Listbox algosLB;
    private Row modelthreshold;
    private Row labelthreshold;
    private Row contextthreshold;
    private Row skipeweight;
    private Row subeweight;
    private Row skipnweight;
    private Row subnweight;
    private Button OKbutton;
    private Radiogroup allVersionsChoiceRG;
    private ProcessSummaryType process;
    private VersionSummaryType version;

    @Override
    public String getLabel(Locale locale) {
        return "Similarity Search";
    }

    @Override
    public void execute(PortalContext context) {
        // Show a message on the portal
        try {
            Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = context.getSelection().getSelectedProcessModelVersions();

            // At least 2 process versions must be selected. Not necessarily of different processes
            if (selectedProcessVersions.size() != 1) {
                context.getMessageHandler().displayInfo("Select one process model for similarity search.");
                return;
            }

            showDialog(context, selectedProcessVersions);


        } catch (Exception e) {
            context.getMessageHandler().displayError("Unable to perform merge", e);
        }
    }

    private void showDialog(PortalContext context, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) throws DialogException {
        this.context = context;

        Map.Entry<ProcessSummaryType, List<VersionSummaryType>> entry = selectedProcessVersions.entrySet().iterator().next();
        this.process = entry.getKey();
        this.version = entry.getValue().iterator().next();

        try {
            this.similaritySearchW = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/similaritysearch.zul", null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FolderTreeController folderTreeController = new FolderTreeController(similaritySearchW);

        Row algoChoiceR = (Row) this.similaritySearchW.getFellow("similaritySearchAlgoChoice");
        Row buttonsR = (Row) this.similaritySearchW.getFellow("similaritySearchButtons");
        this.OKbutton = (Button) this.similaritySearchW.getFellow("similaritySearchOKbutton");
        Button cancelButton = (Button) this.similaritySearchW.getFellow("similaritySearchCancelbutton");

        this.allVersionsChoiceRG = (Radiogroup) this.similaritySearchW.getFellow("allVersionsChoiceRG");
        this.modelthreshold = (Row) this.similaritySearchW.getFellow("modelthreshold");
        this.labelthreshold = (Row) this.similaritySearchW.getFellow("labelthreshold");
        this.contextthreshold = (Row) this.similaritySearchW.getFellow("contextthreshold");
        this.skipeweight = (Row) this.similaritySearchW.getFellow("skipeweight");
        this.subeweight = (Row) this.similaritySearchW.getFellow("subeweight");
        this.skipnweight = (Row) this.similaritySearchW.getFellow("skipnweight");
        this.subnweight = (Row) this.similaritySearchW.getFellow("subnweight");

        this.algosLB = (Listbox) algoChoiceR.getFirstChild().getNextSibling();
        // build the listbox to choose algo
        Listitem listItem = new Listitem();
        listItem.setLabel("Greedy");
        this.algosLB.appendChild(listItem);
        listItem.setSelected(true);

        listItem = new Listitem();
        listItem.setLabel("Hungarian");
        this.algosLB.appendChild(listItem);

        updateActions();

        this.algosLB.addEventListener("onSelect",
                new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        updateActions();
                    }
                });
        this.OKbutton.addEventListener("onClick",
                new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        this.OKbutton.addEventListener("onOK",
                new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        cancelButton.addEventListener("onClick",
                new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        this.similaritySearchW.doModal();
    }

    protected void cancel() {
        this.similaritySearchW.detach();
    }

    protected void searchSimilarProcesses() throws InterruptedException {
        String message;
        try {
            ProcessSummariesType resultToDisplay;
            Boolean latestVersions = "latestVersions".compareTo(allVersionsChoiceRG.getSelectedItem().getId()) == 0;

            Integer folderId = 0;
            if(context.getCurrentFolder() != null) {
               folderId = context.getCurrentFolder().getId();
            }
            if (folderId == null) {
                folderId = 0;
            }

            ParametersType parametersType = setParams(this.algosLB.getSelectedItem().getLabel(),
                    ((Doublebox) this.modelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

            ProcessSummariesType result = similarityService.searchForSimilarProcesses(
                    process.getId(), version.getName(),
                    latestVersions, folderId, context.getCurrentUser().getId(),
                    this.algosLB.getSelectedItem().getLabel(), parametersType);

            message = "Search returned " + result.getProcessSummary().size();
            if (result.getProcessSummary().size() > 1) {
                message += " processes.";
            } else {
                message += " process.";
            }

            if (result.getProcessSummary()!= null && result.getProcessSummary().size() > 1) {
                resultToDisplay = sort(process, result);
            } else {
                resultToDisplay = result;
            }

            if(result.getTotalProcessCount() > 1) displayProcessSummaries("Sim Search " + count++, resultToDisplay, context);
            Messagebox.show(message);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            e.printStackTrace();
            for(StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString() + "\n");
            }
            message = "Search failed (" + sb.toString() + ")";
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            this.similaritySearchW.detach();
        }
    }

    private ParametersType setParams(String method, double modelThreshold, double labelThreshold, double contextThreshold,
                                           double skipnWeight, double subnWeight, double skipeWeight) {
        ParametersType params = new ParametersType();

        params.getParameter().add(addParam("modelthreshold", modelThreshold));
        params.getParameter().add(addParam("labelthreshold", labelThreshold));
        params.getParameter().add(addParam("contextthreshold", contextThreshold));

        if (GREEDY_ALGORITHM.equals(method)) {
            params.getParameter().add(addParam("skipnweight", skipnWeight));
            params.getParameter().add(addParam("subnweight", subnWeight));
            params.getParameter().add(addParam("skipeweight", skipeWeight));
        }

        return params;
    }


    /* Used to create a parameter object. */
    private ParameterType addParam(String name, double value) {
        ParameterType p = new ParameterType();
        p.setName(name);
        p.setValue(value);
        return p;
    }

    private ProcessSummariesType sort(ProcessSummaryType process, ProcessSummariesType toBeSorted) {
        ProcessSummariesType res = new ProcessSummariesType();
        for (int i = 0; i < toBeSorted.getProcessSummary().size(); i++) {
            if (toBeSorted.getProcessSummary().get(i).getId().equals(process.getId())) {
                res.getProcessSummary().add(0, toBeSorted.getProcessSummary().get(i));
            } else {
                sortInsertion(sortVersions(toBeSorted.getProcessSummary().get(i)), res);
            }
        }
        return res;
    }

    private ProcessSummaryType sortVersions(ProcessSummaryType process) {
        ProcessSummaryType res = new ProcessSummaryType();
        res.setDomain(process.getDomain());
        res.setId(process.getId());
        res.setLastVersion(process.getLastVersion());
        res.setName(process.getName());
        res.setOriginalNativeType(process.getOriginalNativeType());
        res.setOwner(process.getOwner());
        res.setRanking(process.getRanking());
        List<VersionSummaryType> versions = new ArrayList<>();

        for (int j = 0; j < process.getVersionSummaries().size(); j++) {
            int i = 0;
            while (i < versions.size() && versions.get(i).getScore() > process.getVersionSummaries().get(j).getScore()) {
                i++;
            }
            versions.add(process.getVersionSummaries().get(j));
        }
        res.getVersionSummaries().addAll(versions);
        return res;
    }

    /**
     * Insert process in sortedList which is kept ordered on best score got by versions
     *
     * @param process
     * @param sortedList
     */
    private void sortInsertion(ProcessSummaryType process, ProcessSummariesType sortedList) {
        int i = 0;
        while (i < sortedList.getProcessSummary().size() &&
                sortedList.getProcessSummary().get(i).getVersionSummaries().get(0).getScore() > process.getVersionSummaries().get(0).getScore()) {
            i++;
        }
        sortedList.getProcessSummary().add(i, process);
    }

    protected void updateActions() {
        this.OKbutton.setDisabled(false);
        String algo = this.algosLB.getSelectedItem().getLabel();
        this.modelthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.labelthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.contextthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.skipeweight.setVisible(algo.compareTo("Greedy") == 0);
        this.subeweight.setVisible(algo.compareTo("Greedy") == 0);
        this.skipnweight.setVisible(algo.compareTo("Greedy") == 0);
        this.subnweight.setVisible(algo.compareTo("Greedy") == 0);
    }

}