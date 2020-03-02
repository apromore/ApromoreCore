/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.dao;

import java.util.Map;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface ProcessModelVersionRepositoryCustom {

    /* ************************** JPA Methods here ******************************* */

    /**
     * The Map of max model versions.
     * @param fragmentVersionId the fragment id
     * @return the mapped results
     */
    Map<String, Integer> getMaxModelVersions(Integer fragmentVersionId);

    /**
     * The Map of current model versions.
     * @param fragmentVersionId the fragment id
     * @return the mapped results
     */
    Map<String, Integer> getCurrentModelVersions(Integer fragmentVersionId);


    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
