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
	public Fruit() {
		this.value = 0;
        this.pos = new Point3D(0,0,0);
        this.type = 0;
        this.isAlive = false;
	}
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
	public Edge getEdge() {
		return edge;
	}
	public void setEdge(Edge e) {
		this.edge = e;
	}
	public double getValue() {
		return this.value;
	}
	public Point3D getPos() {
		return this.pos;
	}
	public int getType() {
		return this.type;
	}
	public boolean getisAlive() {
		return this.isAlive;
	}
	public void setisAlive(boolean is) {
		this.isAlive = is;
	}
}
