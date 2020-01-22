package gameClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;

public class AutomaticGameClass{

	static int amoutOfRobotsInGame = 0;
	static boolean oldCode = false;
	static int _scenario_num = -1;

	/**
	 * This class is for the automatic robot playing. 
	 */
	static int myMovesCounter = 0;
	private static long now;
	public static void main(String[] a) {
		int scenario_num = -1;
		scenario_num = Integer.valueOf(JOptionPane.showInputDialog("Input a scenario Number between 0 to 23."));
		if(scenario_num<0 || scenario_num>23) {
			JOptionPane.showMessageDialog(null, "Invalid input. Please run the program again, with a valid input (0 to 23)");
			System.exit(-1);
		}
		runAuto(new DGraph(),scenario_num);
	}
	/**
	 * This method runs the automatic robots with an algorithms such that : the robots will eat the maximum number of fruits.
	 * @param myMovesCounter The number of times we called the game.move method.
	 * @param gameGraphString The info about Nodes and Edges of the Graph that belongs to the game.
	 * @param gui GraphGUI object to display the game.
	 * @param gameToString Info about the game, it will include grade, scenario number, time...
	 * @param amoutOfRobotsInGame The amount of playing robots in the Game.
	 * @param kml A KML_Logger object to save the game to kml.
	 * @param kmlThread A Thread to start the kml with the game.
	 * @param lastUpdateTime The local time.
	 */
	public static void runAuto(DGraph gameGraph , int scenario_num) {
		_scenario_num = scenario_num;
		boolean KML_flag = false;
		myMovesCounter = 0;
		int id = 315554022;
		Game_Server.login(id);
		int got_scenario_num = scenario_num;
		game_service game = Game_Server.getServer(got_scenario_num); // you have [0,23] games
		System.out.println("right after creating game = " + game.toString());
		String gameGraphString = game.getGraph();
		gameGraph.init(gameGraphString);
		GraphGUI  gui = new GraphGUI(gameGraph);
		gui.graphComponent.setGameLevel(scenario_num);
		gui.execute();
		String gameToString = game.toString();
		JSONObject line;
		try {
			int kml_flag = Integer.valueOf(JOptionPane.showInputDialog("Type 0 if you want to get a KML file Or "
					+"1 if you dont want a KML file."));
			if(kml_flag == 0) {
				KML_flag = true;
			}

		} catch (Exception e) {}
		try {
			line = new JSONObject(gameToString);
			JSONObject GameServerJson = line.getJSONObject("GameServer");
			amoutOfRobotsInGame = GameServerJson.getInt("robots"); //ttt.getInt("robots");
			System.out.println("gameToString = " + gameToString);
			System.out.println("gameGraphString = " + gameGraphString);
			// the list of fruits should be considered in your solution.
			//			gameGraph.Fruits.clear();
			gameGraph.Fruits = new Fruit[15];
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
		catch (JSONException e) {}
		int refresh_rate = 105;

		if(scenario_num==5) {refresh_rate=125;}
		else if(scenario_num==23) {refresh_rate=60;} //different games need different moves count.
		else if(scenario_num==20) {refresh_rate=104;}
		//		gui.scenario_num = scenario_num;
		game.startGame();
		KML_Logger kml = new KML_Logger(gameGraph, game,scenario_num);
		if(KML_flag) {
			Thread kmlThread = new Thread(kml);
			kmlThread.start();
		}

		//		if(amoutOfRobotsInGame==1 || scenario_num==20) {oldCode=true;}
		if(amoutOfRobotsInGame==1) {oldCode=true;}
		if(oldCode) {System.out.println("oldcode");}


		if(!oldCode) {
			repaintGUIClass _repaintGUIClass = new repaintGUIClass(game,gameGraph,gui);
			Thread repaintThread = new Thread(_repaintGUIClass);
			repaintThread.start();
		}
		try {
			//long lastUpdateTime = System.currentTimeMillis();
			while(game.isRunning()) {
				//				if(System.currentTimeMillis() - lastUpdateTime >= refresh_rate) { //if enough time has passed (50 milliseconds) 
				try {
					if(oldCode) {
						gameGraph.Fruits = new Fruit[15];
						//						gameGraph.Fruits.clear();
						Iterator<String> f_iter = game.getFruits().iterator();
						while(f_iter.hasNext()) {
							try {
								Fruit f = new Fruit(f_iter.next());
								gameGraph.addFruit(f);
							} catch (Exception e) {}
						}
					}
					moveRobots(game, gameGraph, gui);
					if(oldCode) { gui.graphComponent.repaint();}
					long timePassed = System.currentTimeMillis()-now;
					Thread.sleep(refresh_rate-timePassed);
					//lastUpdateTime = System.currentTimeMillis();
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		String results = game.toString();
		String finalGrade = "Game Over";
		try {
			JSONObject GameInfoFromJson = new JSONObject(game.toString());
			finalGrade = String.valueOf(GameInfoFromJson.getJSONObject("GameServer").getInt("grade"));
			finalGrade = "Game Over. Score : " + finalGrade;
			gui.graphComponent.grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
			gui.graphComponent.repaint();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(KML_flag) {
			String remark = kml.KML_Info;
			game.sendKML(remark); // Should be your KML (will not work on case -1).
		}
		JOptionPane.showMessageDialog(GraphGUI.frame, finalGrade);
		System.out.println("Game Over: "+results);
		System.out.println("myMovesCounter = " + myMovesCounter);
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen by Shortest-Path algorithm.
	 * @param game game_service object to play the game.
	 * @param gameGraph The graph of the game according to the scenario number.
	 * @param log gam.move().
	 * @param robot_json A Json string represents the Robot.
	 */
	private static void moveRobots(game_service game, DGraph gameGraph, GraphGUI gui) {
		List<String> log = game.move();
		now = System.currentTimeMillis();
		myMovesCounter++;
		if(log!=null) {
			//			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject robotInfoFromJson = line.getJSONObject("Robot");
					int robotId = robotInfoFromJson.getInt("id");
					//					int src = robotInfoFromJson.getInt("src");
					int dest = robotInfoFromJson.getInt("dest");
					//					String pos = robotInfoFromJson.getString("pos");


					if(_scenario_num==20) { //this part is possibly good for all the levels! but I only tested it in 20 so far, so I am limiting it
						//check if the fruit I am after is still there

						ArrayList<node_data> robotPath = gameGraph.Robots.get(robotId).PathToFruit;
						if(robotPath.size()>1) {
							int lastDest = ((Node)robotPath.get(robotPath.size()-1)).getKey();
							boolean foundMyFruitInTheCurrentList = false;
							for (int j = 0; j < gameGraph.Fruits.length && !foundMyFruitInTheCurrentList; j++) {
								if(gameGraph.Fruits[j]==null) {continue;}
								if (lastDest==gameGraph.Fruits[j].getEdge().getDest()) {foundMyFruitInTheCurrentList = true;}							
							}
							//if you got here, and didn't find your fruit in the graph, it isn't there anymore.
							//delete your path, and get a new one.

							if(!foundMyFruitInTheCurrentList) {
								robotPath.clear();
								System.out.println("I think my fruit was already eaten! I'm droping my list!");
								gameGraph.Robots.get(robotId).addedAnotherFruitToMyPath = false; //when you empty the list, also mark this robot as ready for adding another fruit to the path that will be found for it

							}

						}


						//let's try to check for every node in my path, if there is a fruit that it's src or dest is in my path already
						// if so, add that to my path, and mark it is alive

						//&& robotPath.size()<5

						if(!gameGraph.Robots.get(robotId).addedAnotherFruitToMyPath && robotPath.size()>2 ) { //only attempt this if you have a longer path then 2

							boolean keepLooking = true;
							for (int k = 1; keepLooking && k<3 && k < robotPath.size()-1; k++) {
								//-1 so to not add the same fruit again, from 1 because 1 is original src, where he started moving from. not relevant.
								//also only check for the next 2 nodes
								int currentNodeInCurrentPath = ((Node)robotPath.get(k)).getKey();

								for (int j = 0; keepLooking && j < gameGraph.Fruits.length ; j++) {
									if(gameGraph.Fruits[j]==null) {continue;}
									if (currentNodeInCurrentPath==gameGraph.Fruits[j].getEdge().getSrc()) {//One of the nodes in my path is another fruit's src

										System.out.println("1I added a fruit that was next to my path.");
										Node addMe = gameGraph.Nodes.get(gameGraph.Fruits[j].getEdge().getDest()); //add the dest of the extra found fruit
										Node thenAddMeAgain = gameGraph.Nodes.get(currentNodeInCurrentPath); //then go back to your original path

										System.out.println("robotPath before = " + robotPath.toString());

										robotPath.add(k+1, addMe);
										robotPath.add(k+2, thenAddMeAgain);

										System.out.println("robotPath after = " + robotPath.toString());

										if(gameGraph.Fruits[j].getisAlive()) {System.out.println("Even though it was already alive.");}
										//									gameGraph.Fruits[j].setisAlive(true);
										keepLooking = false; //one fruit is enough. only add that.

										gameGraph.Robots.get(robotId).addedAnotherFruitToMyPath = true; //mark this robot as already had a fruit added to him, so as to not try this again.

									}		

									//didn't check this yet. above part (only src check) is good enough to pass level 20 that we were stuck on, so I didn't bother (it's 1:36AM, ok?!)

									//								//same code, but for dest
									//								else if(currentNodeInCurrentPath==gameGraph.Fruits[j].getEdge().getDest()) {
									//									System.out.println("2I added a fruit that was next to my path.");
									//									Node addMe = gameGraph.Nodes.get(gameGraph.Fruits[j].getEdge().getSrc()); //add the src of the extra found fruit
									//									Node thenAddMeAgain = gameGraph.Nodes.get(currentNodeInCurrentPath); //then go back to your original path
									//									robotPath.add(k+1, addMe);
									//									robotPath.add(k+2, thenAddMeAgain);
									//									if(gameGraph.Fruits[j].getisAlive()) {System.out.println("Even though it was already alive.");}
									////									gameGraph.Fruits[j].setisAlive(true);
									//									keepLooking = false; //one fruit is enough. only add that.
									//
									//								}

								}
							}
						}
					}//end if _scenario_num==20

					if(dest==-1) {
						if(!oldCode) {
							if(gameGraph.Robots.get(robotId).getPathToFruit().size()<=1) { //0 no path 1 only previous src node exists
								dest = nextNode(game,robotId,gameGraph);
								//								System.out.println("gave dest " + dest);
								gameGraph.Robots.get(robotId).addedAnotherFruitToMyPath = false; //for level 20 - when you get a new path, also mark this robot as ready for adding another fruit to the path that was found for it

							}
							else {//still has path. take the node at index 1 and remove it.
								dest = gameGraph.Robots.get(robotId).getPathToFruit().get(1).getKey();
								gameGraph.Robots.get(robotId).getPathToFruit().remove(1);

							}





						}
						else {//only 1 robot in the game. Always re-calculate closest fruit.
							dest = nextNode(game,robotId,gameGraph);
						}
						game.chooseNextEdge(robotId, dest);
						gameGraph.Robots.get(robotId).setSrc(dest);
						JSONObject GameInfoFromJson = new JSONObject(game.toString());
						gui.graphComponent.grade = GameInfoFromJson.getJSONObject("GameServer").getInt("grade");
					}
					else {
						if(oldCode) gameGraph.Robots.get(robotId).setPos(new Point3D(robotInfoFromJson.getString("pos")));
						gameGraph.Robots.get(robotId).setDest(dest);
					}






					//welp, this didn't help stage 20
					//even if dest isn't -1, look for more fruits close to you!

					//					ArrayList<node_data> robotPath = gameGraph.Robots.get(robotId).PathToFruit;
					//					Graph_Algo _Algo =  new Graph_Algo(gameGraph);
					//					ArrayList<node_data> Path = new ArrayList<node_data>();
					//					int robotLastNodeInPath = ((Node) robotPath.get(robotPath.size()-1)).getKey();
					//					boolean addedAnotherFruit = false;
					//					//for every fruit, check if is within 5 nodes of the target last node in the robot's path.
					//					//if so, and it isn't alive, get that as well.
					//					for (int j = 0; j < gameGraph.Fruits.length && addedAnotherFruit; j++) { //
					//						if(gameGraph.Fruits[j]==null) {continue;}
					//						Fruit f2 = gameGraph.Fruits[i];
					//						if(f2.getisAlive()) {continue;} //don't take that fruit if it's already alive.
					//						int f2Src = f2.getEdge().getSrc();
					//						Path = (ArrayList<node_data>) _Algo.shortestPath(robotLastNodeInPath, f2Src);
					//						Path.remove(0);
					//						Path.add(gameGraph.getNode(f2.getEdge().getDest()));
					//						
					//						System.out.println("Found another fruit, and it is this much hopes from my current fruit = " + Path.size());
					//						
					//						if(Path.size()<=5) {
					//							robotPath.addAll(Path);
					//							addedAnotherFruit = false;	
					//							System.out.println("Managed to add another fruit to the path!");
					//						}
					//
					//
					//
					//
					//					}


				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * this method sets the path of each robot in the game according to our algorithm.
	 * @param game game_service Object.
	 * @param robotId the ID of the given Robot.
	 * @param gameGraph The Graph that belong to the game scenario number.
	 * @return the destination of the robot that his ID is robotId.
	 */
	private static int nextNode(game_service game, int robotId, DGraph gameGraph) {
		int ans = gameGraph.Robots.get(robotId).getSrc();
		ArrayList<node_data> Path = new ArrayList<node_data>();
		//		gameGraph.init(game.getGraph());
		try {
			gameGraph.Robots.get(robotId).setisEating(false);
			Robot_Algo RobotAlgo = new Robot_Algo(gameGraph);
			Fruit currentClosestFruit = RobotAlgo.getClosestFruit(gameGraph.Robots.get(robotId).getID(), game, gameGraph);
			if(currentClosestFruit.getEdge() != null) {
				//				System.out.println("gameGraph.Fruits.toString() = " + gameGraph.Fruits.toString());
				Graph_Algo Algo =  new Graph_Algo(gameGraph);
				Path = (ArrayList<node_data>) Algo.shortestPath(gameGraph.Robots.get(robotId).getSrc(), currentClosestFruit.getEdge().getSrc());
				Path.add(gameGraph.getNodes().get(currentClosestFruit.getEdge().getDest()));
				gameGraph.Robots.get(robotId).setPathToFruit(Path);
				gameGraph.Robots.get(robotId).setisEating(true);
				ans = Path.get(1).getKey();
				gameGraph.Robots.get(robotId).getPathToFruit().remove(1); //remove what you gave back
				return ans;
			}
		} catch (Exception e) {}
		return ans;
	}
}
