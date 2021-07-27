/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.integration;

import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ShareFeatureUnitTest extends BaseTest {

    UserManagementBuilder builder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UsermetadataTypeRepository usermetadataTypeRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UsermetadataRepository usermetadataRepository;


    @Before
    public final void setUp() {
        builder = new UserManagementBuilder();
    }

    @Test
    @Ignore
    public void testSaveUser() {
        // given
        Group group = groupRepository.saveAndFlush(builder.withGroup("testGroup1", "USER").buildGroup());
        Role role = roleRepository.saveAndFlush(builder.withRole("testRole").buildRole());
        User user = builder.withGroup(group).withRole(role).withMembership("n@t.com").withUser("TestUser", "first",
                "last", "org").buildUser();
        // when
        User savedUSer = userRepository.saveAndFlush(user);
        // then
        assertThat(savedUSer.getId()).isNotNull();
        assertThat(savedUSer.getMembership().getEmail()).isEqualTo(user.getMembership().getEmail());
    }

    @Test
    @Ignore
    public void insertUsermetadataTest() {
//	 		Given
        Usermetadata um = builder.withUserMetaDataType("test Type", 1).withUserMetaData("Test", "test")
                .buildUserMetaData();

        UsermetadataType type = usermetadataTypeRepository.save(um.getUsermetadataType());
        um.setUsermetadataType(type);

//	        When
        um = usermetadataRepository.saveAndFlush(um);
        Usermetadata umExpected = usermetadataRepository.findById(um.getId()).get();

//	        Then
        assertThat(um.getId()).isNotNull();
        assertThat(umExpected.getId()).isNotNull();
        assertThat(umExpected.getCreatedTime()).isEqualTo(um.getCreatedTime());

    }

}
