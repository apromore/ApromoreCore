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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FilterReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

import java.util.List;
import java.util.ArrayList;

/**
 * A <code>CommaSeparatedValues</code> object represents a
 * two-dimensional array of strings which may be read and written in
 * comma-separated-value string representation.  The CSV encoding
 * is general enough to encode arbitary ragged two-dimensional arrays
 * of strings.
 *
 * <p>The CSV notation is character oriented, so any data read to or
 * from files or streams must use a character encoding.  The behavior
 * of reads and writes for unknown characters is determined by
 * {@link InputStreamReader} and {@link OutputStreamWriter}, constructed
 * with the user-specified character set.
 *
 * <p> The CSV format is row-oriented, consisting of a number of rows,
 * followed by the end of the stream.  Each row consists of a number
 * of elements separated by commas.  The rows are not required to
 * contain the same number of elements.
 *
 * <p>An element may be plain or quoted.  Plain elements consist of a
 * sequence of characters not containing any double quote
 * (<code>&quot;</code>), comma (<code>,</code>), or newline
 * (<code>\n</code>) characters.  Any leading or trailing whitespace
 * is trimmed to produce the element string.  For CSV processing, a
 * whitespace character may be either a space (<code>' '</code>) or a
 * tab (Java literal <code>'\t'</code>) character.
 *
 * <p>Quoted elements consist of a sequence of characters surrounded
 * by double quotes.  The elements between the double quotes may
 * include comma or newline characters.  Double quotes may be
 * included, but must be escaped with another double quote.  Any space
 * before or after the quote symbols is ignored, but any whitespace
 * between the element-wrapping quotes is included in the element
 * string.

 *
 * <ul>
 * <li>Wikipedia. 2007. <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Comma-separated values</a>.
 * </li>
 * <li>Creativyst Software. 2007.  <a href="http://www.creativyst.com/Doc/Articles/CSV/CSV01.htm">How To: The Comma Separated Value (CSV) File Format</a>.
 * </li>
 * </ul>

 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe3.1
 */
public class CommaSeparatedValues implements Serializable {

    static final long serialVersionUID = 8098086161027647465L;

    final String[][] mArray;

    /**
     * Construct a comma-separated values array from the specified
     * file using the specified character set.
     *
     * @param file File from which to read.
     * @param charset Encoding of characters in the stream.
     * @throws IOException If there is an underlying I/O error.
     * @throws IllegalArgumentException If the stream of characters
     * produced by the reader is not a well-defined CSV string.
     */
    public CommaSeparatedValues(File file, String charset)
        throws IOException {

        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader bufReader = null;
        try {
            in = new FileInputStream(file);
            reader = new InputStreamReader(in,charset);
            bufReader = new BufferedReader(reader);
            mArray = read(bufReader);
        } finally {
            Streams.closeQuietly(bufReader);
            Streams.closeQuietly(reader);
            Streams.closeQuietly(in);
        }
    }

    /**
     * Construct a comma-separated values array from the specified
     * input stream using the specified character set.  The stream is
     * converted to an input reader and then buffered.  The input
     * stream will be fully read and closed after the read is
     * complete or if there is an exception.
     *
     * @param in Input stream from which to read.
     * @param charset Encoding of characters in the stream.
     * @throws IOException If there is an underlying I/O error.
     * @throws IllegalArgumentException If the stream of characters
     * produced by the reader is not a well-defined CSV string.
     */
    public CommaSeparatedValues(InputStream in, String charset)
        throws IOException {

        InputStreamReader reader = null;
        BufferedReader bufReader = null;
        try {
            reader = new InputStreamReader(in,charset);
            bufReader = new BufferedReader(reader);
            mArray = read(bufReader);
        } finally {
            Streams.closeQuietly(bufReader);
            Streams.closeQuietly(reader);
            Streams.closeQuietly(in);
        }
    }

    /**
     * Construct a comma-separated values array from the specified
     * reader.  The reader will be fully read and closed after the
     * read is complete or if there is an exception. No further
     * buffering is done to the reader.
     *
     * @param reader Reader from which the CSV object will be read.
     * @throws IOException If there is an underlying I/O error.
     * @throws IllegalArgumentException If the stream of characters
     * produced by the reader is not a well-defined CSV string.
     */
    public CommaSeparatedValues(Reader reader) throws IOException {
        mArray = read(reader);
    }


    /**
     * Returns the underlying array for this comma-separated
     * values object.  Modifying this array will change the
     * values that are written out.
     *
     * @return The array underlying this CSV object.
     */
    public String[][] getArray() {
        return mArray;
    }

