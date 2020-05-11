/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.canoniser.result;

import java.util.Date;

import org.apromore.plugin.PluginResultImpl;

public class CanoniserMetadataResult extends PluginResultImpl {

    private String processAuthor;
    private String processName;
    private String processVersion;
    private String processDocumentation;
    private Date processCreated;
    private Date processLastUpdate;

    public String getProcessAuthor() {
        return processAuthor;
    }

    public void setProcessAuthor(String processAuthor) {
        this.processAuthor = processAuthor;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getProcessDocumentation() {
        return processDocumentation;
    }

    public void setProcessDocumentation(String processDocumentation) {
        this.processDocumentation = processDocumentation;
    }

    public Date getProcessCreated() {
        return processCreated;
    }

    public void setProcessCreated(Date processCreated) {
        this.processCreated = processCreated;
    }

    public Date getProcessLastUpdate() {
        return processLastUpdate;
    }

    public void setProcessLastUpdate(Date processLastUpdate) {
        this.processLastUpdate = processLastUpdate;
    }

}
