/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.service.model;

/**
 * Simple Java Bean to hold the name and value pair.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class NameValuePair {

    private String name;
    private String value;
    private Double version;

    /**
     * Default Constructor.
     */
    public NameValuePair() {
    }

    /**
     * Public Constructor.
     * @param name  the name
     * @param value the value
     */
    public NameValuePair(final String name, final String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * Public Constructor.
     * @param name  the name
     * @param value the value
     * @param version the version
     */
    public NameValuePair(final String name, final String value, final Double version) {
        this.name = name;
        this.value = value;
        this.version = version;
    }

    /**
     * return the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * set the name.
     * @param newName the name
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * return the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * set the value.
     * @param newValue the value
     */
    public void setValue(final String newValue) {
        this.value = newValue;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }
}
