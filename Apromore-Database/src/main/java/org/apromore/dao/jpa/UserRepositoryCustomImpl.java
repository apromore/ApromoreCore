/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.dao.jpa;

import org.apromore.dao.UserRepositoryCustom;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.UserRepositoryCustom#findUserByEmail(String)
     * {@inheritDoc}
     */
    @Override
    public User findUserByEmail(final String email) {
        Query query = em.createQuery("SELECT u FROM User u JOIN u.membership m WHERE m.email = :email");
        query.setParameter("email", email);
        User user = (User) query.getSingleResult();

        if (user != null){
            return user;
        }
        return null;
    }

    /**
     * @see org.apromore.dao.UserRepositoryCustom#hasAccess(String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean hasAccess(String userId, String permissionId) {
        Query query = em.createQuery("SELECT p FROM User usr JOIN usr.roles r JOIN r.permissions p WHERE usr.rowGuid = :userId " +
                "AND p.rowGuid = :permId");
        query.setParameter("userId", userId);
        query.setParameter("permId", permissionId);
        List<Permission> permissions = query.getResultList();

        return permissions != null && permissions.size() > 0;
    }



    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
