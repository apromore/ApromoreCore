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

package com.processconfiguration.bddc;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import net.sf.javabdd.BDD;

import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;

/**
 * Replacement for {@link ExecBDDC}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class JavaBDDService implements BDDService {

    /**
     * Logger.  Named after this class.
     */
    private static final Logger LOGGER = Logger.getLogger(JavaBDDService.class.getName());

    private final Parser parser;
    private final BDD    constraints;

    /**
     * Constructor.
     *
     * @param constraintString  a well-formed BDDC expression
     * @throws IllegalArgumentException if <var>constraintString</var> isn't a well-formed BDDC expression
     */
    public JavaBDDService(final String constraintString) throws IllegalArgumentException {
        LOGGER.info("constraintString=" + constraintString);
        parser = new Parser(new StringBufferInputStream(constraintString));
        parser.init();
        try {
          constraints = parser.AdditiveExpression();
          LOGGER.info("constraints=" + constraints);
        } catch (ParseException e) {
          throw new IllegalArgumentException("Not a well-formed constraint expression: " + constraintString, e);
        }
    }

    /**
     * Reset the background process.
     */
    public void reset() throws Exception {
        throw new RuntimeException("Reset not implemented for JavaBDD");
    }

    /**
     * Initialises a process by setting all the facts (variables) to arguments.
     */
    public void init(final String init) {
        LOGGER.info("init=" + init);
    }

    // TODO: confirm that this is dead code; if so, remove it
    public boolean isViolated(final String cond) {
        LOGGER.info("cond=" + cond);
        return false;
    }

    public boolean isViolated(TreeMap<String, String> valuation) {
        LOGGER.info("valuation=" + valuation);
        BDD premise = parser.getFactory().one();
        for (String fID: valuation.keySet()) {
            BDD variable = parser.findVariable(fID);
            switch (valuation.get(fID)) {
            case "true":  premise = premise.and(variable);       break;
            case "false": premise = premise.and(variable.not()); break;
            }
        }
        return premise.imp(constraints.not()).isOne();
    }

    public boolean isXOR(List<String> factsList) {
        LOGGER.info("factsList=" + factsList);
        return constraints.imp(parser.exactlyOne(toBDDList(factsList))).isOne();
    }

    private List<BDD> toBDDList(Collection<String> factsList) {
        List<BDD> bddList = new ArrayList(factsList.size());
	for (String string: factsList) {
            bddList.add(parser.findVariable(string));
        }
        return bddList;
    }

    /**
     * Once the fact's setting is accepted, it has to be set in the process.
     */ //TODO: do we need it?
    public void setFact(String fID, String value) {
        LOGGER.info("fID=" + fID + " value=" + value);
    }

    /**
     * Given a partial configuration, it checks whether a set of facts is forceable to TRUE or FALSE.
     *
     * @return 1 if the configuration implies <var>fID</var> must be true, -1 if false, 0 otherwise
     */
    public int isForceable(String fID) {
        LOGGER.info("fID=" + fID);
        BDD bdd = parser.findVariable(fID);

        // Forced true?
        if (constraints.imp(bdd).isOne()) {
            return 1;
        }

        // Forced false?
        if (constraints.imp(bdd.not()).isOne()) {
            return -1;
        }

        // Neither forced true nor false
        return 0;
    }
}
