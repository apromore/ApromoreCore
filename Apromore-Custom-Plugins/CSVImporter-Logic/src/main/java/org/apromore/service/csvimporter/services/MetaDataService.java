/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.services;

import org.apromore.service.csvimporter.model.LogMetaData;

import java.io.InputStream;
import java.util.List;

public abstract class MetaDataService {

    public abstract void validateLog(InputStream in, String charset) throws Exception;

    public abstract LogMetaData extractMetadata(InputStream in, String charset) throws Exception;

    public abstract List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception;

    public LogMetaData processMetadata(LogMetaData logMetaData, List<List<String>> lines) {
        return new MetaDataProcessorImpl().processMetaData(logMetaData, lines);
    }

    public boolean isTimestamp(int colPos, List<List<String>> lines) {
        return new MetaDataProcessorImpl().isTimestamp(colPos, lines);
    }

    public boolean isTimestamp(int colPos, String format, List<List<String>> lines) {
        return new MetaDataProcessorImpl().isTimestamp(colPos, format, lines);
    }

}
