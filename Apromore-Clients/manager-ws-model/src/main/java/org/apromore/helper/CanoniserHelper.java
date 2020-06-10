/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.helper;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.model.NativeMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CanoniserHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserHelper.class);

    private CanoniserHelper() {
    }

    public static NativeMetaData convertFromCanoniserMetaData(final CanoniserMetadataResult metaData) {
        NativeMetaData xmlMetaData = new NativeMetaData();
        xmlMetaData.setProcessAuthor(metaData.getProcessAuthor());
        if (metaData.getProcessCreated() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(metaData.getProcessCreated());
            try {
                xmlMetaData.setProcessCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("", e);
            }
        }
        if (metaData.getProcessLastUpdate() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(metaData.getProcessLastUpdate());
            try {
                xmlMetaData.setProcessLastUpdate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("", e);
            }
        }
        xmlMetaData.setProcessName(metaData.getProcessName());
        xmlMetaData.setProcessDocumentation(metaData.getProcessDocumentation());
        xmlMetaData.setProcessVersion(metaData.getProcessVersion());
        return xmlMetaData;
    }

}
