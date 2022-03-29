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

package org.apromore.dao;

import java.util.List;
import org.apromore.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object User.
 * 
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {

  /**
   * Gets specified User in the System.
   * 
   * @param username the username of the user we are searching for.
   * @return the username of the user we are searching for.
   */
  User findByUsername(String username);

  /**
   * Gets specified User in the System.
   * 
   * @param rowGuid the id of the user we are searching for.
   * @return the id of the user we are searching for.
   */
  User findByRowGuid(String rowGuid);

  /**
   * Searches user by username.
   * 
   * @param searchString the username of the user we are searching for.
   * @return the username of the user we are searching for.
   */
  List<User> findByUsernameLike(String searchString);

  User findByMembershipEmail(String email);
}
