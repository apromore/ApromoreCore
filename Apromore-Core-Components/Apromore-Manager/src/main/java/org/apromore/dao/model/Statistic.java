/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.util.UuidAdapter;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "statistic",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"count"})
        }
)
@Configurable("statistic")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Statistic implements Serializable {

    private Long count;
    private byte[] id;
    private byte[] pid;
    private Integer logid;
    private String stat_key;
    private String stat_value;

    /**
     * Default constructor.
     */
    public Statistic() {
        super();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "count", unique = true, nullable = false)
    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }

    @Column(name = "id", nullable = false, length = 16)
    public byte[] getId() {
        return this.id;
    }
    public void setId(final byte[] id) {
        this.id = id;
    }

    @Column(name = "pid", length = 16)
    public byte[] getPid() {
        return pid;
    }
    public void setPid(final byte[] pid) {
        this.pid = pid;
    }

    @Column(name = "logid")
    public Integer getLogid() {
        return logid;
    }
    public void setLogid(final Integer logid) {
        this.logid = logid;
    }

    @Column(name = "stat_key", length = 1023)
    public String getStat_key() {
        return stat_key;
    }
    public void setStat_key(final String stat_key) {
        this.stat_key = stat_key;
    }

    @Column(name = "stat_value", length = 1023)
    public String getStat_value() {
        return stat_value;
    }
    public void setStat_value(final String stat_value) {
        this.stat_value = stat_value;
    }

}
