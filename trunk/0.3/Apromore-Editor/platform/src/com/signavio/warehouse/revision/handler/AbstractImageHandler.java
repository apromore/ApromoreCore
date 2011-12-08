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
package com.signavio.warehouse.revision.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;

import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.revision.business.FsModelRepresentationInfo;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

public class AbstractImageHandler extends AbstractRevisionExportHandler {

	public AbstractImageHandler(ServletContext servletContext) {
		super(servletContext);
		
	}

	public <T extends FsSecureBusinessObject> byte[] getImage(RepresentationType type, AbstractTranscoder transcoder, T sbo, Map<TranscodingHints.Key, Object> transcodingHints){
		FsModelRevision rev = (FsModelRevision) sbo;
		
		FsModelRepresentationInfo rep = rev.getRepresentation(type);
		
		//png does not exist, create it
		if(rep == null) {
			
			byte[] result = new byte[]{};
			
			//get svg representation
			FsModelRepresentationInfo svg = rev.getRepresentation(RepresentationType.SVG);
			
			InputStream in = new ByteArrayInputStream(svg.getContent());
			
		  	//PNGTranscoder transcoder = new PNGTranscoder();
		  	try {
		    	TranscoderInput input = new TranscoderInput(in);

		    	// Setup output
		    	ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		    	try {
			    	TranscoderOutput output = new TranscoderOutput(outBytes);
			    	
			    	if(transcodingHints != null) {
			    		for(TranscodingHints.Key hint : transcodingHints.keySet()) {
			    			transcoder.addTranscodingHint(hint, transcodingHints.get(hint));
			    		}
			    	}
			    	
			    	// Do the transformation
					transcoder.transcode(input, output);
					
					result = outBytes.toByteArray();
					
					//save representation
					rev.createRepresentation(type, result);
					
					outBytes.close();
		    	} catch (TranscoderException e) {
					
				} catch (IOException e) {
					
				} finally {
					try {
						outBytes.close();
					} catch (IOException e) {
						
					}
		    	}
		  	} finally {
		    	try {
					in.close();
				} catch (IOException e) {
					
				}
			}
		  	
		  	return result;
		} else {
			return rep.getContent();
		}
	}
}
