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

package org.apromore.logman.utils;

import java.nio.charset.StandardCharsets;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class LogUtils {
    public static final String LIFECYCLE_CODE = "lifecycle:transition";
    public static final String COMPLETE_CODE = "complete";
    public static final String START_CODE = "start";
    public static final String PLUS_COMPLETE_CODE = "+complete";
    public static final String PLUS_START_CODE = "+start";

    public static boolean isCompleteEvent(String name) {
        return name.toLowerCase().endsWith(PLUS_COMPLETE_CODE); //"+complete"
    }

    public static boolean isStartEvent(String name) {
        return name.toLowerCase().endsWith(PLUS_START_CODE); //"+start"
    }

    // Get the start event name corresponding to the complete event <name>
    public static String getStartEvent(String name) {
        return name.substring(0, name.length() - 8) + "start"; // StringValues.b[119]; //"start"
    }

    // Get the complete event name corresponding to the start event <name>
    public static String getCompleteEvent(String name) {
        return name.substring(0, name.length() - 5) + "complete"; //StringValues.b[118]; //"complete"
    }

    
    public static String getCollapsedEvent(String name) {
        if(isStartEvent(name)) return name.substring(0, name.length() - 6); // remove '+start'
        if(isCompleteEvent(name)) return name.substring(0, name.length() - 9); //remove '+complete'
        return name;
    }
    
	public static String getConceptName(XAttributable attrib) {
		String name = XConceptExtension.instance().extractName(attrib);
		return (name != null ? name : "<no name>");
	}
	
	// Add complete lifecycle:transition to a log having no lifecycle:transition event attribute
	public static void addCompleteLifecycle(XLog log) {
		XFactory factory = new XFactoryNaiveImpl();
		for (XTrace trace: log) {
			for (XEvent event: trace) {
				event.getAttributes().put(LIFECYCLE_CODE, factory.createAttributeLiteral(LIFECYCLE_CODE, COMPLETE_CODE, null));
			}
		}
	}

}
