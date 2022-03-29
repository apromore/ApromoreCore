/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2014 Pasquale Napoli.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.util;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.commons.io.IOUtils;

/**
 * Helps with debugging and seeing the data travel between services.
 */
public class StreamUtil {

    private static final String ANF_URI = "org.apromore.anf";
    private static final String CPF_URI = "org.apromore.cpf";
    private static final String XPDL_URI = "org.wfmc._2009.xpdl2";

    /**
     * Convert a InputStream to a String
     *
     * @param is the inputStream to convert
     * @return the string for that input stream
     */
    public static String convertStreamToString(final InputStream is) {
        return inputStream2String(is);
    }

    /**
     * Convert a DataHandler to a String
     *
     * @param dh the DataHandler to convert
     * @return the string for that DataHandler
     */
    public static String convertStreamToString(final DataHandler dh) {
        try {
            return inputStream2String(dh.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataHandler: " + e.toString();
        }
    }

    /**
     * Convert a DataHandler to a String
     *
     * @param ds the DataSource to convert
     * @return the string for that DataSource
     */
    public static String convertStreamToString(final DataSource ds) {
        try {
            return inputStream2String(ds.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataSource: " + e.toString();
        }
    }

    /**
    * Converts an input stream to a string.
    *
    * @param is the input stream
    * @return the String that was the input stream
    */
    public static String inputStream2String(final InputStream is) {
        if (is != null) {
            try {
                return IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                return "error in reading the input streams: " + e.toString();
            }
        }
        return "";
    }

}
