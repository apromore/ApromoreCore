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

/**
 * TestCanonical2PNML is a class for converting an CanonicalProcessType
 *  object into a PnmlType object.
 * <p>
 *
 * @author Martin SInger, Niko Waldow
 * @version     %I%, %G%
 * @since 1.0
 */

package org.apromore.canoniser.pnml.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.canonical2pnml.AddXorOperators;
import org.apromore.canoniser.pnml.internal.canonical2pnml.DataHandler;
import org.apromore.pnml.NodeType;
import org.apromore.canoniser.pnml.internal.canonical2pnml.RemoveConnectorTasks;
import org.apromore.canoniser.pnml.internal.canonical2pnml.RemoveEvents;
import org.apromore.canoniser.pnml.internal.canonical2pnml.RemoveSplitJoins;
import org.apromore.canoniser.pnml.internal.canonical2pnml.RemoveState;
import org.apromore.canoniser.pnml.internal.canonical2pnml.TranslateAnnotations;
import org.apromore.canoniser.pnml.internal.canonical2pnml.TranslateHumanResources;
import org.apromore.canoniser.pnml.internal.canonical2pnml.TranslateNet;
import org.apromore.canoniser.pnml.internal.canonical2pnml.TranslateSubnet;
import org.apromore.canoniser.pnml.internal.canonical2pnml.UpdateSpecialOperators;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.PNMLSchema;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PnmlType;
//import org.apromore.pnml.PositionType;
import org.apromore.pnml.TransitionType;

public class Canonical2PNML {

    static final private Logger LOGGER = Logger.getLogger(Canonical2PNML.class.getCanonicalName());

    DataHandler data = new DataHandler();
    RemoveConnectorTasks removeConnectorTasks = new RemoveConnectorTasks();
    RemoveEvents removeEvents = new RemoveEvents();
    RemoveState removeState = new RemoveState();
    RemoveSplitJoins removeSplitJoins = new RemoveSplitJoins();
    TranslateAnnotations ta = new TranslateAnnotations();
    TranslateNet tn = new TranslateNet();
    private long ids = 0;  //System.currentTimeMillis();

    public PnmlType getPNML() {
        return data.getPnml();
    }

    /*
    public Canonical2PNML(CanonicalProcessType cproc) {
        removeConnectorTasks.setValue(data, cproc);
        removeConnectorTasks.remove();
        cproc = removeConnectorTasks.getCanonicalProcess();
        decanonise(cproc, null);
        ta.setValue(data);
    }
    */

    public Canonical2PNML(CanonicalProcessType cproc,
                          AnnotationsType      annotations,
                          boolean              isCpfTaskPnmlTransition,
                          boolean              isCpfEdgePnmlPlace) {

        data.setCpfTaskPnmlTransition(isCpfTaskPnmlTransition);
        data.setCpfEdgePnmlPlace(isCpfEdgePnmlPlace);

        removeConnectorTasks.setValue(data, cproc);
        removeConnectorTasks.remove();
        cproc = removeConnectorTasks.getCanonicalProcess();
        decanonise(cproc, annotations);
        ta.setValue(data);
        if (annotations != null) {
            ta.mapNodeAnnotations(annotations);
        }

        // Expand XOR (and OR) routing from from PNML transitions to their complete structures
        AddXorOperators ax = new AddXorOperators();
        ax.setValues(data, ids);
        ax.add(cproc);
        ids = ax.getIds();
        cproc = ax.getCanonicalProcess();

        // Structural simplifications
        simplify();
    }

