/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package de.hpi.bpmn2_0.util;

import de.hpi.bpmn2_0.factory.configuration.Configuration;

public class SignavioIDChecker {
    private static boolean isValidID(String id) {
        if (id == null) {
            return false;
        }

        boolean testRes = true;

        testRes = testRes && id.length() <= 250;
        testRes = testRes && id.matches("^\\D(.)*");

        return testRes;


//		!bpmnEl.getId().matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}")s
    }

    public static boolean isValidID(String id, Configuration conf) {
        boolean result = true;

        if (conf != null && conf.ensureSignavioStyle && id != null) {
            result = id.matches("sid-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}-\\w{4,12}");
        }

        return result && isValidID(id);
    }
}
