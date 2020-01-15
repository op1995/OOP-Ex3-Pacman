package gameClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
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
	public static void main(String[] a) {
		test1(new DGraph(),10);
	}
	public static void test1(DGraph gg , int scenario_num) {
		int i=0;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		for(String r: game.getRobots()) {
			System.out.println(r);
		}
		String g = game.getGraph();
		gg.init(g);
		GraphGUI  gui = new GraphGUI(gg);
		gui.execute();
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots"); //ttt.getInt("robots");
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			gg.Fruits.clear();
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				try {
					gg.addFruit(new Fruit(f_iter.next()));
				} catch (Exception e) {}
			}	
			int src_node =0;// arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				try {
					game.addRobot(src_node+a);
					System.out.println(game.getRobots());
					gg.addRobot(new Robot(game.getRobots().get(a)));
				} catch (Exception e) {}
			}
			for(int robot : gg.Robots.keySet()) {
				gg.Robots.get(robot).setisEating(false);
			}
		}
		catch (JSONException e) {}
		game.startGame();
		while(game.isRunning()) {
			try {
				if(i%2==0){
					Thread.sleep(49);
					moveRobots(game, gg, gui);
					gui.graphComponent.repaint();

				}
				i++;
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		String results = game.toString();
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
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					if(dest==-1) {	
						dest = nextNode(game,rid,gg);
						game.chooseNextEdge(rid, dest);
						gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
						gg.Robots.get(rid).setSrc(dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
						gg.Fruits.clear();
						Iterator<String> f_iter = game.getFruits().iterator();
						while(f_iter.hasNext()) {
							try {
								Fruit f = new Fruit(f_iter.next());
								f.setisAlive(false);
								gg.addFruit(f);
							} catch (Exception e) {}
						}	
					}
					else {
						gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
		
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(game_service game, int src, DGraph gg) {
		int ans = -1;
		ArrayList<node_data> Path = new ArrayList<node_data>();
		try {
			System.out.println(gg.Robots.get(src).getSrc());
			gg.Robots.get(src).setisEating(false);
			Robot_Algo RobotAlgo = new Robot_Algo(gg);
			Fruit f = RobotAlgo.getClosestFruit(gg.Robots.get(src), game, gg);
			Graph_Algo Algo =  new Graph_Algo(gg);
			Path = (ArrayList<node_data>) Algo.shortestPath(gg.Robots.get(src).getSrc(), f.getEdge().getSrc());
			Path.add(gg.getNodes().get(f.getEdge().getDest()));
			gg.Robots.get(src).setisEating(true);
			ans=Path.get(1).getKey();
			return ans;
		} catch (Exception e) {}
		Path.add(gg.getNodes().get(gg.Robots.get(src).getSrc()));
		System.out.println(Path.toString());
		return ans;
	}
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
