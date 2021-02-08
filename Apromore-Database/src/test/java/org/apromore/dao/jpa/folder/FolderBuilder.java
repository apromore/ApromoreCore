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
package org.apromore.dao.jpa.folder;

import org.apromore.dao.model.Folder;

public class FolderBuilder {

    Folder folder;
    Folder parentFolder;

    public FolderBuilder withFolder(String name, String desc) {
	folder = new Folder();
	folder.setName(name);
	folder.setDescription(desc);
	return this;
    }

    public FolderBuilder withFolder(Folder folder) {
	folder = folder;
	return this;
    }

    public FolderBuilder withParent(Folder parentFolder) {
	this.parentFolder = parentFolder;
	return this;
    }

    public Folder build() {

	if (parentFolder != null) {
	    folder.setParentFolder(parentFolder);
	    if (!parentFolder.getParentFolderChain().equals("-1")) {
	    folder.setParentFolderChain(parentFolder.getParentFolderChain() + "_" + parentFolder.getId());
	    }
	}
	return folder;
    }

}
