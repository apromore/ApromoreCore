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

package org.apromore.plugin.portal.compareBP;

// Java 2 Standard Edition packages
import java.io.ByteArrayInputStream;
import java.util.*;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;
import org.apache.commons.io.IOUtils;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apache.commons.math3.analysis.function.Exp;
import org.apromore.canoniser.Canoniser;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.PluginParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.compare.CompareService;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.io.PNMLSerializer;
import org.processmining.exporting.bpmn.BPMNExport;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.bpmn.converter.BPMNConverter;
import org.processmining.plugins.bpmn.converter.PetriNetToBPMNConverterPlugin;
import org.semanticweb.kaon2.jb;
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * A user interface to the BPMN miner service.
 */
@Component("plugin")
public class ComparePlugin extends DefaultPortalPlugin {
    private final CompareService compareService;
    private final ProcessService processService;
    private final CanoniserService canoniserService;
    private Map<ProcessSummaryType, List<VersionSummaryType>> processVersions;

    private static final Logger LOGGER = LoggerFactory.getLogger(ComparePlugin.class.getCanonicalName());

    @Inject
    public ComparePlugin(final CompareService compareService, final ProcessService processService, final CanoniserService canoniserService){
        this.compareService = compareService;
        this.processService = processService;
        this.canoniserService = canoniserService;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Compare";
    }

    @Override
    public void execute(PortalContext context) {
        LOGGER.info("Executing");
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = context.getSelection().getSelectedProcessModelVersions();
        Iterator<List<VersionSummaryType>> selectedVersions = selectedProcessVersions.values().iterator();

        // At least 1 process versions must be selected. Not necessarily of different processes
        if (selectedProcessVersions.size() == 1 && selectedVersions.next().size() < 1 || selectedProcessVersions.size() < 1) {
            context.getMessageHandler().displayInfo("Select at least 1 process model for the comparison.");
            return;
        }

        try {
            processVersions = context.getSelection().getSelectedProcessModelVersions();
            for (ProcessSummaryType process : processVersions.keySet()) {
                for (VersionSummaryType vst : processVersions.get(process)) {
                    int procID = process.getId();
                    String procName = process.getName();
                    String branch = vst.getName();
                    Version version = new Version(vst.getVersionNumber());
                    String username = context.getCurrentUser().getUsername();
                    int folderId = context.getCurrentFolder() == null ? 0 : context.getCurrentFolder().getId();

                    Set<RequestParameterType<?>> requestProperties = new HashSet<>();
                    requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
                    requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));

                    ExportFormatResultType data = processService.exportProcess(procName, procID, branch, version, "PNML 1.3.2", "MN", false, requestProperties);
                    byte[] bytes = IOUtils.toByteArray(data.getNative().getInputStream());
                    PNMLSerializer pnmlSerializer = new PNMLSerializer();

                    NetSystem net = pnmlSerializer.parse(bytes);

                    new CompareController(context, compareService, jbptToUma(net));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public PetriNet jbptToUma(NetSystem net) {
        PetriNet copy = new PetriNet();
        Map<Vertex, Place> places = new HashMap<>();
        Map<Vertex, Transition> transitions = new HashMap<>();

        int index = 0;

        for (org.jbpt.petri.Place place: net.getPlaces()) {
            Place newPlace = copy.addPlace("p" + index++);
            places.put(place, newPlace);
        }

        for (org.jbpt.petri.Transition trans: net.getTransitions()) {
            String name = trans.getLabel()== null  || trans.getLabel().isEmpty() ? "t" + index++ : trans.getLabel();
            Transition newTrans = copy.addTransition(name);
            transitions.put(trans, newTrans);
        }

        for (Flow flow: net.getFlow()) {
            if (flow.getSource() instanceof org.jbpt.petri.Place)
                copy.addArc(places.get(flow.getSource()), transitions.get(flow.getTarget()));
            else
                copy.addArc(transitions.get(flow.getSource()), places.get(flow.getTarget()));
        }

        for (org.jbpt.petri.Place place: net.getSourcePlaces())
            places.get(place).setTokens(1);

        return copy;
    }

}
