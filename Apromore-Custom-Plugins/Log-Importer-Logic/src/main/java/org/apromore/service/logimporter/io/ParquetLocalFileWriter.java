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

package org.apromore.service.logimporter.io;

import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;

public class ParquetLocalFileWriter {

    public ParquetFileWriter getParquetWriter(File outputParquet, MessageType sampleSchema) throws IOException {

        ParquetFileWriter writer;
        // Classpath manipulation so that ServiceLoader in parquet-osgi reads its own META-INF/services
        // rather than the servlet context bundle's (i.e. the portal)
        Thread thread = Thread.currentThread();
        synchronized (thread) {
            ClassLoader originalContextClassLoader = thread.getContextClassLoader();
            try {
                thread.setContextClassLoader(Path.class.getClassLoader());
                writer = new ParquetFileWriter(new Path(outputParquet.toURI()), sampleSchema, true);
            } finally {
                thread.setContextClassLoader(originalContextClassLoader);
            }
        }
        return writer;
    }
}
