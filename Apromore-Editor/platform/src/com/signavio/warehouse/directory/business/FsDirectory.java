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
package com.signavio.warehouse.directory.business;

import java.io.File;
import java.io.FilenameFilter;
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
import com.signavio.platform.security.business.util.UUID;
import com.signavio.platform.util.fsbackend.FileSystemUtil;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.model.business.ModelType;
import com.signavio.warehouse.model.business.ModelTypeManager;

/**
 * Implementation of a directory in the file accessing Oryx backend.
 * 
 *  TODO : Auto renameof new model, directory ?
 * 
 * @author Stefan Krumnow
 *
 */
public class FsDirectory extends FsSecureBusinessObject {
		
	// Members
	protected String path;
//	private String name;
	
	/**
	 * Constructor
	 * 
	 * Constructs a new directory object for an EXISTING filesystem directory.
	 * 
	 * @param parentDirectory
	 * @param name
	 */
	public FsDirectory(String path) {
		
		if (!FileSystemUtil.isFileDirectory(path)){
			throw new IllegalStateException("Path does not point to a directory.");
		} else if ( ! FileSystemUtil.isFileExistent(path) || ! FileSystemUtil.isFileAccessible(path)){
			throw new IllegalStateException("Directory can not be accessed");
		}

		this.path = path;
		
	}
	
	public String getName() {
		return getDirName();
	}
	
	public String getDescription() {
		return "";
	}
	
	public Date getCreationDate() {
		return new Date();
	}
	
	public void setName(String name) {
		name = FileSystemUtil.getCleanFileName(name);
		if (name.equals(getName())) {
			return ;
		}
		String newName = getPathPrefix() + File.separator + name;
		if (FileSystemUtil.renameFile(getPath(), newName)){
			this.path = newName;
		} else {
			throw new IllegalArgumentException("Cannot rename directory");
		}
	}
	
	public void setDescription(String description) {
		return ;
	}
	
	public void setCreationDate() {
		return ;
	}

	
	public Set<FsDirectory> getChildDirectories(){
		Set<FsDirectory> childDirectories = new HashSet<FsDirectory>();
		File[] children = FileSystemUtil.getFileChildren(getPath(), null);
		if (children == null) {
			return childDirectories;
		}
		for (File f : children){
			if (f.isDirectory()) {
				childDirectories.add(new FsDirectory(f.getAbsolutePath()));
			}
		}
		return childDirectories;
	}
	
	
	public Set<FsModel> getChildModels() {
		Set<FsModel> childModels = new HashSet<FsModel>();
		File[] children = FileSystemUtil.getFileChildren(getPath(), ModelTypeManager.getInstance().getFilenameFilter());
		if (children == null) {
			return childModels;
		}
		for (File f : children){
			if (f.isFile()) {
				childModels.add(new FsModel(f.getAbsolutePath()));
			}
		}
		return childModels;
	}

