/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashSet;

/**
 * @author Sven Wagner-Boysen
 *         <p/>
 *         Removes XML-invalid string sequences.
 */
public class EscapingStringAdapter extends XmlAdapter<String, String> {

    //	public static final char substitute = '\uFFFD';
    private static final HashSet<Character> illegalChars;

    static {
        final String escapeString = "\u0000\u0001\u0002\u0003\u0004\u0005"
                + "\u0006\u0007\u0008\u000B\u000C\u000E\u000F\u0010\u0011\u0012"
                + "\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C"
                + "\u001D\u001E\u001F\uFFFE\uFFFF";

        illegalChars = new HashSet<Character>();
        for (int i = 0; i < escapeString.length(); i++) {
            illegalChars.add(escapeString.charAt(i));
        }
    }

    private boolean isIllegal(char c) {
        return illegalChars.contains(c);
    }

    /**
     * Deletes all illegal characters in the given string. If no illegal characters were
     * found, no copy is made and the given string is returned.
     *
     * @param string
     * @return
     */
    private String escapeCharacters(String string) {
        if (string == null) {
            return string;
        }

        StringBuffer copyBuffer = null;
        boolean copied = false;
        for (int i = 0; i < string.length(); i++) {
            if (isIllegal(string.charAt(i))) {
                if (!copied) {
                    copyBuffer = new StringBuffer(string);
                    copied = true;
                }
                copyBuffer.deleteCharAt(i);
            }
        }
        return copied ? copyBuffer.toString() : string;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
      */
    // @Override
    public String marshal(String text) throws Exception {
        return escapeCharacters(text);
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
      */
    // @Override
    public String unmarshal(String text) throws Exception {
        return text;
    }

}
