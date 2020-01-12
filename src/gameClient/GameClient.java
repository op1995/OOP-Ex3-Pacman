package gameClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import utils.Point3D;

public class GameClient {
	public static void main(String[] args) {
		user();
	}
	public static void user() {
		int idf = 1;
		int scenario_num = 4;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		for(String r: game.getRobots()) {
			System.out.println(r);
		}
		String g = game.getGraph();
		DGraph gg = new DGraph();
		gg.init(g);
		GraphGUI gui = new GraphGUI(gg);
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
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				try {
					System.out.println(f_iter.next());
					gg.addFruit(new Fruit(f_iter.next()));
				} catch (Exception e) {}
			}	
			int src_node =9;// arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				try {
					game.addRobot(src_node+a);
					System.out.println(game.getRobots());
					gg.addRobot(new Robot(game.getRobots().get(a)));
				} catch (Exception e) {}
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		game.startGame();
		while(game.isRunning()) {
			try {
				
				if(idf%2 == 0) {
					Thread.sleep(100);
					gui.graphComponent.repaint();
				}
				idf++;
				moveRobots(game, gg, gui);
				
			} catch (Exception e) {	}
			
		}
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
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
					if(dest == -1) {
						try {
							dest = nextNode(gg, src, gui);
							game.chooseNextEdge(rid, dest);
							gg.Robots.get(rid).setPos(new Point3D(ttt.getString("pos")));
							try {
								Iterator<String> f_iter = game.getFruits().iterator();
								gg.Fruits.clear();
								while(f_iter.hasNext()) {
									gg.addFruit(new Fruit(f_iter.next()));
								}
							} catch (Exception e) {System.out.println(e);}
							}
					catch (Exception e) {System.out.println(e);}
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
	private static int nextNode(graph g, int src, GraphGUI gui) {
		int ans = -1;
		if(gui.a) {
			ans = Integer.valueOf(JOptionPane.showInputDialog("Dest"));
			System.out.println(ans);
		}
		gui.a = false;
		return ans;
	}

}
