package org.apromore.fxes; 

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

import org.apromore.fxes.log.FXAttributeType;
import org.apromore.fxes.log.FXLog;
import org.apromore.fxes.log.FXLog.EventIterator;
import org.apromore.fxes.log.FXLog.TraceIterator;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Before;
import org.junit.Test;


public class FXLogTest {

	private FXLog fxlg = null; 
	private XLog xlg = null;

	private Path lgPath = Paths.get("./TestLogs/SepsisCases.xes.gz");

	@Before
	public void setup() throws Exception
	{

		XesXmlGZIPParser parser = new XesXmlGZIPParser();
		FileInputStream fleIS = new FileInputStream(lgPath.toFile());
		xlg = parser.parse(fleIS).get(0);
		fleIS.close();

		fleIS = new FileInputStream(lgPath.toFile());
                fxlg = new FXLog();
		fxlg.readGZip(fleIS);
		fleIS.close();

	}


	@Test
	public void testLogContent() throws Exception
	{
		TraceIterator tIt = fxlg.traceIterator();
		EventIterator eIt = fxlg.eventIterator(0);
		Set<String> tAtsKs = fxlg.getTraceAttributes();
		Set<String> eAtsKs = fxlg.getEventAttributes();

		assertTrue(fxlg.traceCount() == xlg.size());

		int tInd = 0;
		while(tIt.hasNext())
		{
			int tid = tIt.next();
			XTrace xT = xlg.get(tInd);
			assertTraceAttributesAreEqual(tid, xT, tAtsKs);

			eIt.goToTrace(tid);
			int eInd = 0;
			while(eIt.hasNext())
			{
				int eid = eIt.next();
				XEvent xE = xT.get(eInd);
				assertEventAttributesAreEqual(eid, xE, eAtsKs);

				eInd++;
			}
			tInd++;
		}

	}

	private void assertTraceAttributesAreEqual(int trId, XTrace xT,
			Set<String> tAtsKs) 
	{
		for(String atK: tAtsKs)
		{
			Object val = fxlg.getValueOfTraceAttribute(trId, atK);
			XAttribute xlgVal = xT.getAttributes().get(atK);
			try {
				assertTrue((val != null && xlgVal != null) || (val == null && xlgVal == null));
			}catch (Exception ex)
			{
				System.out.println();
			}

			if(val != null)
			{
				if(fxlg.getTraceAttributesTypesMap().get(atK) == FXAttributeType.TIMESTAMP)
				{
					assertTrue(((Date)val).getTime() == ((XAttributeTimestamp) xlgVal).getValueMillis());
				}else
				{
					assertTrue(String.valueOf(val).equals((xlgVal.toString())));
				}
			}
		}
	}

	private void assertEventAttributesAreEqual(int evId, XEvent event,
			Set<String> eAtsKs) 
	{
		for(String atK: eAtsKs)
		{
			Object val = fxlg.getValueOfEventAttribute(evId, atK);
			XAttribute xlgVal = event.getAttributes().get(atK);
			assertTrue((val != null && xlgVal != null) || (val == null && xlgVal == null));
			if(val != null)
			{
				if(fxlg.getEventAttributesTypesMap().get(atK) == FXAttributeType.TIMESTAMP)
				{
					assertTrue(((Date)val).getTime() == ((XAttributeTimestamp) xlgVal).getValueMillis());
				}else
				{
					assertTrue(String.valueOf(val).equals((xlgVal.toString())));
				}
			}
		}
	}

	@Test
	public void testClone() throws Exception
	{

		fxlg = fxlg.clone();

		testLogContent();

	}

	@Test
	public void testSerializeToXES() throws Exception
	{
		File outpFle = File.createTempFile("log_ser", ".xes.gz", null);
		fxlg.serializeToGZXES(new FileOutputStream(outpFle));

		XesXmlGZIPParser parser = new XesXmlGZIPParser();
		FileInputStream fleIS = new FileInputStream(outpFle);
		xlg = parser.parse(fleIS).get(0);
		fleIS.close();

		fleIS = new FileInputStream(outpFle);
		fxlg = new FXLog();
		fxlg.readGZip(fleIS);
		fleIS.close();

		testLogContent();

	}

}
