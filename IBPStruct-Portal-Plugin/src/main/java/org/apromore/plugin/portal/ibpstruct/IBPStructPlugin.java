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

package org.apromore.plugin.portal.ibpstruct;

// Java 2 Standard Edition packages

import au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.ProcessService;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.ibpstruct.IBPStructService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.plugin.property.RequestParameterType;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

// Java 2 Enterprise Edition packages
// Third party packages
// Local packages

/**
 * iBPStruct service. Created by Adriano Augusto 18/04/2016
 */
@Component("plugin")
public class IBPStructPlugin extends DefaultPortalPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(IBPStructPlugin.class);

    private final String nativeType = "BPMN 2.0";

    private PortalContext portalContext;
    private final IBPStructService ibpstructService;
    private final CanoniserService canoniserService;
    private final DomainService domainService;
    private final ProcessService processService;
    private final UserInterfaceHelper userInterfaceHelper;
    private final BPMNDiagramImporter importerService;

    private Map<ProcessSummaryType, List<VersionSummaryType>> processVersions;
    private SelectDynamicListController domainCB;

    private ProcessSummaryType psm;
    private VersionSummaryType vst;

    private String[] structPoliciesArray = new String[] {
            "A*",
            "Limited A*",
            "Depth-First",
            "Breadth-First"
    };

    /* zk gui variables */
    private Window settings;
    private Textbox structProcName;
    private Selectbox structPolicies;
    private Radiogroup timeBounded;
    private Radiogroup pullup;
    private Radiogroup forceStructuring;
    private Slider maxMinutes;
    private Slider branchingFactor;
    private Slider maxStates;
    private Slider maxSolutions;
    private Slider maxDepth;
    private Button cancelButton;
    private Button okButton;
    private Groupbox advancedOptions;

    @Inject
    public IBPStructPlugin(final IBPStructService    ibpstructService,
                           final CanoniserService canoniserService,
                           final DomainService domainService,
                           final ProcessService processService,
                           final UserInterfaceHelper userInterfaceHelper,
                           final BPMNDiagramImporter importerService) {

        this.ibpstructService    = ibpstructService;
        this.canoniserService    = canoniserService;
        this.domainService       = domainService;
        this.processService      = processService;
        this.userInterfaceHelper = userInterfaceHelper;
        this.importerService     = importerService;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Structure";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(PortalContext context) {
        this.portalContext = context;

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        processVersions = new HashMap<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof ProcessSummaryType) {
                processVersions.put((ProcessSummaryType) entry.getKey(), entry.getValue());
            }
        }

        if( processVersions.size() != 1 ) {
            Messagebox.show("Please, select exactly one process.", "Wrong Process Selection", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }


        portalContext.getMessageHandler().displayInfo("Executing iBPStruct service...");

        for (ProcessSummaryType process : processVersions.keySet()) this.psm = process;
        for (VersionSummaryType version : processVersions.get(psm)) this.vst = version;

        try {
            this.settings = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/ibpstruct.zul", null, null);

            this.advancedOptions = (Groupbox) this.settings.getFellow("advancedOptions");
            this.advancedOptions.setOpen(false);
            this.advancedOptions.setClosable(true);

            this.structProcName = (Textbox) this.settings.getFellow("structProcName");
            this.structProcName.setValue("structured_" + psm.getName());

            this.structPolicies = (Selectbox) this.settings.getFellow("structPolicies");
            this.structPolicies.setModel(new ListModelArray<Object>(structPoliciesArray));
            this.timeBounded = (Radiogroup) this.settings.getFellow("timeBounded");
            this.pullup = (Radiogroup) this.settings.getFellow("pullup");
            this.forceStructuring = (Radiogroup) this.settings.getFellow("forceStructuring");
            this.maxMinutes = (Slider) this.settings.getFellow("maxMinutes");
            this.branchingFactor = (Slider) this.settings.getFellow("branchingFactor");
            this.maxStates = (Slider) this.settings.getFellow("maxStates");
            this.maxSolutions = (Slider) this.settings.getFellow("maxSolutions");
            this.maxDepth = (Slider) this.settings.getFellow("maxDepth");

            this.cancelButton = (Button) this.settings.getFellow("ibpsCancelButton");
            this.okButton = (Button) this.settings.getFellow("ibpsOKButton");

            this.cancelButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    runStructuring();
                }
            });
            this.settings.doModal();
        } catch (IOException e) {
            Messagebox.show("Something went wrong (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }


    }



    protected void cancel() {
        this.settings.detach();
    }

    protected void runStructuring() {
        this.settings.detach();

        String finalProcName = structProcName.getValue();
        String policy = null;
        boolean keepBisimulation = this.pullup.getSelectedIndex() == 0 ? false : true;
        boolean timeBounded = this.timeBounded.getSelectedIndex() == 0 ? true : false;
        boolean forceStructuring  = this.forceStructuring.getSelectedIndex() == 0 ? true : false;
        int maxMinutes = this.maxMinutes.getCurpos();
        int maxChildren = this.branchingFactor.getCurpos();
        int maxStates = this.maxStates.getCurpos();
        int maxSolutions = this.maxSolutions.getCurpos();
        int maxDepth = this.maxDepth.getCurpos();

        LOGGER.info("flags: " + keepBisimulation + " - " + timeBounded + " - " + forceStructuring);
        LOGGER.info("numbers: " + maxMinutes + " - " + maxChildren + " - " + maxStates + " - " + maxSolutions);

        switch( structPolicies.getSelectedIndex() ) {
            case 0:
                policy = "ASTAR";
                break;
            case 1:
                policy = "LIM_ASTAR";
                break;
            case 2:
                policy = "DEPTH";
                break;
            case 3:
                policy = "BREADTH";
                break;
            default:
                policy = "ASTAR";
        }

        try {
            List<String> domains = domainService.findAllDomains();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            int procID = psm.getId();
            String procName = psm.getName();
            String branch = vst.getName();
            Version version = new Version(vst.getVersionNumber());
            String username = portalContext.getCurrentUser().getUsername();
            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            String model = processService.getBPMNRepresentation(procName, procID, branch, version);

            BPMNDiagram unstructuredDiagram = importerService.importBPMNDiagram(model);
            BPMNDiagram structuredDiagram = ibpstructService.structureProcess(  unstructuredDiagram,
                                                                                policy,
                                                                                maxDepth,
                                                                                maxSolutions,
                                                                                maxChildren,
                                                                                maxStates,
                                                                                maxMinutes,
                                                                                timeBounded,
                                                                                keepBisimulation,
                                                                                forceStructuring);

            String structuredModel = (new BPMNDiagramExporterImpl()).exportBPMNDiagram(structuredDiagram);

            version = new Version(1, 0);
            Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
            String now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
            boolean publicModel = true;
            if( finalProcName == null || finalProcName.isEmpty() ) finalProcName = "structured_" + procName;

            ProcessModelVersion pmv = processService.importProcess(username,
                    folderId,
                    finalProcName,
                    version,
                    this.nativeType,
                    canoniserService.canonise(this.nativeType, new ByteArrayInputStream(structuredModel.getBytes()), canoniserProperties),
                    domainCB.getValue(),
                    "Model generated by the Apromore BPMN process mining service.",
                    now,  // creation timestamp
                    now,  // last update timestamp
                    publicModel);

            this.portalContext.displayNewProcess(userInterfaceHelper.createProcessSummary(
                    pmv.getProcessBranch().getProcess(),
                    pmv.getProcessBranch(),
                    pmv,
                    this.nativeType,
                    domainCB.getValue(),
                    now,  // creation timestamp
                    now,  // last update timestamp
                    username,
                    publicModel));

            this.portalContext.refreshContent();
        } catch(Exception e) {
            Messagebox.show("Something went wrong (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
