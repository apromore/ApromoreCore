/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.dao.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Node.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Node
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {

    /**
     * Find the Node by It's Uri.
     * @param uri the uri to search for.
     * @return the found Node.
     */
    @Query("SELECT n FROM Node n JOIN n.nodeMappings nm JOIN nm.fragmentVersion f WHERE n.uri = ?1 and f.id = ?2")
    Node findNodeByUriAndFragmentVersion(String uri, Integer fragmentId);

    /**
     * Find Nodes by using the Fragment URI.
     * @param fragmentURI the fragment uri.
     * @return the list of found nodes.
     */
    @Query("SELECT n FROM Node n JOIN n.nodeMappings nm JOIN nm.fragmentVersion f WHERE f.uri = ?1")
    List<Node> getNodesByFragmentURI(String fragmentURI);

}
