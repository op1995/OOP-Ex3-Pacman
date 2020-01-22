package gameClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Edge;
import utils.Point3D;

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
		long before = System.currentTimeMillis();
		List<String> log = _game.getRobots();
		Robot_Algo _RobotAlgo = new Robot_Algo(_gameGraph);
		while(_game.isRunning()) {
			
			try {
				HashMap<Fruit, Edge> UpdatedFruitsList = new HashMap<Fruit, Edge>();
				Fruit[] UpdatedFruits = new Fruit[15];
				
				Iterator<String> _f_iter = _game.getFruits().iterator();
				while(_f_iter.hasNext()) {
					try {
						Fruit f = new Fruit(_f_iter.next());
//						f.setEdge(_RobotAlgo.findEdge(f));
						boolean foundPlace = false;
						for (int i = 0; i < UpdatedFruits.length && !foundPlace; i++) {
							if(UpdatedFruits[i]==null) {
								UpdatedFruits[i]=f;
								foundPlace = true;
							}
						}
//						UpdatedFruitsList.put(f, f.getEdge());
					}
					catch (Exception e) {
						System.err.println("Error in refreshong the fruits");
						e.printStackTrace();
					}
				}

				//first, check for every fruit in my OWN list, if they exist in the freshly fetched fruits list from the server.
				//if so, leave them alone.
				//otherwise, delete them. They were already eaten.
				
				HashMap<Fruit, Edge> alreadyExistsList = new HashMap<Fruit, Edge>();
				
				for (int i = 0; i < _gameGraph.Fruits.length; i++) {
					if(_gameGraph.Fruits[i]==null) {continue;}
					Fruit currentFruitFromGraph = _gameGraph.Fruits[i];
					boolean removed = false;
//					for(Fruit f: UpdatedFruitsList.keySet()) {
					for (int j = 0; j < UpdatedFruits.length && !removed; j++) {
						if(UpdatedFruits[j]==null) {continue;}
						Fruit f = UpdatedFruits[j];
						if(f.toString().equals(currentFruitFromGraph.toString())) {
							UpdatedFruits[j] = null;
//							UpdatedFruitsList.remove(f); // fruit already exists. Delete form updated list.
							removed = true;
						}
					}
					if(!removed) {
						_gameGraph.Fruits[i]=null; //fruit is not in the updated list. it was eaten. delete it.
					}
				}
				
				//now add the remaining fruits in the updated list
//				for(Fruit f: UpdatedFruitsList.keySet()) {
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
				
				
//				_f_iter = _game.getFruits().iterator();
//				String fString;
//				while(_f_iter.hasNext()) {
//					fString = _f_iter.next();
//				
////				for(Fruit f2: _gameGraph.Fruits.keySet()) {
//					boolean found_dontDeleteMe = false;
//
//					for(Fruit f: UpdatedFruitsList.keySet()) {
////						if(f.toString().equals(f2.toString())) {
//						if(f.toString().equals(fString)) {
//
//							
//							found_dontDeleteMe = true;
////							alreadyExistsList.put(f, f.getEdge());
//							UpdatedFruitsList.remove(f); //this fruit has already found a match in our OWN fruits list. No need to check it again.
//						}
//					}
//					if(!found_dontDeleteMe) {// if you didn't find this fruit from my OWN list, kill it. It was already eaten.
//						_gameGraph.Fruits.
//						_gameGraph.Fruits.remove(f2);
//					}
//				}
//
//				//now, every fruit still remaining in the UpdatedFruitsList is a new one,
//				//as we removed the ones that we got from the server, but were already in our OWN list.
//				//So - we should add the remaining ones on that list.
//
//				for(Fruit f: UpdatedFruitsList.keySet()) {
//					if(!alreadyExistsList.containsKey(f)) {
//						_gameGraph.addFruit(f);
//					}
//					
//				}
			}
			catch (Exception e) {
				System.out.println("Error in fruitsRefresher thread");
				e.printStackTrace();
			}
			
			
			
			log = _game.getRobots();
			try {
				for(String r: log) {
					JSONObject line = new JSONObject(r).getJSONObject("Robot");
//					System.out.println("repaintGUIClass line.toString() = " + line.toString());
					int robotId = line.getInt("id");
//					System.out.println("repaintGUIClass robotId = " + robotId);
					String pos = line.getString("pos");
//					System.out.println("repaintGUIClass pos = " + pos);
					_gameGraph.Robots.get(robotId).setPos(new Point3D(pos));
				}
//				Iterator<String> f_iter = game.getFruits().iterator();
//				while(f_iter.hasNext()) {
//
//					Fruit f = new Fruit(f_iter.next());
//					gameGraph.addFruit(f);
//
//					log = game.getFruits();
//					for(String currentFruitString: log) {
//						
//					}
//
//
//				}
			}

			catch(Exception e) {
				System.err.println("Error in repaintGUIClass");
				e.printStackTrace();

			}
			long now = System.currentTimeMillis();
			long diff = now-before;
//			System.out.println("Time passed since last repaint = " + diff);
//			System.out.println("log is = " + log.toString());
			before = now;
			
			_gui.graphComponent.repaint();
			
		}

	}
}
