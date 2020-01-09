package gameClient;
import dataStructure.*;
import utils.Point3D;
public class Robot {
	private int id;
	private double value; // Gained value of acquired fruits.
	private int src; //current node (location)
	private int dest; //current final node to get to
	private double speed;
	private Point3D position; // based on pos.
	private String[] pattern = {"\"id\":", "\"value\":", "\"src\":", "\"dest\":", "\"speed\":", "\"pos\":"};
	public Robot(String dataString) {
		dataString = dataString.substring(12, dataString.length()-3);
		for (String currentReplace: pattern) {
			dataString.replace(currentReplace, "");
		}
		String[] dataArray = dataString.split(",");
		
		this.id = Integer.valueOf(dataArray[0]);
		this.value = Double.valueOf(dataArray[1]);
		this.src = Integer.valueOf(dataArray[2]);
		this.dest = Integer.valueOf(dataArray[3]);
		this.speed = Double.valueOf(dataArray[4]);
		this.position = new Point3D(Double.valueOf(dataArray[5]), Double.valueOf(dataArray[6]), Double.valueOf(dataArray[7]));		
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
