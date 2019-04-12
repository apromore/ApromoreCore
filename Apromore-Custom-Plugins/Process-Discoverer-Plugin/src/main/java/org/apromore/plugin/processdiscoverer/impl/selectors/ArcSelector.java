/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer.impl.selectors;

import org.apromore.plugin.processdiscoverer.impl.Arc;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.apromore.plugin.processdiscoverer.impl.collectors.ArcInfoCollector;
import org.apromore.plugin.processdiscoverer.impl.collectors.Calculator;
import org.apromore.plugin.processdiscoverer.impl.reachability.ReachabilityChecker;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.ObjectDoublePair;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class ArcSelector {

    private final ReachabilityChecker reachabilityChecker = new ReachabilityChecker();
    private final MutableList<ObjectDoublePair<Arc>> sorted_arcs_frequency;
    private final Set<Arc> retained_arcs;
    private final Calculator calculator;
    private final boolean preserve_connectivity;
    private double threshold = 0.0;
    private double min;
    private double max;
    private boolean inverted;
    private VisualizationType type;

    public ArcSelector(ArcInfoCollector arcInfoCollector, double arcs, boolean preserve_connectivity, VisualizationType type, VisualizationAggregation aggregation, boolean inverted) {
        this.inverted = inverted;
        this.type = type;
        ObjectDoubleHashMap<Arc> arcs_frequency = arcInfoCollector.getArcsFrequencyMap(type, aggregation);

        this.calculator = new Calculator();
        calculator.method9(Long.toString(System.currentTimeMillis()));

        // min = arcs_frequency.min() + 1
        calculator.method10(calculator.method5(), (long) arcs_frequency.min(),1);
        min = calculator.method6();
        
        // max = arcs_frequency.max() + 1
        calculator.method10(calculator.method5(), (long) arcs_frequency.max(),1);
        max = calculator.method6();
        
        if(arcs_frequency.size() > 0) threshold = getLog((1 + max) - min)  * arcs;

        retained_arcs = new HashSet<>(arcs_frequency.keySet());
        this.preserve_connectivity = preserve_connectivity;

        sorted_arcs_frequency = arcs_frequency.keyValuesView().toList();
        sorted_arcs_frequency.sort(new Comparator<ObjectDoublePair<Arc>>() {
            @Override
            public int compare(ObjectDoublePair<Arc> o1, ObjectDoublePair<Arc> o2) {
                if(inverted) return Double.compare(o1.getTwo(), o2.getTwo());
                return Double.compare(o2.getTwo(), o1.getTwo());
            }
        });
    }

    public Set<Arc> selectArcs() {
        Set<Arc> source_sink_arcs = new UnifiedSet<>();
        for(int i = sorted_arcs_frequency.size() - 1; i >= 0; i--) {
            calculator.method10(calculator.method5(), (long) sorted_arcs_frequency.get(i).getTwo(),1);
            double current = scale(calculator.method6());
            Arc arc = sorted_arcs_frequency.get(i).getOne();
            if(current < threshold) {
                if(retained_arcs.contains(arc)) {
                    if(type != VisualizationType.FREQUENCY && (arc.getSource() == 1 || arc.getTarget() == 2)) {
                        source_sink_arcs.add(arc);
                        continue;
                    }
                    retained_arcs.remove(arc);
                    if (preserve_connectivity && (!reachabilityChecker.reachable(arc.getTarget(), retained_arcs) || !reachabilityChecker.reaching(arc.getSource(), retained_arcs))) {
                        retained_arcs.add(arc);
                    }
                }
            }else {
                break;
            }
        }

        if(type != VisualizationType.FREQUENCY && !preserve_connectivity) {
            IntHashSet nodes = new IntHashSet();
            for(Arc arc : retained_arcs) {
                if(arc.getSource() > 2) nodes.add(arc.getSource());
                if(arc.getTarget() > 2) nodes.add(arc.getTarget());
            }
            int size = 0;
            retained_arcs.addAll(source_sink_arcs);
            while (size != nodes.size()) {
                size = nodes.size();
                for (int i = 0; i < sorted_arcs_frequency.size(); i++) {
                    Arc arc = sorted_arcs_frequency.get(i).getOne();
                    if (!retained_arcs.contains(arc) && (nodes.contains(arc.getTarget()) || nodes.contains(arc.getSource()))) {
                        if (!reachabilityChecker.reachable(arc.getTarget(), retained_arcs) || !reachabilityChecker.reaching(arc.getSource(), retained_arcs)) {
                            retained_arcs.add(arc);
                            nodes.add(arc.getSource());
                            nodes.add(arc.getTarget());
                            break;
                        }
                    }
                }
            }

            size = 0;
            while (size != retained_arcs.size()) {
                UnifiedSet<Arc> set_arcs = new UnifiedSet<>(retained_arcs);
                size = retained_arcs.size();
                for (Arc arc : set_arcs) {
                    if (!nodes.contains(arc.getSource()) && !nodes.contains(arc.getTarget())) {
//                        retained_arcs.remove(arc);
                    } else if ((arc.getSource() == 1 && getIncomingArcs(retained_arcs, arc.getTarget()).size() > 1) ||
                            (arc.getTarget() == 2 && getOutgoingArcs(retained_arcs, arc.getSource()).size() > 1)) {
                        retained_arcs.remove(arc);
                        if (preserve_connectivity && (!reachabilityChecker.reachable(arc.getTarget(), retained_arcs) || !reachabilityChecker.reaching(arc.getSource(), retained_arcs))) {
                            retained_arcs.add(arc);
                        }else {
                            break;
                        }
                    }else if ((arc.getSource() == 1 && getIncomingArcs(retained_arcs, arc.getTarget()).size() == 1) &&
                            (getOutgoingArcs(retained_arcs, arc.getTarget()).size() == 1 && getOutgoingArcs(retained_arcs, arc.getSource()).toArray(new Arc[1])[0].getTarget() == 2)) {
                        retained_arcs.remove(arc);
                    }
                }
            }
        }

        return retained_arcs;
    }

    private Set<Arc> getOutgoingArcs(Set<Arc> arcs, int node) {
        Set<Arc> outgoing_arcs = new UnifiedSet<>();
        for(Arc arc : arcs) {
            if(arc.getSource() == node) {
                outgoing_arcs.add(arc);
            }
        }
        return outgoing_arcs;
    }

    private Set<Arc> getIncomingArcs(Set<Arc> arcs, int node) {
        Set<Arc> incoming_arcs = new UnifiedSet<>();
        for(Arc arc : arcs) {
            if(arc.getTarget() == node) {
                incoming_arcs.add(arc);
            }
        }
        return incoming_arcs;
    }

    private double scale(long value) {
        double v = (value - min) + 1;
        return (inverted) ? getLog((((1 + max) - min) - v) + 1) : getLog(v);
    }


    private double getLog(double value) {
        return (Math.log10(value) / Math.log10(2));
    }

}