    /**
     * Write this comma-separated values object to the specified file
     * using the specified charset.  Characters in the elements that
     * are not encodable in the specified character set are replaced
     * with the question mark (<code>?</code>) character.
     *
     * @param file File to which this CSV object is written.
     * @param charset Character encoding to use for characters.
     * @throws IOException If there is an underlying I/O exception.
     */
    public void toFile(File file, String charset) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            toStream(out,charset);
        } finally {
            Streams.closeQuietly(out);
        }
    }

    /**
     * Write this comma-separated values object to the specified
     * output stream using the specified charset.  Characters in the
     * elements that are not encodable in the specified character set
     * are replaced with the question mark (<code>?</code>) character.
     *
     * @param out Stream to which this CSV object is written.
     * @param charset Character encoding to use for characters.
     * @throws IOException If there is an underlying I/O exception.
     */
    public void toStream(OutputStream out, String charset)
        throws IOException {

        OutputStreamWriter writer = null;
        BufferedWriter bufWriter = null;
        try {
            writer = new OutputStreamWriter(out,charset);
            bufWriter = new BufferedWriter(writer);
            toWriter(writer);
        } finally {
            Streams.closeQuietly(bufWriter);
            Streams.closeQuietly(writer);
        }
    }

    /**
     * Write this comma-separated values object to the specified
     * writer.
     *
     * @param writer Writer to which this CSV object is written.
     * @throws IOException If there is an underlying I/O exception.
     */
    public void toWriter(Writer writer) throws IOException {
        try {
            for (int i = 0; i < mArray.length; ++i) {
                if (i > 0) writer.write('\n');
                String[] row = mArray[i];
                for (int j = 0; j < row.length; ++j) {
                    if (j > 0) writer.write(',');
                    escape(writer,row[j]);
                }
            }
        } finally {
            Streams.closeQuietly(writer);
        }
    }

    /**
     * Returns a string-based representation of this comma-separated
     * values object.  Reading the string back in through a file or
     * stream will reproduce the same array of values.
     *
     * @return The string-based representation of this CSV array.
     */
    @Override
    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        try {
            toWriter(writer);
        } catch (IOException e) {
            // never thrown by char array writer
        }
        return writer.toString();
    }


    static final int COMMA = 0;
    static final int NEWLINE = 1;
    static final int EOF = 2;

    static String[][] read(Reader reader) throws IOException {
        ReportingReader reportingReader = new ReportingReader(reader);
        try {
            List<String[]> rowList = new ArrayList<String[]>();
            read(reportingReader,rowList);
            String[][] rows = new String[rowList.size()][];
            rowList.toArray(rows);
            return rows;
        } finally {
            Streams.closeQuietly(reader);
        }
    }

    static void read(ReportingReader reader, List<String[]> rowList)
        throws IOException {

        List<String> eltList = new ArrayList<String>();
        int firstChar = firstChar(reader);
        if (firstChar == -1) return; // completely empty
        while (true) {
            StringBuilder sb = new StringBuilder();
            switch (readElement(firstChar,sb,reader)) {
            case COMMA:
                eltList.add(trim(sb));
                break;
            case NEWLINE:
                eltList.add(trim(sb));
                String[] elts = eltList.<String>toArray(Strings.EMPTY_STRING_ARRAY);
                rowList.add(elts);
                eltList = new ArrayList<String>();
                break;
            case EOF:
                eltList.add(trim(sb));
                String[] elts2 = eltList.toArray(Strings.EMPTY_STRING_ARRAY);
                rowList.add(elts2);
                eltList = new ArrayList<String>();
                return;
            }

            firstChar = firstChar(reader);

        }
    }


    static boolean isSpace(int c) {
        return c == ' '
            || c == '\t';
    }

    static String trim(StringBuilder sb) {
        int end = sb.length() - 1;
        while (end >= 0 && isSpace(sb.charAt(end)))
            --end;
        return sb.substring(0,end+1);
    }

    static int firstChar(ReportingReader reader) throws IOException {
        while (true) {
            int c = reader.read();
            if (c == -1) return -1;
            if (!isSpace(c)) return c;
        }
    }

    static int readElement(int firstChar, StringBuilder sb,
                           ReportingReader reader) throws IOException {
        if (firstChar == '"')
            return readQuotedElement(sb,reader);
        if (firstChar == '\n')
            return NEWLINE;
        if (firstChar == ',')
            return COMMA;
        if (firstChar == -1)
            return EOF;
        sb.append((char)firstChar);
        return readElement(sb,reader);
    }

    static int readQuotedElement(StringBuilder sb, ReportingReader reader)
        throws IOException {

        int c;
        while ((c = reader.read()) != -1) {
            if (c == '"') {
                c = reader.read();
                if (c == '"') {
                    sb.append('"');
                    continue;
                }
                while (isSpace(c))
                    c = reader.read();
                if (c == -1)
                    return EOF;
                if (c == '\n')
                    return NEWLINE;
                if (c == ',')
                    return COMMA;
                throw reader.illegalArg("Unexpected chars after close quote.");
            }
            sb.append((char)c);

        }
        throw reader.illegalArg("EOF in quoted element.");
    }

    static int readElement(StringBuilder sb, ReportingReader reader)
        throws IOException {

        int c;
        while ((c = reader.read()) != -1) {
            if (c == '"')
                throw reader.illegalArg("Unexpected quote symbol.");
            if (c == ',')
                return COMMA;
            if (c == '\n')
                return NEWLINE;
            sb.append((char)c);
        }
        return EOF;
    }

    static void escape(Writer writer, String elt)
        throws IOException {

        for (int i = 0; i < elt.length(); ++i) {
            char c = elt.charAt(i);
            if (c == '\n' || c == ',' || c == '"') {
                quote(writer,elt);
                return;
            }
        }
        writer.write(elt); // no need to escape
    }

    static void quote(Writer writer, String elt)
        throws IOException {

        writer.write('"');
        for (int i = 0; i < elt.length(); ++i) {
            char c = elt.charAt(i);
            if (c == '"')
                writer.write('"');
            writer.write(c);
        }
        writer.write('"');
    }

    static class ReportingReader extends FilterReader {
        int mLineNumber = 0;
        int mColumnNumber = 0;
        ReportingReader(Reader in) {
            super(in);
        }
        @Override
        public int read() throws IOException {
            int c = super.read();
            if (c == '\n') {
                ++mLineNumber;
                mColumnNumber = 0;
            } else {
                ++mColumnNumber;
            }
            return c;
        }
        IllegalArgumentException illegalArg(String msg) {
            String report =
                "Line=" + mLineNumber
                + " Column=" + mColumnNumber
                + "\n"
                + msg;
            return new IllegalArgumentException(report);
        }

    }


}
