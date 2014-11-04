package org.processmining.plugins.signaturediscovery.encoding;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author R.P. Jagadeesh Chandra 'JC' Bose
 * @date 14 July 2010 
 * @since 01 July 2010
 * @version 1.0
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @copyright R.P. Jagadeesh Chandra 'JC' Bose
 * 			  Architecture of Information Systems Group (AIS) 
 * 			  Department of Mathematics and Computer Science
 * 			  University of Technology, Eindhoven, The Netherlands
 */

/*
 * InstanceVector is equivalent to one trace, but not containing the encoded trace
 * Here it only contains the vector transformed from the trace with features as fields 
 * and feature frequency in the trace as cells
 * And the trace label
 */
public class InstanceVector {
	/*
	 * A map of those features in the featureSet which are found in the encoded trace
	 * Those features in the featureSet not found in the trace are not in here (zero frequency)
	 * Key: a feature (as pattern) in the featureSet and found in the encoded trace
	 * Value: frequency count of feature in the encoded trace
	 * Example
	 * (ab0ab1,2), (cd0cd1, 3), (de0de1de2,2)
	 */
	Map<String, Integer> featureCountMap;
	
	/*
	 * Similar to featureCountMap, but for alphabet feature
	 * The pattern here is represented as a set, not string as in case of sequence features
	 * Example: 
	 * ("ab0","ab1","ab2"),5
	 * ("ab0","ab3"),3
	 * ("cd0","cd1","cd2","cd3"),8 
	 */
	Map<Set<String>, Integer> featureAlphabetCountMap;
	
	/*
	 * Array contains the numerical frequency count of every feature in this trace
	 * This is for all features in the feature set, including those not found in the trace (zero frequency)
	 */
	int[] standardizedNumericVector;
	
	
	/*
	 * Array contains either 1 if feature found in the trace or 0 if not found
	 * For all features in the feature set. 
	 */
	int[] standardizedNominalVector;
	
	/*
	 * Trace label
	 */
	String label;
	
	/*
	 * Bruce 27 May 2014
	 * 
	 */
	String name; //Trace ID
	String encodedTrace; 
	
	public InstanceVector(){
		
	}
	
	public Map<String, Integer> getSequenceFeatureCountMap(){
		return featureCountMap;
	}
	
	public Map<Set<String>, Integer> getAlphabetFeatureCountMap(){
		return featureAlphabetCountMap;
	}
	
	public void setSequenceFeatureCountMap(Map<String, Integer> featureCountMap){
		this.featureCountMap = featureCountMap;
	}
	
