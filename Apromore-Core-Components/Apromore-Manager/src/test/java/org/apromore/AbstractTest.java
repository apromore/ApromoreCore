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
package org.apromore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Workspace;
import org.apromore.portal.helper.Version;
import org.easymock.EasyMockSupport;

public class AbstractTest extends EasyMockSupport {
    public Process createProcess(User user, NativeType natType, Folder folder) {
        Process process = new Process();
        process.setId(1234);
        process.setFolder(folder);
        process.setCreateDate("1.1.2020");
        process.setDomain("domain");
        process.setName("ProcessName");
        process.setUser(user);
        process.setNativeType(natType);
        return process;
    }
    
    public ProcessBranch createBranch(Process process) {
        ProcessBranch branch = new ProcessBranch();
        branch.setId(1234);
        branch.setBranchName("BranchName");
        branch.setProcess(process);
        branch.setCreateDate("1.1.2020");
        branch.setLastUpdateDate("1.1.2020");
        return branch;
    }
    
    public void addProcessModelVersions(ProcessBranch branch, ProcessModelVersion...pmvs) {
        if (pmvs != null && pmvs.length > 0) {
            branch.setProcessModelVersions(new ArrayList<>(Arrays.asList(pmvs)));
        }
    }
    
    public ProcessModelVersion createPMV(ProcessBranch branch, Native nativeDoc, Version version) {
        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setId(123);
        pmv.setOriginalId("123");
        pmv.setCreateDate("1.1.2020");
        pmv.setLastUpdateDate("1.1.2020");
        pmv.setProcessBranch(branch);
        pmv.setLastUpdateDate("");
        pmv.setNativeType(nativeDoc.getNativeType());
        pmv.setNativeDocument(nativeDoc);
        pmv.setVersionNumber(version.toString());
        pmv.setNumEdges(0);
        pmv.setNumVertices(0);
        pmv.setLockStatus(Constants.NO_LOCK);
        return pmv;
    }
    
    public Native createNative(NativeType nativeType, String nativeContent) {
        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(nativeContent);
        nat.setLastUpdateDate("1.1.2020");
        return nat;
    }

    public NativeType createNativeType() {
        NativeType nat = new NativeType();
        nat.setExtension("ext");
        nat.setNatType("BPMN 2.0");
        nat.setExtension("bpmn");
        return nat;
    }
    
    public Version createVersion(String versionNumber) {
        Version version = new Version(versionNumber);
        return version;
    }

    public User createUser(String userName, Group group, Set<Group> groups, Set<Role> roles) {
        User user = new User();
        user.setId(123);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setUsername(userName);
        user.setOrganization("Apromore");
        user.setCountry("Australia");
        user.setSubscription("UserSubscription");
        user.setRowGuid("UserRowGuid");
        user.setGroup(group);
        user.setRole("UserRole");
        user.setRoles(roles);
        user.setGroups(groups);
        return user;
    }
    
    public Permission createPermission() {
        Permission p = new Permission();
        p.setId(123);
        p.setDescription("PermissionDesc");
        p.setName("PermissionName");
        p.setRowGuid("PermissionRowGuid");
        return p;
    }
    
    public Role createRole(Set<Permission> permissions) {
        Role role = new Role();
        role.setId(123);
        role.setName("RoleName");
        role.setRowGuid("RoleGuid");
        role.setPermissions(permissions);
        role.setDescription("RoleDescription");
        return role;
    }
    
    public Group createGroup(Integer groupId, Group.Type groupType) {
        Group group = new Group();
        group.setRowGuid("groupGUID");
        group.setName("GroupName");
        group.setId(groupId);
        group.setType(groupType);
        return group;
    }
    
    public <E> Set<E> createSet(E...arrayT) {
        return new HashSet<E>(Arrays.asList(arrayT));
    }
    
    public GroupProcess createGroupProcess(Group group, Process process, 
                                        boolean hasOwnership, boolean hasRead, boolean hasWrite) {
        GroupProcess gp = new GroupProcess();
        gp.setId(123);
        gp.setGroup(group);
        gp.setProcess(process);
        AccessRights accessRights=new AccessRights(hasRead,hasWrite,hasOwnership);
        gp.setAccessRights(accessRights);
        return gp;
    }
    
    public Log createLog(User user, Folder folder) {
        Log log = new Log();
        log.setId(123);
        log.setName("LogName");
        log.setDomain("Domain");
        log.setRanking("Ranking");
        log.setFilePath("FileTimestamp");
        log.setUser(user);
        log.setFolder(folder);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        log.setCreateDate(now);
        return log;
    }
    
    public Folder createFolder(String name, Folder parentFolder, Workspace workspace) {
        Folder folder = new Folder();
        folder.setId(123);
        folder.setName(name);
        folder.setParentFolder(parentFolder);
        folder.setWorkspace(workspace);
        folder.setDateCreated(new Date());
        folder.setDateModified(new Date());
        folder.setDescription("Description");
        folder.setCreatedBy(new User());
        folder.setModifiedBy(new User());
        return folder;
    }
    
    public Workspace createWorkspace(User createdByUser) {
        Workspace wp = new Workspace();
        wp.setCreatedBy(createdByUser);
        wp.setDateCreated(new Date());
        wp.setId(123);
        wp.setName("Workspace1");
        wp.setDescription("Description");
        return wp;
    }
}
