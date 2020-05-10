/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.dialogController;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.Media;

/**
 * A ZK {@link Media} implementation.
 */
class MediaImpl implements Media {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaImpl.class);

    private final String format;
    private final String name;
    private final byte[] bytes;
    private final Charset charset;

    /**
     * {@inheritDoc}
     *
     * This implementation materializes the binary content into memory.
     *
     * @param name     file name; the format will be extracted from the file name extension
     * @param in       binary content
     * @param charset  how binary content should be converted into characters for {@link #getReaderData}
     */
    public MediaImpl(final String name, final InputStream in, Charset charset) throws IOException {
        Matcher matcher = Pattern.compile(".*\\.(?<extension>[^/\\.]*)").matcher(name);

        this.bytes   = ByteStreams.toByteArray(in);
        this.name    = name;
        this.charset = charset;
        this.format  = matcher.matches() ? matcher.group("extension") : null;

        LOGGER.debug("Media " + name + " created, size=" + bytes.length + ", format=" + format);
    }


    // Implementation of the Media interface

    public byte[] getByteData() { return bytes; }

    public String getContentType() { return "application/octet-stream"; }

    public String getFormat() { return format; }

    public String getName() { return name; }

    public Reader getReaderData() { return new InputStreamReader(new ByteArrayInputStream(bytes), charset); }

    public InputStream getStreamData() { return new ByteArrayInputStream(bytes); }

    public String getStringData() { return new String(bytes, charset); }

    public boolean inMemory() { return true; }

    public boolean isBinary() { return false; }

    public boolean isContentDisposition() { return false; }
}
