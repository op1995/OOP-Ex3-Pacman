//package gameClient;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
//import Server.game_service;
//import dataStructure.DGraph;
//import dataStructure.Edge;
//
//public class fruitsRefresher implements Runnable{
//
//	game_service _game;
//	DGraph _gameGraph;
//	GraphGUI _gui;
//	
//	public fruitsRefresher(game_service game, DGraph gameGraph, GraphGUI gui) {
//		_game = game;
//		_gameGraph = gameGraph;
//		_gui = gui;
//	}
//
//
//	public void run() {
//		Robot_Algo _RobotAlgo = new Robot_Algo(_gameGraph);
////		while(_game.isRunning()) {
//			try {
//				HashMap<Fruit, Edge> UpdatedFruitsList = new HashMap<Fruit, Edge>();
//				Iterator<String> _f_iter = _game.getFruits().iterator();
//				while(_f_iter.hasNext()) {
//					try {
//						Fruit f = new Fruit(_f_iter.next());
//						f.setEdge(_RobotAlgo.findEdge(f));
//						UpdatedFruitsList.put(f, f.getEdge());
//					}
//					catch (Exception e) {
//						System.err.println("Error in refreshong the fruits");
//						e.printStackTrace();
//					}
//				}
//
//				//first, check for every fruit in my OWN list, if they exist in the freshly fetched fruits list from the server.
//				//if so, leave them alone.
//				//otherwise, delete them. They were already eaten.
//
//				for(Fruit f2: _gameGraph.Fruits.keySet()) {
//					boolean found_dontDeleteMe = false;
//
//					for(Fruit f: UpdatedFruitsList.keySet()) {
//						if(f.toString().equals(f2.toString())) {
//							found_dontDeleteMe = true;
//							UpdatedFruitsList.remove(f); //this fruit has already found a match in our OWN fruits list. No need to check it again.
//						}
//					}
//					if(!found_dontDeleteMe) {// if you didn't find this fruit from my OWN list, kill it. It was already eaten.
//						_gameGraph.Fruits.remove(f2);
//					}
//				}
//
//				//now, every fruit still remaining in the UpdatedFruitsList is a new one,
//				//as we removed the ones that we got from the server, but were already in our OWN list.
//				//So - we should add the remaining ones on that list.
//
//				for(Fruit f: UpdatedFruitsList.keySet()) {
//					_gameGraph.addFruit(f);
//				}
//			}
//			catch (Exception e) {
//				System.out.println("Error in fruitsRefresher thread");
//				e.printStackTrace();
//			}
////		}
//
//	}
//
//}
