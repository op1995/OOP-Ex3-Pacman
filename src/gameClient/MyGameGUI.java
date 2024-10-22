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

public class MyGameGUI{
	public static void main(String[] a) {
		int mode = -1;
		String modeString = JOptionPane.showInputDialog("Which mode would you like?\n 0 - Automatic\n 1 - Manual");
		if(modeString!=null) {
			try {
				mode = Integer.valueOf(modeString);
			}
			catch(Exception e) {
				//    			System.err.println("1Please try again and enter a valid value");
				JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 or 1)");
				System.exit(-1);
			}	
		}
		else {
			JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 or 1)");
			System.exit(-1);
		}

		if(mode<0 || mode>1) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 or 1)");
			System.exit(-1);
		}

		int scenario_num = -1;
		scenario_num = Integer.valueOf(JOptionPane.showInputDialog("Input a scenario Number between 0 to 23."));
		if(scenario_num<0 || scenario_num>23) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 to 23)");
			System.exit(-1);
		}

		if(mode==0) {AutomaticGameClass.runAuto(new DGraph(), scenario_num);		}
		else {runManual(new DGraph(), scenario_num);}
	}
	/**
	 * This method runs a user manual robot placement.
	 * @param myMovesCounter The number of times we called the game.move method.
	 * @param gameGraphString The info about Nodes and Edges of the Graph that belongs to the game.
	 * @param gui GraphGUI object to display the game.
	 * @param gameToString Info about the game, it will include grade, scenario number, time...
	 * @param amoutOfRobotsInGame The amount of playing robots in the Game.
	 * @param lastUpdateTime The local time.
	 */
	public static void runManual(DGraph gameGraph , int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		for(String gotRobot: game.getRobots()) {
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
//			gameGraph.Fruits.clear();
			gameGraph.Fruits = new Fruit[15];
			Iterator<String> fruits_iterator = game.getFruits().iterator();
			while(fruits_iterator.hasNext()) {
				try {
					gameGraph.addFruit(new Fruit(fruits_iterator.next()));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}	 
			JOptionPane.showMessageDialog(GraphGUI.frame,"Pick the robots locations by writing the node key thet you want the robot placed on.");
			int src_node = 0;
			for(int a = 0;a<amoutOfRobotsInGame;a++) {
				try {
					src_node = Integer.valueOf(JOptionPane.showInputDialog("node key"));
					game.addRobot(src_node);
					//					System.out.println("game.getRobots() = " + game.getRobots());
					gameGraph.addRobot(new Robot(game.getRobots().get(a)));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}//end add robots at random nodes.
			for(int robot : gameGraph.Robots.keySet()) {
				gameGraph.Robots.get(robot).setisEating(false); //Initialize the game, so all the robots should be set to not eating mode
			}
		}
		catch (JSONException e) {}
		JOptionPane.showMessageDialog(GraphGUI.frame, "Click OK to start the game.");
		gui.chooseRobot = true;
		GraphComponent.ManualModeOn = true;
		game.startGame();
		long lastUpdateTime = System.currentTimeMillis();
		while(game.isRunning()) {

			if(System.currentTimeMillis() - lastUpdateTime >= 50) //if enough time has passed (50 milliseconds) 
				try {
					gameGraph.Fruits = new Fruit[15];
					Iterator<String> f_iter = game.getFruits().iterator();
					while(f_iter.hasNext()) {
						try {
							Fruit f = new Fruit(f_iter.next());
							f.setisAlive(false);
							gameGraph.addFruit(f);
						} catch (Exception e) {}
					}
					moveRobots(game, gameGraph, gui);
					gui.graphComponent.repaint();
					lastUpdateTime = System.currentTimeMillis();
				} catch (Exception e) {e.printStackTrace();}
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
	 * This method Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen the user.
	 * @param game game_service object to play the game.
	 * @param gameGraph The graph of the game according to the scenario number.
	 * @param log gam.move().
	 * @param robot_json A Json string represents the Robot.
	 */
	private static void moveRobots(game_service game, DGraph gameGraph, GraphGUI gui) {
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
						dest = nextNode(robotId,gameGraph);
						if(dest==-1) {continue;} // if we got back -1 here, it means the user hasn't chosen a fruit. Nothing to do.
						game.chooseNextEdge(robotId, dest);
						gameGraph.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gameGraph.Robots.get(robotId).setSrc(dest);
						JSONObject GameInfoFromJson = new JSONObject(game.toString());
						grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
						
					}
					else {
						gameGraph.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gameGraph.Robots.get(robotId).setDest(dest);
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}

	}
	/**
	 * this method sets the path of each robot in the game according to the user's input.
	 * @param game game_service Object.
	 * @param robotId the ID of the given Robot.
	 * @param gameGraph The Graph that belong to the game scenario number.
	 * @return the destination of the robot that his ID is robotId.
	 */
	private static int nextNode(int robotId, DGraph Graph) {

		try {
			if(Graph.Robots.get(robotId).getPathToFruit().size()>=1) {
				int dest = Graph.Robots.get(robotId).getPathToFruit().get(0).getKey(); //get the next node on the list
				Graph.Robots.get(robotId).getPathToFruit().remove(0); //remove it from the list
				return dest;
			}
			else {
				return -1; //the list of the robot's path is empty, i.e. - the user hasn't chosen a next fruit. return -1 to indicate no where to go.
			}

		}
		catch(Exception e) {
			System.err.println("Error in nextNode method.");
			e.printStackTrace();
			System.err.println("Returning -1");
			return -1;
		}
	}
}