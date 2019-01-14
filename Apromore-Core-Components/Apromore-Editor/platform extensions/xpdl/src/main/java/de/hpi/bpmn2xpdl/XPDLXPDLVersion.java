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

package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("XPDLVersion")
public class XPDLXPDLVersion extends XMLConvertible {

    @Text
    protected String content = "2.2";

    public String getContent() {
        return content;
    }

    public void readJSONxpdlversionunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "xpdlversionunknowns");
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeJSONxpdlversionunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "xpdlversionunknowns");
    }
}
