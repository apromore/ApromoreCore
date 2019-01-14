package plugin.bpmn.to.maude.notation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

//import org.eclipse.core.filesystem.EFS;
//import org.eclipse.core.filesystem.IFileStore;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.application.WorkbenchAdvisor;
//import org.eclipse.ui.ide.IDE;





import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;





import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;






public class Proc implements java.io.Serializable {
	
	public ArrayList<AndJoin> AndJoin;//
	public ArrayList<AndSplit> AndSplit;//
	public ArrayList<XorJoin> XorJoin;//
	public ArrayList<XorSplit> XorSplit;//
	public ArrayList<End> End;//
	public ArrayList<EndSndMsg> EndSndMsg;//
	public ArrayList<EventSplit> EventBasedgat;//
	public ArrayList<InterRcv> InterRcv;//
	public ArrayList<InterSnd> InterSnd;//
	public ArrayList<OrSplit> OrSplit;//
	public ArrayList<Start> Start;//
	public ArrayList<StartRcvMsg> StartRcvMsg;//
	public ArrayList<Terminate> Terminate;//
	public ArrayList<Task> Task;//
	public ArrayList<SendTask> SendTask;//
	public ArrayList<ReceiveTask> ReceiveTask;//
	public Boolean empty;
	public Boolean eclipse;
	
		
	public Proc()
	{
		this.eclipse = false;
		this.empty = true;
		this.AndJoin = new ArrayList<AndJoin>();
		this.AndSplit = new ArrayList<AndSplit>();
		this.XorJoin = new ArrayList<XorJoin>();
		this.XorSplit = new ArrayList<XorSplit>();
		this.End = new ArrayList<End>();
		this.EndSndMsg = new ArrayList<EndSndMsg>();
		this.EventBasedgat = new ArrayList<EventSplit>();
		this.InterRcv = new ArrayList<InterRcv>();
		this.InterSnd = new ArrayList<InterSnd>();
		this.OrSplit = new ArrayList<OrSplit>();
		this.Start = new ArrayList<Start>();
		this.StartRcvMsg = new ArrayList<StartRcvMsg>();
		this.Terminate = new ArrayList<Terminate>();
		this.Task = new ArrayList<Task>(); 
		this.SendTask = new ArrayList<SendTask>();
		this.ReceiveTask = new ArrayList<ReceiveTask>();
	}
	public Proc(String stringprocess)
	{
		this.eclipse = false;
		this.empty = true;
		this.AndJoin = new ArrayList<AndJoin>();
		this.AndSplit = new ArrayList<AndSplit>();
		this.XorJoin = new ArrayList<XorJoin>();
		this.XorSplit = new ArrayList<XorSplit>();
		this.End = new ArrayList<End>();
		this.EndSndMsg = new ArrayList<EndSndMsg>();
		this.EventBasedgat = new ArrayList<EventSplit>();
		this.InterRcv = new ArrayList<InterRcv>();
		this.InterSnd = new ArrayList<InterSnd>();
		this.OrSplit = new ArrayList<OrSplit>();
		this.Start = new ArrayList<Start>();
		this.StartRcvMsg = new ArrayList<StartRcvMsg>();
		this.Terminate = new ArrayList<Terminate>();
		this.Task = new ArrayList<Task>(); 
		this.SendTask = new ArrayList<SendTask>();
		this.ReceiveTask = new ArrayList<ReceiveTask>();
		ArrayList<String> proc = extractProcElement(stringprocess);
		analizeProc(proc);
		
	}	
	
	public ArrayList<String> extractProcElement(String spool)
	{
		//isolo la parte proc
		String stringprocelem = null;
		int scanIndex = 0;
		int substrStartIndex = 0;
		while(scanIndex<spool.length())
			{
			substrStartIndex = spool.indexOf("proc(", scanIndex);
			int parCount = 1;
			if(substrStartIndex == -1)
				break;
			scanIndex = substrStartIndex+5;
						
			while(parCount > 0 && scanIndex < spool.length()) {
				if(spool.charAt(scanIndex) == '(')
					parCount++;
				else if(spool.charAt(scanIndex) == ')')
					parCount--;
				scanIndex++;				
				}			
			stringprocelem = spool.substring(substrStartIndex+5, scanIndex);
			////System.out.println("stringprocelem in extractprocelement di Process: "+stringprocelem);
			}		
		//Split String process in arrayList<String> processelement
		
		ArrayList<String> ArrayProcElem = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(stringprocelem, "|");
		while (st.hasMoreTokens())
		    	{
					ArrayProcElem.add(st.nextToken());
				}

		return ArrayProcElem;
	}

