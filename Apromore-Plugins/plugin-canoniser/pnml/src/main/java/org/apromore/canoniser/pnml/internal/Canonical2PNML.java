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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.canonical2pnml.AddXorOperators;
import org.apromore.canoniser.pnml.internal.canonical2pnml.DataHandler;
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
    }

    /**
     * @param transition
     * @return whether <var>transition</var> is silent
     */
    private boolean isSilent(TransitionType transition) {
        return transition.getName() == null;
    }

    public static void main(String[] arg) throws Exception {

        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(System.in, true).getValue();
        PNMLSchema.marshalPNMLFormat(System.out, (new Canonical2PNML(cpf, null, false, false)).getPNML(), false);
    }
}
