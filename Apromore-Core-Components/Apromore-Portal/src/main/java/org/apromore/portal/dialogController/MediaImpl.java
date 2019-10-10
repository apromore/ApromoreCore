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
