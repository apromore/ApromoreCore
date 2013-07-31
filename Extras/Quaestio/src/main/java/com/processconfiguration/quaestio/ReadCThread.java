/*******************************************************************************
 * Copyright © 2006-2011, www.processconfiguration.com
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 *      Marcello La Rosa - initial API and implementation, subsequent revisions
 *      Florian Gottschalk - individualizer for YAWL
 *      Possakorn Pitayarojanakul - integration with Configurator and Individualizer
 ******************************************************************************/
/**
 * Copyright © 2006-2009, Marcello La Rosa (marcello.larosa@gmail.com)
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *      Marcello La Rosa - initial API and implementation
 */
package com.processconfiguration.quaestio;

import com.processconfiguration.qml.QMLType;

public class ReadCThread extends Thread {

	public String constraints;
	private QMLType qml = null;

	public ReadCThread(QMLType qml){
		this.qml  = qml;
	}

	
	@Override
	public void run(){
		constraints = qml.getConstraints();
	}
}
