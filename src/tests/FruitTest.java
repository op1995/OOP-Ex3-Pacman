package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.Node;
import gameClient.Fruit;
import utils.Point3D;

class FruitTest {


	//This is a simple test to make sure the constructor works correctly.
	//The constructor of this class uses the Json string received from the server about the fruit to create the Fruit object.
	//By using a known scenario we can make sure the parser takes and sets the values given to it correctly.
	//(the 1st fruit is always at the same place)

	@Test
	public void constructorTest() {
		boolean answer = true;
		game_service game = Game_Server.getServer(0);
		DGraph testGraph = new DGraph();
		testGraph.init(game.getGraph());
		Iterator<String> fruits_iterator = game.getFruits().iterator();
		String fruitString = fruits_iterator.next();
		System.out.println(fruitString);
		Fruit testFruit = new Fruit(fruitString);

		if(testFruit.getValue()!=5 || testFruit.getType()!=-1 || !testFruit.getPos().equalsXY(new Point3D(35.197656770719604,32.10191878639921,0.0))) {
			answer = false;
		}
		assertTrue(answer);
	}

	//A very simple test for the getters and setters of the class.
	@Test
	public void getSetTest() {
		boolean answer = true;
		Fruit testFruit = new Fruit(); //create a new fruit

		Node newSrc = new Node(0, new Point3D(0,0,0)); //make values to set as the fruit's values using setters
		Node newDest = new Node(1, new Point3D(2,2,0));
		Edge newEdge = new Edge(newSrc, newDest, 2);
		Point3D newPosition = new Point3D(1,1,0);

		testFruit.setEdge(newEdge);
		testFruit.setPos(newPosition);
		testFruit.setisAlive(true);

		if(!testFruit.getisAlive() || testFruit.getEdge()!=newEdge || !testFruit.getPos().equalsXY(newPosition) || testFruit.getType()!=0 || testFruit.getValue()!=0) {
			answer = false;
		}

		assertTrue(answer);

	}


}
