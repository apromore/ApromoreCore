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
package com.signavio.platform.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.util.RequestUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.IORequestException;
import com.signavio.platform.exceptions.JSONRequestException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.servlets.DispatcherServlet;

/**
 * AbstractHandler describes the highlevel class of an Handler.
 * @author Willi
 *
 */
public abstract class AbstractHandler {
	
	/**
	 * Define some variables
	 */
	private ServletContext servletContext;

	protected final String SERVER_URL;

	protected final String PLATFORM_URI;
	
	/**
	 * Constructor
	 * @param servletContext
	 */
	public AbstractHandler(ServletContext servletContext) {
		this.servletContext = servletContext;
		
		SERVER_URL = Platform.getInstance().getPlatformProperties().getServerName();
		
		PLATFORM_URI = servletContext.getContextPath() + "/p";
	}
	
	/**
	 * Get the servlet context
	 * @return
	 */
	protected ServletContext getServletContext() {
		return this.servletContext;
	}
	
	/**
	 * Returns the server url
	 * @param req
	 * @return <scheme>://<server name>(:<port>)/ (Port only if it is not the default port)
	 */
	protected String getServerURL() {
		return SERVER_URL;
	}
	
	/**
	 * Returns the absolute path to the root directory of the webapps folder
	 * @return Directory string of the root folder
	 */
    protected String getRootDirectory() {
    	return this.getServletContext().getRealPath("");
    }
    
    /**
     * Returns the full path to the server's root directory
     * (for tomcat, it's the webapps folder).
     * @return The full path to the server's root directory.
     */
    protected String getServerRootPath() {
    	String realPath = this.getServletContext().getRealPath("");
    	File backendDir = new File(realPath);
    	return backendDir.getParent();
    }
    
