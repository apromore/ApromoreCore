/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
package com.signavio.warehouse.model.business.modeltype;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.signavio.platform.util.fsbackend.FileSystemUtil;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.model.business.ModelType;
import com.signavio.warehouse.model.business.ModelTypeFileExtension;
import com.signavio.warehouse.revision.business.RepresentationType;

@ModelTypeFileExtension(fileExtension=".signavio.xml")
public class SignavioModelType implements ModelType {

	private static final String XPATH_PREFIX = "/oryxmodel/";
	
	private static final String DESCRIPTION_ElEMENT_NAME = "description";
	private static final String TYPE_ElEMENT_NAME = "type";
	private static final String JSON_ElEMENT_NAME = "json-representation";
	private static final String SVG_ElEMENT_NAME = "svg-representation";
	
	public String getFileExtension() {
		return this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension();
	}

	public String getDescriptionFromModelFile(String path) {
		return FileSystemUtil.readXmlNodeChildFromFile(XPATH_PREFIX + DESCRIPTION_ElEMENT_NAME, path, null);
	}
	
	public String getTypeStringFromModelFile(String path) {
		return FileSystemUtil.readXmlNodeChildFromFile(XPATH_PREFIX + TYPE_ElEMENT_NAME, path, null);
	}

	public boolean storeDescriptionToModelFile(String description, String path) {
		if (!FileSystemUtil.writeXmlNodeChildToFile(DESCRIPTION_ElEMENT_NAME, description, false, path)){
			throw new IllegalStateException("Could not write new description to file");
		}
		return true;
	}

	public boolean storeTypeStringToModelFile(String typeString, String path) {
		if (!FileSystemUtil.writeXmlNodeChildToFile(TYPE_ElEMENT_NAME, typeString, false, path)){
			throw new IllegalStateException("Could not write new type to file");
		}
		return true;
	}

	public byte[] getRepresentationInfoFromModelFile(RepresentationType type, String path) {
		try {
			switch (type) {
			case JSON :
				String json = FileSystemUtil.readXmlNodeChildFromFile(XPATH_PREFIX + JSON_ElEMENT_NAME, path, null);
				if (json != null) {
					return json.getBytes("utf-8");
				}
				break;
			case SVG :
				String svg = FileSystemUtil.readXmlNodeChildFromFile(XPATH_PREFIX + SVG_ElEMENT_NAME, path, null);
				if (svg != null) {
					
						return svg.getBytes("utf-8");
					
				}
				break;
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not read data from file", e);
		}
		System.out.println("Failed to return reprensentation of type " + type);
		return null;
	}
	
	public void storeRepresentationInfoToModelFile(RepresentationType type, byte[] content, String path) {
		try {
			if (RepresentationType.JSON == type){
				if (!FileSystemUtil.writeXmlNodeChildToFile(JSON_ElEMENT_NAME, new String(content, "utf-8"), false, path)) {
					throw new IllegalStateException("Could not write new revision data to file");
				}
			} else if (RepresentationType.SVG == type) {
				
					if (!FileSystemUtil.writeXmlNodeChildToFile(SVG_ElEMENT_NAME, new String(content, "utf-8"), true, path)) {
						throw new IllegalStateException("Could not write new revision data to file");
					}
				
			} else  {
				System.out.println("Imitated creation of reprensentation of type " + type);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not write new revision data to file", e);
		}
	}

	public void storeRevisionToModelFile(String jsonRep, String svgRep,String path) {
		if (!FileSystemUtil.writeXmlNodeChildToFile(JSON_ElEMENT_NAME, jsonRep, true, path)) {
			throw new IllegalStateException("Could not write new revision data to file");
		}
		
		if (!FileSystemUtil.writeXmlNodeChildToFile(SVG_ElEMENT_NAME, svgRep, true, path)) {
			throw new IllegalStateException("Could not write new revision data to file");
		}
	}
		
	public boolean acceptUsageForTypeName(String typeName) {
		return false;
	}

	//@Override
	public File storeModel(String path, String id, String name, String description,
			String type, String jsonRep, String svgRep) {
		File modelFile;
		if ((modelFile = FileSystemUtil.createFile(path, this.getInitialModelString(id, name, description, type, jsonRep, svgRep)))
				!= null) {
			return modelFile;
		} else {
			throw new IllegalStateException("Could not create new model");
		}
	}

	private String getInitialModelString(String id, String name, String description, String type, String jsonRep, String svgRep) {
		return 	"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + 
				"<oryxmodel>\n" + 
					"<description>" + description + "</description>\n" +
					"<type>" + type + "</type>\n" +
					"<json-representation><![CDATA[" + jsonRep + "]]></json-representation>\n" +
					"<svg-representation><![CDATA[" + svgRep + "]]></svg-representation>\n" +
				"</oryxmodel>\n";
	}

//	@Override
	public boolean renameFile(String parentPath, String oldName, String newName) {
		if(parentPath != "") {
			parentPath += File.separator;
		}
		return FileSystemUtil.renameFile(parentPath + oldName + getFileExtension(), parentPath + newName + getFileExtension());
	}

//	@Override
	public void deleteFile(String parentPath, String name) {
		FileSystemUtil.deleteFileOrDirectory(parentPath + File.separator + name + getFileExtension());
	}
	
}
