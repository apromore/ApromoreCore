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

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the native type in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "native_type")
@Configurable("nativeType")
public class NativeType implements Serializable {

    private Integer id;
    private String natType;
    private String extension;

    private Set<Native> natives = new HashSet<>();
    private Set<Process> processes = new HashSet<>();


    /**
     * Default Constructor.
     */
    public NativeType() { }



    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
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



    /**
     * Get the Primary Key for the Object.
     * @return Returns the Id.
     */
    @Column(name = "nat_type", unique = true, nullable = false, length = 20)
    public String getNatType() {
        return this.natType;
    }

    /**
     * Set the natType for the Object.
     * @param newNatType The natType to set.
     */
    public void setNatType(final String newNatType) {
        this.natType = newNatType;
    }

    /**
     * Get the extension for the Object.
     *
     * @return Returns the extension.
     */
    @Column(name = "extension", length = 10)
    public String getExtension() {
        return this.extension;
    }

    /**
     * Set the extension for the Object.
     *
     * @param newExtension The extension to set.
     */
    public void setExtension(final String newExtension) {
        this.extension = newExtension;
    }

    /**
     * Get the natives for the Object.
     *
     * @return Returns the natives.
     */
    @OneToMany(mappedBy = "nativeType")
    public Set<Native> getNatives() {
        return this.natives;
    }

    /**
     * Set the natives for the Object.
     *
     * @param newNatives The natives to set.
     */
    public void setNatives(final Set<Native> newNatives) {
        this.natives = newNatives;
    }

    /**
     * Get the processes for the Object.
     *
     * @return Returns the processes.
     */
    @OneToMany(mappedBy = "nativeType")
    public Set<Process> getProcesses() {
        return this.processes;
    }

    /**
     * Set the processes for the Object.
     *
     * @param newProcesses The processes to set.
     */
    public void setProcesses(final Set<Process> newProcesses) {
        this.processes = newProcesses;
    }

}
