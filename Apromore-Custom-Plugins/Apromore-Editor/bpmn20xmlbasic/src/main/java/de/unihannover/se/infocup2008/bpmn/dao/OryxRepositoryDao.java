package de.unihannover.se.infocup2008.bpmn.dao;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides access to the oryx repository
 *
 * @author Team Royal Fawn
 */
public class OryxRepositoryDao {

    private static final Pattern ERDF_REGEX = Pattern.compile(
            "<body style=\"overflow:hidden;\">((?:.*\\s)*)<\\/body>",
            Pattern.CASE_INSENSITIVE & Pattern.DOTALL);

    /**
     * Reads the eRDF from the model in the oryx repository identified by the id
     *
     * @param oryxId the id of the model in the oryx repository
     * @return the eRDF or <code>null</code> in case of any errors
     */
    public static String getERDFFromOryx(String oryxId) {

        try {
            URL url = new URL("http://oryx-editor.org/backend/poem/model/"
                    + oryxId + "/self");

            BufferedReader in = new BufferedReader(new InputStreamReader(url
                    .openStream()));

            StringBuffer buffer = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
                buffer.append("\n");
            }
            in.close();

            // System.out.println(buffer);

            Matcher m = ERDF_REGEX.matcher(buffer);
            if (m.find()) {
                MatchResult r = m.toMatchResult();
                String eRDF = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                        + r.group(1);
                return eRDF;
            }
        } catch (Exception e) {
            return null; // in case of errors just return null
        }
        return null; // in case of errors just return null
    }
}
