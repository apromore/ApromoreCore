/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.mapper;

import org.apromore.dao.model.Group;
import org.apromore.model.GroupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class GroupMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMapper.class.getName());

    /**
     * Convert a group object to a GroupType Webservice object.
     *
     * @param group the DB Group Model
     * @return the Webservice GroupType
     */
    public static GroupType toGroupType(Group group) {
        
        GroupType groupType = new GroupType();
        groupType.setId(group.getRowGuid());
        groupType.setName(group.getName());

        return groupType;
    }
}
