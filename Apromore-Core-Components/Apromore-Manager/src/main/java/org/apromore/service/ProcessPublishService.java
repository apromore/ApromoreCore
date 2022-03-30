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
package org.apromore.service;

import org.apromore.dao.model.ProcessPublish;
import org.apromore.portal.model.ProcessSummaryType;

/**
 * Interface for the Process Publish Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author Jane Hoh
 */
public interface ProcessPublishService {
    ProcessPublish savePublishDetails(final int processId, final String publishId, final boolean publishStatus);

    ProcessPublish updatePublishStatus(final String publishId, final boolean publishStatus);

    ProcessPublish getPublishDetails(final int processId);

    boolean isPublished(final String publishId);

    ProcessSummaryType getSimpleProcessSummary(final String publishId);
}
