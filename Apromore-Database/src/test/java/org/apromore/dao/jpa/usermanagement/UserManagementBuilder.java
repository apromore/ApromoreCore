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
package org.apromore.dao.jpa.usermanagement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.Group.Type;
import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.UsermetadataType;

public class UserManagementBuilder {

	Group group;

	User user;
	Set<Role> roles=new HashSet<Role>();
	Set<Group> groups=new HashSet<Group>();
	Membership membership;
	
	Usermetadata usermetadata;
	UsermetadataType usermetadataType;

	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");		
	public UserManagementBuilder() {

	}

	public UserManagementBuilder withUser(String username,String first,String last,String org) {

		user=new User();
		user.setFirstName(first);
		user.setLastName(last);
		user.setUsername(username);
		user.setOrganization(org);
		user.setDateCreated(new Date());
		return this;

	}
	
	public UserManagementBuilder withUserMetadataType(String type,int version) {
		  usermetadataType = new UsermetadataType();
	        usermetadataType.setIsValid(true);
	        usermetadataType.setType(type);
	        usermetadataType.setVersion(version);
		return this;

	}

	public UserManagementBuilder withRole(String name) {
		Role r = new Role();
		r.setName(name);
		r.setDescription("test Role");
		roles.add(r);
		return this;
	}

	public UserManagementBuilder withMembership(String email) {

		membership=new Membership();
		membership.setAnswer("testAnswer");
		membership.setQuestion("testQ");
		membership.setEmail(email);
		membership.setMobilePin("123");
		membership.setPassword("123");
		membership.setSalt("123");
		membership.setDateCreated(new Date());
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
	
	public Role buildRole() {
		return roles.iterator().next();
	}
	
	public User buildUser()
	{
		user.setGroups(groups);
		user.setGroup(groups.iterator().next());
		user.setMembership(membership);
		membership.setUser(user);
		user.setRoles(roles);
		return user;
		
	}

	public UserManagementBuilder withGroup(Group group) {
		groups.clear();
		groups.add(group);
		return this;
	}
	
	public UserManagementBuilder withRole(Role role) {
		roles.clear();
		roles.add(role);
		return this;
	}

	public UserManagementBuilder withUserMetaData(String content,String createdBy) {
			usermetadata = new Usermetadata();
			usermetadata.setContent(content);
			usermetadata.setCreatedBy(createdBy);
			usermetadata.setCreatedTime(dateFormat.format(new Date()));
			usermetadata.setIsValid(true);	        
			usermetadata.setUsermetadataType(usermetadataType);
			usermetadata.setUpdatedBy(createdBy);
			usermetadata.setUpdatedTime(dateFormat.format(new Date()));
		return this;
	}

	public Usermetadata buildUserMetaData() {
		// TODO Auto-generated method stub
		return usermetadata;
	}

}
