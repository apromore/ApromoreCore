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

package org.apromore.service.logvisualizer;

import org.deckfour.xes.model.XLog;
import org.json.JSONArray;

/**
 * Created by Raffaele Conforti on 01/12/2016.
 */
public interface LogVisualizerService {

    String visualizeLog(XLog log, double activities, double arcs);

    JSONArray generateJSONArrayFromLog(XLog log, double activities, double arcs, boolean frequency_vs_duration, int avg_vs_min_vs_max);

}
