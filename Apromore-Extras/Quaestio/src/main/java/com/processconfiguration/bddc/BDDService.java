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

package com.processconfiguration.bddc;

import java.util.List;
import java.util.TreeMap;

public interface BDDService {

        /**
         * Initialises a process by setting all the facts (variables) to arguments.
         */
	public void init(String init);

	public boolean isViolated(String cond);

	public boolean isViolated(TreeMap<String, String> valuation);

	public boolean isXOR(List<String> factsList);

        /**
         * Once the fact's setting is accepted, it has to be set in the process.
         */ //TODO: do we need it?
	public void setFact(String fID, String value);

        /**
         * Given a partial configuration, it checks whether a set of facts is forceable to TRUE or FALSE.
         */
	public int isForceable(String fID);
}
