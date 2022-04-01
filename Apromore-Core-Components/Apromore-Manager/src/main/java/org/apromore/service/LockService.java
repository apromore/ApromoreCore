/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;

/**
 * Lock Service, This service is used to to control the Locks that are held on the database tables and records.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface LockService {

    /**
     * Lock a process Model.
     *
     * @param processModelVersionId the process model
     * @return if locked successfully or not
     */
    boolean lockProcessModelVersion(Integer processModelVersionId);

    /**
     * unLock a previously locked process Model.
     * @param processModelVersion the process model
     */
    void unlockProcessModelVersion(ProcessModelVersion processModelVersion);

}

