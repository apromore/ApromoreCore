/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.plugin.portal;

/**
 * Communication interface to display informational/warning messages on the Portal
 */
public interface MessageHandler {

    /**
     *  Display the supplied message with INFO level.
     *
     * @param message
     */
    void displayInfo(String message);

    /**
     *
     * Display the supplied message and the exception
     *
     * @param message
     * @param exception
     */
    void displayError(String message, Exception exception);

    /**
     * Display the supplied message
     *
     * @param level
     * @param message
     */
    void displayMessage(Level level, String message);

}
