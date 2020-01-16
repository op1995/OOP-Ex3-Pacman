package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import dataStructure.Node;

/** 		// TODO complete the following comments.
 * This class represents a set of graph-theory algorithms.
 * In this class you will be able to run the following algorithms:
 * 		1. isConnected - Check if the graph is connected. 
 * 		   time complexity = O(|V|+|E|) such that V is the nodes of the graph and E is the edges.
 * 		2. ShortestPath - Returns the Shortest Path between two given nodes in the graph.
 * 
 * 		3. TSP - Given a list of node targets the method will Return a simple short path between the targets. 
 * 
 * @author Ibrahem Chahine, Ofir Peller.
 *
 */
public class Graph_Algo implements graph_algorithms, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5167313342016016336L;
	/**
	 * 
	 */
	public DGraph Graph;
	public Graph_Algo() {
		this.Graph = new DGraph();
	}
	public Graph_Algo(graph g) {
		this.Graph =  (DGraph) g;
	}
	/*
	 * Inits from graph. 
	 */
	@Override
	public void init(graph g) {
		this.Graph = (DGraph) g;
	}
	public void init(Graph_Algo g) {
		this.Graph = (DGraph) g.copy();
	}
	/** 
	 * This method Computes a deep copy of this graph.
	 * @param copy The deep copy of this graph
	 * @return Deep copy of this graph
	 */
	@Override
	public graph copy() {
		DGraph copy = new DGraph();
		for (int i : this.Graph.getNodes().keySet()) {
			double weight = this.Graph.getNodes().get(i).getWeight();
			Point3D p = new Point3D(Graph.getNodes().get(i).getLocation().x(),Graph.getNodes().get(i).getLocation().y(),Graph.getNodes().get(i).getLocation().z());
			String info = this.Graph.getNodes().get(i).getInfo();
			copy.addNode(new Node(i,weight,p,info));
		}
		for( int u : this.Graph.getNodes().keySet()) {
			for (int v : this.Graph.getEdge(u).keySet()) {
				copy.connect(u,v,this.Graph.getEdge(u).get(v).getWeight());
			}
		}
		return copy;
	}
	/*
	 * This method saves the project to file.
	 * @param file_name The given filename of the project.
	 * @param file the File.
	 * @param tempToFile A Graph_Algo object.
	 */
	@Override
	public void save(String file_name) {
		try {
			FileOutputStream file = new FileOutputStream(file_name); 
			ObjectOutputStream out;
			out = new ObjectOutputStream(file);
			DGraph graphCopy = (DGraph)this.copy();
			Graph_Algo tempToFile = new Graph_Algo();
			tempToFile.init(graphCopy);
			out.writeObject(tempToFile); 
			out.close(); 
			file.close();
			System.out.println("Object has been serialized"); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	/*
	 * This method inits the project from graph.
	 * @param file_name The filename of the project.
	 * @param file the File of the project.
	 */
	@Override
	public void init(String file_name) {
		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file); 

			init((Graph_Algo)in.readObject());
			in.close(); 
			file.close(); 

			System.out.println("Object has been deserialized"); 
		} 

		catch(IOException ex) 
		{ 
			System.out.println("IOException is caught"); 
		} 

		catch(ClassNotFoundException ex) 
		{ 
			System.out.println("ClassNotFoundException is caught"); 
		} 
	}
	/**
	 * Returns true if and only if (iff) there is a valid path from EVREY node to each
	 * other node. NOTE: assume directional graph - a valid path (a-->b) does NOT imply a valid path (b-->a).
	 * Go to the check(Graph) method to get further Explanation on how this task is done. 
	 * @return true if and only if (iff) there is a valid path from EVREY node to each other node
	 */
	@Override
	public boolean isConnected() {
		if(this.Graph.getNodes().isEmpty()) {
			return true;
		}
		return check(Graph);
	}
	/*
	 * This method returns the Weight of the shortest path from src to dest.
	 * @param tmp the shortest path from src to dest.
	 * @param lastNodeInPath the destination node.
	 * return the Weight of the shortest path from src to dest.
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		try {
			List<node_data> temp = shortestPath(src, dest);
			Node lastNodeInPath = (Node) temp.get(temp.size()-1);
			return lastNodeInPath.getWeight();
		}
		catch(Exception e) {
			System.err.println(" ERROR - shortestPathDist. stack trace = ");
			e.printStackTrace();
			System.err.println("returning infinity");
			return Double.POSITIVE_INFINITY;
		}

	}

	/*
	 * This method returns the shortestPath from src to dest, utilizing Dijkstra Algorithm.
	 * @param graphNodes HashMap of the Nodes of this graph.
	 * @param myHeap a mineHeap containing the nodes of the graph.
	 * return the list of the shortest path from src to dest.
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		//		first check if the two nodes are connected
		HashMap<Integer,Boolean> visited1 = new HashMap<Integer,Boolean>();
		for(int u : this.Graph.getNodes().keySet()) {
			visited1.put(u,false);
		}
		DFS(this.Graph, src, visited1);
		if(!(visited1.get(dest))) {
			throw new RuntimeException("There is no existing path between the source and the destination!");
		}

		Node source = (Node) this.Graph.getNode(src);
		source.setWeight(0);
		source.predecessor = null;
		HashMap<Integer, Node> graphNodes = this.Graph.getNodes();
		MyMinheap myHeap = new MyMinheap(graphNodes.size());
		for (int i : graphNodes.keySet()) {
			if(graphNodes.get(i).getKey()!=src) { graphNodes.get(i).setWeight(Double.POSITIVE_INFINITY);}
			graphNodes.get(i).visited = false;
			graphNodes.get(i).predecessor = null;
			myHeap.add(graphNodes.get(i));
		}



		while(!myHeap.isEmpty()) {
			Node currentNode = myHeap.heapExtractMin();
			int currentKey = currentNode.getKey();

			for(int i : this.Graph.getEdges().get(currentKey).keySet()) {
				Node currentNextNode = (Node) this.Graph.getNode(i);
				if(!currentNextNode.visited) {


					double currentEdgeWeight = this.Graph.getEdges().get(currentKey).get(i).getWeight();
					double optionalNewDistance = currentNode.getWeight() + currentEdgeWeight;

					if(optionalNewDistance<currentNextNode.getWeight()) {
						currentNextNode.setWeight(optionalNewDistance);
						currentNextNode.predecessor = currentNode;

						for (int j = 0; j < myHeap.size; j++) { //update the minHeap
							if(myHeap.heapNodeArray[j]==currentNextNode) {
								myHeap.minHeapify((j-1)/2); //heapify the parent node
								j=myHeap.size;
							}
						}//end updating minHeap	
					}
				}
			}//end for loop
			currentNode.visited=true;
		}
		Node currentNode2 = (Node) this.Graph.getNode(dest);
		ArrayList<node_data> Answer = new ArrayList<node_data>();
		while(currentNode2!=null) {
			Answer.add(currentNode2);
			currentNode2 = currentNode2.predecessor;
		}
		Collections.reverse(Answer);


		return Answer;
	}
	
	/*
	 * A class used as a container for both the shortestPath list and it's weight.
	 * @param distance weight of the path.
	 * @param path the list of the shortestPath between the src and dest.
	 */
	
	
	class shortestPathNode{//used in TSP only. container for both shortestPath list and it's distance.
		public double distance;
		public List<node_data> path;
		
		public shortestPathNode(List<node_data> newPath) {
			newPath.remove(0);
			this.path = newPath;
			this.distance = ((Node) newPath.get(newPath.size()-1)).getWeight();
			System.out.println(this.distance);
		}
		
		public shortestPathNode(node_data nodeToAdd) {
			//constructor for path from node to itself. Empty list and distance=0.
			this.path = new ArrayList<node_data>();
			this.distance = 0;
			
		}
	}
	
	
	/*
	 * This method returns a relatively short path going through all the nodes specified in the provided list.
	 * @param targets the list of targets we must go through
	 * @param Distances a double HashMap, utilizing a special container, to contain the shortestPaths of each of the targets to each other, and the path's weight
	 * return a simple path going over all nodes in the list
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) {
		
		//for each node in the list, calculate the shortestPath from it to the other targets. Send that to the constructor of the special nodes (shortestPathNode)
		//and set it at the relevant place in the double HashMap.
		//after finding the best node to start from (it will have the shortest overall weight for the path), for each of the nodes in that route,
		//add the path found earlier by shortestPath to the final answer.
		//(they already don't include the node you start the path from, so it's safe to add each of them freely. no node will be added twice. The first node of the path is added manually first)
		
		if(!this.isConnected()) {//as Instructed by Boaz - if the Graph isn't connected, return null.
			System.err.println("Graph isn't connected. Returning NULL for TSP");
			return null;
		}
		
		for (int i = 0; i < targets.size(); i++) {
			try {
				this.Graph.getNode(targets.get(i));
			}
			catch(Exception e) {
				throw new RuntimeException("The targets contain a key that isn't within this graph!");
			}
		}
		
		double minimum = Double.POSITIVE_INFINITY;
		ArrayList<Integer> targetsOrderList = new ArrayList<Integer>();
		HashMap<Integer, HashMap<Integer,shortestPathNode>> Distances = new HashMap<Integer, HashMap<Integer,shortestPathNode>>();
		for (int i : targets) {
			Distances.put(i, new HashMap<Integer,shortestPathNode>()); //create a new inner HashMap for each of the targets
			for (int j : targets) {
				
				if(i==j) {Distances.get(i).put(j, new shortestPathNode(this.Graph.getNode(j)));}
				else{Distances.get(i).put(j, new shortestPathNode(shortestPath(i,j)));}
			}
		}//end calculate all ShortestPath of all targets to each other
		
		int targets_size = targets.size();
		for (int i = 0; i < targets_size; i++) {//who is the first node in the path
			double currentPathDistance = 0;
			int currentPrev = targets.get(i);
			ArrayList<Integer> CURRENT_targets = new ArrayList<Integer>();
			ArrayList<Integer> CURRENT_Path = new ArrayList<Integer>();
			CURRENT_Path.add(currentPrev); //add the beginning node to the current path list
			for (int j = 0; j < targets_size; j++) { //add all the targets that aren't the 1st node to the current targets list
				if(i!=j) {CURRENT_targets.add(targets.get(j));}				
			}
			
			while(CURRENT_targets.size()!=0) {//now choose the current minimum shortestPath from the current node to any of those left, until non are left
				double currentMinimum = Double.POSITIVE_INFINITY;
				int nextNodeKey = -1;
				int nextNodeIndex = -1;
				for (int j = 0; j < CURRENT_targets.size(); j++) {
					double currentCandidate = Distances.get(currentPrev).get(CURRENT_targets.get(j)).distance;
					if (currentCandidate<=currentMinimum) {
						currentMinimum = currentCandidate;
						nextNodeKey = CURRENT_targets.get(j);
						nextNodeIndex = j;
					}
				}
				if(CURRENT_targets.size() == 1) {
					currentMinimum = Distances.get(currentPrev).get(CURRENT_targets.get(0)).distance;
					nextNodeKey = CURRENT_targets.get(0);
					nextNodeIndex = 0;
				}
				CURRENT_targets.remove(nextNodeIndex); //remove the chosen next node
				currentPrev = nextNodeKey; // update who is the next node to travel FROM (the chosen next node)
				currentPathDistance = currentPathDistance + currentMinimum; //update distance until now
				CURRENT_Path.add(nextNodeKey);
			}
			
			if (currentPathDistance<minimum) { //if the current path is the shortest - update the minimum path distance and it's the list showing it's path
				minimum = currentPathDistance;
				targetsOrderList.clear();
				for (int p: CURRENT_Path) {
					targetsOrderList.add(p);
				}
			}//end if (currentPathDistance<minimum) 
		}//end who is the first node in the path
		
		List <node_data> answer = new ArrayList<node_data>();
		answer.add(this.Graph.getNode(targetsOrderList.get(0))); //add the first node in the orederList manually, as the shortestPath saved values don't include the first node in the path (to prevent adding a node twice)
		for (int i = 0; i < targetsOrderList.size()-1; i++) { //get every needed shortest path, for the found targets order list, and add each to the final answer. 
			
			int currentKey = targetsOrderList.get(i);
			int nextKey = targetsOrderList.get(i+1);
			
			List <node_data> currentPath = Distances.get(currentKey).get(nextKey).path;
			answer.addAll(currentPath);
		}
		
		System.out.println(minimum);
		System.out.println(answer.toString());
		return answer;
		
	}//end TSP
	
	
	public static void DFS(DGraph g, int v, HashMap<Integer,Boolean> visited) {
		if(visited.containsKey(v)) {
			visited.replace(v, true);
		}
		else {
			visited.put(v,true);
		}
		ArrayList<Node> Neighbors = g.getNodes().get(v).getNeighbors();
		for(Node u2 : Neighbors) {
			if(!visited.get(u2.getKey())) {
				DFS(g,u2.getKey(),visited);
			}
		}
	}
	/*
	 * This method checks if a given graph is connected or not.
	 * Explanation :
			 * The idea is, if every node can be reached from a vertex v, and every node can reach v, 
			 * then the graph is strongly connected.
			 * then, we check if all vertices are reachable from v. 
			 * then, we check if all vertices can reach v (In reversed graph, if all vertices are reachable from v, 
			 * then all vertices can reach v in original graph).
	 * @param graph The given graph.
	 * @param visited A Hashmap such that the keys belong to nodes in the graph and the values are boolean values stating if 
	 * 		  the node that belongs to the key are been visited.
	 * @param NodeList A list of nodes in the graph
	 * @return true if the graph is connected, else false.
	 */
	public boolean check(DGraph graph)
	{
		// stores vertex is visited or not
		HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
		for (int i : graph.getNodes().keySet()) {
			visited.put(i, false);
		}// choose random starting point
		Set<Integer> NodeList = graph.getNodes().keySet();
		Object[] arr = NodeList.toArray();
		int v = (int) arr[0];
		// run a DFS starting at v
		DFS(graph, v, visited);
		// If DFS traversal doesn’t visit all vertices,
		// then graph is not strongly connected
		for (int b: visited.keySet())
			if (!visited.get(b))
				return false;
		for (int i : graph.getNodes().keySet()) {
			visited.put(i, false);
		}
		DGraph gr = (DGraph) copy();
		for(int u : this.Graph.Edges.keySet()) {
			for(int k : this.Graph.Edges.get(u).keySet()) {
				if(!(this.Graph.Edges.containsKey(k) && this.Graph.Edges.get(k).containsKey(u))) {
					gr.connect(k,u,0);
					gr.removeEdge(u,k);
				}
			}
		}
		// Again run a DFS starting at v
		DFS(gr, v, visited);
		// If DFS traversal doesn’t visit all vertices,
		// then graph is not strongly connected
		for (int b: visited.keySet())
			if (!visited.get(b))
				return false;
		// if graph "passes" both DFSs, it is strongly connected
		return true;
	}
}