	public void analizeProc(ArrayList<String> ArrayProcElem)
	{
		for(int i=0; i<ArrayProcElem.size(); i++)
		{			
			Pattern patterntask = Pattern.compile("(task\\()|^(task\\()|^\\s*+(task\\()");
			Matcher matchertask = patterntask.matcher(ArrayProcElem.get(i));
			
			Pattern patternxorsplit = Pattern.compile("(xorSplit\\()|^(xorSplit\\()|^\\s*+(xorSplit\\()");
			Matcher matcherxorsplit = patternxorsplit.matcher(ArrayProcElem.get(i));
			
			Pattern patternend = Pattern.compile("(end\\()|^(end\\()|^\\s*+(end\\()");
			Matcher matcherend = patternend.matcher(ArrayProcElem.get(i));
			
			Pattern patternsendtask = Pattern.compile("(taskSnd\\()|^(taskSnd\\()|^\\s*+(taskSnd\\()");
			Matcher matchersendtask = patternsendtask.matcher(ArrayProcElem.get(i));
			
			Pattern patternstartrcvmsg = Pattern.compile("(startRcv\\()|^(startRcv\\()|^\\s*+(startRcv\\()");
			Matcher matcherstartrcvmsg = patternstartrcvmsg.matcher(ArrayProcElem.get(i));
			
			Pattern patternandjoin = Pattern.compile("(andJoin\\()|^(andJoin\\()|^\\s*+(andJoin\\()");
			Matcher matcherandjoin = patternandjoin.matcher(ArrayProcElem.get(i));
			
			Pattern patternandsplit = Pattern.compile("(andSplit\\()|^(andSplit\\()|^\\s*+(andSplit\\()");
			Matcher matcherandsplit = patternandsplit.matcher(ArrayProcElem.get(i));
			
			Pattern patternxorjoin = Pattern.compile("(xorJoin\\()|^(xorJoin\\()|^\\s*+(xorJoin\\()");
			Matcher matcherxorjoin = patternxorjoin.matcher(ArrayProcElem.get(i));
			
			Pattern patternendsndmsg = Pattern.compile("(endSnd\\()|^(endSnd\\()|^\\s*+(endSnd\\()");
			Matcher matcherendsndmsg = patternendsndmsg.matcher(ArrayProcElem.get(i));
			
			Pattern patterneventbasedgat = Pattern.compile("(eventSplit\\()|^(eventSplit\\()|^\\s*+(eventSplit\\()");
			Matcher matchereventbasedgat = patterneventbasedgat.matcher(ArrayProcElem.get(i));
			
			Pattern patternmsgcatchevent = Pattern.compile("(interRcv\\()|^(interRcv\\()|^\\s*+(interRcv\\()");
			Matcher matchermsgcatchevent = patternmsgcatchevent.matcher(ArrayProcElem.get(i));
			
			Pattern patternmsgthrowevent = Pattern.compile("(InterSnd\\()|^(InterSnd\\()|^\\s*+(InterSnd\\()");
			Matcher matchermsgthrowevent = patternmsgthrowevent.matcher(ArrayProcElem.get(i));
			
			Pattern patternorsplit = Pattern.compile("(orSplit\\()|^(orSplit\\()|^\\s*+(orSplit\\()");
			Matcher matcherorsplit = patternorsplit.matcher(ArrayProcElem.get(i));
			
			Pattern patterstart = Pattern.compile("(start\\()|^(start\\()|^\\s*+(start\\()");
			Matcher matcherstart = patterstart.matcher(ArrayProcElem.get(i));
			
			Pattern patternterminate = Pattern.compile("(terminate\\()|^(terminate\\()|^\\s*+(terminate\\()");
			Matcher matcherterminate = patternterminate.matcher(ArrayProcElem.get(i));
			
			Pattern patternreceivetask = Pattern.compile("(taskRcv\\()|^(taskRcv\\()|^\\s*+(taskRcv\\()");
			Matcher matcherreceivetask = patternreceivetask.matcher(ArrayProcElem.get(i));	
			
			if (matchertask.find()) 
			{
				
				int startindex = matchertask.group().length();
				String subtask = ArrayProcElem.get(i).substring(startindex);
				Task stringtask = new Task(subtask);
				this.Task.add(stringtask);			
			}
			
			else if(matcherxorsplit.find())
			{
				int startindex = matcherxorsplit.group().length();
				String subxorsplit = ArrayProcElem.get(i).substring(startindex);
				//System.out.println("\nsubxorsplit: "+subxorsplit);
				XorSplit stringxorsplit = new XorSplit(subxorsplit);
				this.XorSplit.add(stringxorsplit);
								
			}
			
			else if(matcherend.find())
			{
				int startindex = matcherend.group().length();
				String subend= ArrayProcElem.get(i).substring(startindex);
				End stringend = new End(subend);
				this.End.add(stringend);				
			}
			else if(matchersendtask.find())
			{
				int startindex = matchersendtask.group().length();
				String subsendtask= ArrayProcElem.get(i).substring(startindex);
				SendTask stringsendtask = new SendTask(subsendtask);
				this.SendTask.add(stringsendtask);				
			}
			else if(matcherstartrcvmsg.find())
			{
				int startindex = matcherstartrcvmsg.group().length();
				String substartrcvmsg= ArrayProcElem.get(i).substring(startindex);
				StartRcvMsg stringstartrcvmsg = new StartRcvMsg(substartrcvmsg);
				this.StartRcvMsg.add(stringstartrcvmsg);
			}
			else if(matcherandjoin.find())
			{
				int startindex = matcherandjoin.group().length();
				String subandjoin= ArrayProcElem.get(i).substring(startindex);
				//System.out.println("\nsubandjoin: "+subandjoin);
				AndJoin stringandjoin = new AndJoin(subandjoin);
				this.AndJoin.add(stringandjoin);				
			}			
			else if(matcherandsplit.find())
			{
				int startindex = matcherandsplit.group().length();
				String subandsplit= ArrayProcElem.get(i).substring(startindex);
				AndSplit stringandsplit = new AndSplit(subandsplit);
				this.AndSplit.add(stringandsplit);				
			}			
			else if(matcherxorjoin.find())
			{
				int startindex = matcherxorjoin.group().length();
				String subxorjoin= ArrayProcElem.get(i).substring(startindex);
				XorJoin stringxorjoin = new XorJoin(subxorjoin);
				this.XorJoin.add(stringxorjoin);				
			}
			else if(matcherendsndmsg.find())
			{
				//System.out.println("\nmatcherendsndmsg: ");
				int startindex = matcherendsndmsg.group().length();
				String subendsndmsg= ArrayProcElem.get(i).substring(startindex);
				//System.out.println("\nsubendsndmsg: "+subendsndmsg);
				EndSndMsg stringendsndmsg = new EndSndMsg(subendsndmsg);
				this.EndSndMsg.add(stringendsndmsg);				
			}
			else if(matchereventbasedgat.find())
			{
				int startindex = matchereventbasedgat.group().length();
				String subeventbasedgat = ArrayProcElem.get(i).substring(startindex);
				EventSplit stringeventbasedgat = new EventSplit(subeventbasedgat);
				this.EventBasedgat.add(stringeventbasedgat);				
			}
			else if(matchermsgcatchevent.find())
			{
				int startindex = matchermsgcatchevent.group().length();
				String submsgcatchevent = ArrayProcElem.get(i).substring(startindex);
				InterRcv stringmsgcatchevent = new InterRcv(submsgcatchevent);
				this.InterRcv.add(stringmsgcatchevent);				
			}
			else if(matchermsgthrowevent.find())
			{
				int startindex = matchermsgthrowevent.group().length();
				String submsgthrowevent = ArrayProcElem.get(i).substring(startindex);
				InterSnd stringmsgthrowevent = new InterSnd(submsgthrowevent);
				this.InterSnd.add(stringmsgthrowevent);				
			}
			else if(matcherorsplit.find())
			{
				int startindex = matcherorsplit.group().length();
				String suborsplit = ArrayProcElem.get(i).substring(startindex);
				OrSplit stringorsplit = new OrSplit(suborsplit);
				this.OrSplit.add(stringorsplit);				
			}
			else if(matcherstart.find())
			{
				int startindex = matcherstart.group().length();
				String substart = ArrayProcElem.get(i).substring(startindex);
				Start stringstart = new Start(substart);
				this.Start.add(stringstart);			
			}
			else if(matcherterminate.find())
			{
				int startindex = matcherterminate.group().length();
				String subterminate = ArrayProcElem.get(i).substring(startindex);
				Terminate stringterminate = new Terminate(subterminate);
				this.Terminate.add(stringterminate);				
			}
			else if(matcherreceivetask.find())
			{
				int startindex = matcherreceivetask.group().length();
				String subreceivetask = ArrayProcElem.get(i).substring(startindex);
				ReceiveTask stringreceivetask = new ReceiveTask(subreceivetask);
				this.ReceiveTask.add(stringreceivetask);				
			}			
		}		
	}
	
	public void printProcess()
	{
		if(this.Start != null)
		{
			for(int i = 0; i<this.Start.size(); i++)
			{
			this.Start.get(i).printStart();;
			}			
		}
		if(this.StartRcvMsg != null)
		{
			for(int i = 0; i<this.StartRcvMsg.size(); i++)
			{
			this.StartRcvMsg.get(i).printStartRcvMsg();
			}			
		}
		if(this.AndJoin != null)
		{
			for(int i = 0; i<this.AndJoin.size(); i++)
			{
			this.AndJoin.get(i).printAndJoin();;
			}			
		}		
		if(this.AndSplit != null)
		{
			for(int i = 0; i<this.AndSplit.size(); i++)
			{
			this.AndSplit.get(i).printAndSplit();;
			}			
		}
		if(this.EndSndMsg != null)
		{
			for(int i = 0; i<this.EndSndMsg.size(); i++)
			{
			this.EndSndMsg.get(i).printEndSndMsg();;
			}			
		}
		if(this.EventBasedgat != null)
		{
			for(int i = 0; i<this.EventBasedgat.size(); i++)
			{
			this.EventBasedgat.get(i).printEventSplit();;
			}			
		}
		if(this.InterRcv != null)
		{
			for(int i = 0; i<this.InterRcv.size(); i++)
			{
			this.InterRcv.get(i).printInterRcv();
			}			
		}
		if(this.InterSnd != null)
		{
			for(int i = 0; i<this.InterSnd.size(); i++)
			{
			this.InterSnd.get(i).printInterSnd();
			}			
		}
		if(this.OrSplit != null)
		{
			for(int i = 0; i<this.OrSplit.size(); i++)
			{
			this.OrSplit.get(i).printOrSplit();;
			}			
		}
		if(this.ReceiveTask != null)
		{
			for(int i = 0; i<this.ReceiveTask.size(); i++)
			{
			this.ReceiveTask.get(i).printReceiveTask();;
			}			
		}
		if(this.SendTask != null)
		{
			for(int i = 0; i<this.SendTask.size(); i++)
			{
			this.SendTask.get(i).printSendTask();;
			}			
		}
				
		if(this.Task != null)
		{
			for(int i = 0; i<this.Task.size(); i++)
			{
			this.Task.get(i).printTask();;
			}			
		}
		if(this.Terminate != null)
		{
			for(int i = 0; i<this.Terminate.size(); i++)
			{
			this.Terminate.get(i).printEnd();
			}			
		}
		if(this.XorJoin != null)
		{
			for(int i = 0; i<this.XorJoin.size(); i++)
			{
			this.XorJoin.get(i).printXorJoin();
			}			
		}
		if(this.XorSplit != null)
		{
			for(int i = 0; i<this.XorSplit.size(); i++)
			{
			this.XorSplit.get(i).printXorSplit();
			}			
		}
		if(this.End != null)
		{
			for(int i = 0; i<this.End.size(); i++)
			{
			this.End.get(i).printEnd();;
			}			
		}
		
	}
	
