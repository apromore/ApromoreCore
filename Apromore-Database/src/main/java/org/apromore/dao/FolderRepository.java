/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.dao;

import java.util.Collection;
import java.util.List;

import org.apromore.dao.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Folder.
 *
 * @see org.apromore.dao.model.Folder
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer>, FolderRepositoryCustom {

    /**
     * Returns a Log
     * @param folderId the id of the folder
     * @return the folder
     */
    @Query("SELECT DISTINCT f FROM Folder f WHERE f.id = ?1")
    Folder findUniqueByID(Integer folderId);


    List<Folder> findByParentFolderIdOrParentFolderChainLike(Integer id, String folderChainPrefix);

    Folder findById(Integer id);

    @Query("SELECT DISTINCT f FROM Folder f WHERE f.id in ?1")
    List<Folder> findByIdIn(Collection<Integer> ids);

}
