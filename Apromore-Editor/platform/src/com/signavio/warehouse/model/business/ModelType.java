/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.warehouse.model.business;

import java.io.File;

import com.signavio.warehouse.revision.business.RepresentationType;

public interface ModelType {
	
	public String getFileExtension();

	public String getDescriptionFromModelFile(String path);
	public boolean storeDescriptionToModelFile(String description, String path);

	public String getTypeStringFromModelFile(String path);
	public boolean storeTypeStringToModelFile(String typeString, String path);
	
	public byte[] getRepresentationInfoFromModelFile(RepresentationType type, String path);
	public void storeRepresentationInfoToModelFile(RepresentationType type, byte[] content, String path);

	public void storeRevisionToModelFile(String jsonRep, String svgRep, String path);

	public File storeModel(String path, String id, String name, String description, String type, String jsonRep, String svgRep);

	public boolean acceptUsageForTypeName(String typeName);
	
	public boolean renameFile(String parentPath, String oldName, String newName);
	
	public void deleteFile(String parentPath, String name);
}
