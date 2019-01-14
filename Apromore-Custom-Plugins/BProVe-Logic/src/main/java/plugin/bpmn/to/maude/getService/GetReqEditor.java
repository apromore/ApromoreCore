package plugin.bpmn.to.maude.getService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import plugin.bpmn.to.maude.handlers.PostMultipleParameters;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;


/**
 * Created by Fabrizio Fornari on 18/12/2017.
 */
/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated resources.
 */
@SuppressWarnings("deprecation")
public class GetReqEditor {

	static String property=null;
	static String poolName=null;
	static String taskName1=null , taskName2=null;
	static String sndMsgName=null , rcvMsgName=null;
	//LOCAL
	//static String defaultAddress = "http://localhost:8080/";
	static String defaultAddress = "http://pros.unicam.it:8080/";


	public void process(final HttpResponse response, final HttpContext context)
	            throws HttpException, IOException {
	        if (response == null) {
	            throw new IllegalArgumentException("HTTP response may not be null");
	        }
	        if (context == null) {
	            throw new IllegalArgumentException("HTTP context may not be null");
	        }
	        // Always drop connection after certain type of responses
	        int status = response.getStatusLine().getStatusCode();
	        if (status == HttpStatus.SC_BAD_REQUEST ||
	                status == HttpStatus.SC_REQUEST_TIMEOUT ||
	                status == HttpStatus.SC_LENGTH_REQUIRED ||
	                status == HttpStatus.SC_REQUEST_TOO_LONG ||
	                status == HttpStatus.SC_REQUEST_URI_TOO_LONG ||
	                status == HttpStatus.SC_SERVICE_UNAVAILABLE ||
	                status == HttpStatus.SC_NOT_IMPLEMENTED) {
	            response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
	            return;
	        }
	        // Always drop connection for HTTP/1.0 responses and below
	        // if the content body cannot be correctly delimited
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
	            if (entity.getContentLength() < 0 &&
	                    (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
	                response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
	                return;
	            }
	        }
	        // Drop connection if requested by the client
	        HttpRequest request = (HttpRequest)
	            context.getAttribute(ExecutionContext.HTTP_REQUEST);
	        if (request != null) {
	            Header header = request.getFirstHeader(HTTP.CONN_DIRECTIVE);
	            if (header != null) {
	                response.setHeader(HTTP.CONN_DIRECTIVE, header.getValue());
	            }
	        }
	    }
	  

	public static PostMultipleParameters PostReq_BProve_Maude_WebService_Property( PostMultipleParameters inputM) throws Exception {
        String address=null;
        boolean parse = false;
        if(inputM.getProperty()==null) {
        	address = defaultAddress+"BProVe_WebService/webapi/BPMNOS/parseModel";
            parse = true;
        }else{
            address = defaultAddress+"BProVe_WebService/webapi/BPMNOS/model/verification";
            parse = false;
        }

		String jsonString = class2Json(inputM);
		System.out.println("\njsonString: "+jsonString);
		PostMultipleParameters resultM = new PostMultipleParameters();
		
        try {
			URL url = new URL(address);
		    URLConnection con = url.openConnection();
		    HttpURLConnection http = (HttpURLConnection)con;
		    http.setRequestMethod("POST"); // PUT is another valid option
		    http.setDoOutput(true);
		    http.setDoInput(true);
		    http.setRequestProperty("Content-Type", "application/json");
		    http.setRequestProperty("Accept", "application/json");
		    http.setRequestMethod("POST");
		    
		    
		        
		    byte[] out = jsonString.getBytes(StandardCharsets.UTF_8);
		    int length = out.length;

		    http.setFixedLengthStreamingMode(length);
		    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		    http.connect();
		    try(OutputStream os = http.getOutputStream()) {
		        os.write(out);
		        os.close();
		    }		
		    
		     BufferedReader inB = new BufferedReader(
		                                    new InputStreamReader(
		                                    		http.getInputStream()));
		        String decodedString=new String();
		        for (String line; (line = inB.readLine()) != null; decodedString += line);
				inB.close();
				resultM=json2Class(decodedString);	
				System.out.println("\ndecodedString: "+decodedString);
        } catch (Exception e2) {
            e2.printStackTrace();
           // JOptionPane.showMessageDialog(null, "e2.printStackTrace();\n" + e2);
		}


        return resultM;

	}
	
	public static String class2Json(PostMultipleParameters pModel){
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {

			// Convert object to JSON string
			jsonInString = mapper.writeValueAsString(pModel);

		} catch (JsonGenerationException e123) {
			e123.printStackTrace();
		} catch (JsonMappingException e123) {
			e123.printStackTrace();
		} catch (IOException e123) {
			e123.printStackTrace();
		}
			return jsonInString;
	   }
			
	   
	   private static PostMultipleParameters json2Class(String jsonString) {
			ObjectMapper mapper = new ObjectMapper();

			PostMultipleParameters jsonPostMultipleParameters = new PostMultipleParameters();
			
			try {
			
				jsonPostMultipleParameters = mapper.readValue(jsonString, PostMultipleParameters.class);
				

			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return jsonPostMultipleParameters;
		}


}
