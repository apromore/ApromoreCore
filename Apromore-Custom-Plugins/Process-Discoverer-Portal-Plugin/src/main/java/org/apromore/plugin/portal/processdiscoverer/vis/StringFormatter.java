/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.vis;

public class StringFormatter {
    private final int MAXLEN = 25;
    private final int MAXWORDLEN = 15;

    public String escapeChars(String value) {
    	return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    public String shortenName(String name, int len) {
        boolean needEllipsis = false;
        if (len <= 0) {
            len = MAXLEN;
        }
        String[] parts = name.split(" ");
        if (parts.length > 2) {
            if (parts[0].length() > MAXWORDLEN || parts[1].length() > MAXWORDLEN) {
                name = parts[0].substring(0, MAXWORDLEN);
            } else {
                name = parts[0] + " " + parts[1];
            }
            needEllipsis = true;
        }
        if (name.length() > len) {
            name = name.substring(0, len);
            needEllipsis = true;
        }
        if (needEllipsis) {
            name += "...";
        }
        return name;
    }
}