	public FsDirectory getParentDirectory() {
		File rootDir = new File(FsRootDirectory.getSingleton().getPath());
		File parentDir = new File(getPathPrefix());
		try {
			if (rootDir.getCanonicalPath().equals(parentDir.getCanonicalPath())){
				return FsRootDirectory.getSingleton();
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot determine canonical path.", e);
		}
		return new FsDirectory(getPathPrefix());
	}
	

	public void addChildDirectory(FsDirectory dir) {
		dir.moveTo(this);	
	}
	
	public FsDirectory createDirectory(String name, String description) {
		
		name = FileSystemUtil.getCleanFileName(name);
		
		String path = getPath() + File.separator + name;
		File f = new File (path);
		if (f.exists()) {
			throw new IllegalArgumentException("Name already exists.");
		}
		if ( FileSystemUtil.createDirectory(path) != null ) {
			return new FsDirectory(path);
		} else {
			throw new IllegalArgumentException("Could not create Directory");
		}

	}

	public void addChildModel(FsModel model) throws UnsupportedEncodingException, JSONException {
		model.moveTo(this);
	}

	public FsModel createModel(String name, String description, String type, String jsonRep, String svgRep, String comment) {
		return createModel(UUID.getUUID().toString(), name, description, type, jsonRep, svgRep, comment);
	}

	public FsModel createModel(String id, String name, String description, String type, String jsonRep, String svgRep, String comment) {
		
		name = FileSystemUtil.getCleanFileName(name);
		
		String namespace;
		try {
			GenericDiagram diagram = BasicDiagramBuilder.parseJson(jsonRep);
			
			namespace = diagram.getStencilsetRef().getNamespace();
		} catch (JSONException e) {
			throw new IllegalStateException("Could not create new model", e);
		}
		
		ModelType modelType = ModelTypeManager.getInstance().getModelType(namespace);
		
		String path = getPath() + File.separator + name + modelType.getFileExtension();
		File f = new File (path);
		if (f.exists()) {
			throw new IllegalArgumentException("Name already exists.");
		}
		
		modelType.storeModel(path, id, namespace, description, type, jsonRep, svgRep);

		return new FsModel(getPath(), name, modelType.getFileExtension());
	}
	
	public void delete() {
		for (FsModel child : getChildModels()) {
			child.delete();
		}
		for (FsDirectory child : getChildDirectories()) {
			child.delete();
		}
		FileSystemUtil.deleteFileOrDirectory(getPath());
	}
	
	
	public void search(final String searchTerm, List<FsSecureBusinessObject> result) {	
		// Find Models
		FilenameFilter modelFilter = new FilenameFilter(){

			public boolean accept(File dir, String name) {
				String[] nameAndExtension = ModelTypeManager.splitNameAndExtension(name);
				if (nameAndExtension == null || nameAndExtension.length < 2){
					return false;
				}
				return ModelTypeManager.getInstance().getFilenameFilter().accept(dir, name) &&
					nameAndExtension[0].toLowerCase().contains(searchTerm.toLowerCase());
			}
		};
		File[] children = FileSystemUtil.getFileChildren(getPath(), modelFilter);
		if (children != null) {
			for (File f : children){
				if (f.isFile()) {
					result.add(new FsModel(f.getAbsolutePath()));
				}
			}
		}
		
		// Find Directories
		FilenameFilter dirFilter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains(searchTerm.toLowerCase());
			}
		};
		File[] dirChildren = FileSystemUtil.getFileChildren(getPath(), dirFilter);
		if (dirChildren != null) {
			for (File f : dirChildren){
				if (f.isDirectory()) {
					result.add(new FsDirectory(f.getAbsolutePath()));
				}
			}
		}
		
		// Visit Child Directories
		for (FsDirectory child : getChildDirectories()){
			child.search(searchTerm, result);
		}
	}
	
	/*
	 * 
	 * Private Functions
	 * 
	 */

	public String getPath(){
		return path;
	}
	
	private String getDirName(){
		if (path.contains(File.separator)){
			int i = path.lastIndexOf(File.separator);
			return path.substring(i+1);
		} else {
			throw new IllegalStateException("Path cannot be resolved.");
		}	
	}
	
	private String getPathPrefix() {
		if (path.contains(File.separator)){
			int i = path.lastIndexOf(File.separator);
			return path.substring(0, i);
		} else {
			throw new IllegalStateException("Path cannot be resolved.");
		}
	}
	
	private void moveTo(FsDirectory newParent) {
		if (newParent.equals(getParentDirectory())) {
			return ;
		}
		String dirName = getName();
		if (!FileSystemUtil.renameFile(getPath(), newParent.getPath() + File.separator + dirName)){
			throw new IllegalArgumentException("Cannot move directory");
		}
	}
	
	
	/*
	 * 
	 * INTERFACE COMPLIANCE METHODS 
	 * 
	 */
	
	@Override
	public boolean equals(Object o){
		if (o instanceof FsDirectory){
			FsDirectory d = (FsDirectory)o;
			return getPath().equals(d.getPath());
		}
		return false;
	}
	
	public Set<FsDirectory> getChildDirectoriesByPrivileges(Set<String> privileges) {
		return getChildDirectories();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsModel.class.isAssignableFrom(type)){
			return (Set<T>) getChildModels();
		} else if (FsDirectory.class.isAssignableFrom(type)){
			return (Set<T>) getChildDirectories();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		if (FsDirectory.class.isAssignableFrom(businessObjectClass)){
			Set<T> parentDirectories = new HashSet<T>(1);
			FsDirectory parentDirectory = getParentDirectory();
			if (parentDirectory != null){
				parentDirectories.add((T)parentDirectories);
			}
			return parentDirectories;
		} else if (FsEntityManager.class.isAssignableFrom(businessObjectClass)){
			return (Set<T>)FsEntityManager.getSingletonSet();
		} else {
			return super.getParents(businessObjectClass);
		}
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