	public void setAlphabetFeatureCountMap(Map<Set<String>, Integer> featureAlphabetCountMap){
		this.featureAlphabetCountMap = featureAlphabetCountMap;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	/*
	 * Bruce 27 May 2014
	 */
	public String getName(){
		return name;
	}
	
	/*
	 * Bruce 27 May 2014
	 */
	public void setName(String name){
		this.name = name;
	}	
	
	/*
	 * Bruce 27 May 2014
	 */
	public String getEncodedTrace(){
		return encodedTrace;
	}
	
	/*
	 * Bruce 27 May 2014
	 */
	public void setEncodedTrace(String encode){
		this.encodedTrace = encode;
	}	
	
	public <E> int[] toNumericAttributeVector(List<E> featureList){
		int[] attributeVector = new int[featureList.size()];
		int noFeatures = featureList.size();
		for(int i = 0; i < noFeatures; i++){
			if(featureCountMap != null && featureCountMap.containsKey(featureList.get(i)))
				attributeVector[i] = featureCountMap.get(featureList.get(i));
			else if(featureAlphabetCountMap != null && featureAlphabetCountMap.containsKey(featureList.get(i)))
				attributeVector[i] = featureAlphabetCountMap.get(featureList.get(i));
			else
				attributeVector[i] = 0;
		}
		return attributeVector;
	}
	
	public <E> String[] toNominalAttributeVector(List<E> featureList){
		String[] attributeVector = new String[featureList.size()];
		int noFeatures = featureList.size();
		for(int i = 0; i < noFeatures; i++){
			if(featureCountMap != null && featureCountMap.containsKey(featureList.get(i)))
				attributeVector[i] = "Y";
			else if(featureAlphabetCountMap != null && featureAlphabetCountMap.containsKey(featureList.get(i)))
				attributeVector[i] = "Y";
			else
				attributeVector[i] = "N";
		}
		return attributeVector;
	}
	
	/*
	 * Convert to string format for Weka .arff file
	 */	
	public String toStringNumericVector(int[] numericVector){
		String str = numericVector[0]+"";
		for(int i = 1; i < numericVector.length; i++)
			str = str+","+numericVector[i];
		str += ","+label;
		return str;
	}
	
	/*
	 * Convert to string format for Weka .arff file
	 */	
	public String toStringNominalVector(String[] nominalVector){
		String str = nominalVector[0];
		for(int i = 1; i < nominalVector.length; i++)
			str = str+","+nominalVector[i];
		str += ","+label;
		return str;
	}
	
	/*
	 * Convert to string format for Weka .arff file
	 */	
	public String toStringStandarizedNumericVector(){
		String str = standardizedNumericVector[0]+"";
		for(int i = 1; i < standardizedNumericVector.length; i++)
			str = str+","+standardizedNumericVector[i];
		str += ","+label;
		return str;
	}
	
	/*
	 * Convert to string format for Weka .arff file
	 */
	public String toStringStandarizedNominalVector(){
		String str = standardizedNominalVector[0]+"";
		for(int i = 1; i < standardizedNominalVector.length; i++)
			str = str+","+standardizedNominalVector[i];
		str += ","+label;
		return str;
	}
	
	/*
	 * featureList for sequence feature: (ab0ab1, cd0cd1cd2, ef0ef1, de0de1, ...)
	 * featureList for alphabet feature: ((ab0,ab1), (cd0,cd1,cd2), (ef0,ef1), (de0,de1), ...)
	 * Return: standardizedNumericVector array
	 * Array of frequency count for every feature in the featureList (including those with zero frequency)
	 * Note that: either sequence or alphabet feature can be selected at a time, not both.  
	 */
	public <E> void standarizeNumericVector(List<E> featureList){
//		Logger.printCall("Calling InstanceVector->standardizeNumericVector()");
		int noFeatures = featureList.size();
		standardizedNumericVector = new int[noFeatures];
		
		for(int i = 0; i < noFeatures; i++){
			if(featureCountMap != null && featureCountMap.containsKey(featureList.get(i)))
				standardizedNumericVector[i] = featureCountMap.get(featureList.get(i));
			else if(featureAlphabetCountMap != null && featureAlphabetCountMap.containsKey(featureList.get(i)))
				standardizedNumericVector[i] = featureAlphabetCountMap.get(featureList.get(i));
			else
				standardizedNumericVector[i] = 0;
		}
//		Logger.printReturn("Returning InstanceVector->standardizeNumericVector()");
	}
	
	/*
	 * Similar to standarizeNumericVector
	 */
	public <E> void standarizeNominalVector(List<E> featureList){
//		Logger.printCall("Calling InstanceVector->standardizeNominalVector()");
		
		int noFeatures = featureList.size();
		standardizedNominalVector = new int[noFeatures];
		
		for(int i = 0; i < noFeatures; i++){
			if(featureCountMap != null && featureCountMap.containsKey(featureList.get(i)))
				standardizedNominalVector[i] = 1;
			else if(featureAlphabetCountMap != null && featureAlphabetCountMap.containsKey(featureList.get(i)))
				standardizedNominalVector[i] = 1;
			else
				standardizedNominalVector[i] = 0;
		}
		
//		Logger.printReturn("Returning InstanceVector->standardizeNominalVector()");
	}

	public int[] getStandardizedNumericVector() {
		return standardizedNumericVector;
	}

	public void setStandardizedNumericVector(int[] standardizedVector) {
		this.standardizedNumericVector = standardizedVector;
	}
	
	public int[] getStandardizedNominalVector() {
		return standardizedNominalVector;
	}

	public void setStandardizedNominalVector(int[] standardizedVector) {
		this.standardizedNominalVector = standardizedVector;
	}
}
