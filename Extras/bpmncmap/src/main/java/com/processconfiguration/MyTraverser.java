package com.processconfiguration;

import java.util.HashSet;
import java.util.Set;

import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.Visitor;

/**
 * Workaround for looping traversal of the auto-generated {@link DepthFirstTraverserImpl}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class MyTraverser extends DepthFirstTraverserImpl {

    /** The set of previously traversed BPMN data output associations. */
    Set<TDataOutputAssociation> traversedSet = new HashSet<>();

    @Override public void traverse(TDataOutputAssociation aBean, Visitor aVisitor) {
        if (traversedSet.contains(aBean)) { return; }  // break the loop between TDataOutputAssociation@sourceRef and TTask@dataOutputAssociation

        traversedSet.add(aBean);
        super.traverse(aBean, aVisitor);
    }
}





