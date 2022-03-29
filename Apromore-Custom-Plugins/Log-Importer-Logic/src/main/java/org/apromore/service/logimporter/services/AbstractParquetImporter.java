/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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

package org.apromore.service.logimporter.services;

abstract class AbstractParquetImporter implements ParquetImporter {

    private Integer maxEventCount;

    /**
     * Return an instance of the AbstractParquetImporter provided a max event count.
     *
     * @param maxEventCount the maximum number of events this importer considers valid to import;
     *                      <code>null</code> indicates no limit.
     */
    protected AbstractParquetImporter(final Integer maxEventCount) {
        this.maxEventCount = maxEventCount;
    }

    /**
     * Checks if the line count is valid.
     *
     * @param lineCount a count of events
     * @return whether <var>lineCount</var> is within the configured {@link #maxEventCount}
     */
    protected boolean isValidLineCount(int lineCount) {
        return maxEventCount == null || lineCount <= maxEventCount;
    }
}
