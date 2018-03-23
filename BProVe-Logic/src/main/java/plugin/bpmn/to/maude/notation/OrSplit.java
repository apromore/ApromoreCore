package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.bpmn.to.maude.notation.Gateway.GatewayType;


public class OrSplit extends Gateway  implements java.io.Serializable{

	public OrSplit()
	{
		
	}
		
	public OrSplit(String orsplit)
	{
		
		ArrayList<String> SplitGateway = TokenGateway(orsplit);
		Edge edge = new Edge();		
		this.Edge = new Edge(SplitGateway.get(0));
		
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(1)));
				
		this.Gtype = GatewayType.Join;
		
	}
	
	public boolean compareOrSplit(OrSplit orsplit1, OrSplit orsplit2)
	{
		if(!orsplit1.EdgeSet.get(0).EdgeToken.equals(orsplit2.EdgeSet.get(0).EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareOrSplitName (OrSplit orsplit1, OrSplit orsplit2)
	{
		if(orsplit1.name.equals(orsplit2.name)) 
		{
		return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printOrSplit()
	{
		//System.out.println("\nOrsplit: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();	
	}

}
