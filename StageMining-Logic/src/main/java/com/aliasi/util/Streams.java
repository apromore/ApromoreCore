/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import org.xml.sax.InputSource;

/**
 * Static utility methods for processing input and output streams,
 * readers and writers.
 *
 * @author  Bob Carpenter
 * @version 4.0.1
 * @since   LingPipe1.0
 */
public class Streams {

    /**
     * Forbid instance construction.
     */
    private Streams() {
        /* no instances */
    }

    /**
     * Returns Java's canonical name of the default character set for
     * the system's current default locale.  Note that this is
     * returned as a Java charset name, not an official mime name.
     *
     * <P><i>Note:</i> This method is available in the J2EE version
     * of Java as <code>javax.mail.internet.getDefaultJavaCharset()</code>.
     *
     * <P><i>Note 2:</i> For example, the standard English install
     * of Sun's J2SE 1.4.2 on windows sets the default character set to
     * <code>&quot;Cp1252&quot;</code>, the Windows variant of Latin1.
     *
     * @return The default charset for the current platform.
     */
    public static String getDefaultJavaCharset() {
        byte[] bytes = new byte[0];
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        InputStreamReader defaultReader = new InputStreamReader(bytesIn);
        return defaultReader.getEncoding();
    }

    /**
     * Returns all the bytes read from the specified input stream as an
     * array.  This method does <b>not</b> close the input stream after
     * it is done reading.
     *
     * <p>This method will block waiting for input.
     *
     * @param in Input stream from which to read bytes.
     * @return Array of bytes read from stream.
     * @throws IOException If the underlying stream throws an exception
     * while reading.
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in,out);
        return out.toByteArray();
    }

    /**
     * Reads the full contents of the specified reader and returns
     * it as a character array.  This method does not close the
     * reader.
     *
     * @param reader Reader from which to get characters.
     * @return The characters read from the reader.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static char[] toCharArray(Reader reader) throws IOException {
        CharArrayWriter writer = new CharArrayWriter();
        copy(reader,writer);
        return writer.toCharArray();
    }

    /**
     * Reads the full contents of the specified input stream and
     * returns the characters determined by the specified character set.
     *
     * @param in The input stream from which to read the bytes.
     * @param charset Character set used to convert bytes to characters.
     * @return The characters read from the input stream.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static char[] toCharArray(InputStream in, String charset)
        throws IOException {

        CharArrayWriter writer = new CharArrayWriter();
        InputStreamReader reader = new InputStreamReader(in,charset);
        copy(reader,writer);
        return writer.toCharArray();
    }


    /**
     * Reads the character content from the specified input source and
     * returns it as a character array.  If the input source has a
     * specified character stream (a <code>Reader</code>), then that
     * is used.  If it has a specified byte stream (an
     * <code>InputStream</code>), then that is used.  If it has
     * neither a character nor a byte stream, a byte stream is created
     * from the system identifier (URL).  For both specified and
     * URL-constructed byte streams, the input source's specified
     * character set will be used if it is specified, otherwise the
     * platform default is used (as specified in {@link
     * InputStreamReader#InputStreamReader(InputStream)}).
     *
     * <P>I/O errors will arise from errors reading from a specified
     * stream, from the character set conversion on a byte stream, or
     * from errors forming or opening a URL specified as a system
     * identifier.
     *
     * <P>Note that this method does <i>not</i> close the streams within the
     * input source.  See {@link #closeInputSource(InputSource)}.
     *
     * @param in Input source from which to read.
     * @return Array of characters read from input source.
     * @throws IOException If there is an I/O error reading.
     */
    public static char[] toCharArray(InputSource in) throws IOException {
        Reader reader = null;
        InputStream inStr = null;
        reader = in.getCharacterStream();
        if (reader == null) {
            inStr = in.getByteStream();
            if (inStr == null)
                inStr = new URL(in.getSystemId()).openStream();
            String charset = in.getEncoding();
            if (charset == null)
                reader = new InputStreamReader(inStr);
            else
                reader = new InputStreamReader(inStr,charset);
        }
        return toCharArray(reader);
    }

