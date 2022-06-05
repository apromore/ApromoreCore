/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "log",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name", "folderId"})
    }
)
@Configurable("log")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Log implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "domain")
    private String domain;

    @Column(name = "ranking", length = 10)
    private String ranking;

    @Column(name = "createdate")
    private String createDate;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User user;

    @ManyToOne
    @JoinColumn(name = "folderid")
    private Folder folder;

    @ManyToOne
    @JoinColumn(name = "storage_id", nullable = true)
    private Storage storage;

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = true)
    private CustomCalendar calendar;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<GroupLog> groupLogs = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usermetadata_log",
        joinColumns = @JoinColumn(name = "log_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "usermetadata_id", referencedColumnName = "id"))
    private Set<Usermetadata> usermetadataSet = new HashSet<>();

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<LogicalLogAttribute> logAttributes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "log_physical_log",
        joinColumns =
            { @JoinColumn(name = "log_id", referencedColumnName = "id") },
        inverseJoinColumns =
            { @JoinColumn(name = "physical_log_id", referencedColumnName = "id") })
    private PhysicalLog physicalLog;

    public Log(Integer logId) {
        id = logId;
    }

    @Override
    public Log clone() {
        Log newLog = new Log();
        newLog.setName(this.getName());
        newLog.setDomain(this.getDomain());
        newLog.setRanking(this.getRanking());
        newLog.setFilePath(this.getFilePath());
        newLog.setUser(this.getUser());
        newLog.setFolder(this.getFolder());

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        newLog.setCreateDate(now);

        return newLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Log log = (Log) o;
        return id != null && Objects.equals(id, log.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
