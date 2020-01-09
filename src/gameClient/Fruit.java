package gameClient;

import utils.Point3D;

public class Fruit {
	private double value;
	private int type;
	private Point3D pos;
	private String[] pattern = {"\"value\":", "\"type\":", "\"pos\":"};
	public Fruit(String dataString) {
		dataString = dataString.substring(12, dataString.length()-3);
		for (String currentReplace: pattern) {
			dataString.replace(currentReplace, "");
		}
		String[] dataArray = dataString.split(",");
		this.value = Double.valueOf(dataArray[0]);
		this.type = Integer.valueOf(dataArray[1]);
		this.pos = new Point3D(Double.valueOf(dataArray[2]), Double.valueOf(dataArray[3]), Double.valueOf(dataArray[4]));
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
