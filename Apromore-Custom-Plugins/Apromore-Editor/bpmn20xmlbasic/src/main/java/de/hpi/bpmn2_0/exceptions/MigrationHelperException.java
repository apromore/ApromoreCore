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

package de.hpi.bpmn2_0.exceptions;

/**
 * @author Philipp Giese
 */
public class MigrationHelperException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -2779849528275571981L;

    public MigrationHelperException() {

    }

    public MigrationHelperException(String message) {
        super(message);
    }

    public MigrationHelperException(Throwable cause) {
        super(cause);
    }

    public MigrationHelperException(String message, Throwable cause) {
        super(message, cause);
    }

}
