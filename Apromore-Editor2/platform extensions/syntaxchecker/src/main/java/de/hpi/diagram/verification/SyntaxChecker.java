/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package de.hpi.diagram.verification;

import org.json.JSONObject;

import java.util.Map;

public interface SyntaxChecker {

    /**
     * @return true if there are no syntax errors
     */
    boolean checkSyntax();

    /**
     * returns the errors if any were found
     *
     * @return key = resource ID, value = error text
     */
    Map<String, String> getErrors();

    /**
     * @return json representation of errors
     */
    JSONObject getErrorsAsJson();

    /**
     * @return true if any errors have been found
     */
    boolean errorsFound();
}
