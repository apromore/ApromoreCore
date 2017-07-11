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

package org.apromore.plugin.portal.APM;

// Java 2 Standard Edition packages
import java.util.*;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;
import org.apache.commons.io.IOUtils;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.LogSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.apm.APMService;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.io.PNMLSerializer;
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.EventLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("plugin")
public class APMPlugin extends DefaultPortalPlugin {
    private final APMService apmService;
    private final ProcessService processService;
    private final CanoniserService canoniserService;
    private Map<ProcessSummaryType, List<VersionSummaryType>> processVersions;

    private String label = "Extract Variability Specification";
    private String groupLabel = "Discover";

    private static final Logger LOGGER = LoggerFactory.getLogger(APMPlugin.class.getCanonicalName());

    @Inject private org.apromore.portal.ConfigBean portalConfig;

    @Inject
    public APMPlugin(final APMService apmService, final ProcessService processService, final CanoniserService canoniserService){
        this.apmService = apmService;
        this.processService = processService;
        this.canoniserService = canoniserService;
    }

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public PetriNet getNet(ProcessSummaryType process, VersionSummaryType vst, PortalContext context, HashSet<String> labels) throws Exception{
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

        for(org.jbpt.petri.Transition t : net.getObservableTransitions())
            if (t.getLabel().trim().length() > 0)
                labels.add(t.getLabel().trim());

        return jbptToUma(net);
    }

    @Override
    public void execute(PortalContext context) {
        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }else if(entry.getKey() instanceof ProcessSummaryType){
                selectedProcessVersions.put((ProcessSummaryType) entry.getKey(), entry.getValue());
            }
        }

        // Populate "details" with the process:version selections
        List<ProcessSummaryType> procS = new ArrayList<>();
        List<VersionSummaryType> verS = new ArrayList<>();
        List<PetriNet> nets = new ArrayList<>();
        List<HashSet<String>> observable = new ArrayList<>();
        String prefix = "silent";

        try {
            for (ProcessSummaryType processSummary: selectedProcessVersions.keySet()) {
                List<VersionSummaryType> versionSummaries = selectedProcessVersions.get(processSummary);
                if (versionSummaries.isEmpty()) {
                    List<VersionSummaryType> x = processSummary.getVersionSummaries();
                    versionSummaries.add(x.get(x.size() - 1));  // default to the head version
                }
                for (VersionSummaryType versionSummary: versionSummaries) {
                    procS.add(processSummary);
                    verS.add(versionSummary);
                    HashSet<String> obs = new HashSet<>();
                    nets.add(getNet(processSummary, versionSummary, context, obs));
                    observable.add(new HashSet<String>(obs));
                }
            }

            PetriNet[] netsArray =  new PetriNet[nets.size()];
            for(int i = 0; i < nets.size(); i++)
                netsArray[i] = nets.get(i);

            APMController controller = new APMController(context, apmService);
            controller.apmVerify(netsArray, prefix);

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
