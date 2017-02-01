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

package de.hpi.diagram.verification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

abstract public class AbstractSyntaxChecker implements SyntaxChecker {
    protected Map<String, String> errors;

    public AbstractSyntaxChecker() {
        this.errors = new HashMap<String, String>();
    }

    abstract public boolean checkSyntax();

    public Map<String, String> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.clear();
    }

    public boolean errorsFound() {
        return errors.size() > 0;
    }

    public JSONObject getErrorsAsJson() {
        JSONObject jsonObject = new JSONObject();

        for (Entry<String, String> error : this.getErrors().entrySet()) {
            try {
                jsonObject.put(error.getKey(), error.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }
}
