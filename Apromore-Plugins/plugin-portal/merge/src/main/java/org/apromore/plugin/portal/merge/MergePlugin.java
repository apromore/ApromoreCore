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

package org.apromore.plugin.portal.merge;

// Java packages
import java.io.IOException;
import java.util.*;

// Third party packages
import org.apromore.portal.custom.gui.PortalTab;
import org.apromore.portal.custom.gui.impl.PortalTabImpl;
import org.apromore.portal.custom.gui.impl.RowValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.spring.SpringUtil;
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

// Local packages
import org.apromore.manager.client.ManagerService;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.Level;
import org.apromore.plugin.portal.PortalContext;


@Component("plugin")
public class MergePlugin extends DefaultPortalPlugin {

    public static final String INITIAL_VERSION = "1.0";
    public static final String MANAGER_SERVICE = "managerClient";
    private static final Logger LOGGER = LoggerFactory.getLogger(MergePlugin.class.getCanonicalName());

    private PortalContext context;
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


    // DefaultPortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return "Merging";
    }

    @Override
    public void execute(PortalContext context) {
        try {
            LOGGER.info("Executing");
            Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = context.getSelection().getSelectedProcessModelVersions();
            Iterator<List<VersionSummaryType>> selectedVersions = selectedProcessVersions.values().iterator();

            // At least 2 process versions must be selected. Not necessarily of different processes
            if (selectedProcessVersions.size() == 1 && selectedVersions.next().size() < 2 || selectedProcessVersions.size() < 2) {
                context.getMessageHandler().displayInfo("Select at least 2 process models for merge.");
                return;
            }

            showDialog(context, selectedProcessVersions);


            LOGGER.info("Executed");

        } catch (Exception e) {
            LOGGER.info("Unable to perform merge", e);
            context.getMessageHandler().displayError("Unable to perform merge", e);
        }
    }


    // Internal methods

    private void showDialog(PortalContext context, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions)
        throws InterruptedException, IOException, SuspendNotAllowedException
    {
        this.context = context;

        this.processMergeW = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/processmerge.zul", null, null);
        this.processMergeW.setTitle("Merge processes.");

        Row processNameR = (Row) this.processMergeW.getFellow("mergednamep");
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();

        Row versionNameR = (Row) this.processMergeW.getFellow("mergednamev");
        this.versionNameT = (Textbox) versionNameR.getFirstChild().getNextSibling();
        this.versionNameT.setValue(INITIAL_VERSION);

        Row mergeDomainR = (Row) this.processMergeW.getFellow("mergeddomainR");

        ManagerService manager = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
        LOGGER.info(MANAGER_SERVICE + " Spring bean from SpringUtil  bound to " + manager);
        List<String> domains = manager.readDomains().getDomain();
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
                if (context.getCurrentFolder() != null) {
                    folderId = context.getCurrentFolder().getId();
                }

                ManagerService manager = (ManagerService) SpringUtil.getBean(MANAGER_SERVICE);
                LOGGER.info("Manager " + manager);
                if (manager == null) {
                    throw new RuntimeException("Unable to get Spring bean \"" + MANAGER_SERVICE + "\"");
                }
                ProcessSummaryType result = manager.mergeProcesses(selectedProcessVersions, this.processNameT.getValue(),
                        this.versionNameT.getValue(), this.domainCB.getValue(), context.getCurrentUser().getUsername(), folderId,
                        this.makePublic.isChecked(), this.algosLB.getSelectedItem().getLabel(), this.removeEnt.isChecked(),
                        ((Doublebox) this.mergethreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.skipnweight.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
                        ((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

                message = "Merge built one process.";
                context.displayNewProcess(result);
            } catch (Exception e) {
                message = "Merge failed (" + e.getMessage() + ")";
            }

            context.getMessageHandler().displayInfo(message);
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
