/*-
 * #%L
 * This file is part of "Apromore Core".
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

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "usermetadata_process")
@Configurable("usermetadata_process")
public class UsermetadataProcess implements Serializable, Cloneable {
    /**
     *
     */
    private Integer id;
    /**
     * FK USERMETADATA ID
     */
    private Usermetadata usermetadata;
    /**
     * FK PROCESS ID
     */
    private Process process;

    /**
     * Default Constructor.
     */
    public UsermetadataProcess() {
    }

    /**
     * Convenient constructor.
     */
    public UsermetadataProcess(Usermetadata newUsermetadata, Process newProcerss) {
        this.usermetadata = newUsermetadata;
        this.process = newProcerss;
    }

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * FK USERMETADATA ID
     */
    @ManyToOne
    @JoinColumn(name = "usermetadata_id")
    public Usermetadata getUsermetadata() {
        return this.usermetadata;
    }

    /**
     * FK USERMETADATA ID
     */
    public void setUsermetadata(Usermetadata usermetadata) {
        this.usermetadata = usermetadata;
    }

    /**
     * FK PROCESS ID
     */
    @ManyToOne
    @JoinColumn(name = "process_id")
    public Process getProcess() {
        return this.process;
    }

    /**
     * FK PROCESS ID
     */
    public void setProcess(Process process) {
        this.process = process;
    }
}
