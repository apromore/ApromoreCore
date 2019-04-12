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

package org.apromore.plugin.processdiscoverer.impl.logprocessors;

import java.nio.charset.StandardCharsets;

import org.apromore.plugin.processdiscoverer.impl.util.StringValues;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class EventNameAnalyser {

    public boolean isCompleteEvent(String name) {
        return name.toLowerCase().endsWith(StringValues.b[120]);
    }

    public boolean isStartEvent(String name) {
        return name.toLowerCase().endsWith(StringValues.b[121]);
    }

    public String getStartEvent(String name) {
        return name.substring(0, name.length() - 8) + StringValues.b[119];
    }

    public String getCompleteEvent(String name) {
        return name.substring(0, name.length() - 5) + StringValues.b[118];
    }

    public String getCollapsedEvent(String name) {
        if(isStartEvent(name)) return name.substring(0, name.length() - 6);
        if(isCompleteEvent(name)) return name.substring(0, name.length() - 9);
        return name;
    }
}
