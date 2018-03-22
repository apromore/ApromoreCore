package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class MsgEvent extends Event  implements java.io.Serializable{
	
	public MsgEvent()
	{
		
	}
	
	public MsgEvent(String msgevent)
	{
		
	}
	
	public ArrayList<String> TokenMsgEvent(String msgevent)
	{
		ArrayList<String> TokenMsgEvent = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(msgevent, ",");
		while (st.hasMoreTokens())
	    	{
				TokenMsgEvent.add(st.nextToken());
	    	}
		return TokenMsgEvent;	
	}

	public ArrayList<String> MsgEventPointDivision(String MsgEvent)
		{
			ArrayList<String> MsgDivision = new ArrayList<String>();
			
		
				StringTokenizer st = new StringTokenizer(MsgEvent, ".");
				while (st.hasMoreTokens())
					{
						MsgDivision.add(st.nextToken());
					}
			
			return MsgDivision;	
		}		
}
