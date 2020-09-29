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
package org.apromore.dao.jpa.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import org.apromore.BaseTestClass;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserManagementTest extends BaseTestClass {

	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;


	UserManagementBuilder builder;

	@Before
	public void Setup() {
		builder = new UserManagementBuilder();

	}

	@Test
	public void testSaveGroup() {
		// given
		Group userGroup = builder.withGroup("testGroup", "USER").buildGroup();

		// when
		Group savedUSerGroup = groupRepository.saveAndFlush(userGroup);
		// then
		assertThat(savedUSerGroup.getId()).isNotNull();
		assertThat(savedUSerGroup.getName()).isEqualTo(userGroup.getName());
		assertThat(savedUSerGroup.getType()).isEqualTo(userGroup.getType());
		
		Group group=groupRepository.findByName("testGroup");
			
	}
	
	@Test
	public void testSaveUser() {
		// given
		Group group=groupRepository.saveAndFlush(builder.withGroup("testGroup", "USER").buildGroup());
		Role role=roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
		User user = builder.withGroup(group)
				.withRole(role)
				.withMembership("n@t.com")
				.withUser("TestUser", "first", "last", "org")
				.buildUser();
		// when
		User savedUSer = userRepository.saveAndFlush(user);
		// then
		assertThat(savedUSer.getId()).isNotNull();
		assertThat(savedUSer.getMembership().getEmail()).isEqualTo(user.getMembership().getEmail());
	}

}