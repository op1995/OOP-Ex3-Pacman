package gameClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;



public class AutomaticGameClass{
	static int myMovesCounter = 0;
	public static void main(String[] a) {
		int scenario_num = -1;
		scenario_num = Integer.valueOf(JOptionPane.showInputDialog("Input a scenario Number between 0 to 23."));
		if(scenario_num<0 || scenario_num>23) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 to 23)");
			System.exit(-1);
		}
		runAuto(new DGraph(),scenario_num);
	}
	
	public static void runAuto(DGraph gameGraph , int scenario_num) {
		myMovesCounter = 0;
		game_service game = Game_Server.getServer(scenario_num); // input will be from [0,23] games
		
		String gameGraphString = game.getGraph();
		gameGraph.init(gameGraphString);
		GraphGUI  gui = new GraphGUI(gameGraph);
		gui.execute();
		String gameToString = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(gameToString);
			JSONObject GameServerJson = line.getJSONObject("GameServer");
			int amoutOfRobotsInGame = GameServerJson.getInt("robots"); //ttt.getInt("robots");
			System.out.println("gameToString = " + gameToString);
			System.out.println("gameGraphString = " + gameGraphString);
			// the list of fruits should be considered in your solution
			gameGraph.Fruits.clear();
			Iterator<String> fruits_iterator = game.getFruits().iterator();
			List<String> gameFruitsList = game.getFruits();
			System.out.println("gameFruitsList = " + gameFruitsList.toString());
			int a = 0;
			while(fruits_iterator.hasNext()) {
				try {
					Fruit fruit = new Fruit(fruits_iterator.next());
					gameGraph.addFruit(fruit);
					int src = fruit.getEdge().getSrc();
					if(a < amoutOfRobotsInGame) {
						game.addRobot(src);
						gameGraph.addRobot(new Robot(game.getRobots().get(a)));
						a++;
					}
				} catch (Exception e) {}
			}	
			for(int robot : gameGraph.Robots.keySet()) {
				gameGraph.Robots.get(robot).setisEating(false); //initing the game, so all the robots should be set to not eating mode
			}
		}
		catch (JSONException e) {
			// TODO: handle exception
		}
		game.startGame();
		KML_Logger kml = new KML_Logger(gameGraph, game,scenario_num);
		Thread kmlThread = new Thread(kml);
		kmlThread.start();
		try {
			long lastUpdateTime = System.currentTimeMillis();
			while(game.isRunning()) {
				if(System.currentTimeMillis() - lastUpdateTime >= 50) //if enough time has passed (50 milliseconds) 
				try {
					moveRobots(game, gameGraph, gui);
					gui.graphComponent.repaint();
					lastUpdateTime = System.currentTimeMillis();
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		String results = game.toString();
		String finalGrade = "Game Over";
		try {
			JSONObject GameInfoFromJson = new JSONObject(game.toString());
			finalGrade = String.valueOf(GameInfoFromJson.getJSONObject("GameServer").getInt("grade"));
			finalGrade = "Game Over. Score : " + finalGrade;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(GraphGUI.frame, finalGrade);
		System.out.println("Game Over: "+results);
		System.out.println("myMovesCounter = " + myMovesCounter);
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gameGraph
	 * @param log
	 */
	private static void moveRobots(game_service game, DGraph gameGraph, GraphGUI gui) {
		List<String> log = game.move();
		System.out.println("log.toString() = " + log.toString());
		myMovesCounter++;
		int grade = 0;
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject robotInfoFromJson = line.getJSONObject("Robot");
					int robotId = robotInfoFromJson.getInt("id");
					int src = robotInfoFromJson.getInt("src");
					int dest = robotInfoFromJson.getInt("dest");
					if(dest==-1) {	
						dest = nextNode(game,robotId,gameGraph);
						game.chooseNextEdge(robotId, dest);
						gameGraph.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gameGraph.Robots.get(robotId).setSrc(dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println("robotInfoFromJson = " + robotInfoFromJson);
						JSONObject GameInfoFromJson = new JSONObject(game.toString());
						System.out.println("Our Current grade is = " + GameInfoFromJson.getJSONObject("GameServer").getInt("grade"));
//						if(grade != GameInfoFromJson.getJSONObject("GameServer").getInt("grade")) {
							grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
							gameGraph.Fruits.clear();
							System.out.println("game.getFruits().toString() = "+game.getFruits().toString());
							Iterator<String> f_iter = game.getFruits().iterator();
							while(f_iter.hasNext()) {
								try {
									Fruit f = new Fruit(f_iter.next());
									gameGraph.addFruit(f);
								} catch (Exception e) {}
							}
//						}		
					}
					else {
						gameGraph.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gameGraph.Robots.get(robotId).setDest(dest);;
					}
//					System.out.println("log.grade = " + game.toString());
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * this method sets the path of each robot in the game according to our algorithm.
	 * @param game
	 * @param robotId
	 * @param gameGraph
	 * @return
	 */
	private static int nextNode(game_service game, int robotId, DGraph gameGraph) {
		int ans = gameGraph.Robots.get(robotId).getSrc();
		ArrayList<node_data> Path = new ArrayList<node_data>();
		gameGraph.init(game.getGraph());
		try {
			System.out.println("gameGraph.Robots.get(robotId).getSrc() = " + gameGraph.Robots.get(robotId).getSrc());
			System.out.println("im here 1");
			gameGraph.Robots.get(robotId).setisEating(false);
			Robot_Algo RobotAlgo = new Robot_Algo(gameGraph);
			System.out.println("im here 2");
			Fruit currentClosestFruit = RobotAlgo.getClosestFruit(gameGraph.Robots.get(robotId).getID(), game, gameGraph);
			System.out.println("currentClosestFruit.toString() = " + currentClosestFruit.toString());
			Edge edge = RobotAlgo.findEdge(currentClosestFruit); //we should change this to get fruit's edge. This was tried but didn't always work. check in to this.
			currentClosestFruit.setEdge(edge);
			if(currentClosestFruit.getEdge() != null) {
				System.out.println("gameGraph.Fruits.toString() = " + gameGraph.Fruits.toString());
				Graph_Algo Algo =  new Graph_Algo(gameGraph);
				System.out.println("im here 3");
				System.out.println("robot :"+gameGraph.Robots.get(robotId).getSrc());
				System.out.println("fruit type is :"+currentClosestFruit.getType());
				System.out.println("fruit edge :"+currentClosestFruit.getEdge().getSrc());
				Path = (ArrayList<node_data>) Algo.shortestPath(gameGraph.Robots.get(robotId).getSrc(), currentClosestFruit.getEdge().getSrc());
				System.out.println("im here 4");
				Path.add(gameGraph.getNodes().get(currentClosestFruit.getEdge().getDest()));
				gameGraph.Robots.get(robotId).setisEating(true);
				System.out.println("im here 5");
				ans = Path.get(1).getKey();
				System.out.println("im here 6");
				Path.add(gameGraph.getNodes().get(gameGraph.Robots.get(robotId).getSrc()));
				System.out.println("im here 7");
				return ans;
			}
		} catch (Exception e) {}
		System.out.println(Path.toString());
		return ans;
	}
}
