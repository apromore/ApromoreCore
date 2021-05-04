/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.ReadOnly;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Entity
@ReadOnly
@Table(name = "dag_connection")
@Configurable("dag_connection")
@Cache(expiry = 180000, size = 1000,
        coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
@Setter
public class DagConnection implements Serializable {

    private Long id;
    private String connectionId;
    private S3Destination s3Destination;
    private String tableName;
    private String query;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "connection_id")
    public String getConnectionId() {
        return connectionId;
    }

    @OneToMany(mappedBy = "s3_destination", cascade = CascadeType.ALL, orphanRemoval = true)
    public S3Destination getS3Destination() {
        return s3Destination;
    }

    @Column(name = "table_name")
    public String getTableName() {
        return tableName;
    }

    @Column(name = "query")
    public String getQuery() {
        return query;
    }
}
