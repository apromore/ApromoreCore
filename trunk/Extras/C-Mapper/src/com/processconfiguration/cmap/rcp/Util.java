package com.processconfiguration.cmap.rcp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.xml.sax.SAXException;

public class Util {
	private static ArrayList<String> valueList;
	
	private static boolean validateXMLagainstSchema(String XMLFile, String SchemaFile){
		try {
			String xsdFile = SchemaFile;
		    String xmlFile = XMLFile;
		      //org.eclipse.emf.common.util.URI xsdURI = null;
		    
		    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	        
	        // 2. Compile the schema. 
	        // Here the schema is loaded from a java.io.File, but you could use 
	        // a java.net.URL or a javax.xml.transform.Source instead.
	        File schemaLocation = new File(xsdFile);
	        
	        
	        Schema schema = factory.newSchema(schemaLocation);
	        
	        Source source = new StreamSource(new File(xmlFile));
	        Validator validator = schema.newValidator();
        
			validator.validate(source);
			
			return true;
			
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			return false;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	
	public static boolean isCorrectQmlFile(String qmlFileName) {
		//-- Here check whether the file is a correct QML File
		if(!Application.ValidateSchema){
			return true;
		}
		else{
			String xsdFile = Application.qmlSchema;
		    String xmlFile = qmlFileName;
		    
		    return validateXMLagainstSchema(xmlFile, xsdFile);
		}
	}
	
	public static boolean isCorrectModelFile(String mfileName) {
		// Validate the Model file
		if(!Application.ValidateSchema){
			return true;
		}
		else{
			String xsdFile = Application.modelSchema;
			String xmlFile = mfileName;
	    
			return validateXMLagainstSchema(xmlFile, xsdFile);
		}
	}
	
	public static boolean isCorrectCmapFile(String cmapFileName) {
		// Validate the CMAP File
		if(!Application.ValidateSchema){
			return true;
		}
		else{
			String xsdFile = Application.cmapSchema;
			String xmlFile = cmapFileName;
	    
			return validateXMLagainstSchema(xmlFile, xsdFile);
		}
	}
	
	public static ArrayList<String> getSortedFactListAsc(ArrayList<String> target){
		ArrayList<String> retValue = target;
		for(int i=0; i<target.size()-1; i++){
			for(int j=i+1; j<target.size(); j++){
				int intIFactNumber = Integer.parseInt(target.get(i).substring(1));
				int intJFactNumber = Integer.parseInt(target.get(j).substring(1));
				if(intJFactNumber<intIFactNumber){
					String temp = target.get(i);
					target.set(i, target.get(j));
					target.set(j, temp);
				}
			}
		}
		
		return retValue;
	}

	public static ArrayList<String> getFactListFromCondition(String condition){
		valueList = new ArrayList<String>();
		getFactListToValueList(condition + " ");

		return valueList;
	}
	
	public static ArrayList<String> getUniqueFactListFromCondition(String condition){
		valueList = new ArrayList<String>();
		getFactListToValueList(condition + " ");
		
		ArrayList<String> uniqueFactList = getUniqueArraylist(valueList);
		return uniqueFactList;
	}
	
	public static ArrayList<String> getUniqueSortedAscFactListFromCondition(String condition){
		valueList = new ArrayList<String>();
		getFactListToValueList(condition + " ");
		
		ArrayList<String> uniqueFactList = getUniqueArraylist(valueList);
		ArrayList<String> uniqueSortedFactList = getSortedFactListAsc(uniqueFactList);
		
		return uniqueSortedFactList;
	}
	
	private static void getFactListToValueList(String condition) {
		// TODO Auto-generated method stub
		int factIndex = condition.indexOf("f");
		if(factIndex>=0){
			//System.out.println(factIndex);
			int endIndex = getNotInterIndex(factIndex, condition);
			if(endIndex == 0) return;
			valueList.add(condition.substring(factIndex, endIndex));
			String newCondition = condition.substring(factIndex+1);
			getFactListToValueList(newCondition);
		}
	}

	
	public static ArrayList<String> getUniqueArraylist(ArrayList<String> target){
		ArrayList<String> uniqueList = new ArrayList<String>();
		for(int t=0; t<target.size(); t++){
			String curValue = target.get(t);
			boolean found = false;
			for(int i =0; i<uniqueList.size();i++){
				if(curValue.equals(uniqueList.get(i))){
					found = true;
				}
			}
	
			if(!found){
				uniqueList.add(curValue);
			}
		}
		
		return uniqueList;
	}
	
	private static int getNotInterIndex(int startIndex, String condition) {
		// TODO Auto-generated method stub
		int retValue = 0;
		String target = condition;
		for(int i = startIndex+1; i<target.length(); i++){
			String singleValue = target.substring(i,i+1);
			if(!isParsableToInt(singleValue)){
				retValue = i;
				break;
			}
		}
		return retValue;
	}
	
	private static boolean isParsableToInt(String i)
	{
		try
		{
			Integer.parseInt(i);
			return true;
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
	}
}
