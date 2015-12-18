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
