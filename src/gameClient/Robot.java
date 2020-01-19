package gameClient;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructure.*;
import utils.Point3D;
public class Robot extends Thread{
	private int id;
	private double value; // Gained value of acquired fruits.
	private int src; //current node (location)
	private int dest; //current final node to get to
	private double speed;
	private Point3D position; // based on pos.
	private boolean isEating;
	private ArrayList<node_data> PathToFruit;
	/**
	 * A constructor.
	 * @param Json A JSON String that represents the robot.
	 */
	public Robot(String Json) {
		 try {
			 	JSONObject g = new JSONObject(Json);
	        	this.id = g.getJSONObject("Robot").getInt("id");
	            String pos = g.getJSONObject("Robot").getString("pos");
	            this.position = new Point3D(pos);
	            this.value = g.getJSONObject("Robot").getDouble("value");
	            this.src = g.getJSONObject("Robot").getInt("src");
	            this.dest = g.getJSONObject("Robot").getInt("dest");
	            this.speed = g.getJSONObject("Robot").getDouble("speed");
	            this.isEating = false;
	            this.PathToFruit = new ArrayList<node_data>();
	        } catch (Exception e) {System.out.println(e);}
	}
	/**
	 * A getter for the ID of this Robot.
	 * @return ID of this Robot.
	 */
	public int getID() {
		return this.id;
	}
	/**
	 * A setter for the source node id of this Robot.
	 * @param src id of the source node.
	 */
	public void setSrc(int src) {
		this.src = src;
	}
	/**
	 * A getter for the source node id of this Robot.
	 * @return id of the source node.
	 */
	public int getSrc() {
		return this.src;
	}
	/**
	 * A setter for the destination node of this Robot.
	 * @param dest The key of the destination node of this Robot.
	 */
	public void setDest(int dest) {
		this.dest = dest;
	}
	/**
	 * A getter for the key of the destination node of this Robot. 
	 * @return key of the destination node of this Robot.
	 */
	public int getDest() {
		return this.dest;
	}
	/**
	 * A setter for the speed of this robot.
	 * @param speed The speed of this robot.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	/**
	 * A getter for the speed of this robot.
	 * @return The speed of this robot.
	 */
	public double getSpeed() {
		return this.speed;
	}
	/**
	 * A setter for the Position of this robot.
	 * @param pos The Position of this robot.
	 */
	public void setPos(Point3D pos) {
		this.position = new Point3D(pos.x(), pos.y(), pos.z());
	}
	/**
	 * A getter for the Position of this robot.
	 * @return Position of this robot.
	 */
	public Point3D getPos() {
		return this.position;
	}
	/**
	 * A setter for the value of this robot.
	 * @param value The value of this robot.
	 */
	public void setValue(double value) {
		this.value = value;
	}
	/**
	 * A getter for the value of this robot.
	 * @return value of this robot.
	 */
	public double getValue() {
		return this.value;
	}
	public void setisEating(boolean is) {
		this.isEating = is;
	}
	public boolean getisEating() {
		return this.isEating;
	}
	/**
	 * A getter for the Path to fruit.
	 * @return PathToFruit.
	 */
	public ArrayList<node_data> getPathToFruit(){
		return PathToFruit;
	}
	/**
	 * A setter for the Path To Fruit.
	 * @param PathToFruit the path to the nearest fruit.
	 */
	public void setPathToFruit(ArrayList<node_data> path) {
		this.PathToFruit = path;
	}
}
