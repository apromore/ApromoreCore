package org.apromore.fxes.log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apromore.fxes.utils.XsDateTimeConversionJava7;
import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Alireza Ostovar (Alirezaostovar@gmail.com)
 *
 */
public class FXLog 
{
	
	private FXTEHolder trEvHolder;
	private Map<String, String> logAttrs;
	private Map<String, String> trGlAttrs;
	private Map<String, String> eGlAttrs;
	
	private boolean deletedTr[], deletedEv[];
	
	
	public FXLog()
	{
		trEvHolder = new FXTEHolder();
		logAttrs = new HashMap<String, String>();
		trGlAttrs = new HashMap<String, String>();
		eGlAttrs = new HashMap<String, String>();
		deletedTr = new boolean[0];
		deletedEv = new boolean[0];
	}
	
	public FXLog(FXLog a)
	{
		trEvHolder = new FXTEHolder(a.trEvHolder);
		logAttrs = new HashMap<String, String>(a.logAttrs);
		trGlAttrs = new HashMap<String, String>(a.trGlAttrs);
		eGlAttrs = new HashMap<String, String>(a.eGlAttrs);
		deletedTr = Arrays.copyOf(a.deletedTr, a.deletedTr.length);
		deletedEv = Arrays.copyOf(a.deletedEv, a.deletedEv.length);
	}
	
	/**
	 * Read from the input stream
	 * @param is
	 * @return
	 * @throws Exception
	 */

