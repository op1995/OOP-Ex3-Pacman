package gameClient;

import java.util.ArrayList;
import java.util.Iterator;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.node_data;
import utils.Point3D;

public class Robot_Algo {
	public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS = EPS2;
	private DGraph Graph;
	/*
	 * A constructor.
	 */
	public Robot_Algo(DGraph g) {
		this.Graph = g;
	}
	/*
	 * This method checks if a given point is on a line.
	 * @param p The given point 
	 * @param src The Source of the line.
	 * @param dest The Destination of the line.
	 * @param dist The distance between src and dest.
	 * @param d1 The distance between src and the point of the fruit + The distance between the point of the fruit and the dest.
	 */
	public static boolean isOnEdge(Point3D p, Point3D src, Point3D dest) {
		boolean ans = false;
		double dist = src.distance2D(dest);
		double d1 = src.distance2D(p) + p.distance2D(dest);
		if(dist>d1-EPS2) {ans=true;}
		return ans;
	}
	/*
	 * This method checks if a given point is on a Edge in a Graph.
	 * @param p The given point.
	 * @param s The source of the Edge.
	 * @param d The Destination of the Edge.
	 * @param g The Graph.
	 * @param src The location of the Source.
	 * @param dest The location of the destination.
	 */
	public static boolean isOnEdge(Point3D p, int s, int d, DGraph g) {
		Point3D src = g.getNode(s).getLocation();
		Point3D dest = g.getNode(d).getLocation();
		return isOnEdge(p, src, dest);
	}
	/*
	 * This method checks if a given fruit in on a given edge.
	 * @param p The point of the fruit.
	 * @param e The given Edge.
	 * @param type The type of the fruit.
	 * @param The Graph.
	 * @param src The key of the source of the Edge.
	 * @param dest The key of the destination of the Edge.
	 */
	public static boolean isOnEdge(Point3D p, Edge e, int type, DGraph g) {
		int src = g.getNode(e.getSrc()).getKey();
		int dest = g.getNode(e.getDest()).getKey();
		if(type<0 && dest>src) {return false;}
		if(type>0 && dest<src) {return false;}
		return isOnEdge(p, src, dest, g);
	}
	/*
	 * This method find the Edge of a given fruit.
	 * @param f The Given Fruit.
	 * @param u A source of an edge in this graph.
	 * @param v A destination of an edge in this graph.
	 */
	public Edge findEdge(Fruit f){
		for(int u : this.Graph.Edges.keySet()) {
			for(int v : this.Graph.Edges.get(u).keySet()) {
				boolean is = isOnEdge(f.getPos(), this.Graph.Edges.get(u).get(v), f.getType(), Graph);
				if(is) {
					Edge edge = this.Graph.Edges.get(u).get(v);
					return edge;
				}
			}
		}
		return null;
	}
	/*
	 * 
	 */
	public Fruit getClosestFruit(int robot, game_service game, DGraph gameGraph){
		if(!this.Graph.Robots.get(robot).getisEating()) {
			
//			System.out.println();
			Graph_Algo Algo = new Graph_Algo(this.Graph);
			double minDest = Double.MAX_VALUE;
			double dist = 0;
			try {
				Fruit minDestFruit = new Fruit();
				for(Fruit f1 : gameGraph.Fruits.keySet()) {
					Fruit f = f1;
					boolean fbool = !f.getisAlive();
					if(fbool) {
						double weight = f.getEdge().getWeight();
						double pathdist = Algo.shortestPathDist(gameGraph.Robots.get(robot).getSrc(), f.getEdge().getSrc());
						dist = (pathdist+weight);
						if(dist < minDest) {
							minDestFruit = f;
							minDest = dist;
						}
					}
				}
				
				minDestFruit.setEdge(findEdge(minDestFruit));
				minDestFruit.setisAlive(true);
				return minDestFruit;
			} catch (Exception e) {
				//System.out.println(e);
			}
			this.Graph.Robots.get(robot).setisEating(true);
		}
		return null;
	}
}

