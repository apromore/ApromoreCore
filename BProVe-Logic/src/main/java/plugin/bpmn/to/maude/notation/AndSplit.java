package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.bpmn.to.maude.notation.Gateway.GatewayType;

public class AndSplit extends Gateway implements java.io.Serializable{
	
	//Edge EdgeSet
	public AndSplit()
	{
		
	}
	
	public AndSplit(String andsplit)
	{
		ArrayList<String> SplitGateway = TokenGateway(andsplit);
		Edge edge = new Edge();		
		this.Edge = new Edge(SplitGateway.get(0));
		
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(1)));
				
		this.Gtype = GatewayType.Join;
	}
	
	public boolean compareAndSplit(AndSplit andsplit1, AndSplit andsplit2)
	{
		if(!andsplit1.EdgeSet.get(0).EdgeToken.equals(andsplit2.EdgeSet.get(0).EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareAndSplitName (AndSplit andsplit1, AndSplit andsplit2)
	{
		if(andsplit1.name.equals(andsplit2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printAndSplit()
	{	
		// Edge EdgeSet
		//System.out.println("\nAndSplit: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();				
	}
}
