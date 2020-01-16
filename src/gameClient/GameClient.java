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
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 * @author boaz.benmoshe
 */


public class GameClient{
	static int myMovesCounter = 0;
	public static void main(String[] a) {
		test1(new DGraph(),1);
	}
	public static void test1(DGraph gameGraph , int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		for(String gotRobot: game.getRobots()) { 				//this is useless. No robots were added yet.
			System.out.println("gotRobot = " + gotRobot);
		}
		
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
			while(fruits_iterator.hasNext()) {
				try {
					gameGraph.addFruit(new Fruit(fruits_iterator.next()));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}	
			int src_node =0;// arbitrary node, you should start at one of the fruits
			for(int a = 0;a<amoutOfRobotsInGame;a++) {
				try {
					game.addRobot(src_node+a);
//					System.out.println("game.getRobots() = " + game.getRobots());
					gameGraph.addRobot(new Robot(game.getRobots().get(a)));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}//end add robots at random nodes.
			for(int robot : gameGraph.Robots.keySet()) {
				gameGraph.Robots.get(robot).setisEating(false); //initing the game, so all the robots should be set to not eating mode
			}
		}
		catch (JSONException e) {
			// TODO: handle exception
		}
		game.startGame();
		long lastUpdateTime = System.currentTimeMillis();
		while(game.isRunning()) {
			
			if(System.currentTimeMillis() - lastUpdateTime >= 50) //if enough time has passed (50 milliseconds) 
			try {
//				System.out.println("50 milliseconds have passed");
				moveRobots(game, gameGraph, gui);
				gui.graphComponent.repaint();
				lastUpdateTime = System.currentTimeMillis();
			} catch (Exception e) {}
		}
		
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
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, DGraph gg, GraphGUI gui) {
		List<String> log = game.move();
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
						dest = nextNode(game,robotId,gg);
						game.chooseNextEdge(robotId, dest);
						gg.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gg.Robots.get(robotId).setSrc(dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println("robotInfoFromJson = " + robotInfoFromJson);
						JSONObject GameInfoFromJson = new JSONObject(game.toString());
						System.out.println("Our Current grade is = " + GameInfoFromJson.getJSONObject("GameServer").getInt("grade"));
//						if(grade != GameInfoFromJson.getJSONObject("GameServer").getInt("grade")) {
							grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
							gg.Fruits.clear();
							System.out.println("game.getFruits().toString() = "+game.getFruits().toString());
							Iterator<String> f_iter = game.getFruits().iterator();
							while(f_iter.hasNext()) {
								try {
									Fruit f = new Fruit(f_iter.next());
									gg.addFruit(f);
								} catch (Exception e) {}
							}
//						}		
					}
					else {
						gg.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gg.Robots.get(robotId).setDest(dest);;
					}
//					System.out.println("log.grade = " + game.toString());
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param robotId
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
	
//	public static void update(game_service game, DGraph gameGraph) throws JSONException{
//		String gameToString = game.toString();
//		JSONObject line;
//		line = new JSONObject(gameToString);
//		JSONObject GameServerJson = line.getJSONObject("GameServer");
//		int amoutOfRobotsInGame = GameServerJson.getInt("robots"); //ttt.getInt("robots");
//		System.out.println("gameToString = " + gameToString);
//		System.out.println("gameGraphString = ");
//		// the list of fruits should be considered in your solution
////		gameGraph.Fruits.clear(); //not needed, the Fruits of the graph is empty
//		Iterator<String> fruits_iterator = game.getFruits().iterator();
//		while(fruits_iterator.hasNext()) {
//			try {
//				gameGraph.addFruit(new Fruit(fruits_iterator.next()));
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}	
//		int src_node =0;// arbitrary node, you should start at one of the fruits
//		for(int a = 0;a<amoutOfRobotsInGame;a++) {
//			try {
//				game.addRobot(src_node+a);
//				System.out.println("game.getRobots() = " + game.getRobots());
//				gameGraph.addRobot(new Robot(game.getRobots().get(a)));
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}//end add robots at random nodes.
//		for(int robot : gameGraph.Robots.keySet()) {
//			gameGraph.Robots.get(robot).setisEating(false);
//		}
//	}
//	List<String> log = game.move();
//	if(log!=null) {
//		long t = game.timeToEnd();
//		for(int i=0;i<log.size();i++) {
//			String robot_json = log.get(i);
//			try {
//				JSONObject line = new JSONObject(robot_json);
//				JSONObject ttt = line.getJSONObject("Robot");
//				int rid = ttt.getInt("id");
//				int src = ttt.getInt("src");
//				int dest = ttt.getInt("dest");
//				ArrayList<node_data> PathToFruit = nextNode(game, rid, gg);
//				System.out.println("path"+PathToFruit.toString());
//				Iterator<node_data> path = PathToFruit.iterator();
//				if(PathToFruit.size()>=2) {
//					if(dest==-1) {
//						while(path.hasNext()){	
////							if(dest==-1) {	
//								try {
//									dest = path.next().getKey();
//									System.out.println("dest = " + dest);
//									game.chooseNextEdge(rid, dest);
//									gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
//									System.out.println("line 134 = " + log.get(i));
//									gg.Robots.get(rid).setSrc(dest);
//									gg.Robots.get(rid).setDest(-1);
//									gg.Fruits.clear();
//									Iterator<String> f_iter = game.getFruits().iterator();
//									while(f_iter.hasNext()) {
//										try {
//											gg.addFruit(new Fruit(f_iter.next()));
//										} catch (Exception e) {}
//									}
//								}catch (Exception e) {}
////							}
////							else {
////								gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
////							}
////							dest=gg.Robots.get(rid).getDest();
//						}
//					}
//					else {
//						gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
//						dest=gg.Robots.get(rid).getDest();
//					}
//				}
//			} 
//			catch (JSONException e) {e.printStackTrace();
//			}
//		}
//	}
}