    public Canonical2PNML(CanonicalProcessType cproc, AnnotationsType annotations, String filename) {
        for (ResourceTypeType res : cproc.getResourceType()) {
            data.put_resourcemap(String.valueOf(res.getId()), res);
        }
        data.setAnno(annotations);
        data.setFilename(filename);

        removeEvents.setValue(annotations, data, cproc);
        removeEvents.remove();
        cproc = removeEvents.getCanonicalProcess();
        if (annotations != null) {
            annotations = removeEvents.getAnnotations();
        }
        removeConnectorTasks.setValue(data, cproc);
        removeConnectorTasks.remove();
        cproc = removeConnectorTasks.getCanonicalProcess();
        removeState.setValue(data, cproc);
        removeState.remove();
        cproc = removeState.getCanonicalProcess();
        removeSplitJoins.setValue(annotations, data, cproc);
        removeSplitJoins.remove();
        cproc = removeSplitJoins.getCanonicalProcess();
        if (annotations != null) {
            annotations = removeSplitJoins.getAnnotations();
        }
        decanonise(cproc, annotations);
        ta.setValue(data);
        if (annotations != null) {
            ta.mapNodeAnnotations(annotations);
        }
        AddXorOperators ax = new AddXorOperators();
        ax.setValues(data, ids);
        ax.add(cproc);
        ids = ax.getIds();
        cproc = ax.getCanonicalProcess();
        UpdateSpecialOperators uso = new UpdateSpecialOperators();
        uso.setValues(data, ids);
        uso.add(cproc);
        ids = uso.getIds();
        cproc = uso.getCanonicalProcess();
        if (data.getSubnet() != null) {
            if (data.getSubnet().size() == 1) {
                TransitionType obj = data.getSubnet().get(0);
                TranslateSubnet ts = new TranslateSubnet();

                ts.setValue(data, obj.getId());
                data.getSubnet().remove(0);
                ts.addSubnet();
                data = ts.getdata();
            }

        }
    }

    /**
     * This main method to be reused by all the constructors for all cases.
     * <p/>
     *
     * @since 1.0
     */
    private void decanonise(CanonicalProcessType cproc, AnnotationsType annotations) {
    	for (NetType net : cproc.getNet()) {        	
            tn.setValues(data, ids, annotations);
            tn.translateNet(net);
            ids = tn.getIds();
        }
        TranslateHumanResources thr = new TranslateHumanResources();
        thr.setValues(data, ids);
        thr.translate(cproc);
        ids = thr.getIds();

        data.getNet().setId("noID");
        data.getNet().setType("http://www.informatik.hu-berlin.de/top/pntd/ptNetb");
        data.getPnml().getNet().add(data.getNet());     
    }

