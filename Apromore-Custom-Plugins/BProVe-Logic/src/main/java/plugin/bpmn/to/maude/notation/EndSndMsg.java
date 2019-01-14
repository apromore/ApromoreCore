package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EndSndMsg extends End implements java.io.Serializable{
	
	//Status Edge Msg
	
	String Status;
	Msg msg;
	
	public EndSndMsg()
	{
		
	}
	
	public EndSndMsg (String endsndmsg)
	{
		ArrayList<String> EndSndCatch = new ArrayList<String>();
		
		EndSndCatch = TokenEnd(endsndmsg);
		this.Status = EndSndCatch.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(EndSndCatch.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = EndSndCatch.get(1).lastIndexOf("\"");
			String substring = EndSndCatch.get(1).substring(startindex, endindex);
			this.Edge = new Edge(substring);
		}
		
		this.msg = new Msg(EndSndCatch.get(2));
	}
	
	public boolean compareEndSndMsg(EndSndMsg endsndmsg1, EndSndMsg endsndmsg2)
	{
		if(endsndmsg1.name.equals(endsndmsg2.name)
				&& !endsndmsg1.Edge.EdgeToken.equals(endsndmsg2.Edge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}	
	
	/*public boolean compareEndSndMsgName (EndSndMsg endsndmsg1, EndSndMsg endsndmsg2)
	{
		if(endsndmsg1.name.equals(endsndmsg2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printEndSndMsg()
	{
		//Status Edge Msg
		//System.out.println("\nEndSndMsg: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();
		this.msg.printMsg();
		////System.out.println("OutputToken: "+OutputToken);
	}
}
