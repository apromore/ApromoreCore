/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
package com.signavio.warehouse.model.business;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.signavio.platform.core.HandlerDirectory;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.warehouse.model.business.modeltype.BPMN2_0XMLModelType;
import com.signavio.warehouse.model.business.modeltype.JpdlModelType;
import com.signavio.warehouse.model.business.modeltype.SignavioModelType;

public class ModelTypeManager {
	
	private static ModelTypeManager SINGLETON;

	public static void createInstance() {
		if (SINGLETON != null) {
			throw new IllegalStateException("Model type manager is already initialized");
		}
		SINGLETON = new ModelTypeManager();
	}
	
	public static ModelTypeManager getInstance() {
		return SINGLETON;
	}
	
	private final Map<String, ModelType> extension2modelTypes;
	private final Set<ModelType> modelTypes = new HashSet<ModelType>();
	private final ModelType backfallModelType;
	private final FilenameFilter filter;
	
	private ModelTypeManager(){
		extension2modelTypes = new HashMap<String, ModelType>();
		backfallModelType = new SignavioModelType();
		extension2modelTypes.put(SignavioModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension(), backfallModelType);
		extension2modelTypes.put(JpdlModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension(), new JpdlModelType());
//		extension2modelTypes.put(BPMN2_0XMLModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension(), new BPMN2_0XMLModelType());
		filter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				for (String extension : extension2modelTypes.keySet()) {
					if (name.endsWith(extension)) return true;
				}
				return false;
			}
		};
		modelTypes.addAll(extension2modelTypes.values());
		modelTypes.add(new BPMN2_0XMLModelType());
	}
	
	
	public FilenameFilter getFilenameFilter(){
		return filter;
	}

	public ModelType getModelType(String extensionOrNamespace) {
		ModelType result = extension2modelTypes.get(extensionOrNamespace);
		if (result == null) {
			for (ModelType type : modelTypes) {
				if (type.acceptUsageForTypeName(extensionOrNamespace)) {
					result = type;
					break;
				}
			}
		}
		return (result != null) ? result : backfallModelType;
	}
	


	public static String[] splitNameAndExtension(String nameWithExtension) {
		int index;
		if (nameWithExtension.endsWith(SignavioModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension())) {
			index = nameWithExtension.length() - SignavioModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension().length();
		} else 
		if (nameWithExtension.endsWith(JpdlModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension())) {
			index = nameWithExtension.length() - JpdlModelType.class.getAnnotation(ModelTypeFileExtension.class).fileExtension().length();
		} else {
			return null;
		}
		return new String[] { nameWithExtension.substring(0, index), nameWithExtension.substring(index) };
	}
}
