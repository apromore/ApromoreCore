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
package org.apromore.dao.model;

import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Embeddable
@Setter
public class OutputLogInfo implements Serializable {
    private String logName;
    private String apromoreWorkspaceDirectory;
    private S3Destination etlOutputDestination;

    @Column(name = "log_name")
    public String getLogName() {
        return logName;
    }

    @Column(name = "apromore_workspace_directory")
    public String getApromoreWorkspaceDirectory() {
        return apromoreWorkspaceDirectory;
    }

    @Embedded
    public S3Destination getEtlOutputDestination() {
        return etlOutputDestination;
    }
}
