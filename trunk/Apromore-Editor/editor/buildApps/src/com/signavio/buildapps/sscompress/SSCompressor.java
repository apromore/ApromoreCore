package com.signavio.buildapps.sscompress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class SSCompressor {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1)
			throw new Exception("Missing argument! Usage: java SSCompressor <SSDirectory>");
		
		//get stencil set directory from arguments
		String ssDirString = args[0];
		
		//get stencil set configuration file
		File ssConf = new File(ssDirString + "/stencilsets.json"); 
		
		if(!ssConf.exists())
			throw new Exception("File " + ssDirString + "/stencilsets.json does not exist.");

		//read stencil set configuration
		StringBuffer jsonObjStr = readFile(ssConf);

		JSONArray jsonObj = new JSONArray(jsonObjStr.toString());
		
		//iterate all stencil set configurations
		for(int i = 0; i < jsonObj.length(); i++) {
			JSONObject ssObj = jsonObj.getJSONObject(i);
			
			/*
			 * StencilsSet has to build out of existing stencil set and extension..
			 */
			if (ssObj.has("basestencilset") && ssObj.has("extensionstointegrate")){
				
				if (ssObj.has("uri")) {
					String ssUri = ssObj.getString("uri");
					File ssFile = new File(ssDirString + ssUri);
					
					if (ssFile.exists()){
						throw new Exception("The stencil set (" + ssDirString + ssUri + ") that should be created does already exist.");
					}
					
					ssFile  = createFile(ssDirString, ssUri);
					
					File baseSsFile = getStencilSet(ssObj.getString("basestencilset"), ssConf, ssDirString);
					JSONObject BaseSsJson = new JSONObject( readFile(baseSsFile).toString() );
					copyDataFromDirectory(baseSsFile.getParentFile(), ssFile.getParentFile());
					
					JSONArray extensions = ssObj.getJSONArray("extensionstointegrate");
					List<JSONObject> extensionsJson = new ArrayList<JSONObject>();
					for (int j = 0; j < extensions.length(); j++) {
						File extensionFile = getStencilSetExtension(
								extensions.getString(j), 
								new File(ssDirString + File.separator + "extensions" + File.separator + "extensions.json"), 
								ssDirString + File.separator + "extensions" + File.separator );
						copyDataFromDirectory(extensionFile.getParentFile(), ssFile.getParentFile());
						extensionsJson.add(new JSONObject( readFile(extensionFile).toString() ));
						
					}
					
					JSONObject jsonSS = createJsonFor(ssObj, BaseSsJson, extensionsJson);
					writeFile(ssFile, jsonSS.toString() );
					
				}
				
				
				
			} else if (ssObj.has("extensionstointegrate")){
				String ssUri = ssObj.getString("uri");
				
				File ssFile = new File(ssDirString + ssUri);
				
				if(!ssFile.exists())
					throw new Exception("Stencil set " + ssDirString + ssUri + " that is referenced in stencil set configuration file does not exist.");
				
				JSONArray extensions = ssObj.getJSONArray("extensionstointegrate");
				JSONObject ssJson = new JSONObject( readFile(ssFile).toString() );
				for (int j = 0; j < extensions.length(); j++) {
					File extensionFile = getStencilSetExtension(
							extensions.getString(j), 
							new File(ssDirString + File.separator + "extensions" + File.separator + "extensions.json"), 
							ssDirString + File.separator + "extensions" + File.separator );
					copyDataFromDirectory(extensionFile.getParentFile(), ssFile.getParentFile());
					JSONObject extension = new JSONObject( readFile(extensionFile).toString() );
					if (ssJson.getString("namespace").equals(extension.getString("extends"))) {
						merge(ssJson, extension);
					} else {
						System.out.println("[Warning] could not merge "  + ssJson.getString("namespace") + " with extension " + extension.getString("namespace"));
						
					}
					
				}
				writeFile(ssFile, ssJson.toString());
			}
			
			
			//get stencil set location
			if(ssObj.has("uri")) {
				String ssUri = ssObj.getString("uri");
				
				File ssFile = new File(ssDirString + ssUri);
				
				if(!ssFile.exists())
					throw new Exception("Stencil set " + ssDirString + ssUri + " that is referenced in stencil set configuration file does not exist.");
				
				String ssDir = ssFile.getParent();
				
				//read stencil set file
				StringBuffer ssString = readFile(ssFile);
				
				// store copy of original stencilset file (w/o SVG includes) with postfix '-nosvg'
				int pIdx = ssUri.lastIndexOf('.');
				File ssNoSvgFile = new File(ssDirString + ssUri.substring(0, pIdx) + "-nosvg" + ssUri.substring(pIdx));
				writeFile(ssNoSvgFile, ssString.toString());
				
				//***include svg files***
				
				//get view property
				Pattern pattern = Pattern.compile("[\"\']view[\"\']\\s*:\\s*[\"\']\\S+?[\"\']");
				
				Matcher matcher = pattern.matcher(ssString);
				
				StringBuffer tempSS = new StringBuffer();
				
				int lastIndex = 0;
				
				//iterate all view properties
				while(matcher.find()) {
					tempSS.append(ssString.substring(lastIndex, matcher.start()));
					
					lastIndex = matcher.end();
					
					//get svg file name
					String filename = matcher.group().replaceFirst("[\"\']view[\"\']\\s*:\\s*[\"\']", "");
					filename = filename.substring(0, filename.length()-1);
					
					//get svg file
					File svgFile = new File(ssDir + "/view/" + filename);
					
					if(!svgFile.exists())
						throw new Exception("SVG File " + svgFile.getPath() + " does not exists!. Compressing stencil sets aborted!");
					
					StringBuffer svgString = readFile(svgFile);
					
					//check, if svgString is a valid xml file
					/*try {
						DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
						DocumentBuilder document = builder.newDocumentBuilder();
						document.parse(svgString.toString());
					} catch(Exception e) {
						throw new Exception("File " + svgFile.getCanonicalPath() + " is not a valid XML file: " + e.getMessage());
					}*/
					
					
					//append file content to output json file (replacing existing json file)
					tempSS.append("\"view\":\"");
					tempSS.append(svgString.toString().replaceAll("[\\t\\n\\x0B\\f\\r]", " ").replaceAll("\"", "\\\\\""));
					//newSS.append(filename);
					tempSS.append("\"");
				}
				
				tempSS.append(ssString.substring(lastIndex));
				//***end include svg files***
				
				/*
				 * BAD IDEA, BECAUSE IT INCREASES THROUGHPUT
				
				//***include png files
				//get icon property
				pattern = Pattern.compile("[\"\']icon[\"\']\\s*:\\s*[\"\']\\S+[\"\']");
				
				matcher = pattern.matcher(tempSS);
				
				StringBuffer finalSS = new StringBuffer();
				
				lastIndex = 0;
				
				//iterate all icon properties
				while(matcher.find()) {
					finalSS.append(tempSS.substring(lastIndex, matcher.start()));
					
					lastIndex = matcher.end();
					
					//get icon file name
					String filename = matcher.group().replaceFirst("[\"\']icon[\"\']\\s*:\\s*[\"\']", "");
					filename = filename.substring(0, filename.length()-1);
					
					//get icon file
					File pngFile = new File(ssDir + "/icons/" + filename);
					
					if(!pngFile.exists())
						throw new Exception("SVG File " + pngFile.getPath() + " does not exists!. Compressing stencil sets aborted!");
					
					StringBuffer pngString = readFile(pngFile);
					
					//append file content to output json file (replacing existing json file)
					finalSS.append("\"icon\":\"javascript:");
					finalSS.append(encodeBase64(pngString.toString()));
					finalSS.append("\"");
				}
				
				finalSS.append(tempSS.substring(lastIndex));
				//***end include png files
				*/
				//write compressed stencil set file
				writeFile(ssFile, tempSS.toString());
				
				System.out.println("Compressed stencil set file " + ssFile.getCanonicalPath());
			}
		}
	}





	private static StringBuffer readFile(File file) throws Exception {
		FileInputStream fin =  new FileInputStream(file);
		StringBuffer result = new StringBuffer();

		String thisLine = "";
		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
		
		while ((thisLine = myInput.readLine()) != null) {  
			result.append(thisLine);
			result.append("\n");
		}
		
		myInput.close();
		fin.close();
		
		return result;
	}
	
	private static void writeFile(File file, String text) throws Exception {
		FileOutputStream fos =  new FileOutputStream(file);
		
		BufferedWriter myOutput = new BufferedWriter(new OutputStreamWriter(fos));
		
		myOutput.write(text);
		myOutput.flush();
		
		myOutput.close();
		fos.close();
	}
	
	/*private static String encodeBase64(String text) {
		byte[] encoded = Base64.encodeBase64(text.getBytes());
		
		return new String(encoded);
	}*/
	
	private static JSONObject createJsonFor(JSONObject newStencilSetData, JSONObject baseStencilSetData, List<JSONObject> extensions) throws JSONException {
		JSONObject result = new JSONObject();
		if (newStencilSetData.has("title")) result.put("title", newStencilSetData.getString("title"));
		if (newStencilSetData.has("title_de")) result.put("title_de", newStencilSetData.getString("title_de"));
		if (newStencilSetData.has("namespace")) result.put("namespace", newStencilSetData.getString("namespace"));
		if (newStencilSetData.has("description")) result.put("description", newStencilSetData.getString("description"));
		if (newStencilSetData.has("description_de")) result.put("description_de", newStencilSetData.getString("description_de"));
		
		if (baseStencilSetData.has("propertyPackages")) 
			result.put("propertyPackages", baseStencilSetData.getJSONArray("propertyPackages") );
		if (baseStencilSetData.has("stencils")) 
			result.put("stencils", baseStencilSetData.getJSONArray("stencils") );
		if (baseStencilSetData.has("rules")) 
			result.put("rules", baseStencilSetData.getJSONObject("rules") );
		
		for (JSONObject extension : extensions) {
			if (baseStencilSetData.getString("namespace").equals(extension.getString("extends"))) {
				merge(result, extension);
			} else {
				System.out.println("[Warning] could not merge "  + baseStencilSetData.getString("namespace") + " with extension " + extension.getString("namespace"));
				
			}
		}
		
		return result;
	}

	/**
	 * This method merges the stencil set with an extension according to the
	 * rules used in oryx/editor/client/scripts/Core/StencilSet/stencilset.js.
	 * 
	 * This means, that the following steps are executed:
	 *  - load new stencils
	 *  - load additional properties
	 *  - remove stencil properties
	 *  - remove stencils
	 * in this specific order!
	 * 
	 * As this strict order cannot be used here (due to the missing JSONArray.remove()
	 * method), another order was choosen that produces the same outcome
	 * as the original one (by additional check).
	 */
	private static void merge(JSONObject ssObj, JSONObject sseObj) throws JSONException {
		JSONArray existingStencils = ssObj.getJSONArray("stencils");
		JSONArray newStencilArray = new JSONArray();
		
		
		JSONArray stencilsToBeAdded = getStencilsToBeAdded(sseObj); 
		Map<String, List<JSONObject>> propertiesToBeAdded = getPropertiesToBeAdded(sseObj);
		Map<String, Set<String>> propertiesToBeRemoved = getPropertiesToBeRemoved(sseObj);
		Set<String> stencilsToBeRemoved = getStencilsToBeRemoved(sseObj);
		
		// Add stencils
		for (int i = 0; i < stencilsToBeAdded.length(); i++) {
			JSONObject stencil = stencilsToBeAdded.getJSONObject(i);
			existingStencils.put(stencil);
		}
		// Copy stencils from existing to new list if they should not be removed.
		//  Modify their properties while doing so.
		if (stencilsToBeRemoved.size() > 0 || propertiesToBeRemoved.size() > 0 || propertiesToBeAdded.size() > 0) {
			for (int i = 0; i < existingStencils.length(); i++) {
				JSONObject stencil = existingStencils.getJSONObject(i);
				String stencilId = stencil.getString("id");
				List<String> rolesOfStencil = new ArrayList<String>();
				if (stencil.has("roles")) {
					JSONArray rolesOfStencilArray = stencil.getJSONArray("roles");
					for (int j = 0; j < rolesOfStencilArray.length(); j++) {
						rolesOfStencil.add(rolesOfStencilArray.getString(j));
					}
				} 
				rolesOfStencil.add(stencilId);
				JSONArray existingProperties;
				if (stencil.has("properties")) {
					existingProperties = stencil.getJSONArray("properties");
				} else {
					existingProperties = new JSONArray();
				}
				boolean added = false;
				// ADD PROPERTIES
				for (String role : rolesOfStencil) {
					List<JSONObject> propertiesToBeAddedHere = propertiesToBeAdded.get(role);
					if (propertiesToBeAddedHere != null) {
						for (JSONObject property : propertiesToBeAddedHere) {
							existingProperties.put(property);
							added = true;
						}
					}
				}
				// RETAIN PROPERTIES THAT SHOULD NOT BE DELETED
				Set<String> propertiesToBeRemovedHere = propertiesToBeRemoved.get(stencilId);
				if (propertiesToBeRemovedHere != null && !propertiesToBeRemovedHere.isEmpty()) {
					JSONArray newPropertyArray = new JSONArray();
					for (int j = 0; j < existingProperties.length(); j++) {
						JSONObject propertyObj = existingProperties.getJSONObject(j);
						String propertyId = propertyObj.getString("id");
						if (!propertiesToBeRemovedHere.contains( propertyId )){
							newPropertyArray.put(propertyObj);
						}
					}
					stencil.put("properties", newPropertyArray);
				} else if (added){
					stencil.put("properties", existingProperties);
				}

				// RETAIN STENCILS THAT SHOULD NOT BE DELETED
				if (!stencilsToBeRemoved.contains( stencilId )){
					newStencilArray.put(stencil);
				}
			}
		}
		ssObj.put("stencils", newStencilArray);
	}




	private static JSONArray getStencilsToBeAdded(JSONObject sseObj) throws JSONException {
		if (sseObj.has("stencils")) {
			return sseObj.getJSONArray("stencils");
		} else {
			return new JSONArray();
		}
	}
	
	private static Map<String, List<JSONObject>> getPropertiesToBeAdded(JSONObject sseObj) throws JSONException {
		Map<String, List<JSONObject>> result = new HashMap<String, List<JSONObject>>();
		if (sseObj.has("properties")){
			JSONArray propertiesToBeAdded = sseObj.getJSONArray("properties");
			for (int i = 0; i < propertiesToBeAdded.length(); i++) {
				JSONObject propertyToBeAdded = propertiesToBeAdded.getJSONObject(i);
				JSONArray roles = propertyToBeAdded.getJSONArray("roles");
				JSONArray properties = propertyToBeAdded.getJSONArray("properties");
				for (int j = 0; j < roles.length(); j++) {
					String role = roles.getString(j);
					List<JSONObject> currentList = result.get(role);
					if (currentList == null) {
						currentList = new ArrayList<JSONObject>();
						result.put(role, currentList);
					}
					for (int k = 0; k < properties.length(); k++) {
						JSONObject obj = properties.getJSONObject(k);
						if (!currentList.contains(obj)) {
							currentList.add(obj);
						}
					}
				}
			}
		
		}
		return result;
	}

	private static Set<String> getStencilsToBeRemoved(JSONObject sseObj) throws JSONException {
		Set<String> result = new HashSet<String>();
		if (sseObj.has("removestencils")){
			JSONArray stencilsToBeRemoved = sseObj.getJSONArray("removestencils");
			for (int i = 0; i < stencilsToBeRemoved.length(); i++) {
				result.add(stencilsToBeRemoved.getString(i));
			}
		}
		return result;
	}

	private static Map<String, Set<String>> getPropertiesToBeRemoved(JSONObject sseObj) throws JSONException {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		if (sseObj.has("removeproperties")){
			JSONArray propertiesToBeRemoved = sseObj.getJSONArray("removeproperties");
			for (int i = 0; i < propertiesToBeRemoved.length(); i++) {
				JSONObject propertiesOfStencil = propertiesToBeRemoved.getJSONObject(i);
				String stencilId = propertiesOfStencil.getString("stencil");
				JSONArray properties = propertiesOfStencil.getJSONArray("properties");
				Set<String> propertySet = new HashSet<String>();
				for (int j = 0; j < properties.length(); j++) {
					propertySet.add(properties.getString(j));
				}
				result.put(stencilId, propertySet);
			}
		}
		return result;
	}
	





	private static void copyDataFromDirectory(File from, File to) throws IOException {
		for (File src : from.listFiles()) {
			File target = new File(to.getAbsolutePath() + File.separator + src.getName());
			if (src.isFile()) {
				copyFile(src, target);
			} else if (src.isDirectory()) {
				if (!target.exists()) target.mkdir();
				copyDataFromDirectory(src, target);
			}
		}
	}
	
	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}



	private static File createFile(String existingDir, String newFilePath) throws IOException {
		int i = newFilePath.lastIndexOf("/");
		String head = newFilePath.substring(0, i);
		File f = new File(existingDir + head);
		f.mkdirs();
		f = new File (existingDir + newFilePath);
		f.createNewFile();
		return f;
	}
	
	private static File getStencilSet(String namespace, File ssConf, String ssDirString) {
		
		try {
			StringBuffer jsonObjStr = readFile(ssConf);
			JSONArray jsonObj = new JSONArray(jsonObjStr.toString());
			for(int i = 0; i < jsonObj.length(); i++) {
				JSONObject ssObj = jsonObj.getJSONObject(i);
				if (ssObj.has("namespace") && ssObj.getString("namespace").equals(namespace)) {
					String path = ssDirString + ssObj.getString("uri");
					File ssFile = new File(path.replace(".json", "-nosvg.json"));
					if (!ssFile.exists()) {
						ssFile = new File(path);
					}
					return ssFile;
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot find stencilset with namespace " + namespace + " in " + ssConf.getAbsolutePath() );
		}
		throw new IllegalArgumentException("Cannot find stencilset with namespace " + namespace + " in " + ssConf.getAbsolutePath() );
		
	}
	
	private static File getStencilSetExtension(String namespace, File sseConf, String sseDirString) {
		
		try {
			StringBuffer jsonObjStr = readFile(sseConf);
			JSONObject jsonObj = new JSONObject(jsonObjStr.toString());
			JSONArray extensions = jsonObj.getJSONArray("extensions");
			for(int i = 0; i < extensions.length(); i++) {
				JSONObject sseObj = extensions.getJSONObject(i);
				if (sseObj.has("namespace") && sseObj.getString("namespace").equals(namespace)) {
					String path = sseDirString + sseObj.getString("definition");
					File sseFile = new File(path);
					return sseFile;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot find stencilset extension with namespace " + namespace + " in " + sseConf.getAbsolutePath() );
		}
		throw new IllegalArgumentException("Cannot find stencilset extension with namespace " + namespace + " in " + sseConf.getAbsolutePath() );
		
	}


}
