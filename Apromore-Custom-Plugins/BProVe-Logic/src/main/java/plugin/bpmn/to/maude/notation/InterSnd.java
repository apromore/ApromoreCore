package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InterSnd extends MsgEvent  implements java.io.Serializable{
	
	//Status Edge Edge Msg
	String Status;
	Edge InputEdge;
	public Edge OutputEdge;
	Msg msg;
	
	
	public InterSnd()
	{
		
	}

	public InterSnd(String intersnd) {
		ArrayList<String> isnd = new ArrayList<String>();
		isnd = TokenMsgEvent(intersnd);
		this.Status = isnd.get(0);
		this.InputEdge = new Edge(isnd.get(1));
		this.OutputEdge = new Edge(isnd.get(2));
		this.msg = new Msg(isnd.get(3));
		
		}
		
	public boolean compareInterSnd(InterSnd internsd1, InterSnd internsd2)
	{
		if(!internsd1.Status.equals(internsd2.Status)) 
				
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareMsgThrowEventName (InterSnd msgthrowevent1, InterSnd msgthrowevent2)
	{
		if(msgthrowevent1.name.equals(msgthrowevent2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printInterSnd()
	{
		//System.out.println("\nInterSnd: ");
		//System.out.println("Status: "+this.Status);
		this.InputEdge.printEdge();
		this.OutputEdge.printEdge();
		this.msg.printMsg();
	}

}
