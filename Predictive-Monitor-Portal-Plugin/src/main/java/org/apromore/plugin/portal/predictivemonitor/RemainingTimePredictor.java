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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.time.Duration;

// Third party packages
import org.json.JSONException;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

class RemainingTimePredictor implements Predictor {

    public String[] getArgs(String pythonCommand, String kafkaHost, String prefixesTopic, String predictionsTopic, String tag) {
        return new String[] { pythonCommand, "PredictiveMethods/RemainingTime/remaining-time-kafka-processor.py", kafkaHost, prefixesTopic, predictionsTopic, tag };
    }

    public void addHeaders(Listhead head) {
        head.appendChild(new Listheader("Remaining Time"));
    }

    public void addCells(Listitem item, DataflowEvent event) {
        String s;
        try {
            s = DataflowEvent.format(Duration.ofSeconds((event.getJSON().getLong("remainingTime"))));
        } catch (JSONException e) {
            s = "";
        }
        item.appendChild(new Listcell(s));
    }
}
