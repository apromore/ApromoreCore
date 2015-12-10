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
package com.signavio.platform.core;


import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.handler.BasisHandler;

/**
 * HandlerEntry describes a reference to a particular Handler
 * @author Willi
 *
 */
public class HandlerEntry {

	protected String uri;
	protected String rel;
	protected boolean isAdminHandler;
	protected Class<? extends BasisHandler> contextClass = null;
	protected Class<? extends AbstractHandler> handlerClass = null;
	protected AbstractHandler handlerInstance = null;
	
	/**
	 * Get the specifies URI for the given Handler
	 * @return The URI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get the relation type for the given Handler
	 * @return The rel value
	 */
	public String getRel() {
		return rel;
	}
	
	/**
	 * Checks if the Handler has a context class
	 * @return A bool whether it has a context class or not
	 */
	public boolean isHasContextClass() {
		return (this.contextClass != null);
	}

	/**
	 * Get the context class for the Handler
	 * @return The context class
	 */
	public Class<? extends BasisHandler> getContextClass() {
		return this.contextClass;
	}
	
	/**
	 * Get the instance of the Handler
	 * @return Instance of the Handler
	 */
	public AbstractHandler getHandlerInstance() {
		return handlerInstance;
	}
	
	/**
	 * Set the instance for the Handler class
	 * @param handler Given instance of the Handler
	 */
	public void setHandlerInstance(AbstractHandler handler) {
		this.handlerInstance = handler;
	}
	
	/**
	 * Get the classifier of the Handler
	 * @return Class of Handler
	 */
	public Class<? extends AbstractHandler> getHandlerClass() {
		return handlerClass;
	}
	
	/**
	 * Flag, if the handler is only accessible for admins.
	 * @return True, if the handler is only accessible for admins.
	 */
	public boolean isAdminHandler() {
		return this.isAdminHandler;
	}
	
	/**
	 * Create a HandlerEntry with a given class name
	 * @param className Class name of the Handler
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public HandlerEntry(String className) throws ClassNotFoundException, NoSuchMethodException {
		Class<? extends AbstractHandler> handlerClass = (Class<? extends AbstractHandler>) Class.forName(className);
		init(handlerClass);
	}
	
	/**
	 * Create a HandlerEntry with a given class of type AbstractHandler
	 * @param handlerClass Classifier of the Handler
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public HandlerEntry(Class<? extends AbstractHandler> handlerClass) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		init(handlerClass);
	}
	
	@SuppressWarnings("unchecked")
	private void init(Class<? extends AbstractHandler> handlerClass) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		// Checks if the class type has an annotation
		if (handlerClass.getAnnotation(HandlerConfiguration.class) != null) {
			// Get the annotation
			HandlerConfiguration annotation = handlerClass.getAnnotation(HandlerConfiguration.class);
			// Set URI and relation type
			this.uri = annotation.uri();
			this.rel = annotation.rel();
			// set admin handler flag
			this.isAdminHandler = annotation.isAdminHandler();
			// Set Hander Class
			this.handlerClass = handlerClass;
			// If there is an context setted, create context class
			if(annotation.context() != BasisHandler.class) {
				this.contextClass = annotation.context();
			} 
		} 
	}
}
