
package de.hpi.bpmn2_0.util;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

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
