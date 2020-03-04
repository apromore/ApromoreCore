/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
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

package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Generator for identifier strings.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class IdFactory {

   /**
    * Regular expression matching an XML NCName.
    *
    * @see <a href="http://www.w3.org/TR/xml-names11/#NT-NCName">Namespaces in XML 1.1</a>
    */
   public static final Pattern NCNAME = Pattern.compile("[\\p{Alpha}_][\\p{Alnum}-_\\x2E]*");
 //Pattern.compile("[\\p{Alpha}_][\\p{Alnum}\\x2e-_]*");

    /**
     * Set of all identifiers in the namespace.
     *
     * This set never contains <code>null</code>.
     */
    private final Set<String> idSet = new HashSet<String>();

    /**
     * Issue a new identifier.
     *
     * Generated identifiers are guaranteed to be XML NCNames and not to be <code>null</code>.
     *
     * @param suggestedId  a suggested value for the identifier, or <code>null</code> to indicate no preference
     * @return a new NCName, never <code>null</code>
     */
    public synchronized String newId(final String suggestedId) {

        String id = suggestedId;

        // Because UUIDs are based on timestamps, the following loop is sure to terminate
        while (idSet.contains(id) || id == null || !NCNAME.matcher(id).matches()) {
            id = "id-" + UUID.randomUUID().toString();  // prefix added to ensure NCName validity
            assert NCNAME.matcher(id).matches();
        }
        assert !idSet.contains(id);
        assert id != null;

        idSet.add(id);
        return id;
    }
}
