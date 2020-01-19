package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import gameClient.Fruit;
import gameClient.Robot;
import gameClient.Robot_Algo;

class Robot_AlgoTest {

	// Each game on the server has the same initial location for each fruit.
	// The game 0 has only 1 fruit. As we can know on what edge it will be initially,
	// we check to make sure we get the right edge.
	
	@Test
	public void findEdgeTest() { 
		boolean answer = true;
		game_service game = Game_Server.getServer(0);
		DGraph testGraph = new DGraph();
		testGraph.init(game.getGraph());
		Iterator<String> fruits_iterator = game.getFruits().iterator();
		while(fruits_iterator.hasNext()) {
			try {
				Fruit fruit = new Fruit(fruits_iterator.next());
				testGraph.addFruit(fruit);
				int src = fruit.getEdge().getSrc();
				if(src!=9) {answer=false;}
				int dest = fruit.getEdge().getDest();
				if(dest!=8) {answer=false;}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		assertTrue(answer);
	}
	
	// Each game on the server has the same initial location for each fruit.
	// The game 1 has 2 fruits at all times. As we can know on what edges they will be initially,and we can place our robot
	// we will place the robot so it will be closer to one of the two fruits in the game.
	// We will check it finds that fruit as the closest fruit to it.
	
	@Test
	public void getClosestFruitTest() {
		boolean answer = true;
		game_service game = Game_Server.getServer(1); //get game number 1
		DGraph testGraph = new DGraph();
		testGraph.init(game.getGraph()); //init the game graph from the game on the server
		Iterator<String> fruits_iterator = game.getFruits().iterator();
		while(fruits_iterator.hasNext()) { //add all the fruits to the game
			try {
				Fruit fruit = new Fruit(fruits_iterator.next());
				testGraph.addFruit(fruit);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Fruit lastFruit = new Fruit();
		for (Fruit f: testGraph.Fruits.keySet()) { //go over all the fruits, get the last one.
			lastFruit = f;
		}
		if(lastFruit.getType()==1) {game.addRobot(lastFruit.getEdge().getSrc());} //add the robot at dest or src node of the fruit's edge, according to the fruit's type
		else {game.addRobot(lastFruit.getEdge().getDest());}
		testGraph.addRobot(new Robot(game.getRobots().get(0))); //add the robot to the game (there should only be 1 robot in game number 1).
		Robot_Algo temp_Robot_Algo_object = new Robot_Algo(testGraph);
		Fruit foundClosestFruit = temp_Robot_Algo_object.getClosestFruit(0, game, testGraph); //run the getClosestFruit method
		
//		if (foundClosestFruit.getEdge() != lastFruit.getEdge()) {answer=false;}
		if (foundClosestFruit != lastFruit) {answer=false;} //check if the answer returned from the getClosestFruit method is correct (should be the fruit we placed the robot next to)
		
		assertTrue(answer);
		
		
		
	}
	
	
}
