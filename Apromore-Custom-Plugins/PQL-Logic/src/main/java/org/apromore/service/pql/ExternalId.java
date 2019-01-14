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

package org.apromore.service.pql;

import java.text.ParseException;

import org.apromore.helper.Version;

/**
 * Hold the composite key of a process model version
 */
public class ExternalId {
    private int processId;
    private String branch;
    private Version version;

    public ExternalId(int processId, String branch, Version version) {
        this.processId = processId;
        this.branch    = branch;
        this.version   = version;
    }

    /**
     * Create an external identifier from a string of the form "7/MAIN/1.0".
     *
     * @param s  a string formatted as an external identifier
     * @return an external identifier
     * @throws ParseException  if <var>s</var> isn't a well-formed external identifier
     */
    public ExternalId(String s) throws ParseException {
        if (!s.matches("[0-9]+[/][a-zA-Z0-9]+[/]([0-9]+([.][0-9]+){1,2})")) {
            throw new ParseException(s, -1);
        }

        String[] fields = s.split("/", 3);

        this.processId = Integer.parseInt(fields[0]);
        this.branch    = fields[1];
        this.version   = new Version(fields[2]);
    }

    public int     getProcessId() { return processId; }
    public String  getBranch()    { return branch; }
    public Version getVersion()   { return version; }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (!o.getClass().equals(getClass())) { return false; }
        ExternalId externalId = (ExternalId) o;
        return externalId.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return processId + "/" + branch + "/" + version;
    }
}

