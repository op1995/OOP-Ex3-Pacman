package gameClient;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Edge;

public class KML_Logger implements Runnable {

	private game_service Game;
	private int Timer;
	private int Scenario_num;
	private DGraph gameGraph;
	private String KML_Data;
	/**
	 * A constructor.
	 * @param graph
	 * @param game
	 * @param scenario_num
	 */
	public KML_Logger(DGraph graph,game_service game, int scenario_num) {
		this.gameGraph= graph;
		this.Game=game;
		this.Scenario_num=scenario_num;
		this.KML_Data = "";
		if(game.timeToEnd()>30000)
			this.Timer=70000;
		else
			this.Timer=30000;
	}
	/**
	 * This method creates a KML data for file for a game.
	 * @param KML_Info the KML data for the file.
	 * @return String that represents the game in a KML.
	 */
	public String createKMLfile() {
		String KML_Info=
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
						"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
						+ "<Document>\n" + 
						"<name>"+ this.Scenario_num + ".kml</name>\r\n";
		KML_Info+=addNodes();
		while(Game.isRunning()) {
			KML_Info+=Fruits();
			KML_Info+=Robots();
		}
		KML_Info+=	"<Style id=\"orange-5px\">" +
				"<LineStyle>" + 
				"<color>ff00aaff</color>"
				+ "<width>2</width>"+ 
				"</LineStyle>" +
				"</Style>" + 
				"<Placemark>\r\n" + 
				"<name>"+ this.Scenario_num +"</name>\r\n" + 
				"<styleUrl>#orange-5px</styleUrl>\r\n" + 
				"<LineString>\r\n" + 
				"<tessellate>1</tessellate>\r\n" + 
				"<coordinates>\n";
		for( int src : gameGraph.Edges.keySet() ) {
			for ( int dest : gameGraph.Edges.get(src).keySet() ) {		
				KML_Info+=gameGraph.Nodes.get(src).getLocation();
				KML_Info+="\r\n";
				KML_Info+=gameGraph.Nodes.get(dest).getLocation();
				KML_Info+="\r\n";
			}
		}
		KML_Info+="</coordinates>\r\n" +  
				"</LineString>\r\n" + 
				"</Placemark>\r\n" +  
				"</Document>\r\n" + 
				"</kml>\r\n";
		try {
			saveToFile(KML_Info);
		} catch (Exception e) {
			System.out.println(e);
		}
		this.KML_Data = KML_Info;
		return KML_Info;
	}
	/**
	 * This method create a KML data for the Robots of the Game.
	 * @param RobotsInfo The KML data for the Robots of this game.
	 * @param Colors An array of colors.
	 * @param RobotPics An array of url's for the robots pics.
	 * @return KML data of the Robots for a KML file.
	 */
	public String Robots() {
		String RobotsInfo="";
		String[] Colors= {"ff0000ff","ffff0000","ff800080","ff00ffff","ffff00ff"};
		String[] RobotPics = {"http://maps.google.com/mapfiles/kml/shapes/horsebackriding.png", "http://maps.google.com/mapfiles/kml/shapes/swimming.png", "http://maps.google.com/mapfiles/kml/shapes/motorcycling.png", "http://maps.google.com/mapfiles/kml/shapes/cycling.png", "http://maps.google.com/mapfiles/kml/shapes/ski.png"};
		int i=0;
		gameGraph.Robots = gameGraph.Robots;
		for(int robot: gameGraph.Robots.keySet()) {
			RobotsInfo+=	"<Placemark>\r\n"+
					"<TimeSpan>\r\n"+
					"<begin>"+LocalDateTime.now()+"</begin>\r\n" + 
					"<end>" + LocalDateTime.now() + "</end>\r\n" +
					"</TimeSpan>\r\n"+
					"<color>"+Colors[i]+"</color>"+
					"<Style id=\"mycustommarker\">\r\n" + 
					"<IconStyle>\r\n" + 
					"<Icon>\r\n" + 
					"<href>"+RobotPics[i]+"</href>" +
					"</Icon>\r\n" + 
					"</IconStyle>\r\n" + 
					"</Style>\r\n"+
					"<Point>\r\n"+  
					"<coordinates>"+ gameGraph.Robots.get(robot).getPos() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
			i++;
		}
		return RobotsInfo;
	}
	/**
	 * This method create a KML data for the Fruits of the Game.
	 * @param FruitsInfo The KML data for the Fruits of this game.
	 * @return KML data of the Fruits for a KML file.
	 */
	public String Fruits() {
		String FruitsInfo="";
		try {
			for(Fruit fruit: gameGraph.Fruits.keySet()) {
				FruitsInfo+=	"<Placemark>\r\n"+
						"<TimeSpan>\r\n"+
						"<begin>"+LocalDateTime.now()+"</begin>\r\n" + 
						"<end>" + LocalDateTime.now() + "</end>\r\n" +
						"</TimeSpan>\r\n"+
						"<Style id=\"mycustommarker\">\r\n" + 
						"<IconStyle>\r\n" + 
						"<Icon>\r\n" ; 
				if(fruit.getType()==-1) {
					FruitsInfo+=	"<href>http://maps.google.com/mapfiles/ms/micons/coffeehouse.png</href>";
				}
				else
					FruitsInfo+=	"<href>http://maps.google.com/mapfiles/ms/micons/grocerystore.png</href>";
				FruitsInfo+=	"</Icon>\r\n" + 
						"</IconStyle>\r\n" + 
						"</Style>\r\n"+
						"<Point>\r\n"+  
						"<coordinates>"+ fruit.getPos() + "</coordinates>\r\n"+  
						"</Point>\r\n"+
						"</Placemark>\r\n";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return FruitsInfo;
	}
	/**
	 * This method adds the Nodes of this Graph to a KML data string.
	 * @return A KML data string of the nodes for a file.
	 */
	public String addNodes() {
		String NodesInfo="";
		for(int node : gameGraph.Nodes.keySet()) {
			NodesInfo+=	"<Placemark>\r\n"+
					"<color>ff00aaff</color>"+
					"<Style id=\"mycustommarker\">\r\n" + 
					"<IconStyle>\r\n" + 
					"<Icon>\r\n" + 
					"<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>" + 
					"</Icon>\r\n" + 
					"</IconStyle>\r\n" + 
					"</Style>\r\n"+
					"<Point>\r\n"+  
					"<coordinates>"+ gameGraph.Nodes.get(node).getLocation() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
		}
		return NodesInfo;
	}
	public void saveToFile(String file_name) throws IOException {
		try {
//			LocalDateTime CreatingTime = LocalDateTime.now();
//			File f=new File(this.Scenario_num+"_"+CreatingTime.toString()+".kml");
			File f=new File(this.Scenario_num+".kml");
			
			PrintWriter printWriter=new PrintWriter(f);
			printWriter.write(file_name);
			printWriter.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public String getKML_Data() {
		return this.KML_Data;
	}
	@Override
	public void run() {
		createKMLfile();
	}

}