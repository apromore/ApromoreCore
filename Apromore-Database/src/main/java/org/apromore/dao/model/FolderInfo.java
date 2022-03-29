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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "folder")
public class FolderInfo {

    private Integer id;

    private String name;

    private FolderInfo parentFolderInfo;

    private String parentFolderChain;

    private Set<FolderInfo> subFolders;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    @Column(name = "folder_name", unique = true, nullable = false, length = 45)
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "parentid")
    public FolderInfo getParentFolderInfo() {
	return parentFolderInfo;
    }

    @OneToMany(mappedBy = "parentFolderInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<FolderInfo> getSubFolders() {
	return this.subFolders;
    }

    public void setSubFolders(Set<FolderInfo> subFolders) {
	this.subFolders = subFolders;
    }

    public void setParentFolderInfo(FolderInfo parentFolderInfo) {
	this.parentFolderInfo = parentFolderInfo;
    }

    @Column(name = "parent_folder_chain")
    public String getParentFolderChain() {
	return parentFolderChain;
    }

    public void setParentFolderChain(String parentFolderChain) {
	this.parentFolderChain = parentFolderChain;
    }

}
