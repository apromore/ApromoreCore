package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

@SuppressWarnings("serial")
public class Pool  implements java.io.Serializable {
	
	String OrganizationName;
	Proc ProcessElements;
	ArrayList<Msg> InputMsg;
	ArrayList<Msg> OutputMsg;
	
	public Pool ()
	{
		
	}
	
	public Pool(String stringpool)
	{	
		////System.out.println("INPUT DI POOL: "+stringpool);
		this.OrganizationName = extractName(stringpool); 
		this.ProcessElements = new Proc(extractProcElement(stringpool));
		this.InputMsg = extractInputMsg(stringpool);
		this.OutputMsg = extractOutputMsg(stringpool);
	}
	
	//EXTRACT POOL'S NAME
	public String extractName(String spool)
	{
		String name = null;
		int StartIndex = 0;
		int LastIndex =0;
		
		////System.out.println("\nINPUTspoolextractname: "+spool);
		Pattern pattern = Pattern.compile("\\s*+(\"+[a-zA-Z0-9- ]+\")+[\\s*,\\s*]+\\s*+(proc)|^\"+[a-zA-Z0-9- ]+\"+[,]+\\s*+(proc)");
		Matcher matcher = pattern.matcher(spool);
		while(matcher.find()) 
		  {
			name = matcher.group();		   
		  }
		////System.out.println("\nextractName: "+name);
		StartIndex = name.indexOf("\"")+1;
		LastIndex = name.lastIndexOf("\"");			
		String subname = name.substring(StartIndex, LastIndex);
		////System.out.println("POOL NAME: "+subname);
		return subname;
	}
	
	//EXTRACT PROC ELEMENT
	public String extractProcElement(String spool)
	{
		////System.out.println("INPUTEXTRACTPROCELEMENT: "+spool);
		String stringprocelement = null;
		String name = null;
		Pattern pattern = Pattern.compile("^([ ]|)+\"+[A-Z0-9- ]+[a-zA-Z0-9- ]+\",");
		Matcher matcher = pattern.matcher(spool);
		
		while(matcher.find()) 
		  {
			name = matcher.group();
		  }
//		//System.out.println("\nQUIIIIIIIIIII: "+name);
//		//System.out.println("\nextractNamediProcElement: "+name);
		int StartIndex = name.length();
		stringprocelement = spool.substring(StartIndex);
		return stringprocelement;
	}		
	
	//EXTRACT INPUTMSG
	public ArrayList<Msg> extractInputMsg(String spool)
	{
//		//System.out.println("INPUT EXTRACT outputMSG: "+spool);
				//JOptionPane.showMessageDialog(null, "\ngspool\n"+spool);
				int StartIndex = 0;
				//int LastIndex =0;
				String patternstart = "in:";
				//String patternlast = "),";
				
				StartIndex = spool.indexOf(patternstart)+patternstart.length();
				String msgsin = spool.substring(StartIndex);
				//JOptionPane.showMessageDialog(null, "\nmsgsin\n"+msgsin);
				////System.out.println("MSGOUT: "+msgsout);
				Msg msg1 = new Msg();
				ArrayList<Msg> extract = new ArrayList<Msg>();
				extract = msg1.SplitMsg(msgsin);	
				//JOptionPane.showMessageDialog(null, "\nmextract\n"+extract);	
				return extract;
			}
		// String name = null;
		// String msgin = null;
		
		// ////System.out.println("EXTRACTINMSG: "+spool);
		// Pattern pattern = Pattern.compile("(in: )");
		// Matcher matcher = pattern.matcher(spool);
		// while(matcher.find()) 
		//   {
		// 	name = matcher.group();		   
		//   }
		// ////System.out.println("\nextractName: "+name);
		// int StartIndex = spool.indexOf(name)+name.length();
		// int LastIndex = spool.indexOf(",", StartIndex);			
		// msgin = spool.substring(StartIndex, LastIndex);	
		// JOptionPane.showMessageDialog(null, "\nmsgsin\n"+msgin);	
		// ////System.out.println("MSGIN: "+msgin);
		
		// /*int StartIndex = 0;
		// int LastIndex =0;
		// String patternstart = ",in:";
		// String patternlast = ",";
		
		// StartIndex = spool.indexOf(patternstart)+patternstart.length();
		// LastIndex = spool.indexOf(patternlast, StartIndex);
		// String msgsin = spool.substring(StartIndex, LastIndex);
		// //System.out.println("MSGIN: "+msgsin);*/
		
		// Msg msg1 = new Msg();
		// ArrayList<Msg> extract = new ArrayList<Msg>();
		// extract = msg1.SplitMsg(msgin);	
		// for(int i=0; i<extract.size();i++){
		// 	//System.out.println("extract.get(i).getMsgName(); "+i+" "+extract.get(i).getMsgName()); 
		// 	JOptionPane.showMessageDialog(null, "\nmextract\n"+extract.get(i).getMsgName());
		// }
		// return extract; 
		
	// }
	
	public ArrayList<Msg> extractOutputMsg(String spool)
	{
		////System.out.println("INPUT EXTRACT outputMSG: "+spool);
		int StartIndex = 0;
		//int LastIndex =0;
		String patternstart = "out:";
		//String patternlast = "),";
		
		StartIndex = spool.indexOf(patternstart)+patternstart.length();
		String msgsout = spool.substring(StartIndex);
		////System.out.println("MSGOUT: "+msgsout);
		Msg msg1 = new Msg();
		ArrayList<Msg> extract = new ArrayList<Msg>();
		extract = msg1.SplitMsg(msgsout);		
		return extract;
	}
	
	public Proc comparePoolperProcess(Pool pool1, Pool pool2)
	{
		Proc result = new Proc();
		Proc proc1 = pool1.ProcessElements;
		Proc proc2 = pool2.ProcessElements;
		Proc procappoggio = new Proc();
		
		result = procappoggio.compareProcess(proc1, proc2);
		return result;
	}
	
	public void printPool()
	{
		Msg msgprint = new Msg();
		//System.out.println("\nOrganizationName: "+this.OrganizationName);
		//System.out.println("\nProcessElements: ");
		this.ProcessElements.printProcess();
		//System.out.println("\nInputMsg: ");
		msgprint.printMsgList(this.InputMsg);
		//System.out.println("\nOutputMsg: ");
		msgprint.printMsgList(this.OutputMsg);
	}	
	
	public String getOrganizationName(){
		return this.OrganizationName;
	}
	
	public Proc getProcessElements(){
		return this.ProcessElements;
	}
	
}
