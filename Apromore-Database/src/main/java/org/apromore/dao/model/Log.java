/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
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
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Log implements Serializable {

    private Integer id;
    private String name;
    private String filePath;
    private String domain;
    private String ranking;
    private String createDate;
    private boolean publicLog;

    private User user;
    private Folder folder;
    
    private Storage storage;

    private CustomCalendar calendar;

    private Set<GroupLog> groupLogs = new HashSet<>();
    private Set<UsermetadataLog> usermetadataLogs = new HashSet<>();
//    private List<ProcessBranch> processBranches = new ArrayList<>();
    private Set<Usermetadata> usermetadataSet = new HashSet<>();


    /**
     * Default constructor.
     */
    public Log() {
        super();
    }

    public Log(Integer logId) {
        id = logId;
    }


    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Column(name = "file_path")
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }

    @Column(name = "domain")
    public String getDomain() {
        return domain;
    }

    public void setDomain(final String newDomain) {
        this.domain = newDomain;
    }

    @Column(name = "ranking", length = 10)
    public String getRanking() {
        return this.ranking;
    }

    public void setRanking(final String newRanking) {
        this.ranking = newRanking;
    }

    @Column(name = "createDate")
    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final String newCreationDate) {
        this.createDate = newCreationDate;
    }


    @ManyToOne
    @JoinColumn(name = "folderId")
    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(final Folder newFolder) {
        this.folder = newFolder;
    }

    @ManyToOne
    @JoinColumn(name = "owner")
    public User getUser() {
        return this.user;
    }

    public void setUser(final User newUser) {
        this.user = newUser;
    }

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<GroupLog> getGroupLogs() {
        return this.groupLogs;
    }

    public void setGroupLogs(Set<GroupLog> newGroupLogs) {
        this.groupLogs = newGroupLogs;
    }

    /**
     * @return all the logs this user metadata is linked to
     */
    @ManyToMany
    @JoinTable(name = "usermetadata_log",
            joinColumns = @JoinColumn(name = "log_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "usermetadata_id", referencedColumnName = "id"))
    public Set<Usermetadata> getUsermetadataSet() {
        return usermetadataSet;
    }

    /**
     * @param newUsermetadataSet all the user metadata which are created on top of this
     */
    public void setUsermetadataSet(final Set<Usermetadata> newUsermetadataSet) {
        this.usermetadataSet = newUsermetadataSet;
    }
    
    
    
    @ManyToOne
    @JoinColumn(name = "storage_id",nullable = true)
    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = true)
    public CustomCalendar getCalendar() {
	return calendar;
    }

    public void setCalendar(CustomCalendar calendar) {
	this.calendar = calendar;
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

}
