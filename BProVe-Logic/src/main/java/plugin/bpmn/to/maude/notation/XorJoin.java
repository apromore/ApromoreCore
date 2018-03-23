package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.bpmn.to.maude.notation.Gateway.GatewayType;


public class XorJoin extends Gateway  implements java.io.Serializable{

	public XorJoin()
	{
		
	}
		
	public XorJoin(String xorjoin)
	{
		//System.out.println("\nSTRING XORJOIN: "+xorjoin);
		ArrayList<String> SplitGateway = TokenGateway(xorjoin);
		Edge edge = new Edge();
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(0)));
		
		this.Edge = new Edge(SplitGateway.get(1)); 
		
		this.Gtype = GatewayType.Join;	
	}
	
	public boolean compareXorJoin (XorJoin xorjoin1, XorJoin xorjoin2)
	{
		for(int i = 0; i<xorjoin1.EdgeSet.size(); i++)
		{
			if(xorjoin1.EdgeSet.get(i).EdgeName.equals(xorjoin2.EdgeSet.get(i).EdgeName)
					&& !xorjoin1.EdgeSet.get(i).EdgeToken.equals(xorjoin2.EdgeSet.get(i).EdgeToken)) return true;
		}
		return false;		
	}
	
	/*public boolean compareXorJoinName (XorJoin xorjoin1, XorJoin xorjoin2)
	{
		if(xorjoin1.name.equals(xorjoin2.name)) 
		{
			return true;			
		}else return false;		
	}*/
	
	public void printXorJoin()
	{
		//System.out.println("\nXorJoin: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();					
	}
	
	
}
