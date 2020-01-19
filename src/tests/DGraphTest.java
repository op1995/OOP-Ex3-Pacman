package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

class DGraphTest {

	@Test 
	public void getNodeTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);		
		assertEquals(Graph.getNode(10), N);
	}
	@Test
	public void getEdgeTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		Node N2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(N2);
		Graph.connect(N.getKey(),N2.getKey(),10);
		Edge edge = (Edge) Graph.getEdge(N.getKey(), N2.getKey());
		assertEquals(edge, Graph.Edges.get(N.getKey()).get(N2.getKey()));
	}
	@Test
	public void addNodeTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		assertTrue(Graph.getNodes().containsKey(N.getKey()));
	}
	@Test
	public void connectTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		Node N2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(N2);
		Graph.connect(N.getKey(),N2.getKey(),10);
		assertTrue(Graph.getEdges().get(N.getKey()).containsKey(N2.getKey()));
	}
	@Test 
	public void removeNodeTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);		
		Graph.removeNode(N.getKey());
		assertTrue(!Graph.getNodes().containsKey(N.getKey()));
	}
	@Test 
	public void removeEdgeTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		Node N2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(N2);
		Graph.connect(N.getKey(),N2.getKey(),10);
		Graph.removeEdge(N.getKey(),N2.getKey());
		assertTrue(!Graph.getEdges().get(N.getKey()).containsKey(N2.getKey()));
	}
	@Test
	public void getVTest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		Node N2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(N2);
		ArrayList<node_data> nodes = (ArrayList<node_data>) Graph.getV();
		for(int u : Graph.Nodes.keySet()) {
			if(!nodes.contains(Graph.Nodes.get(u))) {
				fail("This Node should be in the Collection.");
			}
		}
		nodes.get(0).setWeight(10);
		if(!(nodes.get(0).getWeight() == Graph.Nodes.get(N.getKey()).getWeight())) {
			fail("This Nodes should have the same weight.");
		}
	}
	@Test
	public void getETest() {
		DGraph Graph = new DGraph();
		Node N = new Node(10,new Point3D(0,0,0),"");
		Graph.addNode(N);
		Node N2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(N2);
		Graph.connect(N.getKey(),N2.getKey(),10);
		ArrayList<edge_data> edges = (ArrayList<edge_data>) Graph.getE(10);
		for(int u : Graph.Edges.get(10).keySet()) {
			if(!edges.contains(Graph.Edges.get(10).get(u))) {
				fail("This Edge should be in the Collection.");
			}
		}
		edges.get(0).setTag(0);
		if(!(edges.get(0).getTag() == Graph.Edges.get(10).get(11).getTag())) {
			fail("This Nodes should have the tag.");
		}
	}
}
