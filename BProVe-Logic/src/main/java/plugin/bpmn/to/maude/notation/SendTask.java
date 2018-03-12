package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SendTask extends Task implements java.io.Serializable{
	
	//Status Edge Edge Msg TaskName
	//String MsgName;
	//String MsgToken;
	Msg msg;
		
	public SendTask()
	{
		
	}
	
	public SendTask(String SendTask) {
		
		ArrayList<String> SendTaskString = new ArrayList<String>();
		SendTaskString = EdgeTaskEdge(SendTask);
		
		this.TaskStatus = SendTaskString.get(0);
		this.InputEdge = new Edge(SendTaskString.get(1)); 
		this.OutputEdge = new Edge(SendTaskString.get(2));
		this.msg = new Msg(SendTaskString.get(3));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(SendTaskString.get(4));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = SendTaskString.get(4).lastIndexOf("\"");
			String substring = SendTaskString.get(4).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			this.name = substring;
		}		
		
		/*ArrayList<String> SendTaskString = new ArrayList<String>();
		ArrayList<String> SendTaskParam = new ArrayList<String>();
		ArrayList<String> MsgParam = new ArrayList<String>();
		SendTaskString = EdgeTaskEdge(SendTask);
		for(String a: SendTaskString)
			//System.out.println("DIVISIONE SENDTASK: "+a);
		this.InputEdge = new Edge(SendTaskString.get(0)); 
		this.OutputEdge = new Edge(SendTaskString.get(SendTaskString.size()-1));
		SendTaskParam = PointDivision(SendTaskString.get(1));
		MsgParam = PointDivision(SendTaskString.get(2));
		this.name = SendTaskParam.get(0).substring(1, SendTaskParam.get(0).length()-2);
		this.TaskToken = SendTaskParam.get(1);
		this.TaskStatus = SendTaskParam.get(2);
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(MsgParam.get(0));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = MsgParam.get(0).lastIndexOf("\"");
			String substring =MsgParam.get(0).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			this.MsgName = substring;
		}	
		
		this.MsgToken = MsgParam.get(1).replaceAll("\\D+","");*/		
	}
		
	public boolean compareSendTask(SendTask sendtask1, SendTask sendtask2)
	{
		if(sendtask1.name.equals(sendtask2.name)
				&& !sendtask1.InputEdge.EdgeToken.equals(sendtask2.InputEdge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	public boolean compareSendTaskName (SendTask sendtask1, SendTask sendtask2)
	{
		if(sendtask1.name.equals(sendtask2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void printSendTask()
	{
		//Status Edge Edge Msg TaskName
		
		//System.out.println("\nSendTask: ");
		//System.out.println("TaskStatus: "+this.TaskStatus);
		this.InputEdge.printEdge();
		this.OutputEdge.printEdge();
		this.msg.printMsg();
		//System.out.println("TaskName: "+name);
						
	}
		

}