    /**
     * Get a representation from the resource
     * @param id
     * @param params
     * @return
     */
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		return null;
	}

	/**
	 * Get the representation from not a particular resource
	 * @param params
	 * @return
	 */
	public Object getRepresentation(Object params, FsAccessToken token){
		return null;
	}


	/**
	 * Creates a new representation and returns the initial resources back
	 * @param params
	 * @return
	 * @throws InvalidIdentifierException 
	 */
	public Object postRepresentation(Object params, FsAccessToken token) {
		return null;
	}
	
	/**
	 * Set the representation for a resource
	 * @param id
	 * @param params
	 */
	public <T extends FsSecureBusinessObject> Object putRepresentation(T sbo, Object params, FsAccessToken token) {
		return null;
	}
	
	/**
	 * Set the representation for a resource
	 * @param id
	 * @param params
	 */
	public <T extends FsSecureBusinessObject> void deleteRepresentation(T sbo, Object params, FsAccessToken token) {

	}
		
	
	/**
	 * Implementation of a GET request
	 * @param req
	 * @param res
	 * @param identifier
	 * @throws Exception
	 */
    public <T extends FsSecureBusinessObject> void doGet(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  		
  		// Check if getRepresentation is activated
  		if( isRepresenationActivated("get", sbo != null) )
  		{
  			// Set status code and write the representation
  			res.setStatus(200);
  			res.setContentType("application/json");
  			if( sbo != null ){
  				try{
  					writeOutput( this.getRepresentation(sbo, req.getAttribute("params"), token), res);
  				} catch (JSONException e) {
  					throw new JSONRequestException(e);
  				} catch (IOException e) {
  					throw new IORequestException(e);
  				}
  			} else {
  				try {
					writeOutput( this.getRepresentation(req.getAttribute("params"), token), res );
				} catch (JSONException e) {
					throw new JSONRequestException(e);
				} catch (IOException e) {
					throw new IORequestException(e);
				}
  			}
  		} 
  		else 
  		{
  			// If it is not activated, 405 - Method Not Allowed
  			res.setStatus(405);
  		}
	}

	/**
	 * Implementation of a POST request
     * @param req
     * @param res
	 * @param identifier
     * @throws Exception
     */
    public <T extends FsSecureBusinessObject> void doPost(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  		
    	// Get the parameter list
    	JSONObject parameter = (JSONObject)req.getAttribute("params");
  		
    	// FORM-HTML-PUT-HACK - Check if the parameter method is put
    	try {
			if( (parameter.has("method") && parameter.getString("method").equals("put")) ||
				(parameter.has("_method") && parameter.getString("_method").equals("put"))){
				// If so DO PUT
				this.doPut(req, res, token, sbo);
				return;
			}
		} catch (JSONException e) {
			throw new RequestException("platform.jsonexception", e);
		}
    	
  		// Check if getRepresentation is activated
  		if( sbo == null && isRepresenationActivated("post", false) )
  		{
  			// Set status code and write the representation
			res.setStatus(200);
			try {
				writeOutput(this.postRepresentation(parameter, token), res);
			} catch (JSONException e) {
				throw new JSONRequestException(e);
			} catch (IOException e) {
				throw new IORequestException(e);
			}
  		} 
  		else 
  		{
  			// Post with an identifier is not implemented
  			throw new RequestException("platform.abstractHanlder.postWithIdNotAllowed");
  		} 		
	}
 
    /**
	 * Implementation of a PUT request
     * @param req
     * @param res
	 * @param identifier
     * @throws Exception
     */
    public <T extends FsSecureBusinessObject> void doPut(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {

  		// Check if getRepresentation is activated
  		if( sbo != null && isRepresenationActivated("put", true) )
  		{
  			// Set status code and write the representation
			try{
				res.setStatus(200);
				writeOutput(this.putRepresentation(sbo, req.getAttribute("params"), token), res);
			} catch (JSONException e) {
				throw new JSONRequestException(e);
			} catch (IOException e) {
				throw new IORequestException(e);
			}
  		} 
  		else 
  		{
  			// If it is not activated, 405 - Method Not Allowed
  			res.setStatus(405);
  		}
  		
	}
    
    /**
	 * Implementation of a DELETE request
     * @param req
     * @param res
	 * @param identifier
     * @throws Exception
     */
    public <T extends FsSecureBusinessObject> void doDelete(HttpServletRequest req, HttpServletResponse res, FsAccessToken token, T sbo) {
  		
  		// Check if getRepresentation is activated
  		if( sbo != null && isRepresenationActivated("delete", true) )
  		{
  			// Set status code and write the representation
			try{
				res.setStatus(200);
				this.deleteRepresentation(sbo, req.getAttribute("params"), token);
				writeOutput(new JSONObject("{\"success\":true}"), res);
			} catch (JSONException e) {
				throw new JSONRequestException(e);
			} catch (IOException e) {
				throw new IORequestException(e);
			}
  		} 
  		else 
  		{
  			// If it is not activated, 405 - Method Not Allowed
  			res.setStatus(405);
  		}
	}
    
    /**
     * Check if the given method name is activated with the annotation HandlerMethodActivation
     * @param method
     * @param includesIndentifier
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
	private Boolean isMethodActivated( String method, Class[] args ) throws SecurityException, NoSuchMethodException{
//    	return this.getClass().getMethod( method, args).getAnnotation(HandlerMethodActivation.class) != null;
    	return true;
    }
    
    /**
     * Checks if the representation is activated
     * @param type Type of the representation function
     * @param includesIdentifier Specify if the identifier is included in the request
     * @return True if its activate
     */
    protected Boolean isRepresenationActivated( String type, Boolean includesIdentifier ){
    	try {
			return isMethodActivated(type + "Representation", (includesIdentifier ? new Class[]{ FsSecureBusinessObject.class, Object.class, FsAccessToken.class} : new Class[]{ Object.class, FsAccessToken.class}));
		} catch (Exception e) { 
			return false; 
		}
    }
    
    /**
     * Writes an object to the writer of the response
     * @param o
     * @param res
     * @throws JSONException
     * @throws IOException
     */
    private void writeOutput(Object o, HttpServletResponse res) throws JSONException, IOException{
		if( o instanceof JSONObject ){
			writeOutput((JSONObject)o, res);
		} else if( o instanceof JSONArray ){
			writeOutput((JSONArray)o, res);
		}
	}
    
    /**
     * Writes an JSONObject to the writer of the response
     * @param o
     * @param res
     * @throws JSONException
     * @throws IOException
     */
    private void writeOutput(JSONObject o, HttpServletResponse res) throws JSONException, IOException{
    	o.write( res.getWriter() );
    }
    
    /**
     * Writes an JSONArray to the writer of the response
     * @param o
     * @param res
     * @throws JSONException
     * @throws IOException
     */
    private void writeOutput(JSONArray o, HttpServletResponse res) throws JSONException, IOException{
    	o.write( res.getWriter() );
    }    
    
    /**
     * Parses all the request parameter out and creates a JSONObject.
     * @param req The HttpRequest
     * @deprecated implementation moved to parseParameters filter
     * @return
     */
    @Deprecated
    @SuppressWarnings({ "unchecked", "deprecation" })
	protected Object parseParam(HttpServletRequest req){
    	
    	Object p = req.getAttribute("signavio_request_params");
    	if(p != null) {
    		return (JSONObject)p;
    	}
    	
    	JSONObject j = new JSONObject();
    	try {
	    	// Get the preceding string after the specifies URL
			String[] path 	= DispatcherServlet.parseURL( req.getRequestURI() );
	    	if( path[3] != null && !path[3].equals("") ){
				j.put( "suffix", URLDecoder.decode( path[3], "utf-8"));
			}

	    	// Try to convert the content of the request to a JSONObject
			String content = this.convertStreamToString(req.getInputStream());
			if( content != null && content.length() >= 1 ){
				Map<String, String> ps = new HashMap<String, String>();
				RequestUtil.parseParameters(ps, content, "UTF-8");
				JSONObject jc = new JSONObject(ps);
				Iterator<String> keys = jc.keys();
				while( keys.hasNext() ){
					String key = keys.next();
					j.put(key, ((String[])jc.get(key)).length == 1 ? ((String[])jc.get(key))[0] : jc.get(key));
				}
			}
			
    		// Get all parameter and add those to the list
    		Enumeration<String> paramNames = req.getParameterNames();
	    	while( paramNames.hasMoreElements() ){
	    		String key 		= paramNames.nextElement();
	    		String[] values = req.getParameterValues(key);
	    		
	    		if( values.length == 1 ){
					j.put( key, values[0] );
	    		} else if( values.length > 1 ){
					j.put( key, new JSONArray(values) );
	    		}   		
	    	}

		} catch (Exception e) {} 
		
		req.setAttribute("signavio_request_params", j);
		
		return j;
    	
    }
    
    @Deprecated
    private String convertStreamToString(InputStream is) {

    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            String separator = "";
        	while ((line = reader.readLine()) != null) {
                sb.append(separator + line);
                separator = "\n";
            }
            is.close();
        } catch (IOException e) {
        	try {
                is.close();
             } catch (IOException e2) {}
        	throw new RequestException("platform.stream2StringFailed", e);
        }
 
        return sb.toString();
    }

    
    /**
     * Get the HandlerConfiguration Annotation
     * @return Annotation
     */
    protected HandlerConfiguration getHandlerConfiguration(){
    	return this.getClass().getAnnotation(HandlerConfiguration.class);
    }
    
    /**
     * Get the URI out of the Annotation
     * @return URI from Annotation
     */
    protected String getHandlerURI(){
    	return getHandlerConfiguration().uri();
    }
    
    protected JSONObject generateResource( String rel, String url, Object rep){
		JSONObject j = new JSONObject();
		try {
			j.put("rel", rel);		
			j.put("href", url);
			j.put("rep", rep);
		} catch (JSONException e) {}

		return j;
    }
    
    protected void writeFileToResponse(File file, HttpServletResponse res) throws IOException {
    	PrintWriter out = null;
    	BufferedReader reader = null;
    	
    	try {
  			out = res.getWriter();
  			
  			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while (( line = reader.readLine()) != null){
		          out.append(line);
		          //TODO @jan-felix: wieso nicht \n ?
		          out.append(System.getProperty("line.separator"));
		    }
  		} finally {
  			try {
  				out.close();
  				reader.close();
  			} catch(IOException e) {
  				
  			}
  		}
    }
    
    
    /**
     * Adds default objects as attributes to the request, that are usually 
     *  necessary in our jsp files.
     * @param req
     */
    protected void addJSPAttributes(HttpServletRequest req) {
    	req.setAttribute("explorer_url", Platform.getInstance().getPlatformProperties().getExplorerUri());
    	req.setAttribute("editor_url", Platform.getInstance().getPlatformProperties().getEditorUri());
    	req.setAttribute("libs_url", Platform.getInstance().getPlatformProperties().getLibsUri());
//		req.setAttribute("translation", TranslationFactory.getTranslation(req));
    }
    
    @SuppressWarnings("unchecked")
	public static String getParameter(HttpServletRequest req, String key) {
    	Map<String, Collection<String>> params = 
    		(Map<String, Collection<String>>) req.getAttribute("javaParams");
    	
    	Collection<String> values = params.get(key);
    	if (values != null) {
			return values.iterator().next();
		} else {
			return null;
		}
    }
    
    /**
     * Returns a Map containing all parameters of the request created by the ParametersFilter
     * @param req
     * @return
     */
    @SuppressWarnings("unchecked")
	protected static Map<String, Collection<String>> getJavaParams(HttpServletRequest req) {
    	Map<String, Collection<String>> params = 
    		(Map<String, Collection<String>>) req.getAttribute("javaParams");
    	return params;
    }
    
    /**
     * Renders a jsp with the given name from the WEB-INF/jsp directory and sends it to the user.
     * The JSP is initialized with default attributes like e.g. translation
     * @param pageName
     * @param req
     * @param res
     */
    protected void renderJsp(String pageName, HttpServletRequest req, HttpServletResponse res) {
		
    	try {
    		addJSPAttributes(req);
			req.getRequestDispatcher("/WEB-INF/jsp/" + pageName).include(req, res);
		} catch (ServletException e) {
			throw new RequestException("servletException", e);
		} catch (IOException e) {
			throw new IORequestException(e);
		}
    }
}
