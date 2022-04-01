/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.logimporter.utilities;

import java.util.Comparator;
import java.util.Date;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;

public class XEventComparator implements Comparator<XEvent> {


    @Override
    public int compare(XEvent o1, XEvent o2) {

        Date o1Date;
        Date o2Date;
        if (o1.getAttributes().get("time:timestamp") != null) {
            XAttribute o1da = o1.getAttributes().get("time:timestamp");
            if (((XAttributeTimestamp) o1da).getValue() != null) {
                o1Date = ((XAttributeTimestamp) o1da).getValue();
            } else {
                return -1;
            }
        } else {
            return -1;
        }

        if (o2.getAttributes().get("time:timestamp") != null) {
            XAttribute o2da = o2.getAttributes().get("time:timestamp");
            if (((XAttributeTimestamp) o2da).getValue() != null) {
                o2Date = ((XAttributeTimestamp) o2da).getValue();
            } else {
                return 1;
            }
        } else {
            return 1;
        }

        if (o1Date == null || o1Date.toString().isEmpty()) {
            return 1;
        } else if (o2Date == null || o2Date.toString().isEmpty()) {
            return -1;
        } else {
            return o1Date.compareTo(o2Date);
        }
    }
}
