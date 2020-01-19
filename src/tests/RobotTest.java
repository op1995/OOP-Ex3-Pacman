package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

class RobotTest {

	//This is a simple test to make sure the constructor works correctly.
	//The constructor of this class uses the Json string received from the server about the robot to create the robot object.
	//By using a known scenario we can make sure the parser takes and sets the values given to it correctly.
	
	@Test
	public void constructorTest() {
		boolean answer = true;
		game_service game = Game_Server.getServer(0);
		DGraph testGraph = new DGraph();
		testGraph.init(game.getGraph());
		Iterator<String> fruits_iterator = game.getFruits().iterator();
		Robot testRobot = null;
		while(fruits_iterator.hasNext()) { //there should only be 1 fruit at game 0
			try {
				Fruit fruit = new Fruit(fruits_iterator.next());
				testGraph.addFruit(fruit);
				int src = fruit.getEdge().getSrc();
				game.addRobot(src);
				testRobot = new Robot(game.getRobots().get(0));
				testGraph.addRobot(testRobot);
			} catch (Exception e) {}
		}
		
		if(testRobot != null) {
			if(testRobot.getDest()!=-1 || testRobot.getSrc()!=9 ||testRobot.getSpeed()!=1 || !testRobot.getPos().equalsXY(new Point3D(35.19597880064568, 32.10154696638656, 0)) || testRobot.getID()!=0) {answer=false;}
		}
		
		else {//testRobot wasn't initialized, some error occurred.
			System.err.println("Error initing testRobot. Please make sure you are running the test correctly, with the build path of the project correctly set.");
		}
		assertTrue(answer);
	}
	
	//A very simple test for the getters and setters of the class.
	@Test
	public void setGetTest() {
		boolean answer = true;
		game_service game = Game_Server.getServer(0);
		game.addRobot(0);
		Robot testRobot = new Robot(game.getRobots().get(0)); //this part is to get the correctly formated Json string, to init the robot. Then use the get set and test them.
		testRobot.setDest(1);
		testRobot.setSrc(0);
		testRobot.setSpeed(1);
		testRobot.setisEating(true);
		testRobot.setPos(new Point3D(0,0,0));
		testRobot.setValue(0);
		
		if(testRobot.getDest()!=1 || testRobot.getSrc()!=0 ||testRobot.getSpeed()!=1 || !testRobot.getPos().equalsXY(new Point3D(0, 0, 0)) || !testRobot.getisEating() || testRobot.getValue()!=0) {answer=false;}
		assertTrue(answer);
		
		
	}

}
