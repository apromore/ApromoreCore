/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

package org.apromore.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "search_history",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "search"})})
@Configurable("searchHistory")
public class SearchHistory implements Serializable {

    private Integer id;
    private Integer index;
    private String search;

    private User user;


    /**
     * Default Constructor.
     */
    public SearchHistory() {
    }



    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    @Column(name = "position")
    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(final Integer newIndex) {
        this.index = newIndex;
    }

    @Column(name = "search", length = 200)
    public String getSearch() {
        return this.search;
    }

    public void setSearch(final String newSearch) {
        this.search = newSearch;
    }

    @ManyToOne
    @JoinColumn(name = "userid")
    public User getUser() {
        return this.user;
    }

    public void setUser(User newUser) {
        this.user = newUser;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other == null || !SearchHistory.class.equals(other.getClass())) {
            return false;
        }
        return (other instanceof SearchHistory) && id.equals(((SearchHistory) other).id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }
}
