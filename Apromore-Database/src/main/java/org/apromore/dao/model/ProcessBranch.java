/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * ProcessBranch generated by hbm2java
 */
@Entity
@Table(name = "process_branch")
@Configurable("processBranch")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ProcessBranch implements Serializable {

    private Integer id;
    private String branchName;
    private String createDate;
    private String lastUpdateDate;

    private Process process;
    private ProcessModelVersion currentProcessModelVersion;
    private ProcessModelVersion sourceProcessModelVersion; //not used
    private List<ProcessModelVersion> processModelVersions = new ArrayList<>();


    /**
     * Public Constructor.
     */
    public ProcessBranch() {
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


    @Column(name = "branch_name", length = 1000)
    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(final String newBranchName) {
        this.branchName = newBranchName;
    }

    @Column(name = "createDate")
    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(final String newCreationDate) {
        this.createDate = newCreationDate;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }


    @ManyToOne
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(final Process newProcess) {
        this.process = newProcess;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "currentProcessModelVersion")
    public ProcessModelVersion getCurrentProcessModelVersion() {
        return this.currentProcessModelVersion;
    }

    public void setCurrentProcessModelVersion(final ProcessModelVersion newCurrentProcessModelVersion) {
        this.currentProcessModelVersion = newCurrentProcessModelVersion;
    }

    //Unused
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sourceProcessModelVersion")
    public ProcessModelVersion getSourceProcessModelVersion() {
        return this.sourceProcessModelVersion;
    }

    //Unused
    public void setSourceProcessModelVersion(final ProcessModelVersion newSourceProcessModelVersion) {
        this.sourceProcessModelVersion = newSourceProcessModelVersion;
    }

    @OneToMany(mappedBy = "processBranch")
    @OrderBy("versionNumber ASC")
    public List<ProcessModelVersion> getProcessModelVersions() {
        return this.processModelVersions;
    }

    public void setProcessModelVersions(final List<ProcessModelVersion> newProcessModelVersions) {
        this.processModelVersions = newProcessModelVersions;
    }
    
    @Override
    public ProcessBranch clone() {
        ProcessBranch newBranch = new ProcessBranch();
        newBranch.setBranchName(this.getBranchName());
        newBranch.setProcess(this.getProcess());
        newBranch.setProcessModelVersions(this.getProcessModelVersions());
        newBranch.setCurrentProcessModelVersion(this.getCurrentProcessModelVersion());
        
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        newBranch.setCreateDate(now);
        newBranch.setLastUpdateDate(now);
        
        return newBranch;
    }

}


