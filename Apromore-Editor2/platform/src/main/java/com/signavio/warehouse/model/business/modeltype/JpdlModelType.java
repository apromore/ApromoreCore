/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.warehouse.model.business.modeltype;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.signavio.platform.util.fsbackend.FileSystemUtil;
import com.signavio.warehouse.business.util.jpdl4.InvalidModelException;
import com.signavio.warehouse.business.util.jpdl4.JpdlToJson;
import com.signavio.warehouse.business.util.jpdl4.JsonToJpdl;
import com.signavio.warehouse.model.business.ModelType;
import com.signavio.warehouse.model.business.ModelTypeFileExtension;
import com.signavio.warehouse.model.business.ModelTypeRequiredNamespaces;
import com.signavio.warehouse.revision.business.RepresentationType;

@ModelTypeFileExtension(fileExtension=".jpdl.xml")
@ModelTypeRequiredNamespaces(namespaces={"http://b3mn.org/stencilset/jbpm4#"})
public class JpdlModelType implements ModelType {
	
	private static final byte[] SVG_DUMMY_REPRESENTATION = "<svg xmlns=\"http://www.w3.org/2000/svg\" />".getBytes();
	private static final byte[] PNG_DUMMY_REPRESENTATION = new byte[0];
	
	private static String MODEL_TYPE = "BPMN 1.2 / jBPM Stencils";
	
	public String getFileExtension() {
		return this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension();
	}

	public String getDescriptionFromModelFile(String path) {
		return FileSystemUtil.readXmlNodeChildFromFile("/process/@description", path, null);
	}
	
	public String getTypeStringFromModelFile(String path) {
		return MODEL_TYPE;
	}

	public boolean storeDescriptionToModelFile(String description, String path) {
		return FileSystemUtil.writeXmlNodeAttributeToFile("process", "description", description, path);
	}

	public boolean storeTypeStringToModelFile(String typeString, String path) {
		return true;
	}

	public byte[] getRepresentationInfoFromModelFile(RepresentationType type, String path) {
		if (RepresentationType.JSON == type){
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document jpdlDoc = builder.parse(new File(path));	
				String result = JpdlToJson.transform(jpdlDoc);
				return result.getBytes("utf-8");
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (RepresentationType.SVG == type) {
			return SVG_DUMMY_REPRESENTATION;
		} else if (RepresentationType.PNG == type || RepresentationType.PNG_SMALL == type ) {
			return PNG_DUMMY_REPRESENTATION;
		}
		//System.out.println("Failed to return reprensentation of type " + type);
		return null;
	}
	
	public void storeRepresentationInfoToModelFile(RepresentationType type, byte[] content, String path) {
		if (RepresentationType.JSON == type){
			File f = new File(path);
			String name = f.getName();
			name = name.substring(0, name.length() - this.getClass().getAnnotation(ModelTypeFileExtension.class).fileExtension().length());
			try {
				String result = getNewModelString(new String(content, "utf-8"), name, getDescriptionFromModelFile(path));
  			
  				// Write to file
  				FileWriter fw = new FileWriter(f);
  				fw.write(result);
  				fw.flush();
  				fw.close();
  			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Saving failed.");
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
				throw new RuntimeException("Saving failed.");
			}

		} 
		//else {
		//	System.out.println("Imitated creation of reprensentation of type " + type);
		//}
	}

	public void storeRevisionToModelFile(String jsonRep, String svgRep,String path) {
		try {
			storeRepresentationInfoToModelFile(RepresentationType.JSON, jsonRep.getBytes("utf-8"), path);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Could not store data in file", e);
		}
		//System.out.println("Could not store SVG data");
	}

	private String getInitialModelString(String id, String name, String description, String type, String jsonRep, String svgRep) {
		return getNewModelString(jsonRep, name, description);
	}
	
	
	private String getNewModelString(String json, String processName, String description) {		
		try {
			JSONObject jsonObj = new JSONObject(json);
			if (description != null && !(description.length() == 0)){
				jsonObj.getJSONObject("properties").put("documentation", description);
			}
			if (processName != null && !(processName.length() == 0)){
				jsonObj.getJSONObject("properties").put("name", processName);
			}
			JsonToJpdl transformation = JsonToJpdl.createInstance(jsonObj);
			return transformation.transform();
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Transformation failed.");
		} catch (InvalidModelException e) {
			e.printStackTrace();
			throw new RuntimeException("Transformation failed.");
		}
	}

	public boolean acceptUsageForTypeName(String namespace) {
		for(String ns : this.getClass().getAnnotation(ModelTypeRequiredNamespaces.class).namespaces()) {
			if(ns.equals(namespace)) {
				return true;
			}
		}
		return false;
	}

//	@Override
	public File storeModel(String path, String id, String name,
			String description, String type, String jsonRep, String svgRep) {
		File modelFile;
		if ((modelFile = FileSystemUtil.createFile(path, this.getInitialModelString(id, name, description, type, jsonRep, svgRep)))
				!= null) {
			return modelFile;
		} else {
			throw new IllegalStateException("Could not create new model");
		}
	}

//	@Override
	public boolean renameFile(String parentPath, String oldName, String newName) {
		if(parentPath != "") {
			parentPath += File.separator;
		}
		return FileSystemUtil.renameFile(parentPath + File.separator + oldName + getFileExtension(), parentPath + File.separator + newName + getFileExtension());
	}

//	@Override
	public void deleteFile(String parentPath, String name) {
		FileSystemUtil.deleteFileOrDirectory(parentPath + File.separator + name + getFileExtension());
	}
}
