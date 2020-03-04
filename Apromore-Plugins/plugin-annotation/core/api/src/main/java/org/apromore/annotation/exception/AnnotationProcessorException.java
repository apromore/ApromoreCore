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
package org.apromore.annotation.exception;

import org.apromore.plugin.exception.PluginException;

/**
 * Thrown if a AnnotationPostProcessor can not proceed with the updates.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 *
 */
public class AnnotationProcessorException extends PluginException {

    public AnnotationProcessorException() {
        super();
    }

    public AnnotationProcessorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AnnotationProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AnnotationProcessorException(final String message) {
        super(message);
    }

    public AnnotationProcessorException(final Throwable cause) {
        super(cause);
    }

}
