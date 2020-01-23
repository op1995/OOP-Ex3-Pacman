package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.node_data;
import utils.Point3D;

//this class tests more then what is probably expected to be tested for this task
//this is because these contain more extensive tests on parts that were more related to the main goal of the previous task
//however, these tests are still valid and make sure that methods we use in this task work properly, so we want to include them in the project.

class GraphAlgoTest {

	@Test
	public void isConnectedTest() {
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.connect(n1.getKey(), n2.getKey(), 10.0);
		Graph.connect(n2.getKey(), n1.getKey(), 10.0);
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		assertTrue(Algo.isConnected());
	}
	@Test
	public void isNotConnectedTest() {
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.connect(n1.getKey(), n2.getKey(), 10.0);
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		if(Algo.isConnected()) {
			fail("Shoudn't be connected");
		}
	}
	@Test
	public void shortestPathDistTest() {
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Node n3 = new Node(12,new Point3D(0,0,0),"");
		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.addNode(n3);
		Graph.connect(n1.getKey(), n3.getKey(), 1000.0);
		Graph.connect(n1.getKey(), n2.getKey(), 10.0);
		Graph.connect(n2.getKey(), n3.getKey(), 10.0);
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		double dist = Algo.shortestPathDist(n1.getKey(),n3.getKey());
		assertEquals(dist,20);
	}
	@Test
	public void shortestPathTest() {
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Node n3 = new Node(12,new Point3D(0,0,0),"");
		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.addNode(n3);
		Graph.connect(n1.getKey(), n3.getKey(), 1000.0);
		Graph.connect(n1.getKey(), n2.getKey(), 10.0);
		Graph.connect(n2.getKey(), n3.getKey(), 10.0);
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		ArrayList<node_data> path = (ArrayList<node_data>) Algo.shortestPath(n1.getKey(),n3.getKey());
		boolean PathBool = true;
		if(!(n1.getKey() == path.get(0).getKey())) {
			PathBool = false;
		}
		if(!(n2.getKey() == path.get(1).getKey())) {
			PathBool = false;
		}
		if(!(n3.getKey() == path.get(2).getKey())) {
			PathBool = false;
		}
		assertTrue(PathBool);
	}
	@Test
	public void deepcopyTest() {
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Node n3 = new Node(12,new Point3D(0,0,0),"");
		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.addNode(n3);
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		DGraph copy = (DGraph) Algo.copy();
		copy.connect(n1.getKey(), n2.getKey(), 10);
		if(Graph.Edges.get(n1.getKey()).containsKey(n2.getKey())) {
			fail("This Edge shouldn't in the Original Graph");
		}
		Node n4 = new Node(13,new Point3D(0,0,0),"");
		copy.addNode(n4);
		if(Graph.Nodes.containsKey(n4.getKey())) {
			fail("This Node shouldn't in the Original Graph");
		}
		n3.setWeight(100);
		if(copy.Nodes.get(n3.getKey()).getWeight() == 100) {
			fail("The weight of a node if the deepcopy shouldn't change if it was changed in the Original Graph");
		}
	}
	@Test
	public void TSPTest() { //create a graph on which we know what TSP should return and make sure he gives that as the answer.
		DGraph Graph = new DGraph();
		Node n1 = new Node(10,new Point3D(0,0,0),"");
		Node n2 = new Node(11,new Point3D(0,0,0),"");
		Node n3 = new Node(12,new Point3D(0,0,0),"");
		Node n4 = new Node(13,new Point3D(0,0,0),"");

		Graph.addNode(n1);
		Graph.addNode(n2);
		Graph.addNode(n3);
		Graph.addNode(n4);

		Graph.connect(n1.getKey(), n2.getKey(), 4.0);
		Graph.connect(n2.getKey(), n3.getKey(), 4.0);
		Graph.connect(n3.getKey(), n4.getKey(), 3.0);
		Graph.connect(n4.getKey(), n1.getKey(), 10.0);
		
		List<node_data> actualAnswer = new ArrayList<node_data>();
		actualAnswer.add(n4);
		actualAnswer.add(n1);
		
		Graph_Algo Algo = new Graph_Algo();
		Algo.init(Graph);
		ArrayList<Integer> testTargets = new ArrayList<Integer>();
		testTargets.add(n1.getKey());
		testTargets.add(n4.getKey());
		List<node_data> path = Algo.TSP(testTargets);
		boolean answer = true;
		for (int i = 0; i < path.size() && answer; i++) {
			if(((Node)path.get(i)).getKey()!=((Node)actualAnswer.get(i)).getKey()) {
				answer = false;
			}
		}
		
		assertTrue(answer);

	}
}
