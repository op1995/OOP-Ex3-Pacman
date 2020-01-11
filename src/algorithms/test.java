package algorithms;
import java.util.ArrayList;

import dataStructure.*;
import utils.Point3D;

public class test {
	
	public static void main(String[] args) {
		
//		Node first = new Node(0, 0, new Point3D(0, 0), "first");
//		Node second = new Node(1, 0, new Point3D(0, 0), "second");
//		Node third = new Node(2, 0, new Point3D(0, 0), "third");
//		Node fourth = new Node(3, 0, new Point3D(0, 0), "fourth");
//		
//		DGraph testGraph = new DGraph();
//		testGraph.addNode(first);
//		testGraph.addNode(second);
//		testGraph.addNode(third);
//		testGraph.addNode(fourth);
//		
//		testGraph.connect(0, 1, 1);
//		testGraph.connect(0, 2, 2);
//		testGraph.connect(1, 3, 100);
//		testGraph.connect(2, 3, 2);
		;
		Node first = new Node(0, 0, new Point3D(Math.random()*700,Math.random()*450,0), "first");
		Node second = new Node(1, 0, new Point3D(Math.random()*700,Math.random()*450,0), "second");
		Node third = new Node(2, 0, new Point3D(Math.random()*700,Math.random()*450,0), "third");
		Node fourth = new Node(3, 0, new Point3D(Math.random()*700,Math.random()*450,0), "fourth");
		Node fifth = new Node(4, 0, new Point3D(Math.random()*700,Math.random()*450,0), "fifth");
		Node sixth = new Node(5, 0, new Point3D(Math.random()*700,Math.random()*450,0), "sixth");
		Node seventh = new Node(6, 0, new Point3D(Math.random()*700,Math.random()*450,0), "seventh");
		Node eigth = new Node(7, 0, new Point3D(Math.random()*700,Math.random()*450,0), "eigth");
		Node ninth = new Node(8, 0, new Point3D(Math.random()*700,Math.random()*450,0), "ninth");
		Node tenth = new Node(9, 0, new Point3D(Math.random()*700,Math.random()*450,0), "tenth");
		Node eleventh = new Node(10, 0, new Point3D(0, 0), "eleventh");

		
		DGraph testGraph = new DGraph();
		testGraph.addNode(first);
		testGraph.addNode(second);
		testGraph.addNode(third);
		testGraph.addNode(fourth);
		testGraph.addNode(fifth);
		testGraph.addNode(sixth);
		testGraph.addNode(seventh);
		testGraph.addNode(eigth);
		testGraph.addNode(ninth);
		testGraph.addNode(tenth);
		testGraph.addNode(eleventh);
		
		testGraph.connect(0, 1, 1);
		testGraph.connect(0, 3, 1);
		testGraph.connect(0, 4, 1);
		
		testGraph.connect(1, 2, 1);
		
		testGraph.connect(2, 10, 1);

		
		testGraph.connect(3, 5, 2);
		
		testGraph.connect(4, 6, 5);
		testGraph.connect(4, 7, 7);

		testGraph.connect(5, 8, 1);
		testGraph.connect(5, 9, 3);
		
		testGraph.connect(7, 10, 2);
		
		testGraph.connect(9, 10, 5);
		Graph_Algo graphAlgoTest = new Graph_Algo();
		graphAlgoTest.init(testGraph);
		System.out.println(graphAlgoTest.isConnected());
		ArrayList<node_data> testAnswer = (ArrayList<node_data>) graphAlgoTest.shortestPath(0, 10);
		System.out.println(testAnswer.toString());
		for (int i = 0; i < testAnswer.size(); i++) {
			Node temp = (Node) testAnswer.get(i);
			if(i!=testAnswer.size()-1) {System.out.print(temp.getKey() + ", ");}
			else {System.out.println(temp.getKey());}
		}
//	
		graphAlgoTest.save("testSaveFile");
		Graph_Algo graphAlgoTest2 = new Graph_Algo();
		graphAlgoTest.init("testSaveFile");
		System.out.println("hello");

		
		
	}//end Main
	
	

	
}
