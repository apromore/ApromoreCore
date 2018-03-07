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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Static utility methods for processing files.
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe1.0
 */
public class Files {

    /**
     * Forbid instance construction.
     */
    private Files() {
        /* do nothing */
    }

    /**
     * Writes the specified bytes to the specified file.
     *
     * @param bytes Bytes to write to file.
     * @param file File to which characters are written.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeBytesToFile(byte[] bytes, File file)
        throws IOException {

        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        Streams.closeQuietly(out);
    }

    /**
     * Returns the array of bytes read from the specified file.
     *
     * @param file File from which to read bytes.
     * @return Bytes read from the specified file.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static byte[] readBytesFromFile(File file)
        throws IOException {

        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            String msg = "Files must be less than Integer.MAX_VALUE=" + Integer.MAX_VALUE
                + " in length."
                + " Found file.length()=" + file.length();
            throw new IllegalArgumentException(msg);
        }

        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream((int)fileLength);
        Streams.copy(in,bytesOut);
        Streams.closeQuietly(in);
        return bytesOut.toByteArray();
    }

    /**
     * Writes the characters to the specified file, encoded
     * using the specified character set.
     *
     * @param chars Characters to write to file.
     * @param file File to which characters are written.
     * @param encoding Character encoding used by file.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeCharsToFile(char[] chars,
                                        File file, String encoding)
        throws IOException {

        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(out,encoding);
        writer.write(chars);
        Streams.closeQuietly(writer);
    }


    /**
     * Writes the string to the specified file, encoded using the
     * specified character set.
     *
     * @param s String to write to file.
     * @param file File to which characters are written.
     * @param encoding Character set to use for encoding.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeStringToFile(String s, File file, String encoding)
        throws IOException {

        writeCharsToFile(s.toCharArray(),file,encoding);
    }

    /**
     * Reads all of the bytes from the specified file and convert
     * them to a character array using the specified character set.
     *
     * @param file File from which to read input.
     * @param encoding Encoding to decode bytes in file.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @throws UnsupportedEncodingException If the encoding is not
     * supported.
     * @throws IllegalArgumentException If the file is longer than
     * the maximum integer value.
     */
    public static char[] readCharsFromFile(File file, String encoding)
        throws IOException {

        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            String msg = "Files must be less than Integer.MAX_VALUE=" + Integer.MAX_VALUE
                + " in length."
                + " Found file.length()=" + file.length();
            throw new IllegalArgumentException(msg);
        }
        CharArrayWriter charWriter = new CharArrayWriter((int)fileLength); // may be bigger
        FileInputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader bufferedReader = null;
        try {
            in = new FileInputStream(file);
            inReader = new InputStreamReader(in,encoding);
            bufferedReader = new BufferedReader(inReader);
            Streams.copy(bufferedReader,charWriter);
        } finally {
            Streams.closeQuietly(bufferedReader);
            Streams.closeQuietly(inReader);
            Streams.closeQuietly(in);
        }
        return charWriter.toCharArray();
    }


    /**
     * Reads all of the bytes from the specified file and convert
     * them to a string using the specified character set.
     *
     * @param file File from which to read input.
     * @param encoding Encoding to decode bytes in file.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @throws UnsupportedEncodingException If the encoding is not supported.
     */
    public static String readFromFile(File file, String encoding)
        throws IOException {

        return new String(readCharsFromFile(file,encoding));

    }



    /**
     * Returns prefix of the file's name, defined as the
     * part of the name before the final period, or the
     * whole name if there is no final period.
     *
     * @param file File whose name's prefix is returned.
     * @return Prefix of file.
     */
    public static String baseName(File file) {
        return prefix(file.getName());
    }

    static String prefix(String name) {
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex < 0) return name;
        return name.substring(0,lastDotIndex);
    }


    /**
     * Returns the suffix of the file's name, defined
     * as the part of the name after the final period,
     * or {@code null} if there is no period in the name.
     *
     * @return The file name's extension.
     */
    public static String extension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex < 0) return null;
        return name.substring(lastDotIndex+1);
    }

    /**
     * Removes the specified file and if it is a directory, all
     * contained files.  Returns number of files removed, including
     * specified one.
     *
     * @param file File or directory to remove.
     * @return Number of files and directories removed.
     */
    public static int removeRecursive(File file) {
        if (file == null) return 0; // nothing to remove
        int descCount = removeDescendants(file);
        file.delete();
        return descCount + 1;
    }

    /**
     * Remove the descendants of the specified directory, but not the
     * directory itself.  Returns number of files removed.
     *
     * @param file File whose descendants are removed.
     * @return Number of files or directories removed.
     */
    public static int removeDescendants(File file) {
        if (!file.isDirectory()) return 0;
        int count = 0;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i)
            count += removeRecursive(files[i]);
        return count;
    }


    /**
     * Copies the contents of one file into another.  Even if there
     * is an exception, both file handles will be closed on exit.
     *
     * @param from File from which to copy.
     * @param to File to which to copy.
     * @throws IOException If there is a read or write error.
     */
    public static void copyFile(File from, File to) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] bytes = new byte[1024*4];
            int len = 0;
            while ((len = in.read(bytes)) >= 0)
                out.write(bytes,0,len);
        } finally {
            Streams.closeQuietly(in);
            Streams.closeQuietly(out);
        }
    }

    /**
     * Prefix for file names to convert them into URLs.
     */
    private static String FILE_URL_PREFIX = "file:///";

    /**
     * Name of the property for the system temporary directory.
     */
    private static final String TEMP_DIRECTORY_SYS_PROPERTY = "java.io.tmpdir";



    /**
     * A file filter that accepts files that are directories
     * that are not named "CVS", ignoring case.
     */
    public static final FileFilter NON_CVS_DIRECTORY_FILE_FILTER
        = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory()
                        && !file.getName().equalsIgnoreCase("CVS");
                }
            };

    /**
     * A file filter that accepts all normal files, as
     * specified by {@link File#isFile()}.
     */
    public static final FileFilter FILES_ONLY_FILE_FILTER
        = new FileFilter() {
                public boolean accept(File file) {
                    return file.isFile();
                }
            };


}
