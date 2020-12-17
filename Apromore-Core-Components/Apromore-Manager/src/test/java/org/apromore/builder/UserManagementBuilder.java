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
package org.apromore.builder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupUsermetadata;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Group.Type;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.UsermetadataLog;
import org.apromore.dao.model.UsermetadataType;

public class UserManagementBuilder {

	Group group;
	Set<Group> groups=new HashSet<Group>();

	User user;

	Set<Group> userGroups=new HashSet<Group>();
	 List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
	 List<UsermetadataLog> usermetadataLogSet = new ArrayList();

	UsermetadataType usermetadataType;
	Usermetadata usermetadata;

	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public UserManagementBuilder() {

	}

	public UserManagementBuilder withUser(String username, String rowGuid)
	{
	    user=new User();
	    user.setGroups(userGroups);
	    user.setUsername(username);
	    user.setRowGuid(rowGuid);

	    return this;

	}

	public UserManagementBuilder withDummyUserGroup() {
	    userGroups.add(new Group());
	    return this;
	}

	public UserManagementBuilder withGroup(String name, String type) {
		group = new Group();
		group.setName("testGroup");
		group.setType(Type.valueOf(type));
		groups.add(group);
		return this;
	}

	public Group buildGroup() {

		return groups.iterator().next();

	}

	public User buildUser() {
	    // TODO Auto-generated method stub
	    return user;
	}

	public UserManagementBuilder withUserMetaDataType(String type, int version, boolean isValid,int id) {
	     usermetadataType = new UsermetadataType();
	     usermetadataType.setIsValid(isValid);
	     usermetadataType.setType(type);
	     usermetadataType.setVersion(version);
	     usermetadataType.setId(id);

	        return this;

	}

	public UsermetadataType getUserMetaDataType() {
	    return usermetadataType;
	}

	public UserManagementBuilder withUserMetaData(String content, String creator, boolean isValid, int revision,
		UsermetadataType usermetadataType1) {
	    usermetadata=new Usermetadata();

	    usermetadata.setContent(content);
	    usermetadata.setCreatedBy(creator);
	    usermetadata.setCreatedTime( dateFormat.format(new Date()));
	    usermetadata.setIsValid(isValid);
	    usermetadata.setRevision(revision);
	    usermetadata.setUsermetadataType(usermetadataType1);
	    usermetadata.setUpdatedBy(creator);
	    usermetadata.setUpdatedTime( dateFormat.format(new Date()));
	    return this;
	}

	public Usermetadata getUserMetaData() {
	    // TODO Auto-generated method stub
	    return usermetadata;
	}


	public UserManagementBuilder withGroupUserMetaData(Group group, Usermetadata um, boolean read, boolean write, boolean own) {
	    groupUsermetadataList.add(new GroupUsermetadata(group, um, read, write, own));
	    return this;
	}

	public List<GroupUsermetadata> getGroupUserMetaDataList() {
	    // TODO Auto-generated method stub
	    return groupUsermetadataList;
	}

	public UserManagementBuilder withNewUserMetaDataLogList() {
	   usermetadataLogSet = new ArrayList<>();
	    return this;
	}

	public UserManagementBuilder withUserMetaDataLog(Usermetadata um, Log log) {
	    usermetadataLogSet.add(new UsermetadataLog(um, log));
	    return this;
	}

	public List<UsermetadataLog> getUserMetaDataLogList() {

	    return usermetadataLogSet;
	}

}