	public Proc compareProcess(Proc proc1, Proc proc2)
	{		
		Proc resultproc = new Proc();
				
		//confronto Task 
		for(int i=0; i<proc1.Task.size(); i++)
		{
			Task taskapp = new Task();
			
			if(taskapp.compareTask(proc1.Task.get(i), proc2.Task.get(i)))				
				{
					if(!resultproc.Task.contains(proc2.Task.get(i)))
					{	
						resultproc.Task.add(proc2.Task.get(i));
						resultproc.empty = false;
					}					
				}
		}				
		
		//confronto AndJoin 
		for(int i=0; i<proc1.AndJoin.size(); i++)
		{
			AndJoin andjoinapp = new AndJoin();
			if(andjoinapp.compareAndJoin(proc1.AndJoin.get(i), proc2.AndJoin.get(i)))
				{
					if(!resultproc.AndJoin.contains(proc2.AndJoin.get(i)))
					{
						resultproc.AndJoin.add(proc2.AndJoin.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		
		//confronto AndSplit 
		for(int i=0; i<proc1.AndSplit.size(); i++)
		{
			AndSplit andsplitapp = new AndSplit();
			if(andsplitapp.compareAndSplit(proc1.AndSplit.get(i), proc2.AndSplit.get(i)))
				{
					if(!resultproc.AndSplit.contains(proc2.AndSplit.get(i)))
					{
						resultproc.AndSplit.add(proc2.AndSplit.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		
		//confronto XorJoin
		for(int i=0; i<proc1.XorJoin.size(); i++)
		{
			XorJoin xorjoinapp = new XorJoin();
			if(xorjoinapp.compareXorJoin(proc1.XorJoin.get(i), proc2.XorJoin.get(i)))
				{
					if(!resultproc.XorJoin.contains(proc2.XorJoin.get(i)))
					{
						resultproc.XorJoin.add(proc2.XorJoin.get(i));
						resultproc.empty = false;
					}
				}
			}
			
		//confronto End
		for(int i=0; i<proc1.End.size(); i++)
		{
			End endapp = new End();
			if(endapp.compareEnd(proc1.End.get(i), proc2.End.get(i)))
				{
					if(!resultproc.End.contains(proc2.End.get(i)))
					{
						resultproc.End.add(proc2.End.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		
		//confronto XorSplit
		for(int i=0; i<proc1.XorSplit.size(); i++)
		{
			XorSplit xorsplitapp = new XorSplit(); 
			if(xorsplitapp.compareXorSplit(proc1.XorSplit.get(i), proc2.XorSplit.get(i)))
				{
					if(!resultproc.XorSplit.contains(proc2.XorSplit.get(i)))
					{
						resultproc.XorSplit.add(proc2.XorSplit.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		//System.out.println("\nproc1.EndSndMsg.size(): "+proc1.EndSndMsg.size());
		//confronto EndSndMsg 
		for(int i=0; i<proc1.EndSndMsg.size(); i++)
		{
			EndSndMsg endsndmsgapp = new EndSndMsg();
			//System.out.println("\nproc1.EndSndMsg.get(i): ");
			proc1.EndSndMsg.get(i).printEndSndMsg();
			//System.out.println("\nproc1.EndSndMsg.get(i): "+proc1.EndSndMsg.get(i).Edge.EdgeName);
			//System.out.println("\nproc2.EndSndMsg.get(i): "+proc2.EndSndMsg.get(i).Edge.EdgeName);
			proc2.EndSndMsg.get(i).printEndSndMsg();
			if(endsndmsgapp.compareEndSndMsg(proc1.EndSndMsg.get(i), proc2.EndSndMsg.get(i)))
				{
					if(!resultproc.EndSndMsg.contains(proc2.EndSndMsg.get(i)))
					{
						resultproc.EndSndMsg.add(proc2.EndSndMsg.get(i));
						resultproc.empty = false;
					}
					
				}
			}
		
		
		//confronto EventBasedgat 
		for(int i=0; i<proc1.EventBasedgat.size(); i++)
		{ 
			////System.out.println("FOR EVENTSPLIT");
			EventSplit eventbasedgatapp = new EventSplit();
			if(eventbasedgatapp.compareEventBasedgat(proc1.EventBasedgat.get(i), proc2.EventBasedgat.get(i)))
				{
					if(!resultproc.EventBasedgat.contains(proc2.EventBasedgat.get(i)))
					{
						////System.out.println("INSERISCO EVENTSPLIT IN RESULPROC");
						resultproc.EventBasedgat.add(proc2.EventBasedgat.get(i));
						resultproc.empty = false;
					}
				}
		}
		////System.out.println("\nRESULTPROCSIZE"+resultproc.EventBasedgat.size());
		//CONFRONTO EVENTINTERRCV
		for(int i=0; i<proc1.EventBasedgat.size(); i++)
		{
			////System.out.println("EVENTBASESIZE"+proc1.EventBasedgat.size());
			
			for(int k = 0; k<proc1.EventBasedgat.get(i).eventinterrcv.size(); k++)
			{
			EventInterRcv eventinterapp = new EventInterRcv();
			if(eventinterapp.compareEventInterRcv(proc1.EventBasedgat.get(i).eventinterrcv.get(k), proc2.EventBasedgat.get(i).eventinterrcv.get(k)))
				{
				Edge edgenull = new Edge();
				InterRcv interEvent = new InterRcv(proc2.EventBasedgat.get(i).eventinterrcv.get(k).Status,
						edgenull, proc2.EventBasedgat.get(i).eventinterrcv.get(k).InterEdge,
						proc2.EventBasedgat.get(i).eventinterrcv.get(k).InterMsg);
				
				if(!resultproc.InterRcv.contains(interEvent))
				{
					////System.out.println("DENTRO IF");
					//interEvent.printInterRcv();
					resultproc.InterRcv.add(interEvent);
				}			
				
				}
			}
		}
		
		
		//confronto MsgCatchEvent 
		for(int i=0; i<proc1.InterRcv.size(); i++)
		{
			InterRcv msgcatcheventapp  = new InterRcv();
			if(msgcatcheventapp.compareInterRcv(proc1.InterRcv.get(i), proc2.InterRcv.get(i)))
				{
					if(!resultproc.InterRcv.contains(proc2.InterRcv.get(i)))
					{
						resultproc.InterRcv.add(proc2.InterRcv.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		//confronto MsgThrowEvent
		for(int i=0; i<proc1.InterSnd.size(); i++)
		{		
			InterSnd msgthroweventapp = new InterSnd();
			if(msgthroweventapp.compareInterSnd(proc1.InterSnd.get(i), proc2.InterSnd.get(i)))
			{
				if(!resultproc.InterSnd.contains(proc2.InterSnd.get(i)))
				{
					resultproc.InterSnd.add(proc2.InterSnd.get(i));
					resultproc.empty = false;
				}
			}
		}		
		
		//confronto OrSplit
		for(int i=0; i<proc1.OrSplit.size(); i++)
		{
			OrSplit orsplitapp = new OrSplit();
			if(orsplitapp.compareOrSplit(proc1.OrSplit.get(i), proc2.OrSplit.get(i)))
				{
					if(!resultproc.OrSplit.contains(proc2.OrSplit.get(i)))
					{
						resultproc.OrSplit.add(proc2.OrSplit.get(i));
						resultproc.empty = false;
					}
				}
			}
	
		
		//confronto Start
		for(int i=0; i<proc1.Start.size(); i++)
		{
			Start startapp = new Start();
			if(startapp.compareStart(proc1.Start.get(i), proc2.Start.get(i)))
				{
					if(!resultproc.Start.contains(proc2.Start.get(i)))
					{
						resultproc.Start.add(proc2.Start.get(i));
						resultproc.empty = false;
					}
				}
		}		
		//confronto StartRcvMsg
		for(int i=0; i<proc1.StartRcvMsg.size(); i++)
		{
			StartRcvMsg startrcvmsgapp = new StartRcvMsg();
			if(startrcvmsgapp.compareStartRcvMsg(proc1.StartRcvMsg.get(i), proc2.StartRcvMsg.get(i)))
				{
					if(!resultproc.StartRcvMsg.contains(proc2.StartRcvMsg.get(i)))
					{
						resultproc.StartRcvMsg.add(proc2.StartRcvMsg.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		//confronto Terminate
		for(int i=0; i<proc1.Terminate.size(); i++)
		{
			End terminateapp = new End();
			if(terminateapp.compareEnd(proc1.Terminate.get(i), proc2.Terminate.get(i)))
				{
					if(!resultproc.Terminate.contains(proc2.Terminate.get(i)))
					{
						resultproc.Terminate.add(proc2.Terminate.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		//confronto SendTask
		for(int i=0; i<proc1.SendTask.size(); i++)
		{
			SendTask sendtaskapp = new SendTask();
			if(sendtaskapp.compareSendTask(proc1.SendTask.get(i), proc2.SendTask.get(i)))
				{
					if(!resultproc.SendTask.contains(proc2.SendTask.get(i)))
					{
						resultproc.SendTask.add(proc2.SendTask.get(i));
						resultproc.empty = false;
					}
				}
			}
		
		//confronto ReceiveTask
		for(int i=0; i<proc1.ReceiveTask.size(); i++)
		{		
			ReceiveTask receivetaskapp = new ReceiveTask();
			if(receivetaskapp.compareReceiveTask(proc1.ReceiveTask.get(i), proc2.ReceiveTask.get(i)))
				{
					if(!resultproc.ReceiveTask.contains(proc2.ReceiveTask.get(i)))
					{
						resultproc.ReceiveTask.add(proc2.ReceiveTask.get(i));
						resultproc.empty = false;
					}
				}
			}
					
		return resultproc;
	}
	
	//CREATE FILE COPY, EDIT FILE .bpmn(XLM)
	
	public void colorChanger(String Loadpath) throws Exception{
		String color="#f7a0ff";
		int index = -1;
		boolean so = true;
		//DEBUG //System.out.println("LOADPATH: "+Loadpath);
		//DEBUG JOptionPane.showMessageDialog(null, "LOADPATH "+Loadpath);
		try{
			index = Loadpath.toString().lastIndexOf("/");	
			if(index == -1)so=false;
		}catch(StringIndexOutOfBoundsException e){
			so = false;
		}
		if(!so)index = Loadpath.toString().lastIndexOf("\\");
		String copypath = Loadpath.toString().substring(0, index);
		////System.out.println("COPYPATH: "+copypath);
		Path filecopy = null;
		
		if(so)filecopy = Paths.get(copypath+"/copia1.bpmn");
		else filecopy = Paths.get(copypath+"\\copia1.bpmn");
		
		try{
			try {
				Files.copy(Paths.get(Loadpath), filecopy, REPLACE_EXISTING);
			} catch (IOException x) {
			    // Some other sort of failure, such as permissions.
				//DEBUG JOptionPane.showMessageDialog(null, "1 "+x);
			    System.err.format("createFile error: %s%n", x);
			}
				
		/*try{
			if(so)filecopy = Paths.get(copypath+"/copia1.bpmn");
			else filecopy = Paths.get(copypath+"\\copia1.bpmn");
			try {
		    // Create the empty file with default permissions, etc.
				JOptionPane.showMessageDialog(null, "Trying to create copy file\n"+filecopy);
				Files.createFile(filecopy);
			} catch (FileAlreadyExistsException x) {
				JOptionPane.showMessageDialog(null, "1 "+x);
			System.err.format(x+"file named %s"+" already exists%n", filecopy);
		} catch (IOException x) {
		    // Some other sort of failure, such as permissions.
			JOptionPane.showMessageDialog(null, "2 "+x);
		    System.err.format("createFile error: %s%n", x);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Loadpath),"UTF-8"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filecopy.toString()),"UTF-8"));
				
		String line = null;

        while ((line = reader.readLine()) != null)
        {
            writer.write(line+"\n");
        }

        // Close to unlock.
        reader.close();
        // Close to unlock and flush to disk.
        writer.close();			
		*/
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filecopy.toString());
		
		NodeList process = doc.getElementsByTagName("process");
		////System.out.println("NODELIST PROCESS: "+process.getLength());
		if(process.getLength() == 0){
			//System.out.println("ECLIPSE process = doc.getElementsByTagName(bpmn2:process);");
			this.eclipse = true;
			process = doc.getElementsByTagName("bpmn2:process");
			//System.out.println("ECLIPSE 810");
			NodeList ext = doc.getElementsByTagName("bpmn2:definitions");
			for(int i=0; i<ext.getLength(); i++){
				Element el = (Element)ext.item(i); 
				//Here the error
				el.setAttribute("xmlns:ext", "http://org.eclipse.bpmn2/ext");
				//for(int p=0; p<ext.item(i).getAttributes().getLength(); p++)
				////System.out.println(ext.item(i).getAttributes().item(p).getNodeName());
			}
		}else{
		
			try{
				//System.out.println("TRY ECLIPSE process = doc.getElementsByTagName(bpmn2:process);");
				this.eclipse = false;
				process = doc.getElementsByTagName("process");
				//System.out.println("ECLIPSE 810");
				NodeList ext = doc.getElementsByTagName("definitions");
				  
				for(int i=0; i<ext.getLength(); i++){
					Element el = (Element)ext.item(i); 
					//Here the error
					el.setAttribute("xmlns", "http://www.omg.org/spec/BPMN/20100524/MODEL");
					el.setAttribute("xmlns:ext", "http://org.eclipse.bpmn2/ext");
					el.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
					el.setAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC" );
					el.setAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI");
					el.setAttribute("xmlns:signavio", "http://www.signavio.com");
					el.setAttribute("xsi:schemaLocation", "http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/DD/20100524/DI-XMI http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/DD/20100524/DC-XMI http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100524/DI-XMI");
					el.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
					el.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
					el.setAttribute("xmlns:xsi", "http://org.eclipse.bpmn2/ext");
					el.setAttribute("xmlns:xsi", "http://org.eclipse.bpmn2/ext");

//					xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/DD/20100524/DI-XMI http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/DD/20100524/DC-XMI http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100524/DI-XMI" 
//							id="sid-c9ef7d55-0f23-4501-983c-d6e6c26ca821" 
//							exporter="org.eclipse.bpmn2.modeler.core" 
//							exporterVersion="1.3.3.Final-v20170323-1521-B61" 
//							expressionLanguage="http://www.w3.org/TR/XPath" 
//							targetNamespace="http://www.signavio.com">
					//for(int p=0; p<ext.item(i).getAttributes().getLength(); p++)
					////System.out.println(ext.item(i).getAttributes().item(p).getNodeName());
				}
				
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("\nNOT A MODEL DESIGNED WITH ECLIPSE");
			}
		}
		
		String EdgeName = "";
		
		//START
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI START DELL'XML
		//CERCARE QUALE DI QUESTI START HA COME ID DELL'EDGE DI USCITA L'ID DEI NOSTRI START
		//COLORARE
		for(int h=0; h<this.Start.size(); h++){
			EdgeName = this.Start.get(h).Edge.EdgeName;
			////System.out.println("EDGENAME "+EdgeName);
			for (int i = 0; i < process.getLength(); i++) {
				////System.out.println("process: "+process.item(i).getNodeName());
				for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
					////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
					if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
					
					//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
					for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
					if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
							|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								
						////System.out.println("TROVO OUTGOING");
						////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
						

						if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
							
							////System.out.println("TROVATO");						
							Node nodo1 = process.item(i).getChildNodes().item(y);
							for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
								////System.out.println("NOME NODO1: "+nodo1.getChildNodes().item(l).getNodeName());
								if(this.eclipse == true){//!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
									////System.out.println("NO EXTENSION ELEMENT");								
									Node node = doc.createElement("bpmn2:extensionElements");
									////System.out.println("NODO PARENT: "+nodo1.getNodeName());
									nodo1.appendChild(node);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									node.appendChild(extstyle);
									break;
									
								}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
								////System.out.println("SI EXTENSION ELEMENT");
								Node nodoextension = nodo1.getChildNodes().item(l);
								////System.out.println("NODO EXTENSION ELEMENT: "+nodoextension.getNodeName());
								Element extstyle = doc.createElement("ext:style");
								extstyle.setAttribute("ext:shapeBackground",color);
								nodoextension.appendChild(extstyle);
							}
							}
						}else{
						}
					}
					}			
				}
			}
		
		}
	}
		// STARTRCVMSG
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI START DELL'XML
		//CERCARE QUALE DI QUESTI START HA COME ID DELL'EDGE DI USCITA L'ID DEI NOSTRI START
		//COLORARE
		for(int h=0; h<this.StartRcvMsg.size(); h++){
			String edgename = this.StartRcvMsg.get(h).Edge.EdgeName;
			////System.out.println("NAME: "+name);
			for (int i = 0; i < process.getLength(); i++) {
				////System.out.println("process: "+process.item(i).getNodeName());
				for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
					////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
					if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
					
					//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
					for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
					if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
							|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
						
						if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){

						Node nodo1 = process.item(i).getChildNodes().item(y);
						for(int l=0; l<nodo1.getChildNodes().getLength(); l++){							
							if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
								////System.out.println("NO EXTENSION ELEMENT");								
								Node node = doc.createElement("bpmn2:extensionElements");
								////System.out.println("NODO PARENT: "+nodo1.getNodeName());
								nodo1.appendChild(node);
								Element extstyle = doc.createElement("ext:style");
								extstyle.setAttribute("ext:shapeBackground",color);
								node.appendChild(extstyle);
								break;
								
							}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
							Node nodoextension = nodo1.getChildNodes().item(l);
							Element extstyle = doc.createElement("ext:style");
							extstyle.setAttribute("ext:shapeBackground",color);
							nodoextension.appendChild(extstyle);							
						}
						}						
					}else{						
					}
				}
				}
			}
		}
		}
			}
		//EVENTSPLIT
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI EVENTBASEDGAT DELL'XML
		//CERCARE QUALE DI QUESTI EVENTBASEDGAT HA COME ID DELL'EDGE D'INGRESSO L'ID DEI NOSTRI START
		//COLORARE
				for(int h=0; h<this.EventBasedgat.size(); h++){
					String edgename = this.EventBasedgat.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
								|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){
								
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {								
							}
						}
						}
					}
				}
				}
				}
				
		// INTERRCV
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI MSGCATCHEVENT DELL'XML
		//CERCARE QUALE DI QUESTI MSGCATCHEVENT HA COME ID DELL'EDGE D'INGRESSO L'ID DEI NOSTRI START
		//COLORARE
				
				for(int h=0; h<this.InterRcv.size(); h++){
					if(!this.InterRcv.get(h).OutputEdge.EdgeName.equals("")){
						EdgeName = this.InterRcv.get(h).OutputEdge.EdgeName;
					}else{}
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
								
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {
							}
						}
						}
					}
				}
				}
				}
				//SENDTASK
				for(int h=0; h<this.SendTask.size(); h++){
					String name = this.SendTask.get(h).name;
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
								}
								}								
								}else {
								}
						}
						}
					}
				}
				}			
				//TASK
				for(int h=0; h<this.Task.size(); h++){
					String name = this.Task.get(h).name;
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {								
							}
						}
						}
					}
				}
				}
				
				//XORSPLIT
				for(int h=0; h<this.XorSplit.size(); h++){
					EdgeName = this.XorSplit.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}							
							}else {							
							}
						}
						}
					}
				}
				}
				}
				//END
				
				for(int h=0; h<this.End.size(); h++){
					EdgeName = this.End.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO INCOMING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								////System.out.println("TROVATO");*/
								  						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else{								
							}
						}
						}
					}
				}
				}
				}
				//ANDJOIN				
				for(int h=0; h<this.AndJoin.size(); h++){
					String Edgename = this.AndJoin.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
						for (int i = 0; i < process.getLength(); i++) {
							////System.out.println("process: "+process.item(i).getNodeName());
							for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
//								//System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
								if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
								
								//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
								for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
										|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							*/
					if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(Edgename)){
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {							
							}
						}					
						}			
					}
				}
			}
				}
				
				//ANDSPLIT				
				for(int h=0; h<this.AndSplit.size(); h++){
					EdgeName = this.AndSplit.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
//							//System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO INCOMING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
	
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {
							}
						}				
						}			
					}
				}
			}
				}
				
				//ENDSNDMSG				
				for(int h=0; h<this.EndSndMsg.size(); h++){
					EdgeName = this.EndSndMsg.get(h).Edge.EdgeName;
						for (int i = 0; i < process.getLength(); i++) {
							////System.out.println("process: "+process.item(i).getNodeName());
							for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
								////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
								if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
								
								//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
								for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
										|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
									////System.out.println("TROVO INCOMING");
									////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
									

									if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){	
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								
								////System.out.println("TROVATO");						
								*/
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {							
							}
						}						
						}			
					}
				}
			}
				}	
				//INTERSND				
				for(int h=0; h<this.InterSnd.size(); h++){
					EdgeName = this.InterSnd.get(h).InputEdge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
							*/	
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else {								
							}
						}				
						}			
					}
				}
			}
				}
				
				//ORSPLIT				
				for(int h=0; h<this.OrSplit.size(); h++){
					String edgename = this.OrSplit.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								
								////System.out.println("TROVATO");*/						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else {								
							}
						}				
						}			
					}
				}
			}
				}
				
				//RECEIVETASK				
				//System.out.println("ReceiveTask: "+ReceiveTask.size());
				for(int h=0; h<this.ReceiveTask.size(); h++){
					String name = this.ReceiveTask.get(h).name;
					////System.out.println("START NAME: "+name);
					
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {
								}
						}
						}			
					}
				}
			}		
				//TERMINATE
				for(int h=0; h<this.Terminate.size(); h++){
					EdgeName = this.Terminate.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO OUTGOING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
									
									////System.out.println("TROVATO");						
									Node nodo1 = process.item(i).getChildNodes().item(y);
									for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
										////System.out.println("NOME NODO1: "+nodo1.getChildNodes().item(l).getNodeName());
										if(this.eclipse == true){//!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
											////System.out.println("NO EXTENSION ELEMENT");								
											Node node = doc.createElement("bpmn2:extensionElements");
											////System.out.println("NODO PARENT: "+nodo1.getNodeName());
											nodo1.appendChild(node);
											Element extstyle = doc.createElement("ext:style");
											extstyle.setAttribute("ext:shapeBackground",color);
											node.appendChild(extstyle);
											break;
											
										}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
										////System.out.println("SI EXTENSION ELEMENT");
										Node nodoextension = nodo1.getChildNodes().item(l);
										////System.out.println("NODO EXTENSION ELEMENT: "+nodoextension.getNodeName());
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										nodoextension.appendChild(extstyle);
									}
									}
								}else{
								}
							}
							}			
						}
					}	
				
				
				/*for(int h=0; h<this.Terminate.size(); h++){
					String name = this.Terminate.get(h).name;
					////System.out.println("START NAME: "+name);
					
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
																				
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);							
								}
								}
																
							}else {
								
							}
						}					
						}			
					}
				}
			}*/
					}
				}
		
				//XORJOIN				
				for(int h=0; h<this.XorJoin.size(); h++){
					EdgeName = this.XorJoin.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
																
							}else {								
							}
						}					
						}			
					}
				}
			}
				}
		
				
			
				
				
            
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filecopy.toString()));
		//System.out.println("BEFORE TRANSFORM\n");
		transformer.transform(source, result);

		//System.out.println("Done");
		
						
				
		} catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	   } catch (TransformerException tfe) {
		tfe.printStackTrace();
	   } catch (IOException ioe) {
		ioe.printStackTrace();
	   } catch (SAXException sae) {
		sae.printStackTrace();
	   }
		
		//openFile(filecopy);
			}
	
	//OPEN FILE 
	//I ADDED INTO A DEPENDECIES ORG.ECLIPSE.CORE.FILESYSTEM AND ORG.ECLIPSE.UI.IDE
	
