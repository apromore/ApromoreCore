package de.hpi.petrinet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;
import de.hpi.petrinet.serialization.XMLFileLoaderSaver;
import de.hpi.petrinet.verification.PetriNetInterpreter;
import de.hpi.petrinet.verification.PetriNetSyntaxChecker;

/**
 * Copyright (c) 2008 Gero Decker, Matthias Weidlich
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class PetriNet implements Cloneable {

    protected List<Place> places;
    protected List<Transition> transitions;
    protected List<FlowRelationship> flowRelationships;
    protected List<Place> finalPlaces;
    protected List<Place> initialPlaces;

    protected TransitiveClosure transitiveClosure;
    protected Map<Node, Set<Node>> dominators;
    protected Map<Node, Set<Node>> postdominators;

    protected String id;

    public List<FlowRelationship> getFlowRelationships() {
        if (flowRelationships == null)
            flowRelationships = new MyFlowRelationshipList();
        return flowRelationships;
    }

    public List<Place> getPlaces() {
        if (places == null)
            places = new ArrayList<Place>();
        return places;
    }

    public List<Transition> getTransitions() {
        if (transitions == null)
            transitions = new ArrayList<Transition>();
        return transitions;
    }

    public List<Node> getLabeledTransitions() {
        List<Node> result = new ArrayList<Node>();
        for (Transition t : getTransitions())
            if (t instanceof LabeledTransition)
                result.add((LabeledTransition) t);
        return result;
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        nodes.addAll(getPlaces());
        nodes.addAll(getTransitions());
        return nodes;
    }

    public SyntaxChecker getSyntaxChecker() {
        return new PetriNetSyntaxChecker(this);
    }

    /**
     * Creates a deep copy of whole Petri net. All places,
     * transitions and flows are copied, whereas the source
     * and targets for the latter are also set accordingly.
     *
     * @return the clone of the Petri net
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PetriNet clone = (PetriNet) super.clone();
        Map<Node, Node> nodeCopies = new HashMap<Node, Node>();

        clone.setPlaces(new ArrayList<Place>());
        // will be generated when needed
        clone.setInitialPlaces(null);
        clone.setFinalPlaces(null);

        for (Place p : this.getPlaces()) {
            Place p2 = (Place) p.clone();
            clone.getPlaces().add(p2);
            nodeCopies.put(p, p2);
        }

        clone.setTransitions(new ArrayList<Transition>());
        for (Transition t : this.getTransitions()) {
            Transition t2 = (Transition) t.clone();
            clone.getTransitions().add(t2);
            nodeCopies.put(t, t2);
        }

        clone.setFlowRelationships(new MyFlowRelationshipList());
        for (FlowRelationship f : this.getFlowRelationships()) {
            FlowRelationship newF = (FlowRelationship) f.clone();
            newF.setSource(nodeCopies.get(f.getSource()));
            newF.setTarget(nodeCopies.get(f.getTarget()));
            clone.getFlowRelationships().add(newF);
        }

        // will be generated if needed
        clone.setTransitiveClosure(null);
        return clone;
    }

    public PetriNetFactory getFactory() {
        return PetriNetFactory.eINSTANCE;
    }

    public Marking getInitialMarking() {
        return null;
    }


//	public void optimize(Map<String,Boolean> parameters) {
//		boolean changed = false;
//		do {
//			changed = false;
//			if (parameters != null)
//				for (Iterator<Entry<String,Boolean>> it=parameters.entrySet().iterator(); it.hasNext(); ) {
//					Entry<String,Boolean> e = it.next();
//					if (e.getValue().booleanValue())
//						changed |= doOptimization(e.getKey());
//				}
//		} while (changed);
//	}
//	
//	protected boolean doOptimization(String parameter) {
//		return false;
//	}

//	protected boolean doOptimization(Map<String,Boolean> parameters, String parameter) {
//		if (parameters == null)
//			return false;
//		Boolean b = parameters.get(parameter);
//		if (b == null)
//			return false;
//		else
//			return b.booleanValue();
//	}

    public PetriNetInterpreter getInterpreter() {
        return null;
    }

    @Override
    public String toString() {
        try {
            XMLFileLoaderSaver ls = new XMLFileLoaderSaver();
            Document doc = ls.createNewDocument();
            new PetriNetPNMLExporter().savePetriNet(doc, this);
            return ls.serializeToString(doc);
        } catch (Exception e) {
            return super.toString();
        }
    }

    /**
     * Returns all final places of this petri net (cached).
     */
    public List<Place> getFinalPlaces() {
        if (finalPlaces == null) {
            finalPlaces = new LinkedList<Place>();
            for (Place place : this.getPlaces()) {
                if (place.isFinalPlace()) {
                    finalPlaces.add(place);
                }
            }
        }

        return finalPlaces;
    }

    /**
     * Returns the first final place, intended for use in workflow nets.
     */
    public Place getFinalPlace() {
        return this.getFinalPlaces().get(0);
    }

    /**
     * Returns all initial places of this petri net (cached).
     */
    public List<Place> getInitialPlaces() {
        if (initialPlaces == null) {
            initialPlaces = new LinkedList<Place>();
            for (Place place : this.getPlaces()) {
                if (place.isInitialPlace()) {
                    initialPlaces.add(place);
                }
            }
        }

        return initialPlaces;
    }

    /**
     * Returns the first initial place, intended for use in workflow nets.
     */
    public Place getInitialPlace() {
        return this.getInitialPlaces().get(0);
    }

    /**
     * Checks whether the net is a free choice net.
     *
     * @return true, if the net is free-choice
     */
    public boolean isFreeChoiceNet() {
        boolean isFC = true;

        outer:
        for (Transition t1 : this.getTransitions()) {
            for (Transition t2 : this.getTransitions()) {
                if (t1.equals(t2))
                    continue;
                Collection<Node> preT1 = t1.getPrecedingNodes();
                Collection<Node> preT2 = t2.getPrecedingNodes();
                if (CollectionUtils.containsAny(preT1, preT2)) {
                    preT1.retainAll(preT2);
                    boolean tmp = (preT1.size() == preT2.size());
                    isFC &= tmp;
                    if (!isFC)
                        break outer;
                }
            }
        }
        return isFC;
    }

    /**
     * Checks whether the net is a workflow net. Such a net has
     * exactly one initial and one final place and every place and
     * transition is one a path from i to o.
     *
     * @return true, if the net is a workflow net
     */
    public boolean isWorkflowNet() {
        boolean isWF = (this.getInitialPlaces().size() == 1) && (this.getFinalPlaces().size() == 1);
        // maybe we already know that the net is not a workflow net
        if (!isWF)
            return isWF;

        Node in = this.getInitialPlace();
        Node out = this.getFinalPlace();
        for (Node n : this.getNodes()) {
            if (n.equals(in) || n.equals(out))
                continue;
            isWF &= this.getTransitiveClosure().isPath(in, n);
            isWF &= this.getTransitiveClosure().isPath(n, out);
        }
        return isWF;
    }

    public PTNet getSubnet(Collection<Node> nodes) {
        PTNet net = PTNetFactory.eINSTANCE.createPetriNet();

        Map<Node, Node> nodeCopies = new HashMap<Node, Node>();

        try {
            for (Node n : nodes) {
                if (nodes.contains(n)) {
                    if (n instanceof Place) {
                        Place c = (Place) ((Place) n).clone();
                        net.getPlaces().add(c);
                        nodeCopies.put(n, c);
                    } else {
                        Transition c = (Transition) ((Transition) n).clone();
                        net.getTransitions().add(c);
                        nodeCopies.put(n, c);
                    }
                }
            }
            for (FlowRelationship f : this.getFlowRelationships()) {
                if (nodes.contains(f.getSource()) && nodes.contains(f.getTarget())) {
                    FlowRelationship c = new FlowRelationship();
                    c.setSource(nodeCopies.get(f.getSource()));
                    c.setTarget(nodeCopies.get(f.getTarget()));
                    net.getFlowRelationships().add(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return net;
    }

    /**
     * Checks whether the net is an S-net.
     *
     * @return true, if net is an S-net.
     */
    public boolean isSNet() {
        boolean result = true;
        for (Transition t : this.getTransitions())
            result &= (t.getIncomingFlowRelationships().size() == 1) && ((t.getOutgoingFlowRelationships().size() == 1));
        return result;
    }

    /**
     * Checks whether the net is a T-net.
     *
     * @return true, if net is a T-net.
     */
    public boolean isTNet() {
        boolean result = true;
        for (Place p : this.getPlaces())
            result &= (p.getIncomingFlowRelationships().size() == 1) && ((p.getOutgoingFlowRelationships().size() == 1));
        return result;
    }

    public Map<Node, Set<Node>> getDominators() {
        if (this.dominators == null)
            this.dominators = deriveDominators(false);
        return this.dominators;
    }

    public Map<Node, Set<Node>> getPostDominators() {
        if (this.postdominators == null)
            this.postdominators = deriveDominators(true);
        return this.postdominators;
    }

    protected Map<Node, Set<Node>> deriveDominators(boolean reverse) {

        int initIndex = reverse ? this.getNodes().indexOf(this.getFinalPlace()) : this.getNodes().indexOf(this.getInitialPlace());

        int size = this.getNodes().size();
        final BitSet[] dom = new BitSet[size];
        final BitSet ALL = new BitSet(size);

        for (Node n : this.getNodes())
            ALL.set(this.getNodes().indexOf(n));

        for (Node n : this.getNodes()) {
            int index = this.getNodes().indexOf(n);
            BitSet curDoms = new BitSet(size);
            dom[index] = curDoms;

            if (index != initIndex) curDoms.or(ALL);
            else curDoms.set(initIndex);
        }

        boolean changed = true;

		/*
		 * While we change the dom relation for a node
		 */
        while (changed) {
            changed = false;
            for (Node n : this.getNodes()) {
                int index = this.getNodes().indexOf(n);
                if (index == initIndex) continue;

                final BitSet old = dom[index];
                final BitSet curDoms = new BitSet(size);
                curDoms.or(old);

                Collection<Node> predecessors = reverse ? n.getSucceedingNodes() : n.getPrecedingNodes();
                for (Node p : predecessors) {
                    int index2 = this.getNodes().indexOf(p);
                    curDoms.and(dom[index2]);
                }

                curDoms.set(index);

                if (!curDoms.equals(old)) {
                    changed = true;
                    dom[index] = curDoms;
                }
            }
        }

        Map<Node, Set<Node>> dominators = new HashMap<Node, Set<Node>>();

        for (Node n : this.getNodes()) {
            int index = this.getNodes().indexOf(n);
            dominators.put(n, new HashSet<Node>());
            for (int i = 0; i < size; i++)
                if (dom[index].get(i))
                    dominators.get(n).add(this.getNodes().get(i));
        }

        return dominators;
    }

    protected class MyFlowRelationshipList extends ArrayList<FlowRelationship> {

        private static final long serialVersionUID = 7350067193890668068L;

        @Override
        public FlowRelationship remove(int index) {
            FlowRelationship rel = super.remove(index);
            if (rel != null) {
                rel.setSource(null);
                rel.setTarget(null);
            }
            return rel;
        }

        @Override
        public boolean remove(Object o) {
            boolean removed = super.remove(o);
            if (removed) {
                ((FlowRelationship) o).setSource(null);
                ((FlowRelationship) o).setTarget(null);
            }
            return removed;
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            for (int i = fromIndex; i < toIndex; i++) {
                FlowRelationship rel = get(i);
                rel.setSource(null);
                rel.setTarget(null);
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean removeAll(Collection<?> mylist) {
            for (Iterator<?> it = mylist.iterator(); it.hasNext(); ) {
                FlowRelationship rel = (FlowRelationship) it.next();
                rel.setSource(null);
                rel.setTarget(null);
            }
            return super.removeAll(mylist);
        }
    }

    public TransitiveClosure getTransitiveClosure() {
        if (this.transitiveClosure == null)
            this.transitiveClosure = new TransitiveClosure(this);
        return this.transitiveClosure;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public void setFlowRelationships(List<FlowRelationship> flowRelationships) {
        this.flowRelationships = flowRelationships;
    }

    public void setFinalPlaces(List<Place> finalPlaces) {
        this.finalPlaces = finalPlaces;
    }

    public void setInitialPlaces(List<Place> initialPlaces) {
        this.initialPlaces = initialPlaces;
    }

    public void setTransitiveClosure(TransitiveClosure transitiveClosure) {
        this.transitiveClosure = transitiveClosure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