    /**
     * Copies the content of the reader into the writer.  Blocks if
     * the reader or writer block.  Does not close the reader or
     * writer when finished.
     *
     * @param reader Reader to copy from.
     * @param writer Writer to copy to.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void copy(Reader reader, Writer writer)
        throws IOException {

        char[] buffer = new char[CHAR_COPY_BUFFER_SIZE];
        int numChars;
        while ((numChars = reader.read(buffer)) > 0)
            writer.write(buffer,0,numChars);
    }


    /**
     * Copies the content of the input stream into the output stream.
     * Blocks if the input or output streams block.  Does not close the
     * streams.
     *
     * @param in Input stream to copy from.
     * @param out Output stream to copy to.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void copy(InputStream in, OutputStream out)
        throws IOException {

        byte[] buffer = new byte[BYTE_COPY_BUFFER_SIZE];
        int numBytes;
        while ((numBytes = in.read(buffer)) > 0)
            out.write(buffer,0,numBytes);
    }

    /**
     * Closes the closeable without raising exceptions.  If
     * the specified closeable is null, the method returns
     * immediately.  If there an exception closing the closeable,
     * it is returned, otherwise null is returned.
     *
     * @param c The object to close.
     * @return Any I/O exception raised by the close.
     */
    public static IOException closeQuietly(Closeable c) {
        if (c == null) 
            return null;
        try {
            c.close();
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    /**
     * Close the specified input source.  Any I/O exceptions will be
     * caught and ignored.  The input source may be <code>null</code>,
     * as may its byte stream and/or character stream without throwing
     * an exception.
     *
     * @param in Input source to close.
     */
    public static void closeInputSource(InputSource in) {
        if (in == null) return;
        try {
            closeInputStream(in.getByteStream());
        } finally {
            // execute even if input stream threw exception
            closeReader(in.getCharacterStream());
        }
    }


    /**
     * Close an input stream.  Any I/O exceptions will be caught and
     * ignored.  Input stream may be <code>null</code> without
     * generating an exception.
     *
     * @param in Input stream to close.
     * @deprecated Use {@link #closeQuietly(Closeable)} instead.
     */
    @Deprecated
    public static void closeInputStream(InputStream in) {
        closeQuietly(in);
    }

    /**
     * Close an output stream.  Any IO exceptions will be caught and
     * logged as warnings.  Output stream may be <code>null</code>
     * without generating an exception.
     *
     * @param out Output stream to close.
     * @deprecated Use {@link #closeQuietly(Closeable)} instead.
     */
    @Deprecated
    public static void closeOutputStream(OutputStream out) {
        closeQuietly(out);
    }

    /**
     * Close a reader.  Any IO exceptions will be caught and
     * logged as warnings.  Reader may be <code>null</code>
     * without generating an exception.
     *
     * @param reader Reader to close.
     * @deprecated Use {@link #closeQuietly(Closeable)} instead.
     */
    @Deprecated
    public static void closeReader(Reader reader) {
        closeQuietly(reader);
    }

    /**
     * Close a writer.  Any IO exceptions will be caught and
     * logged as warnings.  Writer may be <code>null</code>
     * without generating an exception.
     *
     * @param writer Writer to close.
     * @deprecated Use {@link #closeQuietly(Closeable)} instead.
     */
    @Deprecated
    public static void closeWriter(Writer writer) {
        closeQuietly(writer);
    }

    /**
     * Size of buffer used for copying streams.
     */
    private static final int BYTE_COPY_BUFFER_SIZE = 1024*8;

    /**
     * Size of buffer used for copying readers to writers.
     */
    private static final int CHAR_COPY_BUFFER_SIZE
        = BYTE_COPY_BUFFER_SIZE / 2;

}
