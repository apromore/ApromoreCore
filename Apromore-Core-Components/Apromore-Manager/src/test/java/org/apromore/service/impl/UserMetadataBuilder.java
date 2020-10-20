package org.apromore.service.impl;

import org.apromore.dao.model.*;

import java.util.HashSet;
import java.util.Set;

public class UserMetadataBuilder {

    Group group;
    UsermetadataType usermetadataType;
    Usermetadata usermetadata;

    Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
    Set<UsermetadataLog> usermetadataLogSet = new HashSet<>();
    Set<UsermetadataProcess> usermetadataProcessSet = new HashSet<>();

    public UserMetadataBuilder() {

    }

    public UserMetadataBuilder withUsermetadataType(Integer id, String type){
        usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType(type);
        usermetadataType.setVersion(1);
        usermetadataType.setId(id);
        return this;
    }

    public UsermetadataType buildUsermetadataType() {
        return usermetadataType;
    }
}