//	public void openFile(final Path filepath) throws Exception
//	{
//		Display.getDefault().asyncExec(new Runnable() {
//		public void run() {
//		   
//		IWorkbenchPage page = null;
//		String TitleEditor = null;
//		
//					
//		int startindex = filepath.toString().lastIndexOf("/");
//		int endindex = filepath.toString().length()-5;
//		TitleEditor = filepath.toString().substring(startindex+1, endindex);
//		////System.out.println("TITILE EDITOR: "+TitleEditor);
//		
//		File fileToOpen = new File(filepath.toString());
//		if (fileToOpen.exists() && fileToOpen.isFile()) {  
//			IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
//			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
//			for(int i=0; i<windows.length; i++) { 
//				IWorkbenchPage[] pages = windows[i].getPages();
//				for(int j=0; i<pages.length; i++){
//					windows[i].setActivePage(pages[j]);
//					page = windows[i].getActivePage();
//					////System.out.println("TITOLO: "+page.getActiveEditor().getTitle());
//					IEditorPart[] editors= page.getEditors();
//					for(int k=0; k<page.getEditors().length; k++){
//						//PASSARE IL NOME FILE!
//						if(editors[k].getTitle().toString().equals(TitleEditor)){
//							page.closeEditor(editors[k], false);
//						} 
//					}
//					}
//			}
//			
//		    try {
//		    	IDE.openEditorOnFileStore( page, fileStore );
//		    } catch ( PartInitException e ) {
//		        //Put your exception handler here if you wish to
//		    }
//		} else {
//		    //Do something if the file does not exist
//		}
//				
//	}
//		});
//	}
	
	
	public Document getColorChangedCounterexample(String inputStringModel) throws Exception{
		
		String color="#f7a0ff";
		int index = -1;
		boolean so = true;
		//DEBUG 
		//System.out.println("LOADPATH: "+inputStringModel);
		//DEBUG JOptionPane.showMessageDialog(null, "LOADPATH "+Loadpath);
//
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(inputStringModel));

		Document doc = db.parse(is);
		
