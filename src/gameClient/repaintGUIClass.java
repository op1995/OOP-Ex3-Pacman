package gameClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Edge;
import utils.Point3D;


/**
 * 
 * @author Ibrahim & Ofir
 * This class's goal is to repaint and refresh the graph's fruits list, in a separate thread.
 */

public class repaintGUIClass implements Runnable{

	game_service _game;
	DGraph _gameGraph;
	GraphGUI _gui;
	
	public repaintGUIClass(game_service game, DGraph gameGraph, GraphGUI gui) {
		_game = game;
		_gameGraph = gameGraph;
		_gui = gui;
	}



	public void run() {
		List<String> log = _game.getRobots();
		while(_game.isRunning()) {
			
			try {
				Fruit[] UpdatedFruits = new Fruit[15];
				
				Iterator<String> _f_iter = _game.getFruits().iterator(); //get the fruits from the server
				while(_f_iter.hasNext()) {
					try {
						Fruit f = new Fruit(_f_iter.next());
						boolean foundPlace = false;
						for (int i = 0; i < UpdatedFruits.length && !foundPlace; i++) { //find a place for each in the list
							if(UpdatedFruits[i]==null) {
								UpdatedFruits[i]=f;
								foundPlace = true;
							}
						}
					}
					catch (Exception e) {
						System.err.println("Error in refreshong the fruits");
						e.printStackTrace();
					}
				}

				//first, check for every fruit in my OWN list, if they exist in the freshly fetched fruits list from the server.
				//if so, leave them alone.
				//otherwise, delete them. They were already eaten.
				
				
				for (int i = 0; i < _gameGraph.Fruits.length; i++) {
					if(_gameGraph.Fruits[i]==null) {continue;}
					Fruit currentFruitFromGraph = _gameGraph.Fruits[i];
					boolean removed = false;
					for (int j = 0; j < UpdatedFruits.length && !removed; j++) {
						if(UpdatedFruits[j]==null) {continue;}
						Fruit f = UpdatedFruits[j];
						if(f.toString().equals(currentFruitFromGraph.toString())) {
							UpdatedFruits[j] = null;
							removed = true;
						}
					}
					if(!removed) {
						_gameGraph.Fruits[i]=null; //fruit is not in the updated list. it was eaten. delete it.
					}
				}
				
				//now add the remaining fruits in the updated list
				Robot_Algo ra = new Robot_Algo(_gameGraph);
				for (int j = 0; j < UpdatedFruits.length; j++) {
					if(UpdatedFruits[j]==null) {continue;}
					Fruit f = UpdatedFruits[j];
					boolean added = false;
					for (int i = 0; i < _gameGraph.Fruits.length && !added; i++) {
						if(_gameGraph.Fruits[i]==null) {
							f.setEdge(ra.findEdge(f));
							_gameGraph.Fruits[i]=f;
							added = true;
						}
					}
				}//done
			}
			catch (Exception e) {
				System.out.println("Error in fruitsRefresher thread");
				e.printStackTrace();
			}
			
			
			//this part refreshes the robots on the graph gui
			log = _game.getRobots();
			try {
				for(String r: log) {
					JSONObject line = new JSONObject(r).getJSONObject("Robot");
					int robotId = line.getInt("id");
					String pos = line.getString("pos");
					_gameGraph.Robots.get(robotId).setPos(new Point3D(pos));
				}
			}

			catch(Exception e) {
				System.err.println("Error in repaintGUIClass");
				e.printStackTrace();

			}

			
			_gui.graphComponent.repaint();
			
		}

	}
}
