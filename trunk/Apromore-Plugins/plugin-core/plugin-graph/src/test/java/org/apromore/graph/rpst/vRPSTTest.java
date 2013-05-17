package org.apromore.graph.rpst;

import java.io.IOException;

import junit.framework.TestCase;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.MultiDirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.utils.IOUtils;

public class vRPSTTest extends TestCase {

	/*public void testBPM08_Figure6() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex v1 = new Vertex("v1");
		Vertex v2 = new Vertex("v2");
		Vertex v3 = new Vertex("v3");
		Vertex v4 = new Vertex("v4");
		Vertex v5 = new Vertex("v5");
		Vertex v6 = new Vertex("v6");
		Vertex v7 = new Vertex("v7");
		
		g.addVertex(s);
		g.addVertex(t);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		
		g.addEdge(s,v1);
		g.addEdge(s,v2);
		g.addEdge(v1,v3);
		g.addEdge(v1,v5);
		g.addEdge(v2,v5);
		g.addEdge(v3,v2);
		g.addEdge(v3,v4);
		g.addEdge(v4,v1);
		g.addEdge(v4,v2);
		g.addEdge(v5,v6);
		g.addEdge(v5,v7);
		g.addEdge(v6,v5);
		g.addEdge(v6,v7);
		g.addEdge(v7,v5);
		g.addEdge(v7,t);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
	
	/*public void testSimpleBond() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex s = new Vertex("s");
		Vertex t = new Vertex("t");
		Vertex x = new Vertex("x");
		Vertex y = new Vertex("y");
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		
		g.addEdge(s,x);
		g.addEdge(y,t);
		g.addEdge(x,a);
		g.addEdge(a,y);
		g.addEdge(y,b);
		g.addEdge(b,x);
		g.addEdge(y,x);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);
		
		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
	
	/*public void testSimplePolygon() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		
		g.addEdge(a,b);
		g.addEdge(b,c);
		g.addEdge(c,d);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
	
	/*public void testMultiSource() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		
		g.addEdge(a,c);
		g.addEdge(b,c);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
	
	/*public void testSingleVertex() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex a = new Vertex("a");
		
		g.addVertex(a);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
	
	/*public void testMultipleVertices() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		
		g.addVertex(a);
		g.addVertex(b);
		g.addVertex(c);
		
		vRPST<DirectedEdge,Vertex> rpst = new vRPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IvRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IvRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IvRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/

    public void testEdgeAndVertex() throws IOException {
        System.out.println("--------------------------------");
        MultiDirectedGraph g = new MultiDirectedGraph();

        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");
        Vertex c = new Vertex("c");

        g.addEdge(a, b);
        g.addVertex(c);

        vRPST<DirectedEdge, Vertex> rpst = new vRPST<DirectedEdge, Vertex>(g);

        System.out.println("ROOT: " + rpst.getRoot());

        for (IvRPSTNode<DirectedEdge, Vertex> node : rpst.getRPSTNodes()) {
            System.out.print(node + ": ");
            for (IvRPSTNode<DirectedEdge, Vertex> child : rpst.getChildren(node)) {
                System.out.print(child.getName() + " ");
            }

            System.out.print(" : ");

            for (IvRPSTNode<DirectedEdge, Vertex> child : node.getOrderedChildren()) {
                System.out.print(child.getName() + " ");
            }

            System.out.println();
        }

        IOUtils.invokeDOT(".", "g.png", g.toDOT());
        IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
    }
	
	/*public void testOneEdge() throws IOException {
		System.out.println("--------------------------------");
		MultiDirectedGraph g = new MultiDirectedGraph();
		
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		
		g.addEdge(a,b);
		
		RPST<DirectedEdge,Vertex> rpst = new RPST<DirectedEdge,Vertex>(g);

		System.out.println("ROOT: " + rpst.getRoot());

		for (IRPSTNode<DirectedEdge,Vertex> node : rpst.getRPSTNodes()) {
			System.out.print(node + ": ");
			for (IRPSTNode<DirectedEdge,Vertex> child : rpst.getChildren(node)) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.print(" : ");
			
			for (IRPSTNode<DirectedEdge,Vertex> child : node.getOrderedChildren()) {
				System.out.print(child.getName() + " ");	
			}
			
			System.out.println();
		}
		
		IOUtils.invokeDOT(".", "g.png", g.toDOT());
		IOUtils.invokeDOT(".", "t.png", rpst.toDOT());
	}*/
}
