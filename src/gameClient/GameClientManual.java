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
import Server.robot;
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
public class GameClientManual{
	public static void main(String[] a) {
		test1(new DGraph(),23);
	}
	public static void test1(DGraph gameGraph , int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		for(String gotRobot: game.getRobots()) {
			System.out.println(gotRobot);
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
			while(fruits_iterator.hasNext()) {
				try {
					gameGraph.addFruit(new Fruit(fruits_iterator.next()));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}	
			int src_node =10;// arbitrary node, you should start at one of the fruits
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
		gui.chooseRobot = true;
		while(game.isRunning()) {
			
			if(System.currentTimeMillis() - lastUpdateTime >= 50) //if enough time has passed (50 milliseconds) 
			try {
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
			finalGrade = "Game Over. Score - " + finalGrade;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(GraphGUI.frame, finalGrade);
		System.out.println("Game Over: "+results);
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
						dest = nextNode(robotId,gg);
						game.chooseNextEdge(robotId, dest);
						gg.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gg.Robots.get(robotId).setSrc(dest);
						System.out.println("Turn to node: "+dest+" Robot id :"+robotId+"  time to end:"+(t/1000));
						System.out.println(robotInfoFromJson);
						JSONObject GameInfoFromJson = new JSONObject(game.toString());
						System.out.println("Our Current grade is = " + GameInfoFromJson.getJSONObject("GameServer").getInt("grade"));
//						if(grade != GameInfoFromJson.getJSONObject("GameServer").getInt("grade")) {
							grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
							gg.Fruits.clear();
							System.out.println("fruits :"+game.getFruits().toString());
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
	private static int nextNode(int robotId, DGraph Graph) {
		if(!Graph.Robots.get(robotId).getPathToFruit().isEmpty()) {
			int dest = Graph.Robots.get(robotId).getPathToFruit().get(1).getKey();
			Graph.Robots.get(robotId).getPathToFruit().remove(1);
			return dest;
		}
		return Graph.Robots.get(robotId).getSrc();
	}
}