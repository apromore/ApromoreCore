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

package org.apromore.plugin.portal.compareBP;

// Java 2 Standard Edition packages
import java.util.*;

import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;
import org.apache.commons.io.IOUtils;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.LogSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.compare.CompareService;
import org.deckfour.xes.model.XLog;
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
public class ComparePlugin extends DefaultPortalPlugin {
    private final CompareService compareService;
    private final ProcessService processService;
    private final CanoniserService canoniserService;
    private final EventLogService eventLogService;
    private Map<ProcessSummaryType, List<VersionSummaryType>> processVersions;

    private String label = "Compare";
    private String groupLabel = "Analyze";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComparePlugin.class.getCanonicalName());

    @Inject private org.apromore.portal.ConfigBean portalConfig;

    @Inject
    public ComparePlugin(final CompareService compareService, final ProcessService processService, final CanoniserService canoniserService, final EventLogService eventLogService){
        this.compareService = compareService;
        this.processService = processService;
        this.canoniserService = canoniserService;
        this.eventLogService = eventLogService;
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

//        if(selectedProcessVersions.size() != 1) {
//            Messagebox.show("Please, select exactly one process.", "Wrong Process Selection", Messagebox.OK, Messagebox.INFORMATION);
//            return;
//        }

        // Populate "details" with the process:version selections
        List<ProcessSummaryType> procS = new ArrayList<>();
        List<VersionSummaryType> verS = new ArrayList<>();
        List<PetriNet> nets = new ArrayList<>();
        List<HashSet<String>> observable = new ArrayList<>();

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

            List<XLog> logs = new ArrayList<>();
            for(LogSummaryType logType : selectedLogSummaryType){
                logs.add(eventLogService.getXLog(logType.getId()));
            }

            CompareController controller = new CompareController(context, compareService);

            if(logs.size() == 2){
                controller.compareLL(logs.get(0), logs.get(1));
                return;
            }else if(logs.size() == 1 && selectedProcessVersions.size() == 1){
                ModelAbstractions model = toModelAbstractions(procS.get(0), verS.get(0));

                controller.compareML(model, new HashSet<String>(), logs.get(0), procS.get(0), verS.get(0));
                return;
            }else if(selectedProcessVersions.size() == 2){
                context.getMessageHandler().displayInfo("Performing comparison.");

                ModelAbstractions model1 = toModelAbstractions(procS.get(0), verS.get(0));
                ModelAbstractions model2 = toModelAbstractions(procS.get(1), verS.get(1));

                controller.compareMM(model1, model2, observable.get(0), observable.get(1), procS.get(0), verS.get(0), procS.get(1), verS.get(1));
                context.getMessageHandler().displayInfo("Performed comparison.");
                return;
            }else if(logs.size() > 2 || selectedProcessVersions.size() > 2){
                context.getMessageHandler().displayInfo("There are " + selectedProcessVersions.size() + " process versions selected, but only 2 can be compared at a time.");
                return;
            }

            // Select the comparison method according to the elements selected in Apromore and input data
            Iterator<List<VersionSummaryType>> selectedVersions = selectedProcessVersions.values().iterator();

            // At least 1 process versions must be selected. Not necessarily of different processes
            if ((selectedProcessVersions.size() == 1 && selectedVersions.next().size() < 1) || selectedProcessVersions.size() < 1) {
                controller.compareLLPopup();
                context.getMessageHandler().displayInfo("Log to log comparison.");
                return;
            }

            switch (selectedProcessVersions.size()) {
                case 1:
                    ModelAbstractions model = toModelAbstractions(procS.get(0), verS.get(0));
                    controller.compareMLPopup(model, observable.get(0), procS.get(0), verS.get(0));
                    context.getMessageHandler().displayInfo("Performed conformance checker.");
                    break;
                default:
                    context.getMessageHandler().displayInfo("Comparison method not found. Check the selected elements.");
            }

        /*try {
            // Populate "details" with the process:version selections
            List<ProcessSummaryType> procS = new ArrayList<>();
            List<VersionSummaryType> verS = new ArrayList<>();
            List<PetriNet> nets = new ArrayList<>();
            List<HashSet<String>> observable = new ArrayList<>();
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

            // If we have exactly two process:version selections, perform the comparison
            switch (selectedProcessVersions.size()) {
            case 1:
                new CompareController(context, compareService, nets.get(0), observable.get(0));
                context.getMessageHandler().displayInfo("Performed conformance checker.");
                break;
            case 2:
                context.getMessageHandler().displayInfo("Performing comparison.");

                ModelAbstractions model1 = toModelAbstractions(procS.get(0), verS.get(0));
                ModelAbstractions model2 = toModelAbstractions(procS.get(1), verS.get(1));

                new CompareController(context,compareService, model1, model2, observable.get(0), observable.get(1), procS.get(0), verS.get(0), procS.get(1), verS.get(1));
                context.getMessageHandler().displayInfo("Performed comparison.");
                break;
            default:
                context.getMessageHandler().displayInfo("There are " + selectedProcessVersions.size() + " process versions selected, but only 2 can be compared at a time.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/

        }catch(Exception e){
            e.printStackTrace();
        }
    }

        private ModelAbstractions toModelAbstractions(ProcessSummaryType process, VersionSummaryType version) throws Exception {
            ExportFormatResultType result = processService.exportProcess(
                    process.getName(),           // process name
                    process.getId(),             // process ID
                    version.getName(),           // branch
                    new Version(version.getVersionNumber()),  // version number,
                    "BPMN 2.0",                  // nativeType,
                    null,                        // annotation name,
                    false,                       // with annotations?
                    Collections.EMPTY_SET        // canoniser properties
            );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TransformerFactory.newInstance().newTransformer().transform(new StreamSource(result.getNative().getInputStream()), new StreamResult(baos));
            return new ModelAbstractions(baos.toByteArray());
        }

//    public PetriNet getNet(int model, PortalContext context, HashSet<String> labels, ProcessSummaryType process ) throws Exception{
////        processVersions = context.getSelection().getSelectedProcessModelVersions();
////
////        for (ProcessSummaryType process : processVersions.keySet()) {
////            i++;
////
////            System.out.println("enter to the net extraction! = "+i +" -- "+ model);
////            for (VersionSummaryType vst : processVersions.get(process)) {
//                System.out.println("1");
//                int procID = process.getId();
//                String procName = process.getName();
//                String branch = vst.getName();
//                Version version = new Version(vst.getMajorVersionNumber());
//                String username = context.getCurrentUser().getUsername();
//                int folderId = context.getCurrentFolder() == null ? 0 : context.getCurrentFolder().getId();
//
//                System.out.println("2");
//
//                Set<RequestParameterType<?>> requestProperties = new HashSet<>();
//                requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
//                requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));
//
//                System.out.println("3");
//
//                ExportFormatResultType data = processService.exportProcess(procName, procID, branch, version, "PNML 1.3.2", "MN", false, requestProperties);
//                byte[] bytes = IOUtils.toByteArray(data.getNative().getInputStream());
//                PNMLSerializer pnmlSerializer = new PNMLSerializer();
//
//                System.out.println("4");
//
//                NetSystem net = pnmlSerializer.parse(bytes);
//
//                System.out.println("5");
//                for(org.jbpt.petri.Transition t : net.getSilentTransitions())
//                    labels.add(t.getName());
//
//                System.out.println("6");
////                if(i == model)
//                    return jbptToUma(net);
//                //new CompareController(context, compareService, jbptToUma(net));
////            }
////        }
////
////        return null;
//    }

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
