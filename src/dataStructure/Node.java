package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;

import utils.Point3D;

public class Node implements node_data, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 668268853111068270L;
	public boolean visited = false;
	public Node predecessor = null;
	private int key;
	private int tag;
	private double weight;
	private Point3D Location;
	private String Info;
	private ArrayList<Node> Neighbors = new ArrayList<Node>();
	/*
	 * constructor
	 */
	public Node(int key,double weight, Point3D location,String info) {
		this.key = key;
		this.weight = weight;
		this.Location = location;
		this.Info = info;
	}
	/*
	 * constructor
	 */
	public Node(int key, Point3D location,String info) {
		this.key = key;
		this.weight = 0;
		this.Location = location;
		this.Info = info;
	}
	/*
	 * constructor 
	 */
	public Node(int key, Point3D location) {
		this.key = key;
		this.weight = 0;
		this.Location = location;
	}
	public ArrayList<Node> getNeighbors(){
		return this.Neighbors;
	}
	public void addNeighbor(Node n) {
		this.Neighbors.add(n);
	}
	public void clearNeighbors() {
		this.Neighbors.clear();
	}
	/*
	 * Getter for the key of the node. 
	 */
	@Override
	public int getKey() {
		return this.key;
	}
	/*
	 * Getter for the Location of the node.
	 */
	@Override
	public Point3D getLocation() {
		return this.Location;
	}
	/*
	 * 	Setter for the Location of the node.
	 */
	@Override
	public void setLocation(Point3D p) {
		this.Location = p;
	}
	/*
	 * 	Getter for the weight of the node.
	 */
	@Override
	public double getWeight() {
		return this.weight;
	}
	/*
	 * 	Setter for the weight of the node.
	 */
	@Override
	public void setWeight(double w) {
		if(w<0) {
			throw new RuntimeException("the Weight cannot be negative");
		}
		this.weight = w;
	}

	@Override
	public String getInfo() {
		return this.Info;
	}

	@Override
	public void setInfo(String s) {
		this.Info = s;
	}

	@Override
	public int getTag() {
		return this.tag;
	}

	@Override
	public void setTag(int t) {
		this.tag = t;		
	}
	@Override
	public String toString() {
		return "key="+this.key;
		
	}
	
}
