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
	public Fruit(String Json) {
    	try {
    		JSONObject g = new JSONObject(Json);
			this.value = g.getJSONObject("Fruit").getDouble("value");
			String pos = g.getJSONObject("Fruit").getString("pos");
	        this.pos = new Point3D(pos);
	        this.type = g.getJSONObject("Fruit").getInt("type");
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
}
