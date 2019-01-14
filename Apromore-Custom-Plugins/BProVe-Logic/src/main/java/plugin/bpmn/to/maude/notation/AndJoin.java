package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndJoin extends Gateway implements java.io.Serializable{
	
	//EdgeSet Edge
	
	public AndJoin()
	{
		
	}
	
	
	public AndJoin(String andjoin)
	{	
		ArrayList<String> SplitGateway = TokenGateway(andjoin);
		Edge edge = new Edge();
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(0)));
		
		this.Edge = new Edge(SplitGateway.get(1)); 
		
		this.Gtype = GatewayType.Join;		
	}
	
		
	public boolean compareAndJoin (AndJoin andjoin1, AndJoin andjoin2)
	{
		for(int i = 0; i<andjoin1.EdgeSet.size(); i++)
		{
			if(andjoin1.EdgeSet.get(i).EdgeName.equals(andjoin2.EdgeSet.get(i).EdgeName)
					&& !andjoin1.EdgeSet.get(i).EdgeToken.equals(andjoin2.EdgeSet.get(i).EdgeToken)) return true;
		}
		return false;	
	}
	
	/*public boolean compareAndJoinName (AndJoin andjoin1, AndJoin andjoin2)
	{
		if(andjoin1.name.equals(andjoin2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printAndJoin()
	{
		//System.out.println("\nAndJoin: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();
		
	}
	

}
