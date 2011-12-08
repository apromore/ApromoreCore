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
/**
 * 
 */
package com.signavio.platform.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.util.RequestUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.exceptions.InputException;
import com.signavio.platform.exceptions.RequestException;
import com.signavio.platform.servlets.DispatcherServlet;

/**
 * @author Bjoern Wagner
 *
 */
public class ParseParametersFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		
		if (!(req instanceof HttpServletRequest)) {
			chain.doFilter(req, res);
		}		
		HttpServletRequest httpReq = (HttpServletRequest) req;
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		parseUriParameters(httpReq, params);
		parseStreamParameters(httpReq, params);
		parseRequestParameters(httpReq, params);
		filterParameters(params);
		JSONObject jsonParams = parametersToJSONObject(params);
		httpReq.setAttribute("params", jsonParams);
		httpReq.setAttribute("javaParams", params);
		chain.doFilter(httpReq, res);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}
	 
	/**
	 * Helper method that adds a new parameter to the map
	 * @param params
	 * @param key
	 * @param value
	 */
	private void addParameter(Map<String, List<String>> params, String key, String value) {
		
		List<String> values = params.get(key);
		if (values == null) {
			values = new ArrayList<String>();
			params.put(key, values);
		}
		values.add(value);
	}
	
	private void parseUriParameters(HttpServletRequest req, Map<String, List<String>> params) {
    	// get parameters form uri after the extension
		String[] path 	= DispatcherServlet.parseURL( req.getRequestURI() );
    	if( path[3] != null && !path[3].equals("") ){
    		try {
				addParameter(params, "suffix", URLDecoder.decode( path[3], "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace(); // ignore exception
			}
		}
	}
	
	/**
	 * Read all parameters directly from the input stream. Necessary because parameters are not mapped to the 
	 * HttpRequest.getParameter api on PUT requests
	 * @param req
	 * @param params
	 */
	private void parseStreamParameters(HttpServletRequest req, Map<String, List<String>> params) {

		try {
			String content = this.convertStreamToString(req.getInputStream());
			if( content != null && content.length() >= 1 ){
				Map<String, String[]> ps = new HashMap<String, String[]>();
				RequestUtil.parseParameters(ps, content, "UTF-8");
				for (String key : ps.keySet()) {
					List<String> param = new ArrayList<String>();
					for(String value : ps.get(key)) {
						param.add(value);
					}
					params.put(key, param);
				}
			}
		} catch (IOException e) {
			throw new InputException(e);
		}
	}
	
	/**
	 * Parse parameters using the HttpRequest API
	 * @param req
	 * @param params
	 */
	@SuppressWarnings("unchecked")
	private void parseRequestParameters(HttpServletRequest req, Map<String, List<String>> params) {
		
		// get all parameter and add those to the map
		Enumeration<String> paramNames = req.getParameterNames();
    	while( paramNames.hasMoreElements() ){
    		String key 		= paramNames.nextElement();
    		String[] values = req.getParameterValues(key);
    		for (String value : values) {
				addParameter(params, key, value); 
			}	
    	}
	}
	
	/**
	 * Remove code from all parameters except those ending with '_xml'
	 * @param params
	 */
	private void filterParameters(Map<String, List<String>> params) {
		for (String key : params.keySet()) {
			if (!key.endsWith("_xml")&&!key.endsWith("_json")) {
				List<String> filteredValues = new ArrayList<String>();
				for (String	value : params.get(key)) {
					filteredValues.add(RequestUtil.filter(value));
				}
				params.put(key, filteredValues);
			}
		}
	}
	
	/**
	 * Convert map to json object containing strings for single values and a json array
	 * for multiple values
	 * @param params
	 * @return
	 */
	private JSONObject parametersToJSONObject(Map<String, List<String>> params) {
		JSONObject jsonParams = new JSONObject();
		
		for (String key : params.keySet()) {
			try {
				if (params.get(key).size() == 1) {
					jsonParams.put(key, params.get(key).iterator().next());
				} else {
					jsonParams.put(key, new JSONArray(params.get(key)));
				}
			} catch(JSONException e) {
				e.printStackTrace(); // ignore
			}
		}
		return jsonParams;
	}
	
    /**
     * Helper method. converts request input stream to java string
     * @param is
     * @return
     */
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
}
