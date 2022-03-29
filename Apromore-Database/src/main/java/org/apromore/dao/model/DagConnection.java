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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.beans.factory.annotation.Configurable;
import lombok.Setter;

@Entity
@Table(name = "dag_connection")
@Configurable("dag_connection")

@Setter
public class DagConnection implements Serializable {

  private Long id;
  private String connectionId;
  private S3Destination s3Destination;
  private String tableName;
  private String query;
  private JobDao job;

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

  @Embedded
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

  @ManyToOne
  @JoinColumn(name = "job_id")
  public JobDao getJob() {
    return job;
  }
}
