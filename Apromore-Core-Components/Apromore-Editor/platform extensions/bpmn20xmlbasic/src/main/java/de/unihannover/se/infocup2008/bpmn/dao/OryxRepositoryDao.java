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
package de.unihannover.se.infocup2008.bpmn.dao;

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