    private void simplify() {
        //LOGGER.info("Performing structural simplifications"); 

        SetMultimap<org.apromore.pnml.NodeType, ArcType> incomingArcMultimap = HashMultimap.create();
        SetMultimap<org.apromore.pnml.NodeType, ArcType> outgoingArcMultimap = HashMultimap.create();

        // Index graph connectivity
        for (ArcType arc: data.getNet().getArc()) {
            incomingArcMultimap.put((org.apromore.pnml.NodeType) arc.getTarget(), arc);
            outgoingArcMultimap.put((org.apromore.pnml.NodeType) arc.getSource(), arc);
        }
        
        // When a synthetic place occurs adjacent to a silent transition on a branch, collapse them
        for (PlaceType place: data.getSynthesizedPlaces()) {
            if (incomingArcMultimap.get(place).size() == 1 &&
                outgoingArcMultimap.get(place).size() == 1) {

                // Assign: --incomingArc-> (place) --outgoingArc->
                ArcType incomingArc = incomingArcMultimap.get(place).iterator().next();
                ArcType outgoingArc = outgoingArcMultimap.get(place).iterator().next();

                TransitionType transition = (TransitionType) outgoingArc.getTarget();
                if (incomingArcMultimap.get(transition).size() == 1 && isSilent(transition)) {
                    // Collapse synthesized place followed by silent transition

                    // Delete: --incomingArc-> (place) --outgoingArc-> [transition]
                    data.getNet().getArc().remove(incomingArc);
                    data.getNet().getArc().remove(outgoingArc);
                    data.getNet().getPlace().remove(place);
                    data.getNet().getTransition().remove(transition);

                    // Re-source transition's outgoing arcs to incomingArc.source;
                    assert incomingArc.getSource() instanceof TransitionType;
                    for (ArcType arc: new HashSet<>(outgoingArcMultimap.get(transition))) {
                        arc.setSource(incomingArc.getSource());
                        outgoingArcMultimap.remove(transition, arc);
                        outgoingArcMultimap.put((TransitionType) incomingArc.getSource(), arc);
                    }
                }
                else {
                    transition = (TransitionType) incomingArc.getSource();
                    if (outgoingArcMultimap.get(transition).size() == 1 && isSilent(transition)) {
                        // Collapse silent transition followed by synthesized place

                        // Delete: [transition] --incomingArc-> (place) --outgoingArc->
                        data.getNet().getArc().remove(incomingArc);
                        data.getNet().getArc().remove(outgoingArc);
                        data.getNet().getPlace().remove(place);
                        data.getNet().getTransition().remove(transition);

                        // Re-target transition's incoming arcs to outgoingArc.target;
                        assert outgoingArc.getTarget() instanceof TransitionType;
                        for (ArcType arc: new HashSet<>(incomingArcMultimap.get(transition))) {
                            arc.setTarget(outgoingArc.getTarget());
                            incomingArcMultimap.remove(transition, arc);
                            incomingArcMultimap.put((TransitionType) outgoingArc.getTarget(), arc);
                        }
                    }
                }
            }
        }
        data.getSynthesizedPlaces().clear();
        //LOGGER.info("Performed structural simplifications");      
        
        // Logic to correct position of process elements        
        PlaceType place = null;
        TransitionType transition = null;
        BigDecimal TranX, TranY, PlaceX, PlaceY;
        int offset1 = 0;
		int offset2 = 0;
		
		for (int i = 0; i < 2; i++) {
			for (ArcType arc : data.getNet().getArc()) {

				if (arc.getSource() instanceof PlaceType) {
					place = (PlaceType) arc.getSource();
					transition = (TransitionType) arc.getTarget();
					offset1 = -75;
					offset2 = +75;
				} else if (arc.getSource() instanceof TransitionType) {
					place = (PlaceType) arc.getTarget();
					transition = (TransitionType) arc.getSource();
					offset1 = +75;
					offset2 = -75;
				}

				TranX = transition.getGraphics().getPosition().getX();
				TranY = transition.getGraphics().getPosition().getY();
				PlaceX = place.getGraphics().getPosition().getX();
				PlaceY = place.getGraphics().getPosition().getY();

				if ((Double.parseDouble(String.valueOf(PlaceX)) == 100
						&& Double.parseDouble(String.valueOf(PlaceY)) == 400)
						&& (Double.parseDouble(String.valueOf(TranX)) == 100
								&& Double.parseDouble(String.valueOf(TranY)) == 400)) {
					; // do nothing
				} else {

					if (Double.parseDouble(String.valueOf(PlaceX)) == 100
							&& Double.parseDouble(String.valueOf(PlaceY)) == 400) {
						place.getGraphics().getPosition()
								.setX(BigDecimal.valueOf(Double.parseDouble(String.valueOf(TranX)) + offset1));
						place.getGraphics().getPosition().setY(TranY);
					} else if (Double.parseDouble(String.valueOf(TranX)) == 100
							&& Double.parseDouble(String.valueOf(TranY)) == 400) {
						transition.getGraphics().getPosition()
								.setX(BigDecimal.valueOf(Double.parseDouble(String.valueOf(PlaceX)) + offset2));
						transition.getGraphics().getPosition().setY(PlaceY);
					}
				}

				if (arc.getGraphics() != null) {
					if (arc.getGraphics().getPosition() != null) {
						arc.getGraphics().getPosition().clear();
					}
				}
			}
		}
	}

    /**
     * @param transition
     * @return whether <var>transition</var> is silent
     */
    private boolean isSilent(TransitionType transition) {
        return transition.getName() == null;
    }

    public static void main(String[] args) throws Exception {

        final String HELP_TEXT = "A document in CPF format is read from standard input.\n" +
                                 "The PNML conversion is written to standard output.\n" +
                                 "Options:\n" +
                                 "-e  CPF edges treated as having a duration, converted tp PNML places\n" +
                                 "-h  this help text\n" +
                                 "-t  CPF tasks treated as instantaneous, converted to PNML transitions\n" +
                                 "-v  validate input against the CPF XML schema";

        boolean validate = false;
        boolean isCpfTaskPnmlTransition = false;
        boolean isCpfEdgePnmlPlace = false;

        for(String arg: args) {
            switch(arg) {
            case "-e":
                isCpfEdgePnmlPlace = true;
                break;
            case "-h": case "-?": case "-help": case "--help":
                System.out.println(HELP_TEXT);
                System.exit(0);
            case "-t":
                isCpfTaskPnmlTransition = true;
                break;
            case "-v":
                validate = true;
                break;
            default:
                System.err.println(arg + " is not a supported option\n" + HELP_TEXT);
                System.exit(-1);
            }
        }

        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(System.in, validate).getValue();
        PNMLSchema.marshalPNMLFormat(System.out, (new Canonical2PNML(cpf, null, isCpfTaskPnmlTransition, isCpfEdgePnmlPlace)).getPNML(), false);
    }
}