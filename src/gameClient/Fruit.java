package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.DGraph;
import dataStructure.Edge;
import utils.Point3D;

public class Fruit {
	private double value;
	private int type;
	private Point3D pos;
	private Edge edge;
	private boolean isAlive;
	private Robot_Algo algo;
	/**
	 * A default constructor.
	 */
	public Fruit() {
		this.value = 0;
        this.pos = new Point3D(0,0,0);
        this.type = 0;
        this.isAlive = false;
	}
	/**
	 * A constructor.
	 * @param Json A fruit represented in a Json String.
	 */
	public Fruit(String Json) {
    	try {
    		JSONObject g = new JSONObject(Json);
			this.value = g.getJSONObject("Fruit").getDouble("value");
			String pos = g.getJSONObject("Fruit").getString("pos");
	        this.pos = new Point3D(pos);
	        this.type = g.getJSONObject("Fruit").getInt("type");
	        this.isAlive = false;
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	}
	/**
	 * A getter for the edge that the fruit is placed on.
	 * @return the edge that the fruit is placed on.
	 */
	public Edge getEdge() {
		return edge;
	}
	/**
	 * A setter for the edge that the fruit is placed on.
	 * @param edge The Edge that the fruit is placed on.
	 */
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	/**
	 * A getter for the value of this fruit. 
	 * @return value of this fruit.
	 */
	public double getValue() {
		return this.value;
	}
	/**
	 * A getter for the position of this fruit.  
	 * @return the position of this fruit. 
	 */
	public Point3D getPos() {
		return this.pos;
	}
	/** 
	 * A setter for the position of this fruit.
	 * @param p A Point3D object that represents the position of this fruit.
	 */
	public void setPos(Point3D p) {
		this.pos = p;
	}
	/**
	 * A getter for the type of this fruit.
	 * @return the type of this fruit.
	 */
	public int getType() {
		return this.type;
	}
	/**
	 * A getter for the boolean parameter for is alive. 
	 * @return true if the fruit being eaten, else false.
	 */
	public boolean getisAlive() {
		return this.isAlive;
	}
	/**
	 * A setter for the is alive.
	 * @param is
	 */
	public void setisAlive(boolean is) {
		this.isAlive = is;
	}
	/**
	 * @return String that represents the fruit.
	 */
	public String toString() {
		final String ans = "{\"Fruit\":{\"value\":" + this.value + "," + "\"type\":" + this.type + "," + "\"pos\":\"" + this.pos + "\"" + "}" + "}";
		return ans;
	}
}
