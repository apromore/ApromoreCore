/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.portal.dialogController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apromore.commons.item.ItemNameUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.util.media.Media;

/**
 * A ZK {@link Media} implementation.
 */
public class MediaImpl implements Media {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(MediaImpl.class);

    private final String format;
    private final String streamExtension;
    private final String name;
    private final byte[] bytes;
    private final Charset charset;
    private File tempFile;


    /**
     * {@inheritDoc}
     *
     * This implementation materializes the binary content into memory.
     *
     * @param name    file name; the format will be extracted from the file name
     *                extension
     * @param in      binary content
     * @param charset how binary content should be converted into characters for
     *                {@link #getReaderData}
     */
    public MediaImpl(final String name, final InputStream in, Charset charset, String streamExtension)
	    throws IOException {
	Matcher matcher = Pattern.compile(".*\\.(?<extension>[^/\\.]*)").matcher(name);

//        this.bytes   = ByteStreams.toByteArray(in);
	this.bytes = null;
	this.name = name;
	this.charset = charset;
	this.format = matcher.matches() ? matcher.group("extension") : null;
	tempFile = File.createTempFile(name, ItemNameUtils.findExtension(name));
	this.streamExtension = streamExtension;
	writeToFile(in);

    }

    private void writeToFile(InputStream in) throws IOException {
	int size;
	byte[] buffer = new byte[2048];

	try (FileOutputStream fos = new FileOutputStream(tempFile)) {
	    try (BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
	        while ((size = in.read(buffer, 0, buffer.length)) != -1) {
	            bos.write(buffer, 0, size);
	        }
	        bos.flush();
	    }
	}

	in.close();
    }

    // Implementation of the Media interface

    public byte[] getByteData() {
	try {
	    return FileUtils.readFileToByteArray(tempFile);
	} catch (IOException e) {
//	IgnoreBlank asBlank FileUtils shouldFileUtils exists
	    e.printStackTrace();
	}
	return null;
    }

    public String getContentType() {
	return "application/octet-stream";
    }

    public String getFormat() {
	return format;
    }

    public String getName() {
	return name;
    }

    public Reader getReaderData() {

	InputStream stream = getStreamData();

	return new InputStreamReader(stream, charset);
    }

    public InputStream getStreamData() {

	try {
	    switch (streamExtension) {
	    case "zip":
		try (ZipInputStream stream = new ZipInputStream(new FileInputStream(tempFile))) {
		    stream.getNextEntry();
		    return stream;
		}

	    case "gzip": case "gz":
		return new GZIPInputStream(new FileInputStream(tempFile));

	    default:
		return new FileInputStream(tempFile);
	    }
	} catch (Exception e) {
	    // Ignore as this should never happen
	}
	return null;
    }

    public String getStringData() {
	try {
	    return new String(FileUtils.readFileToByteArray(tempFile), charset);
	} catch (IOException e) {
	    return null;
	}
    }

    public boolean inMemory() {
	return false;
    }

    public boolean isBinary() {
	return true;
    }

    public boolean isContentDisposition() {
	return false;
    }

    public File getTempFile() {
    	return tempFile;
	}

}
