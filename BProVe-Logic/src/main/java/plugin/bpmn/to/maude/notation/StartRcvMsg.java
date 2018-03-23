package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StartRcvMsg  extends Start implements java.io.Serializable{
	
	//Status Edge Msg
	
	//String MsgName;
	//String MsgToken;
	
	Msg msg;
	
	public StartRcvMsg()
	{
		
	}
	
	public StartRcvMsg(String StartRcvMsg) {
		
		ArrayList<String> StartRcvToken = new ArrayList<String>();	
		//ArrayList<String> StartRcvParam = new ArrayList<String>();
		StartRcvToken = super.TokenStartEdge(StartRcvMsg);
		
		this.Status = StartRcvToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^\\s*+\"");
		Matcher matcher = pattern.matcher(StartRcvToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			//int endindex = StartRcvToken.get(1).lastIndexOf(")");
			String substring = StartRcvToken.get(1).substring(startindex);// endindex);
			this.Edge = new Edge(substring);
		}
		this.msg = new Msg(StartRcvToken.get(2));
	}
	
	public boolean compareStartRcvMsg(StartRcvMsg startrcvmsg1, StartRcvMsg startrcvmsg2)
	{
		if(startrcvmsg1.Edge.EdgeName.equals(startrcvmsg2.Edge.EdgeName)
				&& !startrcvmsg1.Status.equals(startrcvmsg2.Status))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareStartRcvMsgName (StartRcvMsg startrcvmsg1, StartRcvMsg startrcvmsg2)
	{
		if(startrcvmsg1.name.equals(startrcvmsg2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printStartRcvMsg()
	{
		//Status Edge Msg
		//System.out.println("\nStartRcv: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();
		this.msg.printMsg();
		/*//System.out.println("InputToken: "+InputToken);
		//System.out.println("MsgEventName : "+name);
		//System.out.println("MsgName: "+MsgName);
		//System.out.println("MsgToken: "+MsgToken);
		this.OutputEdge.printEdge();*/		
	}	

}
