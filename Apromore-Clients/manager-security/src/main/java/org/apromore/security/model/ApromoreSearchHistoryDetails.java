/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.security.model;

import java.io.Serializable;

/**
 * Just a shell class so Devs doesn't confused between the DAO Models and the Security Models.
 *
 * @author Cameron James
 */
public class ApromoreSearchHistoryDetails implements Serializable {

    private Integer id;
    private String searchString;

    /**
     * Construct the object. This only contains a String but could potentially contain more.
     * @param id the id and position of the search string.
     * @param searchString the search string the user used.
     */
    public ApromoreSearchHistoryDetails(Integer id, String searchString) {
        this.id = id;
        this.searchString = searchString;
    }

    /**
     * returns the Id and the search position.
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * sets the id and the search position.
     * @param id the id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return the search String.
     * @return the search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Sets the search string the user used.
     * @param searchString the search string
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
