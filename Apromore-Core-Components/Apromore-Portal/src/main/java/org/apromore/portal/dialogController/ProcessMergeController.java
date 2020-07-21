/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.util.List;
import java.util.Map;

public class ProcessMergeController extends BaseController {

    private static final long serialVersionUID = 1L;
    private MainController mainC;
    private Window processMergeW;
    private Listbox algosLB;
    private Checkbox removeEnt;
    private Checkbox makePublic;
    private Row mergethreshold;
    private Row labelthreshold;
    private Row contextthreshold;
    private Row skipeweight;
    private Row skipnweight;
    private Row subnweight;
    private Button OKbutton;
    private Textbox processNameT;
    private Textbox versionNameT;
    private SelectDynamicListController domainCB;

    private Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions;


    public ProcessMergeController(MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainC = mainC;

        this.processMergeW = (Window) Executions.createComponents("macros/processmerge.zul", null, null);
        this.processMergeW.setTitle(Labels.getLabel("e.merge.title.text", "Process merging"));


        Row processNameR = (Row) this.processMergeW.getFellow("mergednamep");
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();

        Row versionNameR = (Row) this.processMergeW.getFellow("mergednamev");
        this.versionNameT = (Textbox) versionNameR.getFirstChild().getNextSibling();
        this.versionNameT.setValue(Constants.INITIAL_VERSION);

        Row mergeDomainR = (Row) this.processMergeW.getFellow("mergeddomainR");
        List<String> domains = this.mainC.getDomains();
        this.domainCB = new SelectDynamicListController(domains);
        this.domainCB.setReference(domains);
        this.domainCB.setAutodrop(true);
        this.domainCB.setWidth("85%");
        this.domainCB.setHeight("100%");
        this.domainCB.setAttribute("hflex", "1");
        mergeDomainR.appendChild(domainCB);

        Row removeEntR = (Row) this.processMergeW.getFellow("removeEnt");
        Row makePubicR = (Row) this.processMergeW.getFellow("makePublic");
        Row algoChoiceR = (Row) this.processMergeW.getFellow("mergeAlgoChoice");

        this.OKbutton = (Button) this.processMergeW.getFellow("mergeOKButton");
        Button cancelButton = (Button) this.processMergeW.getFellow("mergeCancelButton");

        this.selectedProcessVersions = selectedProcessVersions;
        this.removeEnt = (Checkbox) removeEntR.getFirstChild().getNextSibling();
        this.makePublic = (Checkbox) makePubicR.getFirstChild().getNextSibling();
        this.mergethreshold = (Row) this.processMergeW.getFellow("mergethreshold");
        this.labelthreshold = (Row) this.processMergeW.getFellow("labelthreshold");
        this.contextthreshold = (Row) this.processMergeW.getFellow("contextthreshold");
        this.skipeweight = (Row) this.processMergeW.getFellow("skipeweight");
        this.skipnweight = (Row) this.processMergeW.getFellow("skipnweight");
        this.subnweight = (Row) this.processMergeW.getFellow("subnweight");

        this.algosLB = (Listbox) algoChoiceR.getFirstChild().getNextSibling();
        Listitem listItem = new Listitem();
        listItem.setLabel("Greedy");
        this.algosLB.appendChild(listItem);
        listItem.setSelected(true);
        listItem = new Listitem();
        listItem.setLabel("Hungarian");
        this.algosLB.appendChild(listItem);

        updateActions();

        this.processNameT.addEventListener("onChange", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                updateActions();
            }
        });
        this.versionNameT.addEventListener("onChange", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                updateActions();
            }
        });
        this.algosLB.addEventListener("onSelect", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                updateActions();
            }
        });
        this.OKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                mergeProcesses();
            }
        });
        this.OKbutton.addEventListener("onOK", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                mergeProcesses();
            }
        });
        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });

        this.processMergeW.doModal();
    }

    protected void cancel() {
        this.processMergeW.detach();
    }

    protected void mergeProcesses() {
        String message;
        if ("".compareTo(this.processNameT.getValue()) != 0 && "".compareTo(this.versionNameT.getValue()) != 0) {
            try {
                Integer folderId = 0;
                if (UserSessionManager.getCurrentFolder() != null) {
                    folderId = UserSessionManager.getCurrentFolder().getId();
                }
                ProcessSummaryType result = getService().mergeProcesses(selectedProcessVersions, this.processNameT.getValue(),
                        this.versionNameT.getValue(), this.domainCB.getValue(), UserSessionManager.getCurrentUser().getUsername(), folderId,
                        this.makePublic.isChecked(), this.algosLB.getSelectedItem().getLabel(), this.removeEnt.isChecked(),
                        ((Doublebox) this.mergethreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.skipnweight.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

                message = "Merge built one process.";
                mainC.displayNewProcess(result);
            } catch (Exception e) {
                message = "Merge failed (" + e.getMessage() + ")";
            }
            mainC.displayMessage(message);
            this.processMergeW.detach();
        }
    }

    protected void updateActions() {
        this.OKbutton.setDisabled("".compareTo(this.processNameT.getValue()) == 0 || "".compareTo(this.versionNameT.getValue()) == 0);

        String algo = this.algosLB.getSelectedItem().getLabel();
        this.mergethreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.labelthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.contextthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.skipeweight.setVisible(algo.compareTo("Greedy") == 0);
        this.skipnweight.setVisible(algo.compareTo("Greedy") == 0);
        this.subnweight.setVisible(algo.compareTo("Greedy") == 0);
    }
}
