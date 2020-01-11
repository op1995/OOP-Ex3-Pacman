package gameClient;
import org.json.JSONArray;
import org.json.JSONObject;

import dataStructure.*;
import utils.Point3D;
public class Robot {
	private int id;
	private double value; // Gained value of acquired fruits.
	private int src; //current node (location)
	private int dest; //current final node to get to
	private double speed;
	private Point3D position; // based on pos.
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
	        } catch (Exception e) {System.out.println(e);}
	}
	public int getID() {
		return this.id;
	}
	public void setSrc(int src) {
		this.src = src;
	}
	public int getSrc() {
		return this.src;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}
	public int getDest() {
		return this.dest;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getSpeed() {
		return this.speed;
	}
	public void setPos(Point3D pos) {
		this.position = new Point3D(pos.x(), pos.y(), pos.z());
	}
	public Point3D getPos() {
		return this.position;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return this.value;
	}
}
