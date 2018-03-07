package org.processmining.stagemining.utils;

import java.util.*;
import java.io.*;
import java.util.LinkedList;
 
class MaxFlow
{
    private int V = 0; //Number of vertices in graph
    
    public MaxFlow(int vertexNum) {
    	this.V = vertexNum;
    }
 
    /* Returns true if there is a path from source 's' to sink
      't' in residual graph. Also fills parent[] to store the
      path */
    boolean bfs(int rGraph[][], int s, int t, int parent[])
    {
        // Create a visited array and mark all vertices as not
        // visited
        boolean visited[] = new boolean[V];
        for(int i=0; i<V; ++i)
            visited[i]=false;
 
        // Create a queue, enqueue source vertex and mark
        // source vertex as visited
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        visited[s] = true;
        parent[s]=-1;
 
        // Standard BFS Loop
        while (queue.size()!=0)
        {
            int u = queue.poll();
 
            for (int v=0; v<V; v++)
            {
                if (visited[v]==false && rGraph[u][v] > 0)
                {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }
 
        // If we reached sink in BFS starting from source, then
        // return true, else false
        return (visited[t] == true);
    }
    
	 // A DFS based function to find all reachable vertices from s.  The function
	 // marks visited[i] as true if i is reachable from s.  The initial values in
	 // visited[] must be false. We can also use BFS to find reachable vertices
	 void dfs(int rGraph[][], int s, boolean[] visited) {
	     visited[s] = true;
	     for (int i = 0; i < V; i++)
	        if (rGraph[s][i]>0 && !visited[i])
	            dfs(rGraph, i, visited);
	 }
 
    // Returns the maximum flow from s to t in the given graph
	// Return a string
	// The first element is the max flow.
	// Then a comma,
	// Then, the next element is a string, each is a pair of vertex indices
	// separated by a comma.
    ArrayList<String> getMinCut(int graph[][], int s, int t) {
        int u, v;
 
        //--------------------------------------------
        // Create a residual graph and fill the residual graph
        // with given capacities in the original graph as
        // residual capacities in residual graph
 
        // Residual graph where rGraph[i][j] indicates
        // residual capacity of edge from i to j (if there
        // is an edge. If rGraph[i][j] is 0, then there is
        // not)
        //--------------------------------------------
        int rGraph[][] = new int[V][V];
 
        for (u = 0; u < V; u++)
            for (v = 0; v < V; v++)
                rGraph[u][v] = graph[u][v];
 
        // This array is filled by BFS and to store path
        int parent[] = new int[V];
 
        int max_flow = 0;  // There is no flow initially
 
        //--------------------------------------------
        // Augment the flow while there is a path from source
        // to sink
        //--------------------------------------------
        while (bfs(rGraph, s, t, parent)) {
            // Find minimum residual capacity of the edges
            // along the path filled by BFS. Or we can say
            // find the maximum flow through the path found.
            int path_flow = Integer.MAX_VALUE;
            for (v=t; v!=s; v=parent[v])
            {
                u = parent[v];
                path_flow = Math.min(path_flow, rGraph[u][v]);
            }
 
            // update residual capacities of the edges and
            // reverse edges along the path
            for (v=t; v != s; v=parent[v])
            {
                u = parent[v];
                rGraph[u][v] -= path_flow;
                rGraph[v][u] += path_flow;
            }
 
            // Add path flow to overall flow
            max_flow += path_flow;
        }
        
        //System.out.println("Max Flow = " + max_flow);
        
        //--------------------------------------------
        // Flow is maximum now, find vertices reachable from s
        //--------------------------------------------
        ArrayList<String> minCut = new ArrayList<String>();
        minCut.add(String.valueOf(max_flow));
        
        boolean[] visited = new boolean[V];
        dfs(rGraph, s, visited);
        
        // Get all edges that are from a reachable vertex to
        // non-reachable vertex in the original graph
        for (int i = 0; i < V; i++)
          for (int j = 0; j < V; j++)
             if (visited[i] && !visited[j] && graph[i][j] > 0) 
            	 minCut.add(i + "," + j);
     
        return minCut;
    }
 
    // Driver program to test above functions
    public static void main (String[] args) throws java.lang.Exception
    {
        // Let us create a graph shown in the above example
        int graph[][] =new int[][] { {0, 16, 13, 0, 0, 0},
                                     {0, 0, 10, 12, 0, 0},
                                     {0, 4, 0, 0, 14, 0},
                                     {0, 0, 9, 0, 0, 20},
                                     {0, 0, 0, 7, 0, 4},
                                     {0, 0, 0, 0, 0, 0}
                                   };
        MaxFlow m = new MaxFlow(6);
        m.getMinCut(graph, 0, 5);
 
    }
}