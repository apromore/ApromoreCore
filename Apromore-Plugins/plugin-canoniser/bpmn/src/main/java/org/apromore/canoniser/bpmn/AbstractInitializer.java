/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;

/**
 * Base class for constructor helper classes, providing a deferred command pattern.
 *
 * We use this while constructing documents because references won't necessarily have
 * existing referenced elements until the first traversal of the document is complete.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AbstractInitializer {

    /** Logger. */
    private final Logger logger = Logger.getAnonymousLogger();

    /** Deferred initialization commands. */
    private final List<Initialization> deferredInitializationList = new ArrayList<Initialization>();

    /** @param initialization  a command for later execution */
    public void defer(final Initialization initialization) {
        deferredInitializationList.add(initialization);
    }

    /**
     * Execute all the {@link #defer}red {@link Initialization}s.
     *
     * @throws CanoniserException if any undone tasks still remain for the BPMN document construction
     */
    public void close() throws CanoniserException {

        // Execute deferred initialization
        for (Initialization initialization : deferredInitializationList) {
            initialization.initialize();
        }
    }

    /**
     * @param message  human-legible text message about the canonisation or de-canonisation
     */
    public void warn(final String message) {
        logger.fine(message);
    }
}
