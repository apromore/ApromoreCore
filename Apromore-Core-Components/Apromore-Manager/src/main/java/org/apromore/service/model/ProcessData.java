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

package org.apromore.service.model;

import org.apromore.helper.Version;

/**
 * @author Chathura Ekanayake
 */
public class ProcessData {

    private Integer id;
    private Version versionNumber;


    public ProcessData() {
    }

    public ProcessData(Integer id, Version versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Version getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Version versionNumber) {
        this.versionNumber = versionNumber;
    }
}
