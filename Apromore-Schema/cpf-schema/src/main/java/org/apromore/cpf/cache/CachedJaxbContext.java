/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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

package org.apromore.cpf.cache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.util.Hashtable;

/**
 * A Cache specially used for JAXB Contexts.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@SuppressWarnings("rawtypes")
public class CachedJaxbContext {

    private static volatile Hashtable<String, JAXBCachedEntry> jaxbInstanceCache = new Hashtable<>();

    public synchronized static JAXBContext getJaxbContext(String type, ClassLoader classLoader) throws JAXBException {
        JAXBCachedEntry cache = jaxbInstanceCache.get(type);
        if (cache == null) {
            cache = new JAXBCachedEntry(type, classLoader);
            jaxbInstanceCache.put(type, cache);
            return cache.getContext();
        }
        return cache.getContext();
    }


    // Important thing is that JAXBContext is itself thread safe so we should not worry.
    public static Unmarshaller createUnMarshaller(String type, ClassLoader classLoader) throws JAXBException {
        JAXBContext context = getJaxbContext(type, classLoader);
        return context.createUnmarshaller();
    }

    // Important thing is that JAXBContext is itself thread safe so we should not worry.
    public static Marshaller createMarshaller(String type, ClassLoader classLoader) throws JAXBException {
        JAXBContext context = getJaxbContext(type, classLoader);
        return context.createMarshaller();
    }

}