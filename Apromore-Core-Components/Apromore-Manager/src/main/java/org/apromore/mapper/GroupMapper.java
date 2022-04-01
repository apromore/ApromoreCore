/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.mapper;

import org.apromore.dao.model.Group;
import org.apromore.portal.model.GroupType;
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
