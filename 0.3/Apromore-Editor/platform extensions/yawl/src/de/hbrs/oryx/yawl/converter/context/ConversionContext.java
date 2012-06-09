/**
 * Copyright (c) 2011-2012 Felix Mannhardt
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
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.context;

import java.util.ArrayList;
import java.util.List;

import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactory;

/**
 * Base class for conversion contexts. Sharing error/warning reporting and access to HandlerFactory.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public abstract class ConversionContext {

	/**
	 * Used to create new Handlers for YAWL Elements
	 */
	private HandlerFactory handlerFactory;
	
	/**
	 * True if conversion failed
	 */	
	private Boolean conversionError;
	
	/**
	 * May contain serveral warning or error messages
	 */
	private List<ConversionException> conversionWarnings;	

	public ConversionContext() {
		super();
		this.conversionWarnings = new ArrayList<ConversionException>();
		this.conversionError = false;
	}

	public void setHandlerFactory(HandlerFactory handlerFactory) {
		this.handlerFactory = handlerFactory;
	}

	public HandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public void setConversionError(Boolean conversionError) {
		this.conversionError = conversionError;
	}

	public Boolean getConversionError() {
		return conversionError;
	}

	public void addConversionWarnings(ConversionException e) {
		this.conversionWarnings.add(e);
	}

	public void addConversionWarnings(String string, Exception e) {
		this.conversionWarnings.add(new ConversionException(string, e));
	}

	public List<ConversionException> getConversionWarnings() {
		return conversionWarnings;
	}	

}
