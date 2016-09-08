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

package org.apromore.service.compare.impl;

// Java 2 Standard
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Third party
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;
import org.apache.commons.io.IOUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

// First party
import org.apromore.service.compare.CompareService;
import org.semanticweb.kaon2.ob;

/**
 * Tests for {@link CompareServiceImpl}.
 */
public class CompareServiceImplUnitTest {

    /**
     * Test the {@link CompareService#discoverBPMNModel} method
     */
    @Ignore("Fails with java.lang.IncompatibleClassChangeError: Class ee.ut.mining.log.AlphaRelations does not implement the requested interface ee.ut.mining.log.ConcurrencyRelations, but works correctly in the live server.")
    @Test
    public void testDiscoverBPMNModel1() throws Exception {
        testDiscoverBPMNModel("model12.pnml", "model3.pnml_log.xes", new HashSet<>(Arrays.asList("In the model, 't15' occurs after 'B' and before 'end'", "In the log, after 'start', 'B' is substituted by 'txor1'")));
    }

    /**
     * Test the {@link CompareService#discoverBPMNModel} method
     */
    @Ignore("Fails with java.lang.IncompatibleClassChangeError: Class ee.ut.mining.log.AlphaRelations does not implement the requested interface ee.ut.mining.log.ConcurrencyRelations, but instead fails with a StackOverflowException in the live server.")
    @Test
    public void testDiscoverBPMNModel2() throws Exception {
        testDiscoverBPMNModel("repairExample.pnml", "repairExample_complete_lifecycle_only.xes", new HashSet<>(Arrays.asList("Foo", "Bar")));
    }

    /**
     * Test the {@link CompareService#discoverBPMNModel} method.
     *
     * @param pnml
     * @param xes
     * @param expectedResult
     */
    private void testDiscoverBPMNModel(String pnml, String xes, Set<String> expectedResult) throws Exception {

        // Create a test instance
        CompareService compareService = new CompareServiceImpl();

        // Obtain the Petri net parameter
        PetriNet net = jbptToUma(new PNMLSerializer().parse(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(pnml))));

        // Obtain the log parameter
        XLog log = new XesXmlParser().parse(getClass().getClassLoader().getResourceAsStream(xes)).get(0);

        HashSet<String> obs = new HashSet<String>();
        for(Transition t : net.getTransitions())
                obs.add(t.getName());

        // Invoke the method
        Set<String> result = compareService.discoverBPMNModel(net, log, obs);

        // Did we get the expected result?
        assertEquals(expectedResult, result);
    }

    /**
     * Convert a Petri net from JBPT to UMA.
     *
     * Cut-'n'-pasted from {@link org.apromore.plugin.portal.compareBP.ComparePlugin#jbptToUma}.
     */
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