	public FXLog read(InputStream is) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copyLarge(is, baos);
		byte[] bytes = baos.toByteArray();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);		
		FXFillAtsMaps flAtsM = new FXFillAtsMaps();
		SAXParserFactory pF = SAXParserFactory.newInstance();
		pF.setNamespaceAware(false);		
		SAXParser pr = pF.newSAXParser();		
		pr.parse(bis, flAtsM);
		
		trEvHolder.settAtsIndM(flAtsM.gettrAttrsIndM());
		trEvHolder.setevAttrsIndM(flAtsM.getevAttrsIndM());
		trEvHolder.settrAttrsTypeM(flAtsM.gettrAttrsTypeM());
		trEvHolder.setevAttrsTypeM(flAtsM.getevAttrsTypeM());
		
		bis.reset();
		XesHandler hr = new XesHandler();
		pF = SAXParserFactory.newInstance();
		pF.setNamespaceAware(false);		
		pr = pF.newSAXParser();		
		pr.parse(bis, hr);
		bis.close();

		return this;
	}
	
	/**
	 * Read from the gzipped input stream
	 * @param is
	 * @return
	 * @throws Exception
	 */

	public FXLog readGZip(InputStream is) throws Exception
	{
		return read(new GZIPInputStream(new BufferedInputStream(is)));
	}
	
	/**
	 * Returns the number of traces in this log
	 * @return
	 */
	
	public int traceCount()
	{
		int n = 0;
		for(int i = 0; i < deletedTr.length; i++)
		{
			if(!deletedTr[i])
			{
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Returns the number of events in this log
	 * @return
	 */
	
	public int eventCount()
	{
		int n = 0;
		for(int i = 0; i < deletedEv.length; i++)
		{
			if(!deletedEv[i])
			{
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Get the set of trace attributes in this log
	 * @return
	 */
	
	public Set<String> getTraceAttributes()
	{
		return trEvHolder.gettrAttrsIndM().keySet();
	}
	
	/**
	 * Get the set of event attributes in this log
	 * @return
	 */
	
	public Set<String> getEventAttributes()
	{
		return trEvHolder.getevAttrsIndM().keySet();
	}
	
	/**
	 * Get the map of trace attributes types in this log
	 * @return
	 */
	
	public Map<String, FXAttributeType> getTraceAttributesTypesMap()
	{
		return trEvHolder.gettrAttrsTypeM();
	}
	
	/**
	 * Get the map of event attributes types in this log
	 * @return
	 */
	
	public Map<String, FXAttributeType> getEventAttributesTypesMap()
	{
		return trEvHolder.getevAttrsTypeM();
	}
	
	/**
	 * Returns an iterator for iterating over traces in this log
	 * @return
	 */
	
	public TraceIterator traceIterator()
	{
		return new TraceIterator();
	}
	
	/**
	 * Returns an iterator for iterating over events in this log
	 * @param tid
	 * @return
	 */
	
	public EventIterator eventIterator(int tid)
	{
		return new EventIterator(tid);
	}
	
	/**
	 * Get the value of a trace attribute
	 * @param tid
	 * @param atK
	 * @return
	 */
	
	public Object getValueOfTraceAttribute(int tid, String atK)
	{
		Map<String, Integer> atMp = trEvHolder.gettrAttrsIndM();
		Integer i = atMp.get(atK);
		if(i != null)
			return trEvHolder.gettrAttrsVals().get(tid * atMp.size() + i);
		
		throw new NoSuchElementException("There is no trace attribute with the specified key!");
	}
	
	/**
	 * Get the value of an event attribute
	 * @param eid
	 * @param atK
	 * @return
	 */
	
	public Object getValueOfEventAttribute(int eid, String atK)
	{
		Map<String, Integer> atMp = trEvHolder.getevAttrsIndM();
		Integer i = atMp.get(atK);
		if(i != null)
			return trEvHolder.getevAttrsVals().get(eid * atMp.size() + i);
		
		throw new NoSuchElementException("There is no event attribute with the specified key!");
	}
	
	/**
	 * Serialize this log to the output stream
	 * @param out
	 * @throws IOException
	 */
	
	public void serializeToXES(OutputStream out) throws IOException
	{
		_serializeToXES(out);
	}
	
	/**
	 * Serialize this log zipped to the output stream
	 * @param out
	 * @throws IOException
	 */
	
	public void serializeToGZXES(OutputStream out) throws IOException
	{
		GZIPOutputStream gzos = new GZIPOutputStream(out);
		BufferedOutputStream bos = new BufferedOutputStream(gzos);
		serializeToXES(bos);
		bos.flush();
		gzos.flush();
		bos.close();
		gzos.close();
	}
	
	/**
	 * Check that eid is within valid range
	 * @param eid
	 */
	
	private void checkEventId(int eid)
	{
		if(eid < 0 || eid >= eventCount())
			throw new NoSuchElementException("An event with the specified id (" + eid + ") does not exist");
	}
	
	/**
	 * Check that tid is within valid range
	 * @param tid
	 */
	
	private void checkTraceId(int tid)
	{
		if(tid < 0 || tid >= traceCount())
			throw new NoSuchElementException("A trace with the specified id (" + tid + ") does not exist");
	}
	
	/**
	 * Remove a trace from this log
	 * @param tid
	 */
	
	public void removeTrace(int tid)
	{
		checkTraceId(tid);
		deletedTr[tid] = true;
	}
	
	/**
	 * Remove a trace from this log
	 * @param eid
	 */
	
	public void removeEvent(int eid)
	{
		checkEventId(eid);		
		deletedEv[eid] = true;
	}
	
	/**
	 * Returns a clone of this log
	 */
	
	public FXLog clone()
	{
		return new FXLog(this);
	}
	
		
	private int eventStartIndex(int tid)
	{
		int sI = (tid == 0) ? 0 : trEvHolder.getevIndCeil().get(tid - 1);
		if(trEvHolder.getevIndCeil().get(tid) == sI)
			return -1;
		
		return sI;
	}
	
	private int eventEndIndex(int tid)
	{
		int lI = trEvHolder.getevIndCeil().get(tid);
		int pli = (tid == 0) ? 0 : trEvHolder.getevIndCeil().get(tid - 1);
		if(lI == pli)
			return -1;
		
		return lI;
	}
	
	public class TraceIterator
	{
		private int pos = 0;
		private int prevTrId = -1;
		
		public boolean hasNext()
		{
			for(; pos < deletedTr.length && deletedTr[pos]; pos++);
			
			return pos < deletedTr.length;
		}
		
		public int next()
		{
			for(; pos < deletedTr.length && deletedTr[pos]; pos++);

			if(pos < deletedTr.length)
				return (prevTrId = pos++);
			
			throw new NoSuchElementException();
		}
						
		public void removeLast()
		{
			if(prevTrId != -1)
			{
				removeTrace(prevTrId);
				prevTrId = -1;
			}
		}
		
	}
	

	public class EventIterator
	{
		protected int pos = -1;
		protected int tid = -1;
		protected int prevEvId = -1;
		protected int evIdceil = -1;
		
		public EventIterator(int traceId) 
		{
			checkTraceId(traceId);
			
			this.tid = traceId;
			pos = eventStartIndex(traceId);
			evIdceil = eventEndIndex(traceId);
		}
		
		public EventIterator() 
		{
			this(0);
		}
		
		public boolean hasNext()
		{
			for(; pos < evIdceil && deletedEv[pos]; pos++);
			
			return pos < evIdceil;
		}
		
		public int next()
		{
			for(; pos < evIdceil && deletedEv[pos]; pos++);
				
				if(pos < evIdceil)
					return (prevEvId = pos++);
			
			throw new NoSuchElementException();
		}
		
		public void goToTrace(int traceId)
		{
			checkTraceId(traceId);
			
			this.tid = traceId;
			pos = eventStartIndex(traceId);
			evIdceil = eventEndIndex(traceId);
			prevEvId = -1;
		}
		
		public void removeLast()
		{
			if(prevEvId != -1)
			{
				removeEvent(prevEvId);
				prevEvId = -1;
			}
		}
	}
	

	private class XesHandler extends DefaultHandler {
		
		
		private Stack<FXTagType> stack;
		
		
		public XesHandler() 
		{
			stack = new Stack<FXTagType>();
		}

		@Override
		public void startElement(String uri, String lN, String qN,
				Attributes attrs) throws SAXException {
			String tgNme = lN.trim();
			if (tgNme.length() == 0) {
				tgNme = qN;
			}
			
			if (tgNme.equalsIgnoreCase("string")
					|| tgNme.equalsIgnoreCase("date")
					|| tgNme.equalsIgnoreCase("int")
					|| tgNme.equalsIgnoreCase("float")
					|| tgNme.equalsIgnoreCase("boolean")) {

				
				String k = attrs.getValue("key");			
				if (k == null || k.isEmpty()) {
					return;
				}
				String v = attrs.getValue("value");
				if (v == null || v.isEmpty()) {
					return;
				}
				
								
				FXTagType pk = stack.peek();
				
				if(pk == FXTagType.event)
				{
					trEvHolder.addAttributeToEvent(FXAttributeType.getAttributeType(tgNme), k, v);			
				}else if(pk == FXTagType.trace)
				{
					trEvHolder.addAttributeToTrace(FXAttributeType.getAttributeType(tgNme), k, v);			
				}else if(pk == FXTagType.log)
				{
					logAttrs.put(k, v);
				}else if(pk == FXTagType.glt)
				{
					trGlAttrs.put(k, v);
				}else if(pk == FXTagType.gle)
				{
					eGlAttrs.put(k, v);
				}
			
			} else if (tgNme.equalsIgnoreCase("event")) {	
				
				stack.push(FXTagType.event);
				trEvHolder.newEvent();
				
			} else if (tgNme.equalsIgnoreCase("trace")) {
				stack.push(FXTagType.trace);
				trEvHolder.newTrace();	
				
			} else if (tgNme.equalsIgnoreCase("log")) {
				stack.push(FXTagType.log);
				
			}  else if (tgNme.equalsIgnoreCase("global")) {
				
				String scope = attrs.getValue("scope");
				if(scope != null && scope.equalsIgnoreCase("trace"))
				{
					stack.push(FXTagType.glt);
				}else
					stack.push(FXTagType.gle);
				
			} 
		}
		
		@Override
		public void endElement(String uri, String lN, String qN)
				throws SAXException {
			String tgNme = lN.trim();
			if (tgNme.length() == 0) {
				tgNme = qN;
			}
			
						
			if (tgNme.equalsIgnoreCase("event")) {
				stack.pop();
				trEvHolder.closeEvent();				
			} else if (tgNme.equalsIgnoreCase("trace")) {
				stack.pop();
				trEvHolder.closeTrace();				
			} else if (tgNme.equalsIgnoreCase("log")) {
				stack.pop();				
			} else if (tgNme.equalsIgnoreCase("global")) {
				stack.pop();
			}
		}
		
		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			
			trEvHolder.openTEholder();
		}
		
		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			
			deletedTr = new boolean[trEvHolder.gettrCount()];
			deletedEv = new boolean[trEvHolder.getevCount()];
		}
	}
	
	private void _serializeToXES(OutputStream out) throws IOException {
		System.out.println("Started serializing log to xes...");
		
		long t1 = System.currentTimeMillis();
		SXDocument doc = new SXDocument(out);
		doc.addComment("This file has been generated with the FXES library. It conforms");
		doc.addComment("to the XML serialization of the XES standard for log storage and");
		doc.addComment("management.");
		doc.addComment("XES standard version: 1.0");
		doc.addComment("FXES library version: 1.0");
		SXTag lgTg = doc.addNode("log");
		lgTg.addAttribute("xes.version", "1.0");
		lgTg.addAttribute("xes.features", "nested-attributes");
		lgTg.addAttribute("fxes.version", "1.0");
		lgTg.addAttribute("xmlns", "http://www.xes-standard.org/");

		Set<String> tAtK = getTraceAttributes();
		Set<String> eAtK = getEventAttributes();
		
		TraceIterator tIt = traceIterator();
		EventIterator eIt = eventIterator(0);
		while (tIt.hasNext()) {
			int tid = tIt.next();
			SXTag tTg = lgTg.addChildNode("trace");
			serializeTraceAttributesToXES(tTg, tid, tAtK);

			eIt.goToTrace(tid);
			while (eIt.hasNext()) {
				int eid = eIt.next();
				SXTag eTg = tTg.addChildNode("event");
				serializeEventAttributesToXES(eTg, eid, eAtK);
			}
		}

		doc.close();

		System.out.println("Serialization took " + "(" + (System.currentTimeMillis() - t1) + " msec).");
	}

	private void serializeTraceAttributesToXES(SXTag tag, int tid,
			Set<String> tAtsK) throws IOException {

		
		for (String atk : tAtsK) {
			SXTag atTg = null;			
			Object v = getValueOfTraceAttribute(tid, atk);
			
			if (v != null) {
				switch (getTraceAttributesTypesMap().get(atk)) {
					case LITERAL: {						
						atTg = tag.addChildNode("string");						
						break;
					}
					case DISCRETE: {
						atTg = tag.addChildNode("int");
						break;
					}
					case CONTINUOUS: {
						atTg = tag.addChildNode("float");
						break;
					}
					case TIMESTAMP: {
						atTg = tag.addChildNode("date");

						Calendar c = new GregorianCalendar();
						c.setTime((Date) v);
						v = new XsDateTimeConversionJava7().format(c);
						
						break;
					}
					case BOOLEAN: {
						atTg = tag.addChildNode("boolean");
						break;
					}
				}
				
				atTg.addAttribute("key", atk);
				atTg.addAttribute("value", v.toString());				
			}
		}
	}
	
	private void serializeEventAttributesToXES(SXTag tag, int eid,
			Set<String> evAtK) throws IOException {

		
		for (String atK : evAtK) {
			SXTag atTg = null;			
			Object v = getValueOfEventAttribute(eid, atK);
			
			if (v != null) {
				switch (getEventAttributesTypesMap().get(atK)) {
					case LITERAL: {						
						atTg = tag.addChildNode("string");						
						break;
					}
					case DISCRETE: {
						atTg = tag.addChildNode("int");
						break;
					}
					case CONTINUOUS: {
						atTg = tag.addChildNode("float");
						break;
					}
					case TIMESTAMP: {
						atTg = tag.addChildNode("date");

						Calendar c = new GregorianCalendar();
						c.setTime((Date) v);
						v = new XsDateTimeConversionJava7().format(c);
						
						break;
					}
					case BOOLEAN: {
						atTg = tag.addChildNode("boolean");
						break;
					}
				}
				
				atTg.addAttribute("key", atK);
				atTg.addAttribute("value", v.toString());				
			}
		}
	}
	
}