//		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//		Document doc = docBuilder.parse(inputStringModel);
		
		//System.out.println("ECLIPSE 1808");
		NodeList process = doc.getElementsByTagName("process");
		////System.out.println("NODELIST PROCESS: "+process.getLength());
		if(process.getLength() == 0){
			//System.out.println("ECLIPSE 1812");
			this.eclipse = true;
			
			try{
			process = doc.getElementsByTagName("bpmn2:process");
			//System.out.println("\nNodeList ext = doc.getElementsByTagName(bpmn2:definitions);");
			NodeList ext = doc.getElementsByTagName("bpmn2:definitions");
			//System.out.println("\nDOPO NodeList ext = doc.getElementsByTagName(bpmn2:definitions);");
			
			for(int i=0; i<ext.getLength(); i++){
				Element el = (Element)ext.item(i); 
				//Here the error
				//System.out.println("ECLIPSE 1824");
				el.setAttribute("xmlns:ext", "http://org.eclipse.bpmn2/ext");
				
				//SIGNAVIO
//				<definitions 
//						xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
//						xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" 
//						xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" 
//						xmlns:ext="http://org.eclipse.bpmn2/ext" 
//						xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" 
//						xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" 
//						xmlns:signavio="http://www.signavio.com" 
//						xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
//						xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/DD/20100524/DI-XMI http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/DD/20100524/DC-XMI http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100524/DI-XMI" id="sid-c9ef7d55-0f23-4501-983c-d6e6c26ca821" exporter="org.eclipse.bpmn2.modeler.core" exporterVersion="1.3.3.Final-v20170323-1521-B61" expressionLanguage="http://www.w3.org/TR/XPath" targetNamespace="http://www.signavio.com">
//				
				
				//for(int p=0; p<ext.item(i).getAttributes().getLength(); p++)
				////System.out.println(ext.item(i).getAttributes().item(p).getNodeName());
			}
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("\nNOT A MODEL DESIGNED WITH ECLIPSE");
			}
		}else{

			try{
				//System.out.println("TRY ECLIPSE process = doc.getElementsByTagName(bpmn2:process);");
				this.eclipse = false;
				process = doc.getElementsByTagName("process");
				//System.out.println("ECLIPSE 810");
				NodeList ext = doc.getElementsByTagName("definitions");
				  
				for(int i=0; i<ext.getLength(); i++){
					Element el = (Element)ext.item(i); 
					//Here the error
					el.setAttribute("xmlns", "http://www.omg.org/spec/BPMN/20100524/MODEL");
					el.setAttribute("xmlns:ext", "http://org.eclipse.bpmn2/ext");
					el.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
					el.setAttribute("xmlns:omgdc", "http://www.omg.org/spec/DD/20100524/DC" );
					el.setAttribute("xmlns:omgdi", "http://www.omg.org/spec/DD/20100524/DI");
					el.setAttribute("xmlns:signavio", "http://www.signavio.com");
					el.setAttribute("xsi:schemaLocation", "http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/DD/20100524/DI-XMI http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/DD/20100524/DC-XMI http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100524/DI-XMI");
					el.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
					el.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
					el.setAttribute("xmlns:xsi", "http://org.eclipse.bpmn2/ext");
					el.setAttribute("xmlns:xsi", "http://org.eclipse.bpmn2/ext");

//					xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/DD/20100524/DI-XMI http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/DD/20100524/DC-XMI http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100524/DI-XMI" 
//							id="sid-c9ef7d55-0f23-4501-983c-d6e6c26ca821" 
//							exporter="org.eclipse.bpmn2.modeler.core" 
//							exporterVersion="1.3.3.Final-v20170323-1521-B61" 
//							expressionLanguage="http://www.w3.org/TR/XPath" 
//							targetNamespace="http://www.signavio.com">
					//for(int p=0; p<ext.item(i).getAttributes().getLength(); p++)
					////System.out.println(ext.item(i).getAttributes().item(p).getNodeName());
				}
				
			}catch(Exception e){
				e.printStackTrace();
				//System.out.println("\nNOT A MODEL DESIGNED WITH ECLIPSE");
			}
		}
		String EdgeName = "";
		
		//START
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI START DELL'XML
		//CERCARE QUALE DI QUESTI START HA COME ID DELL'EDGE DI USCITA L'ID DEI NOSTRI START
		//COLORARE
		for(int h=0; h<this.Start.size(); h++){
			EdgeName = this.Start.get(h).Edge.EdgeName;
			////System.out.println("EDGENAME "+EdgeName);
			for (int i = 0; i < process.getLength(); i++) {
				////System.out.println("process: "+process.item(i).getNodeName());
				for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
					////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
					if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
					
					//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
					for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
					if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
							|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								
						////System.out.println("TROVO OUTGOING");
						////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
						

						if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
							
							////System.out.println("TROVATO");						
							Node nodo1 = process.item(i).getChildNodes().item(y);
							for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
								////System.out.println("NOME NODO1: "+nodo1.getChildNodes().item(l).getNodeName());
								if(this.eclipse == true){//!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
									////System.out.println("NO EXTENSION ELEMENT");								
									Node node = doc.createElement("bpmn2:extensionElements");
									////System.out.println("NODO PARENT: "+nodo1.getNodeName());
									nodo1.appendChild(node);
									//System.out.println("ECLIPSE 1883");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									node.appendChild(extstyle);
									break;
									
								}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
								////System.out.println("SI EXTENSION ELEMENT");
								Node nodoextension = nodo1.getChildNodes().item(l);
								////System.out.println("NODO EXTENSION ELEMENT: "+nodoextension.getNodeName());
								//System.out.println("ECLIPSE 1893");
								Element extstyle = doc.createElement("ext:style");
								extstyle.setAttribute("ext:shapeBackground",color);
								nodoextension.appendChild(extstyle);
							}
							}
						}else{
						}
					}
					}			
				}
			}
		
		}
	}
		// STARTRCVMSG
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI START DELL'XML
		//CERCARE QUALE DI QUESTI START HA COME ID DELL'EDGE DI USCITA L'ID DEI NOSTRI START
		//COLORARE
		for(int h=0; h<this.StartRcvMsg.size(); h++){
			String edgename = this.StartRcvMsg.get(h).Edge.EdgeName;
			////System.out.println("NAME: "+name);
			for (int i = 0; i < process.getLength(); i++) {
				////System.out.println("process: "+process.item(i).getNodeName());
				for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
					////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
					if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
					
					//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
					for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
					if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
							|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
						
						if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){

						Node nodo1 = process.item(i).getChildNodes().item(y);
						for(int l=0; l<nodo1.getChildNodes().getLength(); l++){							
							if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
								////System.out.println("NO EXTENSION ELEMENT");								
								Node node = doc.createElement("bpmn2:extensionElements");
								////System.out.println("NODO PARENT: "+nodo1.getNodeName());
								nodo1.appendChild(node);
								//System.out.println("ECLIPSE 1935");
								Element extstyle = doc.createElement("ext:style");
								extstyle.setAttribute("ext:shapeBackground",color);
								node.appendChild(extstyle);
								break;
								
							}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
							Node nodoextension = nodo1.getChildNodes().item(l);
							//System.out.println("ECLIPSE 1943");
							Element extstyle = doc.createElement("ext:style");
							extstyle.setAttribute("ext:shapeBackground",color);
							nodoextension.appendChild(extstyle);							
						}
						}						
					}else{						
					}
				}
				}
			}
		}
		}
			}
		//EVENTSPLIT
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI EVENTBASEDGAT DELL'XML
		//CERCARE QUALE DI QUESTI EVENTBASEDGAT HA COME ID DELL'EDGE D'INGRESSO L'ID DEI NOSTRI START
		//COLORARE
				for(int h=0; h<this.EventBasedgat.size(); h++){
					String edgename = this.EventBasedgat.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
								|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){
								
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 1982");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 1990");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {								
							}
						}
						}
					}
				}
				}
				}
				
		// INTERRCV
		//DOBBIAMO FARE UNA QUERY PER TIRARE FUORI TUTTI GLI MSGCATCHEVENT DELL'XML
		//CERCARE QUALE DI QUESTI MSGCATCHEVENT HA COME ID DELL'EDGE D'INGRESSO L'ID DEI NOSTRI START
		//COLORARE
				
				for(int h=0; h<this.InterRcv.size(); h++){
					if(!this.InterRcv.get(h).OutputEdge.EdgeName.equals("")){
						EdgeName = this.InterRcv.get(h).OutputEdge.EdgeName;
					}else{}
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
								
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2033");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2041");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {
							}
						}
						}
					}
				}
				}
				}
				//SENDTASK
				//System.out.println("SendTask: "+SendTask.size());
				for(int h=0; h<this.SendTask.size(); h++){
					String name = this.SendTask.get(h).name;
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2079");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2087");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
								}
								}								
								}else {
								}
						}
						}
					}
				}
				}			
				
				//System.out.println("Task: "+Task.size());
				//TASK
				for(int h=0; h<this.Task.size(); h++){
					String name = this.Task.get(h).name;
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2124");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2132");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {								
							}
						}
						}
					}
				}
				}
				
				//XORSPLIT
				for(int h=0; h<this.XorSplit.size(); h++){
					EdgeName = this.XorSplit.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2179");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2187");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}							
							}else {							
							}
						}
						}
					}
				}
				}
				}
				//END
				
				for(int h=0; h<this.End.size(); h++){
					EdgeName = this.End.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO INCOMING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								////System.out.println("TROVATO");*/
								  						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2343");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2251");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else{								
							}
						}
						}
					}
				}
				}
				}
				//ANDJOIN				
				for(int h=0; h<this.AndJoin.size(); h++){
					String Edgename = this.AndJoin.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
						for (int i = 0; i < process.getLength(); i++) {
							////System.out.println("process: "+process.item(i).getNodeName());
							for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
//								//System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
								
								if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
								
								//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
								for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
										|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							*/
//									//System.out.println("\nprocess.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName(): "+process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
//									try{
//									//System.out.println("\nprocess.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeValue(): "+process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeValue());
//									}catch(Exception e){
//										e.printStackTrace();
//									}
									////System.out.println("\nEdgename: "+Edgename);
									
									//process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(Edgename)
					//if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeValue().equals(Edgename)){
						if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){		
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2309");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2317");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {							
							}
						}					
						}			
					}
				}
			}
				}
				
				//ANDSPLIT				
				for(int h=0; h<this.AndSplit.size(); h++){
					EdgeName = this.AndSplit.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
//							//System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO INCOMING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
	
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2372");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2380");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {
							}
						}				
						}			
					}
				}
			}
				}
				
				//ENDSNDMSG				
				for(int h=0; h<this.EndSndMsg.size(); h++){
					EdgeName = this.EndSndMsg.get(h).Edge.EdgeName;
						for (int i = 0; i < process.getLength(); i++) {
							////System.out.println("process: "+process.item(i).getNodeName());
							for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
								////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
								if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
								
								//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
								for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
										|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
									////System.out.println("TROVO INCOMING");
									////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
									

									if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){	
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								
								////System.out.println("TROVATO");						
								*/
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2435");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2443");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
							}else {							
							}
						}						
						}			
					}
				}
			}
				}	
				//INTERSND				
				for(int h=0; h<this.InterSnd.size(); h++){
					EdgeName = this.InterSnd.get(h).InputEdge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
							*/	
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2493");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2501");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else {								
							}
						}				
						}			
					}
				}
			}
				}
				
				//ORSPLIT				
				for(int h=0; h<this.OrSplit.size(); h++){
					String edgename = this.OrSplit.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(edgename)){
					
					
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
								
								////System.out.println("TROVATO");*/						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2551");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2559");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}								
							}else {								
							}
						}				
						}			
					}
				}
			}
				}
				
				//RECEIVETASK				
				for(int h=0; h<this.ReceiveTask.size(); h++){
					String name = this.ReceiveTask.get(h).name;
					////System.out.println("START NAME: "+name);
					
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							
			            	String edgeNameAppoggio = process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue();
			            	edgeNameAppoggio.replaceAll("[^a-zA-Z0-9\\s]", "");
			            	edgeNameAppoggio=edgeNameAppoggio.replaceAll("\\n", "");
							if(edgeNameAppoggio.equals(name)){
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2602");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2610");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);									
								}
								}
							}else {
								}
						}
						}			
					}
				}
			}		
				//TERMINATE
				for(int h=0; h<this.Terminate.size(); h++){
					EdgeName = this.Terminate.get(h).Edge.EdgeName;
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("incoming")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:incoming")){
								////System.out.println("TROVO OUTGOING");
								////System.out.println(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName());
								

								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
									
									////System.out.println("TROVATO");						
									Node nodo1 = process.item(i).getChildNodes().item(y);
									for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
										////System.out.println("NOME NODO1: "+nodo1.getChildNodes().item(l).getNodeName());
										if(this.eclipse == true){//!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
											////System.out.println("NO EXTENSION ELEMENT");								
											Node node = doc.createElement("bpmn2:extensionElements");
											////System.out.println("NODO PARENT: "+nodo1.getNodeName());
											nodo1.appendChild(node);
											//System.out.println("ECLIPSE 2651");
											Element extstyle = doc.createElement("ext:style");
											extstyle.setAttribute("ext:shapeBackground",color);
											node.appendChild(extstyle);
											break;
											
										}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
										////System.out.println("SI EXTENSION ELEMENT");
										Node nodoextension = nodo1.getChildNodes().item(l);
										////System.out.println("NODO EXTENSION ELEMENT: "+nodoextension.getNodeName());
										//System.out.println("ECLIPSE 2661");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										nodoextension.appendChild(extstyle);
									}
									}
								}else{
								}
							}
							}			
						}
					}	
				
				
				/*for(int h=0; h<this.Terminate.size(); h++){
					String name = this.Terminate.get(h).name;
					////System.out.println("START NAME: "+name);
					
				for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){
																				
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);							
								}
								}
																
							}else {
								
							}
						}					
						}			
					}
				}
			}*/
					}
				}
		
				//XORJOIN				
				for(int h=0; h<this.XorJoin.size(); h++){
					EdgeName = this.XorJoin.get(h).Edge.EdgeName;
					////System.out.println("START NAME: "+name);
					for (int i = 0; i < process.getLength(); i++) {
						////System.out.println("process: "+process.item(i).getNodeName());
						for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
							////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
							if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
							
							//MODOFICHIAMO IL FOR PER SCORRERE I NODI FIGLI E NON GLI ATTRIBUTI	
							for (int a = 0; a< process.item(i).getChildNodes().item(y).getChildNodes().getLength(); a++){
							if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("outgoing")
									|| process.item(i).getChildNodes().item(y).getChildNodes().item(a).getNodeName().equals("bpmn2:outgoing")){
								if(process.item(i).getChildNodes().item(y).getChildNodes().item(a).getTextContent().equals(EdgeName)){
				/*for (int i = 0; i < process.getLength(); i++) {
					////System.out.println("process: "+process.item(i).getNodeName());
					for(int y=0; y<process.item(i).getChildNodes().getLength(); y++){
						////System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());
						if(!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")){
						for (int a = 0; a< process.item(i).getChildNodes().item(y).getAttributes().getLength(); a++){
						//if(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").equals("Offer Receive")){
							////System.out.println(process.item(i).getChildNodes().item(y).getNodeName());
							////System.out.println("attributi: "+process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeName());
							if(process.item(i).getChildNodes().item(y).getAttributes().item(a).getNodeValue().equals(name)){*/
								
								////System.out.println("TROVATO");						
								Node nodo1 = process.item(i).getChildNodes().item(y);
								for(int l=0; l<nodo1.getChildNodes().getLength(); l++){
									if(this.eclipse == true){//if(!nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements") && !nodo1.getChildNodes().item(l).getNodeName().equals("#text") && !nodo1.getChildNodes().item(l).getNodeName().equals("bpmn2:extensionElements")){
										////System.out.println("NO EXTENSION ELEMENT");								
										Node node = doc.createElement("bpmn2:extensionElements");
										////System.out.println("NODO PARENT: "+nodo1.getNodeName());
										nodo1.appendChild(node);
										//System.out.println("ECLIPSE 2755");
										Element extstyle = doc.createElement("ext:style");
										extstyle.setAttribute("ext:shapeBackground",color);
										node.appendChild(extstyle);
										break;
										
									}else if(nodo1.getChildNodes().item(l).getNodeName().equals("extensionElements")){
									Node nodoextension = nodo1.getChildNodes().item(l);
									//System.out.println("ECLIPSE 2763");
									Element extstyle = doc.createElement("ext:style");
									extstyle.setAttribute("ext:shapeBackground",color);
									nodoextension.appendChild(extstyle);
									
								}
								}
																
							}else {								
							}
						}					
						}			
					}
				}
			}
				}
		
				
	
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		DOMSource source = new DOMSource(doc);
		
//		StreamResult result = new StreamResult(new File(doc.toString()));
//		//System.out.println("BEFORE TRANSFORM\n");
//		transformer.transform(source, result);
				//System.out.println("\nDOC: "+doc);
				if(doc==null){
					//System.out.println("\nDOC NULL: ");
				}else{
					//System.out.println("\nDOC NOT NULL: ");
				}

		//System.out.println("DONEEEE: "+getStringFromDocument(doc));
		//System.out.println("Done");
		return doc;
					
		
		//openFile(filecopy);
		}
	
	public static String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       javax.xml.transform.Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	} 

	
	
	
	
	public ArrayList<Task> getTasks(){
		return this.Task;
	}
	
	public ArrayList<SendTask> getSendTask(){
		return this.SendTask;
	}
	
	public ArrayList<ReceiveTask> getReceiveTask(){
		return this.ReceiveTask;
	}
	
	
	
	
	
	
	
}