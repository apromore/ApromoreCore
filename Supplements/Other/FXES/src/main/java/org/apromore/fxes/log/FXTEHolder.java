package org.apromore.fxes.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.fxes.utils.XsDateTimeConversion;
import org.apromore.fxes.utils.XsDateTimeConversionJava7;

import gnu.trove.list.array.TIntArrayList;

/**
 * 
 * @author Alireza Ostovar (Alirezaostovar@gmail.com)
 *
 */
public class FXTEHolder {
	
	private int trCount = 0;
	private int evCount = 0;
	
	private Map<String, Integer> trAttrsIndM, evAttrsIndM;
	private Map<String, FXAttributeType> trAttrsTypeM, evAttrsTypeM;
	private List<Object> trAttrsVals, evAttrsVals;
	private TIntArrayList evIndCeil;
	
	private List<Object> trAttrVals_c, evAttrVals_c;
	
	private XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversionJava7();
	
	public FXTEHolder()
	{
		trAttrsIndM = new HashMap<String, Integer>();
		evAttrsIndM = new HashMap<String, Integer>();		

		trAttrsTypeM = new HashMap<String, FXAttributeType>();
		evAttrsTypeM = new HashMap<String, FXAttributeType>();		
		
		trAttrsVals = new ArrayList<Object>();
		evAttrsVals = new ArrayList<Object>();
		
		evIndCeil = new TIntArrayList();
		
		trAttrVals_c = new ArrayList<Object>();
		evAttrVals_c = new ArrayList<Object>();
		
	}
	
	public FXTEHolder(FXTEHolder a)
	{
		trAttrsIndM = new HashMap<String, Integer>(a.trAttrsIndM);
		evAttrsIndM = new HashMap<String, Integer>(a.evAttrsIndM);		

		trAttrsTypeM = new HashMap<String, FXAttributeType>(a.trAttrsTypeM);
		evAttrsTypeM = new HashMap<String, FXAttributeType>(a.evAttrsTypeM);		
		
		trAttrsVals = new ArrayList<Object>(a.trAttrsVals);
		evAttrsVals = new ArrayList<Object>(a.evAttrsVals);
		
		evIndCeil = new TIntArrayList(a.evIndCeil);
		
		trCount = a.trCount;
		evCount = a.evCount;

	}
	
	/**
	 * Open the holder
	 */
	
	void openTEholder()
	{
		for(int i = 0; i < trAttrsIndM.size(); i++)
		{
			trAttrVals_c.add(null);
		}
		
		for(int i = 0; i < evAttrsIndM.size(); i++)
		{
			evAttrVals_c.add(null);
		}
	}
	
	/**
	 * Create new trace
	 */
	
	void newTrace()
	{
		for(int i = 0; i < trAttrVals_c.size(); i++)
		{
			trAttrVals_c.set(i, null);
		}
	}
	
	/**
	 * Close the last trace
	 */
	
	void closeTrace()
	{	
		trAttrsVals.addAll(trAttrVals_c);
		evIndCeil.add(evCount);
		trCount++;
	}
	
	/**
	 * Create new event
	 */
	
	void newEvent()
	{
		for(int i = 0; i < evAttrVals_c.size(); i++)
		{
			evAttrVals_c.set(i, null);
		}
	}
	
	/**
	 * Close the last event
	 */
	
	void closeEvent()
	{
		evAttrsVals.addAll(evAttrVals_c);
		evCount++;
	}
	
	/**
	 * Add attribute to a trace
	 */
	
	public void addAttributeToTrace(FXAttributeType t, String k, String v) 
	{
		Integer atInd = trAttrsIndM.get(k);		
		
		switch (t)
		{
			case LITERAL:
				trAttrVals_c.set(atInd, v);
				break;
			case DISCRETE:
				trAttrVals_c.set(atInd, Long.valueOf(v));
				break;
			case CONTINUOUS:
				trAttrVals_c.set(atInd, Double.valueOf(v));
				break;
			case BOOLEAN: 
				trAttrVals_c.set(atInd, Boolean.valueOf(v));
				break;
			case TIMESTAMP:
			{
				Date d = xsDateTimeConversion.parseXsDateTime(v);
				trAttrVals_c.set(atInd, d);
				break;
			}
		}
	}
	
	/**
	 * Add attribute to an event
	 */
	
	public void addAttributeToEvent(FXAttributeType t, String k, String v) 
	{
		Integer atIx = evAttrsIndM.get(k);
		
		switch (t)
		{
			case LITERAL:
				evAttrVals_c.set(atIx, v);
				break;
			case DISCRETE:
				evAttrVals_c.set(atIx, Long.valueOf(v));
				break;
			case CONTINUOUS:
				evAttrVals_c.set(atIx, Double.valueOf(v));
				break;
			case BOOLEAN: 
				evAttrVals_c.set(atIx, Boolean.valueOf(v));
				break;
			case TIMESTAMP:
			{
				Date date = xsDateTimeConversion.parseXsDateTime(v);
				evAttrVals_c.set(atIx, date);
				break;
			}
		}
	}
	

	/**
	 * Get number of traces
	 */
	
	int gettrCount() {
		return trCount;
	}
	
	/**
	 * Get number of events
	 */

	int getevCount() {
		return evCount;
	}

	Map<String, Integer> gettrAttrsIndM() {
		return trAttrsIndM;
	}

	Map<String, Integer> getevAttrsIndM() {
		return evAttrsIndM;
	}

	Map<String, FXAttributeType> gettrAttrsTypeM() {
		return trAttrsTypeM;
	}

	Map<String, FXAttributeType> getevAttrsTypeM() {
		return evAttrsTypeM;
	}

	List<Object> gettrAttrsVals() {
		return trAttrsVals;
	}

	List<Object> getevAttrsVals() {
		return evAttrsVals;
	}

	TIntArrayList getevIndCeil() {
		return evIndCeil;
	}

	List<Object> gettAtVals_c() {
		return trAttrVals_c;
	}

	List<Object> geteAtVals_c() {
		return evAttrVals_c;
	}


	void settAtsIndM(Map<String, Integer> tAtsIndM) {
		this.trAttrsIndM = tAtsIndM;
	}

	void setevAttrsIndM(Map<String, Integer> evAttrsIndM) {
		this.evAttrsIndM = evAttrsIndM;
	}

	void settrAttrsTypeM(Map<String, FXAttributeType> trAttrsTypeM) {
		this.trAttrsTypeM = trAttrsTypeM;
	}

	void setevAttrsTypeM(Map<String, FXAttributeType> evAttrsTypeM) {
		this.evAttrsTypeM = evAttrsTypeM;
	}


	
	
	
	
}
