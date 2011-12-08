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
