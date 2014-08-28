/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;

public class SimilaritySearchController extends BaseController {

    private MainController mainC;
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

    public SimilaritySearchController(MainController mainC, MenuController menuC, ProcessSummaryType process, VersionSummaryType version)
            throws SuspendNotAllowedException, InterruptedException, DialogException {
        this.mainC = mainC;
        this.version = version;
        this.process = process;
        this.similaritySearchW = (Window) Executions.createComponents("macros/similaritysearch.zul", null, null);

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
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        updateActions();
                    }
                });
        this.OKbutton.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        this.OKbutton.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        cancelButton.addEventListener("onClick",
                new EventListener<Event>() {
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
            Integer folderId = UserSessionManager.getCurrentSecurityItem();
            if (folderId == null) {
                folderId = 0;
            }

            ProcessSummariesType result = getService().searchForSimilarProcesses(
                    process.getId(), version.getName(),
                    this.algosLB.getSelectedItem().getLabel(),
                    latestVersions, folderId, UserSessionManager.getCurrentUser().getId(),
                    ((Doublebox) this.modelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

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

            mainC.displayProcessSummaries(resultToDisplay, true);
            mainC.displayMessage(message);
        } catch (Exception e) {
            message = "Search failed (" + e.getMessage() + ")";
            Messagebox.show(message, "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            this.similaritySearchW.detach();
        }
    }

    /**
     * Sort processes given in listToBeSorted: let p1 and p2 being 2 processes. p1 < p2 iff
     * the p1 latest version got a score less the score got by the p2 latest version.
     * The query (process) is put at rank 1
     *
     * @param process the process we were using as the search criteria.
     * @param toBeSorted the list of processes to be sorted.
     * @return sortedList the sorted list of processes.
     */
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
