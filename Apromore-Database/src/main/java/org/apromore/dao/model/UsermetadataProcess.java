/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import javax.persistence.*;
import java.io.Serializable;


@Table(name = "usermetadata_process")
public class UsermetadataProcess implements Serializable, Cloneable {
    /**
     *
     */
    private Integer id;
    /**
     * FK USERMETADATA ID
     */
    private Integer usermetadataId;
    /**
     * FK PROCESS ID
     */
    private Integer processId;

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
    public Integer getUsermetadataId() {
        return this.usermetadataId;
    }

    /**
     * FK USERMETADATA ID
     */
    public void setUsermetadataId(Integer usermetadataId) {
        this.usermetadataId = usermetadataId;
    }

    /**
     * FK PROCESS ID
     */
    @ManyToOne
    @JoinColumn(name = "process_id")
    public Integer getProcessId() {
        return this.processId;
    }

    /**
     * FK PROCESS ID
     */
    public void setProcessId(Integer processId) {
        this.processId = processId;
    }
}
