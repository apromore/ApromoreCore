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
