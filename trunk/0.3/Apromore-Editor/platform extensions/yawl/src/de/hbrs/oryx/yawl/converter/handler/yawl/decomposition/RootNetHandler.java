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
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import java.util.HashMap;

import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

/**
 * Converts the extra properties of a YAWL root net
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class RootNetHandler extends NetHandler {

	public RootNetHandler(YAWLConversionContext context, YNet net) {
		super(context, net);
	}

	/* (non-Javadoc)
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.NetHandler#convert(java.lang.String)
	 */
	@Override
	public void convert(String parentId) {
		super.convert(parentId);
		getContext().setRootNetId(getNet().getID());
	}

	/* (non-Javadoc)
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.NetHandler#convertProperties()
	 */
	@Override
	protected HashMap<String, String> convertProperties() {
		HashMap<String, String> metadata = super.convertProperties();
		metadata.put("isrootnet", "true");
		return metadata;
	}

}
