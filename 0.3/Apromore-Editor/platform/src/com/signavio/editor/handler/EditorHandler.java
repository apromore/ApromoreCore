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
package com.signavio.editor.handler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.annotations.HandlerMethodActivation;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.security.business.exceptions.BusinessObjectDoesNotExistException;
import com.signavio.platform.security.business.util.UUID;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;
import com.signavio.warehouse.revision.business.RepresentationType;

@HandlerConfiguration(uri = "/editor", rel="editor")
public class EditorHandler extends BasisHandler {

	public final String EDITOR_URL_PREFIX;
	
	private static final Pattern SUPPORTED_USER_AGENT_PATTERN = Pattern.compile(Platform.getInstance().getPlatformProperties().getSupportedBrowserEditorRegExp());
	
	public EditorHandler(ServletContext servletContext) {
		super(servletContext);
		
		EDITOR_URL_PREFIX = Platform.getInstance().getPlatformProperties().getEditorUri() + "/";
	}

	/**
	 * Implementation of a GET request
	 * @param req
	 * @param res
	 * @param identifier
	 * @throws Exception
	 */
	@Override
	@HandlerMethodActivation
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  		
		//check for firefox
		if(!isSupported(req)) {
			//show firefox page
			addJSPAttributes(req);
			try {
				req.getRequestDispatcher("/WEB-INF/jsp/browser.jsp").include(req, res);
			} catch (ServletException e) {
				throw new RequestException("servletException", e);
			} catch (IOException e) {
				throw new IORequestException(e);
			}
		} else {
			//return editor
			JSONObject jParams = (JSONObject) req.getAttribute("params");
	  		
	  		try{
				//get id of model or revision
				String id = jParams.getString("id");
				
				id = id.replace("/directory/", "");
				
				String revision = null;
				
				if(jParams.has("revision")) {
					revision = jParams.getString("revision");
				}
				
				if(jParams.has("data")) {
					//editor requested model data (json)
					String json = getJSONString(id, revision, token, req);
					res.setStatus(200);
					res.setContentType("application/json");
					try {
						res.getWriter().print(json);
					} catch (IOException e) {
						throw new RequestException("platform.ioexception", e);
					}
				} else {
					FsAccount account = token.getAccount();
			  		
			  		
					try {
						//try to get the sbo for the id
						FsSecureBusinessObject tempsbo = FsSecurityManager.getInstance().loadObject(id, token);
						
						if(tempsbo instanceof FsModel) {
							//check, if sbo is a model
							FsModel model = (FsModel) tempsbo;
							//set location 
							res.setHeader("location", req.getRequestURL() + "?id=" + id);
							//print xhtml site
							sendEditorXHTML(res, model.getName(), account);
						} 
//						else if (sbo instanceof ModelRevision) {
//							//check, if sbo is a model revision
//							ModelRevision rev = (ModelRevision) tempsbo;
//							Model model = (Model) SecurityManager.getInstance().loadObject(rev.getModelId(), token);
//							
//							sendEditorXHTML(res, model.getName(), account);
//						} 
						else {
							throw new RequestException("editor.invalidIdentifier");
						}
						
						
					} catch (BusinessObjectDoesNotExistException e) {
						//id is no existing model/revision, get info from session
						//Map<String,String> tempModelInfo = (Map<String,String>) req.getSession().getAttribute(id);
						addJSPAttributes(req);
						
//						Properties translation = TranslationFactory.getTranslation(token);
						
						req.setAttribute("title", "New Process");
						req.setAttribute("language", account.getLanguageCode());
						req.setAttribute("country", account.getCountryCode());

						//set location 
						res.setHeader("location", req.getRequestURL() + "?id=" + id);
						
//						try {
							sendEditorXHTML(res, "New Process", account);
							//req.getRequestDispatcher("/WEB-INF/jsp/editor.jsp").include(req, res);
//						} catch (ServletException e1) {
//							throw new RequestException("servletException", e1);
//						} catch (IOException e1) {
//							throw new IORequestException(e1);
//						}
					}
				}
				
			} catch (JSONException e) {
				//no id supplied, get stencilset namespace and directory id
				try {
					String stencilset = jParams.getString("stencilset");
					
					//TODO check validity of stencilset namespace
					
					String dirId = jParams.getString("directory");
					
					UUID uuid = UUID.getUUID();
					
					Map<String, String> tempModelInfo = new HashMap<String, String>();
					tempModelInfo.put("stencilset", stencilset);
					tempModelInfo.put("directory", dirId);
					tempModelInfo.put("id", uuid.toString());
					if (jParams.has("extensions")){
						tempModelInfo.put("extensions", jParams.getString("extensions"));
					} 
					req.getSession().setAttribute(uuid.toString(), tempModelInfo);

					// Uses the variable from the properties because of http and https problems!
					// The servlet doesn't know if there is a secure https connection.
					String url = Platform.getInstance().getPlatformProperties().getServerName() + req.getRequestURI();
					
					res.sendRedirect(url+ "?id=" + uuid.toString());
				} catch(JSONException e2) {
					res.setStatus(405);
				} catch (IOException e3) {
					throw new RequestException("platform.ioexception", e3);
				}
			}
		}
	}
	
    
	
	private void sendEditorXHTML(HttpServletResponse res, String title, FsAccount account) {

  		String languageCode = account.getAccountInfo().getLanguageCode();
  		String countryCode = account.getAccountInfo().getCountryCode();
  		
  		res.setStatus(200);
		res.setContentType("application/xhtml+xml");
		try {
			res.getWriter().print(getEditorXHTML(title, languageCode, countryCode));
		} catch (IOException e) {

		}
	}
    
    
    private String getEditorXHTML(String title, String languageCode, String countryCode) {
    	
    	String libsUri = Platform.getInstance().getPlatformProperties().getLibsUri();
    	String explorerUri = Platform.getInstance().getPlatformProperties().getExplorerUri();
    	String languageFiles = "";
    	
    	if(!languageCode.equals("en") && !countryCode.equals("us")) {
	    	if (new File(this.getServerRootPath() + EDITOR_URL_PREFIX + "i18n/translation_"+languageCode+".js").exists()) {
	    		// Add regular i18n file for language
	    		languageFiles += "<script src=\"" + EDITOR_URL_PREFIX 
	    		+ "i18n/translation_"+languageCode+".js\" type=\"text/javascript\" />\n";
	    		// Add signavio i18n file for language	    		
	    		languageFiles += "<script src=\"" + EDITOR_URL_PREFIX 
	    		+ "i18n/translation_signavio_"+languageCode+".js\" type=\"text/javascript\" />\n";
	    	}
	    	
	    	if (new File(this.getServerRootPath() + EDITOR_URL_PREFIX + "i18n/translation_" + languageCode+"_" + countryCode + ".js").exists()) {
	    		// Add regular i18n file for language
	    		languageFiles += "<script src=\"" + EDITOR_URL_PREFIX 
	    		+ "i18n/translation_" + languageCode+"_" + countryCode 
	    		+ ".js\" type=\"text/javascript\" />\n";
	    		
	    		// Add signavio i18n file for language	    		
	    		languageFiles += "<script src=\"" + EDITOR_URL_PREFIX 
	    		+ "i18n/translation_signavio_" + languageCode+"_" + countryCode 
	    		+ ".js\" type=\"text/javascript\" />\n";
	    	}
    	}
    	
      	return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
      	    + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
      	  	+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n"
      	  	+ "xmlns:b3mn=\"http://b3mn.org/2007/b3mn\"\n"
      	  	+ "xmlns:ext=\"http://b3mn.org/2007/ext\"\n"
      	  	+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
      	  	+ "xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">\n"
      	  	+ "<head profile=\"http://purl.org/NET/erdf/profile\">\n"
      	  	+ "<title>" + title + " - Signavio</title>\n"
      	  	
      	  	+ "<!-- libraries -->\n"
    	  	+ "<script src=\"" + libsUri + "/prototype-1.5.1.js\" type=\"text/javascript\" />\n"
    	  	+ "<script src=\"" + libsUri + "/path_parser.js\" type=\"text/javascript\" />\n"
    	  	+ "<script src=\"" + libsUri + "/ext-2.0.2/adapter/ext/ext-base.js\" type=\"text/javascript\" />\n"
    	  	+ "<script src=\"" + libsUri + "/ext-2.0.2/ext-all.js\" type=\"text/javascript\" />\n"
    	  	+ "<script src=\"" + libsUri + "/ext-2.0.2/color-field.js\" type=\"text/javascript\" />\n"
      	  	
      	  	+ "<style media=\"screen\" type=\"text/css\">\n"
      	  	+ "@import url(\"" + libsUri + "/ext-2.0.2/resources/css/ext-all.css\");\n"
      	  	+ "@import url(\""+ libsUri + "/ext-2.0.2/resources/css/xtheme-darkgray.css\");\n"
      	  	+ "</style>\n"
      	 
      	  	//+ headExtentions

      	  	+ "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + EDITOR_URL_PREFIX + "css/theme_norm.css\" type=\"text/css\" />\n"
      	  	+ "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + EDITOR_URL_PREFIX + "css/theme_norm_signavio.css\" type=\"text/css\" />\n"
      	  	+ "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + explorerUri + "/src/css/xtheme-smoky.css\" type=\"text/css\" />\n"
      	  	+ "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + explorerUri + "/src/css/custom-style.css\" type=\"text/css\" />\n"
      	  
    	  	+ "<!-- oryx editor -->\n"
      	  	// EN_US is default an base language
      	  	+ "<!-- language files -->\n"
      	  	+ "<script src=\"" + EDITOR_URL_PREFIX + "i18n/translation_en_us.js\" type=\"text/javascript\" />\n"   
      	  	+ "<script src=\"" + EDITOR_URL_PREFIX + "i18n/translation_signavio_en_us.js\" type=\"text/javascript\" />\n"      	  	
      	  	+ languageFiles
      	  	+ "<script src=\"" + libsUri + "/utils.js\" type=\"text/javascript\" />\n"
      	  	+ "<script src=\"" + EDITOR_URL_PREFIX + "oryx.debug.js\" type=\"text/javascript\" />\n"
      	  	
      	  	+ "<!-- erdf schemas -->\n"
      	  	+ "<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />\n"
      	  	+ "<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/\" />\n"
      	  	+ "<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />\n"
      	  	+ "<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />\n"
      	  	+ "<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />\n"
      	  	+ "</head>\n"
      	  	
      	  	+ "<body style=\"overflow:hidden;\">\n" 
      	  	+ "</body>\n"
      	  	+ "</html>";
    }
    
    @SuppressWarnings("unchecked")
	private String getJSONString(String id, String revision, FsAccessToken token, HttpServletRequest req) {
    	
    	JSONObject result = new JSONObject();
		
    	try {
    		String idInContext = (String) this.getServletContext().getAttribute(id);
    		FsSecureBusinessObject sbo = null;
    		if(idInContext != null) {
    			sbo = FsSecurityManager.getInstance().loadObject(idInContext, token);
    		} else {
    			sbo = FsSecurityManager.getInstance().loadObject(id, token);
    		}
    		
    		String modelData = null;
    		String directory = null;
    		
    		String name = "";
    		String description = "";
    		
    		FsModelRevision rev;
    		
    		//id references a model (HEAD revision)
    		if(sbo instanceof FsModel) {
    			FsModel model = (FsModel) sbo;
    			
    			name = model.getName();
    			
    			description = model.getDescription();
    			
    			if(revision != null) {
    				rev = model.getRevision(Integer.parseInt(revision));
    			} else {
    				rev = model.getHeadRevision();
    			}
    			
    			FsDirectory dir = model.getParentDirectory();
    			
    			directory = dir.getId();
    		
    		//id referencens a model revision
    		} 
//    		else if (sbo instanceof ModelRevision) {
//    			
//    			rev = (ModelRevision) sbo;
//    			
//    			Model model = (Model) SecurityManager.getInstance().loadObject(rev.getModelId(), token);
//    			
//    			name = model.getName();
//    			
//    			description = model.getDescription();
//    			
//    			Directory dir = model.getParentDirectory();
//    			
//    			directory = dir.getId();
//    		
//    			//id is neither a model, nor a model revision
//    		} 
    		else {
    			throw new RequestException("editor.invalidIdException");
    		}
    		
    		//get model data (json)
			try {
				modelData = new String(rev.getRepresentation(RepresentationType.JSON).getContent(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RequestException("editor.unsupportedEncodingException");
			}
			
			try {
				result.put("modelId", id);
				
				result.put("parent", directory);
				
				JSONObject modelJSON = new JSONObject(modelData);
				
				result.put("model", modelJSON);
				result.put("name", name);
				result.put("description", description);
				
				result.put("modelHandler", getModelHandlerURI(req) );
				
				result.put("revision", rev.getRevisionNumber());
				
				result.put("versioning", true);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result.toString();
			
    	} catch (BusinessObjectDoesNotExistException e) {
    		//new model. return a json stub with the info in the session
    		Map<String, String> params = (Map<String, String>) req.getSession().getAttribute(id);
    		
    		try {
				result.put("modelId", id);
				
    			result.put("parent", (String)params.get("directory"));
    			
    			String stencilset = (String)params.get("stencilset");
    			String[] extensions;
    			if (params.containsKey("extensions")){
    				String str = params.get("extensions");
    				extensions = str.split(";");
    			} else {
    				extensions = new String[] {};
    			}

				result.put("model", createJSONStub(stencilset, extensions));
				
				result.put("name", "New Model");
				result.put("description", "");
				
				result.put("modelHandler", getModelHandlerURI(req) );
				
				result.put("revision", 0);
				
				result.put("new", true);
				
				result.put("versioning", true);
				
			} catch (JSONException e1) {
				throw new JSONRequestException(e1);
			}
			
			return result.toString();
    	}
    }
    
    private JSONObject createJSONStub(String ssnamespace, String[] extensions) {
    	JSONObject modelData = new JSONObject();
    	try {
			modelData.put("resourceId", "canvas");
			modelData.put("id", "canvas");
			
			JSONObject ssJSON = new JSONObject();
			ssJSON.put("namespace", ssnamespace);
			
			modelData.put("stencilset", ssJSON);
			
			
			if(extensions != null && extensions.length > 0) {
				JSONArray ext = new JSONArray();
				for(int i = 0; i < extensions.length; i++) {
					ext.put(extensions[i]);
				}
				modelData.put("extensions", ext);
				modelData.put("ssextensions", ext);
			}
			
			return modelData;
		} catch (JSONException e) {
			throw new RequestException("editor.creatingJSONStubFailed");
		}
    	
    }
    
    private String getModelHandlerURI(HttpServletRequest req) {
    	return req.getRequestURI().replace(this.getHandlerURI(), "") + "/model";
    }
    
    private boolean isSupported(HttpServletRequest req) {
    	String userAgent = req.getHeader("User-Agent");
  		Matcher m = SUPPORTED_USER_AGENT_PATTERN.matcher(userAgent);
  		return m.find();
    }
}
