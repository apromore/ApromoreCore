/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController.dto;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

/**
 * Created by cameron on 23/01/2014.
 */
public class VersionDetailType {

    private ProcessSummaryType process;
    private VersionSummaryType version;

    public VersionDetailType(final ProcessSummaryType process, final VersionSummaryType version) {
        this.process = process;
        this.version = version;
    }

    public ProcessSummaryType getProcess() {
        return process;
    }

    public void setProcess(final ProcessSummaryType process) {
        this.process = process;
    }

    public VersionSummaryType getVersion() {
        return version;
    }

    public void setVersion(final VersionSummaryType version) {
        this.version = version;
    }
}
