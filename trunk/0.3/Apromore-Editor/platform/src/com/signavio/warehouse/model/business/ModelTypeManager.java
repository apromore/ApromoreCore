/**
 * Copyright (c) 2009, Signavio GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
