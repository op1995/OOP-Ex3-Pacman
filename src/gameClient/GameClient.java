package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
/*
 * TODO Junit.
 */
public class GameClient {
	GraphGUI GUI;
	private static DGraph Graph;
	private static game_service game;
	public GameClient(DGraph g) {
		Graph = g;
		GUI = new GraphGUI(Graph);
	}
	public static void main(String[] args) {
		GameClient client = new GameClient(new DGraph());
		client.user();
	}
	public void user() {
		GUI.execute();
		game = Game_Server.getServer(2);
		game.startGame();
		Graph.init(game.getGraph());
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				try {
					String next_fruit_string = f_iter.next();
					System.out.println(next_fruit_string);
					Graph.addFruit(new Fruit(next_fruit_string));
					
//					System.out.println(f_iter.next());
//					Graph.addFruit(new Fruit(f_iter.next()));

				} catch (Exception e) {}
			}	
			int src_node =0;// arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				try {
					game.addRobot(src_node+a); //sets the starting node of each robot being added
					List<String> getRobotsResult = game.getRobots();
					System.out.println(getRobotsResult);
					Graph.addRobot(new Robot(getRobotsResult.get(a)));
				} catch (Exception e) {}
			}
		} catch (Exception e) {}
		game.startGame();
		System.out.println(game.toString());
		while (game.isRunning()) {
			try {
				Thread.sleep(1000);
				moveRobots();
				GUI.graphComponent.repaint();
			} catch (Exception e) {}
		}
	}
	private void moveRobots() {
		try {
			Graph_Algo Algo = new Graph_Algo(Graph);
			ArrayList<node_data> PathToFruit = (ArrayList<node_data>) Algo.shortestPath(GUI.chosenRobot.getSrc(), GUI.chosenFruit.getEdge().getDest());
			System.out.println(GUI.chosenFruit.getEdge().getSrc());
			for(int i = 1; i<PathToFruit.size(); i++) {
				game.chooseNextEdge(GUI.chosenRobot.getID(), PathToFruit.get(i).getKey());
				Graph.Robots.get(GUI.chosenRobot.getID()).setPos(PathToFruit.get(i).getLocation());
				GUI.graphComponent.repaint();
			}
			Graph.Fruits.clear();
//			Iterator<String> f_iter = game.getFruits().iterator();
//			while(f_iter.hasNext()) {
//				Graph.addFruit(new Fruit(f_iter.next()));
//			}

		} catch (Exception e) {System.out.println();}
	}
}
