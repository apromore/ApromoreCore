package org.apromore.prodrift.graph.util;


//import org.jbpt.graph.DirectedEdge;
//import org.jbpt.graph.DirectedGraph;
//import org.jbpt.hypergraph.abs.Vertex;

public class GraphUtils {

	public static void transitiveClosure(boolean[][] m) {
		int n = m.length;

		long t1 = System.currentTimeMillis();
		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					m[i][j] |= (m[i][k] & m[k][j]);
		
		System.out.println(System.currentTimeMillis() - t1);
		
	}

	public static boolean[][] transitiveReduction(boolean[][] m) {
		int n = m.length;

		boolean[][] originalMatrix = new boolean[n][n];
		copyMatrix(m, originalMatrix);

		for (int j = 0; j < n; ++j)
			for (int i = 0; i < n; ++i)
				if (originalMatrix[i][j])
					for (int k = 0; k < n; ++k)
						if (originalMatrix[j][k])
							m[i][k] = false;
		
		return originalMatrix;
	}

	private static void copyMatrix(boolean[][] m, boolean[][] copyOfM) {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m.length; j++)
				copyOfM[i][j] = m[i][j];
		
	}

//	public static boolean[][] getAdjacencyMatrix(DirectedGraph g,
//			Map<Vertex, Integer> map, List<Vertex> vertices) {
//		for (Vertex v : g.getVertices()) {
//			map.put(v, vertices.size());
//			vertices.add(v);
//		}
//
//		boolean[][] m = new boolean[vertices.size()][vertices.size()];
//
//		for (DirectedEdge e : g.getEdges()) {
//			int src = map.get(e.getSource());
//			int tgt = map.get(e.getTarget());
//			m[src][tgt] = true;
//		}
//
//		return m;
//	}
//
//	public static DirectedGraph getDirectedGraph(boolean[][] m,
//			List<Vertex> vertices) {
//		DirectedGraph g = new DirectedGraph();
//		for (int i = 0; i < m.length; i++) {
//			Vertex src = vertices.get(i);
//			for (int j = 0; j < m.length; j++)
//				if (m[i][j]) {
//					Vertex tgt = vertices.get(j);
//					g.addEdge(src, tgt);
//				}
//		}
//		return g;
//	}

}
