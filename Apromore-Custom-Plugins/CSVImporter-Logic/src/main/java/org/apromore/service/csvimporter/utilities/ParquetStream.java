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
package org.apromore.service.csvimporter.utilities;

import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ParquetStream implements InputFile {
    private final String streamId;
    private final byte[] data;

    public class SeekableByteArrayInputStream extends ByteArrayInputStream {
        public SeekableByteArrayInputStream(byte[] buf) {
            super(buf);
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return this.pos;
        }
    }

    public ParquetStream(String streamId, byte[] stream) {
        this.streamId = streamId;
        this.data = stream;
    }

    @Override
    public long getLength() throws IOException {
        return this.data.length;
    }

    @Override
    public SeekableInputStream newStream() throws IOException {
        return new DelegatingSeekableInputStream(new SeekableByteArrayInputStream(this.data)) {

            @Override
            public void seek(long newPos) throws IOException {
                ((SeekableByteArrayInputStream) this.getStream()).setPos(new Long(newPos).intValue());
            }

            @Override
            public long getPos() throws IOException {
                return new Integer(((SeekableByteArrayInputStream) this.getStream()).getPos()).longValue();
            }
        };
    }

    @Override
    public String toString() {
        return new StringBuilder("ParquetStream[").append(streamId).append("]").toString();
    }
}
