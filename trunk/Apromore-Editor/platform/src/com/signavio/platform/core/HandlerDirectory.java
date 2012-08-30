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
package com.signavio.platform.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.handler.BasisHandler;

/**
 * HandlerManager implements the manager to hold all HandlerEntries.
 * @author Willi
 *
 */
public class HandlerDirectory extends HashMap<String, HandlerEntry> implements Directory {
	
	/**
	 * Define private variables
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;

	/**
	 * To create get a new instance of the HandlerManager
	 * @deprecated Use System.getInstance().getHandlerManger() instead
	 * @return
	 */
	@Deprecated
	public static HandlerDirectory getInstance(){
		return Platform.getInstance().getHandlerDirectory();
	}
	
	public HandlerDirectory(ServletContext sc) {
		super();
		
		this.servletContext = sc;
	}
	
	/**
	 * Get all Handlers from a particular context
	 * @param context 
	 * @return
	 */
	public Collection<HandlerEntry> getAllHandlerByContext(Class<? extends BasisHandler> context){
		Collection<HandlerEntry> res = new ArrayList<HandlerEntry>();
		// For every Handler
		for( HandlerEntry he : this.values() )
		{
			// try to find a Handler where the name is equal than the name of the class
			if( he.getContextClass() != null && context.equals(he.getContextClass()) )
			{
				res.add(he);
			}
		}
		return res;
	}

	/**
	 * Get all BasisHandler for a particular URI 
	 * @param uri
	 * @return
	 */
	public HandlerEntry getBasisHandler(String uri){
		// Add leading /
		if( uri.charAt(0) != '/' )
			uri = '/' + uri;
		// For every Handler
		for( HandlerEntry he : this.values() )
		{
			// try to find one, where the context is empty, the class is inherit from the BasisHandler and the uri is equals to the given uri
			if( he.getContextClass() == null && BasisHandler.class.isAssignableFrom(he.handlerClass) && he.getUri().equals(uri) )
			{
				return he;
			}
		}
		return null;
	}


	/**
	 * Get a Handler by their URI of the context and the URI of the Handler
	 * @param contexturi Defines the URI of the context
	 * @param uri Defines the URI for the Handler
	 * @return
	 */
	public HandlerEntry getHandlerByContextAndUri(String contexturi, String uri){
		// Add leading /
		if( uri.charAt(0) != '/' )
			uri = '/' + uri;
		// Get the context class from the given context URI
		HandlerEntry contextClass = this.getBasisHandler( contexturi );
		// For every Handler
		for( HandlerEntry he : this.values() )
		{
			// try to find one, without a context class equals the given context, and equal the given URI
			if( he.getContextClass() != null && he.getContextClass() == contextClass.getHandlerClass() && he.getUri().equals(uri) )
			{
				return he;
			}
		}
		return null;
	}
	
	/**
	 * Get all list of all classes within a package, 
	 * including all child packages
	 * @param pckgname
	 * @return
	 * @throws ClassNotFoundException
	 */
    @SuppressWarnings("unchecked")
	private static List<Class<? extends AbstractHandler>> getClassesByPackageName(String pckgname) throws ClassNotFoundException {
        // This will hold a list of directories matching the packagename. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }
 
        ArrayList<Class<? extends AbstractHandler>> classes = new ArrayList<Class<? extends AbstractHandler>>();
        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (directory.exists()) {
                // Get the list of the files contained in the package
                File[] files = directory.listFiles();
                for (File file : files) {
                    // we are only interested in .class files
                    if (file.getName().endsWith(".class")) {
                        // removes the .class extension
                    	// Get the class object
                    	Class<? extends Object> cls = Class.forName(pckgname + '.' + file.getName().substring(0, file.getName().length() - 6));
                    	// Checks if its an AbstractHandler
                    	if( AbstractHandler.class.isAssignableFrom(cls) ){
                    		classes.add( (Class<? extends AbstractHandler>) cls );
                    	}
                    } else if( file.isDirectory() ) { 
                    	// Add recursive all child packages
                    	List<Class<? extends AbstractHandler>> childPackages = HandlerDirectory.getClassesByPackageName( pckgname + '.' + file.getName() );
                    	classes.addAll( childPackages );
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }
	
    public void registerHandlersOfPackage(String packageName) {
    	// Try to get all Handler Classes and instantiate the HandlerEntries with it
		try {
			for( Class<? extends AbstractHandler> cls : HandlerDirectory.getClassesByPackageName(packageName) )
			{
				// If the class is an abstract class --> continue
				if( Modifier.isAbstract( cls.getModifiers() ) )
				{
					continue;
				}
				
				// Create a new HandlerEntry with the given classifier
				HandlerEntry he = new HandlerEntry(cls);
				
				// If there is no handler class, something
				// might be wrong, --> continue.
				if( he.getHandlerClass() == null )
				{
					continue;
				}
				
				// Try to instantiate the Handler
				Constructor co = cls.getConstructor(ServletContext.class);
				he.setHandlerInstance( (AbstractHandler)(co.newInstance( this.servletContext )) );
				// Add the classifier to the map
				this.put(cls.getName(), he);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * TODO: This method is for debugging purpose only. It is called at system initialization
	 */
	private void registerHandlers() {
		this.registerHandlersOfPackage("com.signavio.editor.handler");
		this.registerHandlersOfPackage("com.signavio.editor.stencilset.handler");
		this.registerHandlersOfPackage("com.signavio.explorer.handler");
		this.registerHandlersOfPackage("com.signavio.platform.config.handler");
		this.registerHandlersOfPackage("com.signavio.usermanagement.user.handler");
		this.registerHandlersOfPackage("com.signavio.warehouse.directory.handler");
		this.registerHandlersOfPackage("com.signavio.warehouse.model.handler");
		this.registerHandlersOfPackage("com.signavio.warehouse.revision.handler");
		this.registerHandlersOfPackage("com.signavio.warehouse.search.handler");
	}

    
	public void start() {
		registerHandlers();
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
