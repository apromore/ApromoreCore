package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReceiveTask extends Task implements java.io.Serializable{
	 
	//Status Edge Edge Msg TaskName
	//String MsgName;
	//String MsgToken;
	Msg msg;
	
	public ReceiveTask()
	{
		
	}
	
	public ReceiveTask(String ReceiveTask) {
		
		ArrayList<String> ReceiveTaskString = new ArrayList<String>();
		ReceiveTaskString = EdgeTaskEdge(ReceiveTask);
		
		this.TaskStatus = ReceiveTaskString.get(0);
		this.InputEdge = new Edge(ReceiveTaskString.get(1)); 
		this.OutputEdge = new Edge(ReceiveTaskString.get(2));
		this.msg = new Msg(ReceiveTaskString.get(3));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(ReceiveTaskString.get(4));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = ReceiveTaskString.get(4).lastIndexOf("\"");
			String substring = ReceiveTaskString.get(4).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			this.name = substring;
		}
		
		
		/*ArrayList<String> ReceiveTaskString = new ArrayList<String>();
		ArrayList<String> ReceiveTaskParam = new ArrayList<String>();
		ArrayList<String> ReceiveMsgParam = new ArrayList<String>();
		ReceiveTaskString = EdgeTaskEdge(ReceiveTask);		
		this.InputEdge = new Edge(ReceiveTaskString.get(0)); 
		this.OutputEdge = new Edge(ReceiveTaskString.get(ReceiveTaskString.size()-1));
		ReceiveTaskParam = PointDivision(ReceiveTaskString.get(1));
		ReceiveMsgParam = PointDivision(ReceiveTaskString.get(2));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(ReceiveTaskParam.get(0));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = ReceiveTaskParam.get(0).lastIndexOf("\"");
			String substring = ReceiveTaskParam.get(0).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			////System.out.println("STARTINDEX: "+startindex);
			this.name = substring;
		}
		
		this.TaskToken = ReceiveTaskParam.get(1);
		this.TaskStatus = ReceiveTaskParam.get(2);
		this.MsgName = ReceiveMsgParam.get(0);
		this.MsgToken = ReceiveMsgParam.get(1).replaceAll("\\D+","");*/		
	}
	
	public boolean compareReceiveTask(ReceiveTask ReceiveTask1, ReceiveTask ReceiveTask2)
	{
		if(ReceiveTask1.name.equals(ReceiveTask2.name) 
				&& !ReceiveTask1.InputEdge.EdgeToken.equals(ReceiveTask2.InputEdge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	public boolean compareReceiveTaskName (ReceiveTask ReceiveTask1, ReceiveTask ReceiveTask2)
	{
		if(ReceiveTask1.name.equals(ReceiveTask2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void printReceiveTask()
	{
		//System.out.println("\nReceiveTask: ");
		//System.out.println("TaskStatus: "+this.TaskStatus);
		this.InputEdge.printEdge();
		this.OutputEdge.printEdge();
		this.msg.printMsg();
		//System.out.println("TaskName: "+name);
		
		/*//System.out.println("\nReceiveTask: ");
		this.InputEdge.printEdge();
		//System.out.println("TaskName: "+name);
		//System.out.println("TaskToken: "+TaskToken);
		//System.out.println("TaskStatus: "+TaskStatus);
		//System.out.println("MsgName: "+MsgName);
		//System.out.println("MsgToken: "+MsgToken);
		this.OutputEdge.printEdge();*/	
	}
		
}
