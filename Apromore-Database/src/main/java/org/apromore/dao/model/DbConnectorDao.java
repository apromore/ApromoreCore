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

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "db_connector")
@Data
@NoArgsConstructor
public class DbConnectorDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created")
    private boolean created;
    @Column(name = "connection_key")
    private String connectionKey;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "url")
    private String url;
    @Column(name = "database_schema")
    private String databaseSchema;
    @Column(name = "port")
    private String port;

    public DbConnectorDao(boolean created, String connectionKey, String username, String password, String url, String databaseSchema, String port) {
        this.created = created;
        this.connectionKey = connectionKey;
        this.username = username;
        this.password = password;
        this.url = url;
        this.databaseSchema = databaseSchema;
        this.port = port;
    }
}
