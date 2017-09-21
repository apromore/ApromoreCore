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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.io.InputStreamReader;
import javax.xml.datatype.DatatypeFactory;

import org.json.JSONObject;
import org.json.JSONTokener;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for @{link DataflowEvent}.
 */
public class DataflowEventUnitTest {

    @Test public void constructor() throws Exception {
        JSONObject json = new JSONObject(new JSONTokener(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("event.json"))));
        DataflowEvent event = new DataflowEvent(json, new HashMap<String, DataflowEvent>());
        assertEquals("W_Fixing_incoming_lead", event.getActivityName());
        assertEquals("201608", event.getCaseId());
        assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-01-17T07:47:42Z").toGregorianCalendar().getTime(), event.getTime());
    }
}
