package plugin.bpmn.to.maude.notation;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventInterRcv extends EventSplit implements java.io.Serializable{
	
	String Status;
	Edge InterEdge;
	Msg InterMsg;
	
	public EventInterRcv()
	{
		
	}
	
	public EventInterRcv(String inter)
	{
		
		ArrayList<String> interrcv = new ArrayList<String>();
		interrcv = TokenGateway(inter);
		
		Pattern pattern2 = Pattern.compile("(eventInterRcv)+\\(");
		Matcher matcher2 = pattern2.matcher(interrcv.get(0));	
		
		if (matcher2.find()) 
		{
			int startindex = interrcv.get(0).indexOf(matcher2.group())+matcher2.group().length();
			this.Status = interrcv.get(0).substring(startindex).replaceAll("\\s+","");
		}else{
			this.Status = interrcv.get(0).replaceAll("\\s+","");
		}
		
		this.InterEdge = new Edge(interrcv.get(1));
		this.InterMsg = new Msg(interrcv.get(2));
		
	}
	
	public boolean compareEventInterRcv(EventInterRcv eveninter1, EventInterRcv eveninter2)
	{
		if(eveninter1.InterEdge.EdgeName.equals(eveninter2.InterEdge.EdgeName) 
				&& !eveninter1.InterEdge.EdgeToken.equals(eveninter2.InterEdge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	public void SetToken (EventInterRcv eventint)
	{
		//System.out.println("SETTOKEN");
		String token = String.valueOf(1);
		eventint.InterEdge.EdgeToken = token;	
		//System.out.println("TOKEN "+eventint.InterEdge.EdgeToken);
	}
	
	public void printInter()
	{
		//System.out.println("Status: "+this.Status);
		this.InterEdge.printEdge();
		this.InterMsg.printMsg();
	}
}
