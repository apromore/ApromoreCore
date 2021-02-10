/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
    private final int MINLEN = 2;
    private final int MAXLEN = 60;
    private final int MAXWORDLEN = 15;
    private final String DEFAULT = "*";
    private final String REPLACE_REGEXP = "[\\-_]";

    public String escapeChars(String value) {
    	return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    public String fixCutName(String name, int len) {
        name = name.replaceAll(REPLACE_REGEXP, " ");

        if (len <= 0) {
            len = MAXLEN;
        }
        if (name.length() > len) {
            name = name.substring(0, len);
            name += "...";
        }
        return name;
    }

    public String shortenName(String originalName, int len) {
        String name = originalName;

        if (name == null || name.length() == 0) {
            return DEFAULT;
        }
        if (name.length() <= MINLEN) {
            return name;
        }
        if (len <= 0) {
            len = MAXLEN;
        }
        try {
            name = name.replaceAll(REPLACE_REGEXP, " ");
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                int toCheck = parts.length - 1; // first ... last
                // int toCheck = 1; // first second ...
                if (parts[0].length() > MAXWORDLEN || parts[toCheck].length() > MAXWORDLEN) {
                    name = parts[0].substring(0, Math.min(MAXWORDLEN, parts[0].length()));
                    return name + "...";
                } else if (parts.length > 2) {
                    if (name.length() > len) {
                        name = parts[0] + " ... " + parts[toCheck]; // first ... last
                        // name = parts[0] + " " + parts[toCheck] + "..."; // first second ...
                        if (name.length() > len) {
                            return name.substring(0, len)  + "...";
                        }
                        return name;
                    } else {
                        return name;
                    }
                }
            } else if (parts[0].length() > MAXWORDLEN) {
                name = parts[0].substring(0, Math.min(MAXWORDLEN, parts[0].length()));
                return name + "...";
            }
        } catch (Error e) {
            name = originalName;
        }
        return name;
    }
}
