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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.generic.GenericDiagram;

import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.util.fsbackend.FileSystemUtil;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;
import com.signavio.warehouse.revision.business.FsModelRepresentationInfo;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

/**
 * Implementation of a model in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsModel extends FsSecureBusinessObject {
	
	private String pathPrefix;
	private String name;
	private String fileExtension;
	
	/**
	 * Constructor
	 * 
	 * Constructs a new model object for an EXISTING filesystem model.
	 *  
	 * @param parentDirectory
	 * @param name
	 * @param uuid
	 */
	public FsModel(String pathPrefix, String name, String fileExtension){
		
		this.pathPrefix = pathPrefix;
		this.name = name;
		this.fileExtension = fileExtension;
		
		String path = getPath();
		if (FileSystemUtil.isFileDirectory(path)){
			throw new IllegalStateException("Path does not point to a file.");
		} else if ( ! FileSystemUtil.isFileExistent(path) || ! FileSystemUtil.isFileAccessible(path )){
			throw new IllegalStateException("Model can not be accessed");
		}
		
	}
	
	public FsModel(String fullName){
		
		if (FileSystemUtil.isFileDirectory(fullName)){
			throw new IllegalStateException("Path does not point to a file.");
		} else if (fullName.contains(File.separator)){
			int i = fullName.lastIndexOf(File.separator);
			this.pathPrefix = fullName.substring(0, i);
			String remainder = fullName.substring(i+1);
			String[] splittedName = ModelTypeManager.splitNameAndExtension(remainder);
			this.name = splittedName[0];
			this.fileExtension = splittedName[1];
		} else {
			throw new IllegalStateException("Path does not point to a model.");
		}
		
		String path = getPath();
		if ( ! FileSystemUtil.isFileExistent(path) || ! FileSystemUtil.isFileAccessible(path )){
			throw new IllegalStateException("Model can not be accessed.");
		}
		
	}
	
	public void setName(String name) throws UnsupportedEncodingException, JSONException {
		name = FileSystemUtil.getCleanFileName(name);
		if (name.equals(this.name)) {
			return ;
		}
		
		FsModelRevision rev = getHeadRevision();
		GenericDiagram diagram = BasicDiagramBuilder.parseJson(new String(rev.getRepresentation(RepresentationType.JSON).getContent(), "utf8"));
		String namespace = diagram.getStencilsetRef().getNamespace();
		
		if (ModelTypeManager.getInstance().getModelType(namespace).renameFile(getParentDirectory().getPath(), this.name, name)){
			this.name = name;
		} else {
			throw new IllegalArgumentException("Cannot rename model");
		}
	}

	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		ModelTypeManager.getInstance().getModelType(this.fileExtension).storeDescriptionToModelFile(description, getPath());
	}
	
	public String getDescription() {
		return ModelTypeManager.getInstance().getModelType(this.fileExtension).getDescriptionFromModelFile(getPath());
	}
	
	public void setType(String type) {
		ModelTypeManager.getInstance().getModelType(this.fileExtension).storeTypeStringToModelFile(type, getPath());
	}
	
	public String getType() {
		return ModelTypeManager.getInstance().getModelType(this.fileExtension).getTypeStringFromModelFile(getPath());
	}
	
	public Date getCreationDate() {
		return new Date();
	}

	public FsModelRevision getHeadRevision() {
		return new FsModelRevision(this);
	}

	public FsModelRevision getRevision(int revisionNr) {
		FsModelRevision onlyRevision = getHeadRevision();
		assert (onlyRevision.getRevisionNumber() == revisionNr);
		return onlyRevision;
	}

	public FsDirectory getParentDirectory() {
		File rootDir = new File(FsRootDirectory.getSingleton().getPath());
		File parentDir = new File(pathPrefix);
		try {
			if (rootDir.getCanonicalPath().equals(parentDir.getCanonicalPath())){
				return FsRootDirectory.getSingleton();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot determine canonical path.", e);
		}
		return new FsDirectory(pathPrefix);
	}
	
	public void createRevision(String jsonRep, String svgRep, String comment) {
		GenericDiagram diagram;
		try {
			diagram = BasicDiagramBuilder.parseJson(jsonRep);
		} catch (JSONException e) {
			throw new IllegalArgumentException("JSON representation of diagram is not valid.", e);
		}
		String namespace = diagram.getStencilsetRef().getNamespace();
		ModelTypeManager.getInstance().getModelType(namespace).storeRevisionToModelFile(jsonRep, svgRep, getPath());
	}
	
	public FsModelRepresentationInfo getRepresentation(RepresentationType type) {
		
		byte [] resultingInfo = ModelTypeManager.getInstance().getModelType(this.fileExtension).getRepresentationInfoFromModelFile(type, getPath());
		if (resultingInfo != null) {
			return new FsModelRepresentationInfo(resultingInfo);
		}
		return null;
		
	}

	public FsModelRepresentationInfo createRepresentation(RepresentationType type, byte[] content) {
		ModelTypeManager.getInstance().getModelType(this.fileExtension).storeRepresentationInfoToModelFile(type, content, getPath());
		return getRepresentation(type);
	}
	
	public void delete() {
		FsModelRevision rev = getHeadRevision();
		GenericDiagram diagram;
		try {
			diagram = BasicDiagramBuilder.parseJson(new String(rev.getRepresentation(RepresentationType.JSON).getContent(), "utf8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Deleting model failed.", e);
		} catch (JSONException e) {
			throw new RuntimeException("Deleting model failed.", e);
		}
		String namespace = diagram.getStencilsetRef().getNamespace();
		
		ModelTypeManager.getInstance().getModelType(namespace).deleteFile(getParentDirectory().getPath(), this.name);
	}
	
	/*
	 * 
	 * Private Functions
	 * 
	 */
	
	private String getPath(){
		return pathPrefix + File.separator + name + fileExtension;
	}
	
	public void moveTo(FsDirectory newParent) throws UnsupportedEncodingException, JSONException {
		
		FsDirectory parent = getParentDirectory();
		
		if (newParent.equals(parent)) {
			return ;
		}

		FsModelRevision rev = getHeadRevision();
		GenericDiagram diagram = BasicDiagramBuilder.parseJson(new String(rev.getRepresentation(RepresentationType.JSON).getContent(), "utf8"));
		String namespace = diagram.getStencilsetRef().getNamespace();
		
		if (!ModelTypeManager.getInstance().getModelType(namespace).renameFile("", parent.getPath() + File.separator + this.name, newParent.getPath() + File.separator + this.name)){
			throw new IllegalArgumentException("Cannot move model");
		}
	}
	
	
	/*
	 * 
	 * INTERFACE COMPLIANCE METHODS 
	 * 
	 */
	
	@Override
	public boolean equals(Object o){
		if (o instanceof FsModel){
			FsModel m = (FsModel)o;
			return getPath().equals(m.getPath());
		}
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsModelRevision.class.isAssignableFrom(type)){
			return (Set<T>) getRevisions();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		if (FsDirectory.class.isAssignableFrom(businessObjectClass)){
			Set<T> parents = new HashSet<T>(1);
			FsDirectory parentDirectory = getParentDirectory();
			if (parentDirectory != null) {
				parents.add((T)parentDirectory);
			}
			return parents;
		} else if (FsEntityManager.class.isAssignableFrom(businessObjectClass)){
			return (Set<T>)FsEntityManager.getSingletonSet();
		} else {
			return super.getParents(businessObjectClass);
		}
	}
	
	public Set<FsModelRevision> getRevisions() {
		Set<FsModelRevision> result = new HashSet<FsModelRevision>(1);
		result.add(getHeadRevision());
		return result;
	}

	@Override
	public String getId() {
		String path = getPath();
		String rootPath = FsRootDirectory.getSingleton().getPath() + File.separator;
		if (path.startsWith(rootPath)){
			path = FsRootDirectory.ID_OF_SINGLETON + File.separator + path.substring(rootPath.length());
		}
		path = path.replace(File.separator, ";");
		return path;
	}
	
	public List<FsDirectory> getParentDirectories() {
		List<FsDirectory> parents = new ArrayList<FsDirectory>();
		FsDirectory parent = getParentDirectory();
		if(parent != null) {
			parents.add(parent);
			parents.addAll(parent.getParentDirectories());
		}
		return parents;
	}

}
