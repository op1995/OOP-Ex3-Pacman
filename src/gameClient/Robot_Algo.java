package gameClient;

import dataStructure.DGraph;
import dataStructure.Edge;
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
	 */
	public Edge findEdge(Fruit f){
		for(int u : this.Graph.Edges.keySet()) {
			for(int v : this.Graph.Edges.get(u).keySet()) {
				if(isOnEdge(f.getPos(), this.Graph.Edges.get(u).get(v), f.getType(), Graph)) {
					return this.Graph.Edges.get(u).get(v);
				}
			}
		}
		return null;
	}
}