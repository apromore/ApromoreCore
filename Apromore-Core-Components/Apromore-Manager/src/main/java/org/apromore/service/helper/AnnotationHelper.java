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

package org.apromore.service.helper;

/**
 * List of methods that are used to help with getting names and details of annotations.
 *
 * @author Cameron James
 */
public class AnnotationHelper {

    private static final String ANNOTATION_START = "Annotations - ";


    /**
     * Private Constructor as this is a helper class.
     */
    private AnnotationHelper() {}


    /**
     * Takes an annotation name and extracts the real name, some annotations have "Annotation - " at the start
     * but most are stored in the db without this text.
     * @param originalName the name that is usually sent from the UI portal.
     * @return the name that is used for storage in the DB.
     */
    public static String getAnnotationName(String originalName) {
        String result;
        if (originalName.startsWith(ANNOTATION_START)) {
            result = originalName.replaceFirst(ANNOTATION_START, "");
        } else {
            result = originalName;
        }
        return result;
    }
}
