package algorithms;
import java.util.ArrayList;


import dataStructure.*;
import utils.Point3D;

public class test2 {

	public static void main(String[] args) {

		Node first = new Node(0, 0, new Point3D(0, 0), "first");
		Node second = new Node(1, 0, new Point3D(0, 0), "second");
		Node third = new Node(2, 0, new Point3D(0, 0), "third");
		Node fourth = new Node(3, 0, new Point3D(0, 0), "fourth");
		Node fifth = new Node(4, 0, new Point3D(0, 0), "fifth");
		Node sixth = new Node(5, 0, new Point3D(0, 0), "sixth");
		
		DGraph testGraph = new DGraph();
		testGraph.addNode(first);
		testGraph.addNode(second);
		testGraph.addNode(third);
		testGraph.addNode(fourth);
		testGraph.addNode(fifth);
		testGraph.addNode(sixth);
		
		testGraph.connect(0, 1, 5);
		testGraph.connect(0, 2, 6);
		testGraph.connect(1, 3, 1);
		testGraph.connect(2, 3, 2);
		testGraph.connect(2, 4, 3);
		testGraph.connect(3, 5, 1);
		testGraph.connect(5, 4, 1);
		
		Graph_Algo graphAlgoTest = new Graph_Algo();
		graphAlgoTest.init(testGraph);
		ArrayList<Integer> targetsss = new ArrayList<Integer>();
		targetsss.add(0);
		targetsss.add(2);
		targetsss.add(4);
		graphAlgoTest.TSP(targetsss);

	}

}