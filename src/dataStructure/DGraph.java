package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import gameClient.Fruit;
import gameClient.Robot;
import gameClient.Robot_Algo;
import utils.Point3D;
/*
 * This class represents a Graph, A graph is a group of nodes and group of edges connecting the nodes.
 * If you want to learn more about graphs we recommend to visit https://en.wikipedia.org/wiki/Graph_(discrete_mathematics).
 */
public class DGraph implements graph, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5469746264553631873L;
	public HashMap<Integer, Node> Nodes;
	public HashMap<Integer, HashMap<Integer,Edge>> Edges;
	public HashMap<Integer, Robot> Robots;
	public HashMap<Fruit, Edge> Fruits;
	private int EdgeCount;
	private int MC;
	/*
	 * A default constructor.
	 */
	public DGraph() {
		this.Nodes = new HashMap<Integer, Node>();
		this.Edges = new HashMap<Integer, HashMap<Integer,Edge>>();
		this.Robots = new HashMap<Integer, Robot>();
		this.Fruits = new HashMap<Fruit, Edge>();
		this.EdgeCount = 0;
		this.MC = 0;
	}
	/*
	 * A constructor.
	 */
	public DGraph(HashMap<Integer, Node> nodes, HashMap<Integer, HashMap<Integer,Edge>> edges) {
		this.Nodes = nodes;
		this.Edges = edges;
		Set<Integer> NodeList = this.Nodes.keySet();
		for(int u : NodeList) {
			this.Nodes.get(u).clearNeighbors();
			Set<Integer> EdgeList = this.Edges.get(u).keySet();
			for(int v : EdgeList) {
				this.Nodes.get(v).addNeighbor(this.Nodes.get(u));
				this.EdgeCount++;
			}
		}
	}
	/*
	 * This method returns the node that belongs to the given key.
	 * Important: 
	 * 			-If the key deosn't belong to a node in the graph a RuntimeException will be thrown. 
	 * @return the node that belongs to the given key.  
	 */
	@Override
	public node_data getNode(int key) {
		if(!Nodes.containsKey(key)) {
			throw new RuntimeException("The given key doesn't belong to any Nodes in this Graph.");
		}
		return Nodes.get(key);
	}
	/*
	 * This method returns the edge that belongs to the given source and destination keys.
	 * * Important: 
	 * 			-If the source and destination keys deosn't belong to an edge in the graph a RuntimeException will be thrown. 
	 * @return the edge that belongs to the given source and destination.  
	 */
	@Override
	public edge_data getEdge(int src, int dest) {
		if(!Nodes.containsKey(src) || !Nodes.containsKey(dest)) {
			throw new RuntimeException("One or Two of the given Keys doesn't belong to any Node in this Graph");
		}
		if(!Edges.get(src).containsKey(dest)) {
			throw new RuntimeException("the given Keys doesn't belong to any Edge in this Graph.");
		}
		return Edges.get(src).get(dest);
	}
	/*
	 * This method adds a given node to the graph.
	 * Important: 
	 * 			-If the given node already exists in the graph a RuntimeException will be thrown.
	 * @param edge The map of the edges that will be added to the graph with the node.
	 */
	@Override
	public void addNode(node_data n) {
		if(this.Nodes.containsKey(n.getKey())) {
			throw new RuntimeException("the given Key already belong to a node in this Graph");
		}
		this.Nodes.put(n.getKey(), (Node) n);
		HashMap<Integer,Edge> edge = new HashMap<Integer,Edge>();
		this.Edges.put(n.getKey(), edge);
		this.MC++;
	}
	/*
	 * This method creates an edge between two nodes, a source node and a destination node, and sets the weight of the edge.
	 * 
	 * Example: If this function was called with these values connect(10,11,100) and lets assume the keys belong to a node 
	 * 			in the graph, because if not a RuntimeException will be thrown. 
	 *  		Then the Graph will have an edge between the nodes (10), (11) and weighted 100. (10)--100-->(11).
	 * Important : 
	 * 			1. if one or two of the given keys doasn't belong to a node in the graph a RuntimeException will be thrown. 
	 * 			2. if the given weight is negative a RuntimeException will be thrown. 
	 * 			3. if the edge is already in the graph a RuntimeException will be thrown. 
	 * @param src The key of the source node.
	 * @param dest The key of the destination node.
	 * @param value The edge_data that we create for the added edge between src and dest.
	 * @param EdgeCount The number of Edges in the graph.
	 * @param MC the number of changes in the graph. 
	 */
	@Override
	public void connect(int src, int dest, double w) {
		if(!Nodes.containsKey(src) || !Nodes.containsKey(dest)) {
			throw new RuntimeException("One or Two of the given Keys doesn't belong to any Node in this Graph.");
		}
		if(w<0) {
			throw new RuntimeException("The weight of an edge cannot be negative.");
		}
		if(Nodes.get(src) == Nodes.get(dest)) {
			throw new RuntimeException("The given keys belong to the same node.");
		}
		if(!Edges.containsKey(src)) {
			Edge value = new Edge(Nodes.get(src),Nodes.get(dest), w);
			this.Edges.put(src, new HashMap< Integer ,Edge>() );
			this.Edges.get(src).put(dest,value);
			this.Nodes.get(src).addNeighbor((Node) this.getNode(dest));
		}
		else if(Edges.containsKey(src)){
			if(Edges.get(src).containsKey(dest)) {
				throw new RuntimeException("This Edge already exists.");
			}
			Edge value = new Edge(Nodes.get(src),Nodes.get(dest), w);
			this.Edges.get(src).put(dest,value);
			this.Nodes.get(src).addNeighbor((Node) this.getNode(dest));
		}
		EdgeCount++;
		MC++;
	}
	/*
	 * This method returns a shallow copy of the collection of nodes in the graph.
	 * @param shallowcopy the copy.
	 * @return a shallow copy of the nodes.
	 */
	@Override
	public Collection<node_data> getV() {
		Collection<node_data> shallowcopy = new ArrayList<node_data>();
		for(int u : this.Nodes.keySet()) {
			shallowcopy.add(this.Nodes.get(u));
		}
		return shallowcopy;
	}
	/*
	 * This method returns a shallow copy of the collection of edges that belong to a node in the graph.
	 * Important:
	 * 			-If the Given key doesn't belong to any node in the graph a RuntimeException will be thrown.
	 * @param shallowcopy the copy.
	 * @return a shallow copy of the edges the belong to node_id.
	 */
	@Override
	public Collection<edge_data> getE(int node_id) {
		if(!this.Nodes.containsKey(node_id)) {
			throw new RuntimeException("The given key doesn't belong to any node in the Graph.");
		}
		Collection<edge_data> shallowCopy = new ArrayList<edge_data>();
		for(int u : this.Edges.get(node_id).keySet()) {
			shallowCopy.add(this.Edges.get(node_id).get(u));
		}
		return (Collection<edge_data>) shallowCopy;
	}
	public HashMap<Integer, Node> getNodes() {
		HashMap<Integer, Node> Copy = (HashMap<Integer, Node>) this.Nodes;
		return Copy;
	}
	public HashMap<Integer, Edge> getEdge(int node_id) {
		HashMap<Integer, Edge> Copy = (HashMap<Integer ,Edge>) this.Edges.get(node_id);
		return Copy;
	}
	/*
	 * This method removes the node that belongs to the given key from the graph.
	 * Important: 
	 * 			1. if the given key doesn't belong to a node in the graph a RuntimeException will be thrown.
	 * @param key the given key of the node that the user want to remove.
	 * @param node the removed node.
	 * @return node the removed node.
	 * 
	 */
	@Override
	public node_data removeNode(int key) { 
		if(!Nodes.containsKey(key)) {
			throw new RuntimeException("The given key doesn't belong to any Node in this Graph.");
		}
		for (int i = 0; i < Nodes.get(key).getNeighbors().size(); i++) {
			removeEdge(key, Nodes.get(key).getNeighbors().get(i).getKey());
			Nodes.get(key).getNeighbors().remove(i);
		}
		for(int u : this.Nodes.keySet()) {
			if(this.Nodes.get(u).getNeighbors().contains(Nodes.get(key))) {
				this.Nodes.get(u).getNeighbors().remove(Nodes.get(key));
				this.Edges.get(u).remove(key);
			}
		}
		node_data node = Nodes.get(key);
		Nodes.remove(key);
		MC++;
		return node;
	}
	/*
	 * This method removes an edge that belongs to the nodes of the given keys.
	 * Important: 
	 * 			-If the given keys deosn't belong to an edge in the graph a RuntimeException will be thrown.
	 * @param src the source node key.
	 * @param dest the destination node key.
	 * @param Edge the removed edge.
	 * @return Edge.
	 */
	@Override
	public edge_data removeEdge(int src, int dest) {
		if(!this.Edges.get(src).containsKey(dest)) {
			throw new RuntimeException("One or Two of the given Keys doesn't belong to any Edge in this Graph.");
		}
		edge_data Edge = this.Edges.get(src).get(dest);
		this.Edges.get(src).remove(dest);
		this.Nodes.get(src).getNeighbors().remove(this.Nodes.get(dest));
		EdgeCount--;
		MC++;
		return Edge;
	}
	/*
	 * This method returns the number of nodes in the graph.
	 * @return the number of nodes in the graph.
	 */
	@Override
	public int nodeSize() {
		return this.Nodes.size();
	}
	/*
	 * This method returns the number of edges in the graph.
	 * @return the number of edges in the graph.
	 */
	@Override
	public int edgeSize() {
		return this.EdgeCount;
	}
	/*
	 * This method returns the number of changes in the graph.
	 * @return the number of changes in the graph.
	 */
	@Override
	public int getMC() {
		return this.MC;
	}
	public HashMap<Integer, HashMap<Integer, Edge>> getEdges() {
		HashMap<Integer, HashMap<Integer, Edge>> Copy = (HashMap<Integer, HashMap<Integer, Edge>>) this.Edges;
		return Copy;
	}
	public void addRobot(Robot r) {
		this.Robots.put(r.getID(), r);
	}
	public void addFruit(Fruit f) {
		Robot_Algo R_Algo = new Robot_Algo(this);
		Edge edge = R_Algo.findEdge(f);
		f.setisAlive(false);
		if(edge == null ) {
			throw new RuntimeException("The given fruit doesn't belong to any Edge.");
		}
		f.setEdge(edge);
		this.Fruits.put(f,edge);
	}
	public void removeFruit(Fruit f) {
		if (!this.Fruits.containsKey(f)) {
			throw new RuntimeException("The given Fruit doesn't belong to the Fruits in the Game");
		}
		this.Fruits.remove(f);
	}
	public void init(String graphJson) {
		 try {
			 	Nodes.clear();
			 	Edges.clear();
			 	JSONObject g = new JSONObject(graphJson);
	            JSONArray n = g.getJSONArray("Nodes");
	            JSONArray e = g.getJSONArray("Edges");
	            int key;
	            for(int i = 0; i < n.length(); ++i) {
	            	key = n.getJSONObject(i).getInt("id");
	                String pos = n.getJSONObject(i).getString("pos");
	                Point3D position = new Point3D(pos);
	                this.addNode(new Node(key,0,position,""));
	            }
	            for(int i = 0; i < e.length(); ++i) {
	                int src = e.getJSONObject(i).getInt("src");
	                int dest = e.getJSONObject(i).getInt("dest");
	                double weight = e.getJSONObject(i).getDouble("w");
	                this.connect(src, dest, weight);
	            }
	        } catch (Exception e) {System.out.println(e);}
	}
}
